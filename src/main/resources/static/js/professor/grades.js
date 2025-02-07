```javascript
// Gestion des notes
document.addEventListener('DOMContentLoaded', function() {
    initializeFilters();
    initializeGradeInputs();
    initializeCharts();
});

// Initialisation des filtres
function initializeFilters() {
    const classeFilter = document.getElementById('classeFilter');
    const subjectFilter = document.getElementById('subjectFilter');

    if (classeFilter && subjectFilter) {
        classeFilter.addEventListener('change', loadGrades);
        subjectFilter.addEventListener('change', loadGrades);
    }
}

// Chargement des notes
async function loadGrades() {
    const classeId = document.getElementById('classeFilter').value;
    const subjectId = document.getElementById('subjectFilter').value;

    if (!classeId || !subjectId) return;

    try {
        const response = await fetch(`/professor/grades/class/${classeId}/subject/${subjectId}`);
        if (!response.ok) throw new Error('Erreur lors du chargement des notes');

        const grades = await response.json();
        displayGrades(grades);
        loadStatistics(classeId, subjectId);
    } catch (error) {
        showNotification(error.message, 'error');
    }
}

// Affichage des notes
function displayGrades(grades) {
    const tbody = document.querySelector('#gradesTable tbody');
    tbody.innerHTML = grades.map(student => `
        <tr>
            <td>
                <div class="student-name">
                    <div class="student-avatar">
                        ${student.firstName.charAt(0)}${student.lastName.charAt(0)}
                    </div>
                    <span>${student.lastName} ${student.firstName}</span>
                </div>
            </td>
            ${student.grades.map(grade => `
                <td>
                    <input type="number" 
                           class="grade-input" 
                           value="${grade || ''}"
                           min="0" 
                           max="20" 
                           step="0.5"
                           data-student-id="${student.id}"
                           onchange="updateGrade(this)">
                </td>
            `).join('')}
            <td class="average">${calculateAverage(student.grades)}</td>
            <td>
                <button class="btn btn-sm btn-primary" 
                        onclick="addGrade(${student.id})">
                    <i class="fas fa-plus"></i>
                </button>
            </td>
        </tr>
    `).join('');
}

// Calcul de la moyenne
function calculateAverage(grades) {
    const validGrades = grades.filter(g => g !== null);
    if (validGrades.length === 0) return '-';
    
    const sum = validGrades.reduce((a, b) => a + b, 0);
    return (sum / validGrades.length).toFixed(2);
}

// Mise à jour d'une note
async function updateGrade(input) {
    const value = parseFloat(input.value);
    if (isNaN(value) || value < 0 || value > 20) {
        input.value = '';
        return;
    }

    try {
        const response = await fetch('/professor/grades', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
            },
            body: JSON.stringify({
                studentId: input.dataset.studentId,
                value: value
            })
        });

        if (!response.ok) throw new Error('Erreur lors de la sauvegarde');

        updateStudentAverage(input.closest('tr'));
        loadStatistics();
    } catch (error) {
        showNotification(error.message, 'error');
        input.value = input.defaultValue;
    }
}

// Mise à jour de la moyenne d'un étudiant
function updateStudentAverage(row) {
    const grades = Array.from(row.querySelectorAll('.grade-input'))
        .map(input => parseFloat(input.value))
        .filter(grade => !isNaN(grade));
    
    row.querySelector('.average').textContent = calculateAverage(grades);
}

// Ajout d'une nouvelle note
function addGrade(studentId) {
    document.getElementById('gradeModal').classList.add('show');
    document.getElementById('gradeForm').dataset.studentId = studentId;
}

// Chargement des statistiques
async function loadStatistics(classeId, subjectId) {
    try {
        const response = await fetch(`/professor/grades/statistics/${classeId}?subject=${subjectId}`);
        if (!response.ok) throw new Error('Erreur lors du chargement des statistiques');

        const stats = await response.json();
        updateStatistics(stats);
        updateDistributionChart(stats.distribution);
    } catch (error) {
        showNotification(error.message, 'error');
    }
}

// Mise à jour des statistiques
function updateStatistics(stats) {
    document.getElementById('classAverage').textContent = stats.average.toFixed(2) + '/20';
    document.getElementById('highestGrade').textContent = stats.highest.toFixed(2) + '/20';
    document.getElementById('lowestGrade').textContent = stats.lowest.toFixed(2) + '/20';
    document.getElementById('passRate').textContent = stats.passRate.toFixed(1) + '%';
}

// Mise à jour du graphique de distribution
function updateDistributionChart(distribution) {
    const ctx = document.getElementById('gradesDistributionChart').getContext('2d');
    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: ['0-5', '5-8', '8-10', '10-12', '12-15', '15-18', '18-20'],
            datasets: [{
                label: 'Nombre d\'élèves',
                data: distribution,
                backgroundColor: '#4F46E5',
                borderRadius: 8
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: false
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        stepSize: 1
                    }
                }
            }
        }
    });
}

// Génération du bulletin
async function generateReport() {
    const classeId = document.getElementById('classeFilter').value;
    if (!classeId) {
        showNotification('Veuillez sélectionner une classe', 'warning');
        return;
    }

    try {
        const response = await fetch(`/professor/grades/report/${classeId}`);
        if (!response.ok) throw new Error('Erreur lors de la génération du bulletin');

        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'bulletin.pdf';
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
    } catch (error) {
        showNotification(error.message, 'error');
    }
}

// Notifications
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.innerHTML = `
        <div class="notification-content">
            <i class="fas ${type === 'success' ? 'fa-check-circle' : 'fa-exclamation-circle'}"></i>
            <span>${message}</span>
        </div>
        <button class="notification-close">&times;</button>
    `;

    document.body.appendChild(notification);
    setTimeout(() => notification.classList.add('show'), 100);

    notification.querySelector('.notification-close').addEventListener('click', () => {
        notification.classList.remove('show');
        setTimeout(() => notification.remove(), 300);
    });

    setTimeout(() => {
        notification.classList.remove('show');
        setTimeout(() => notification.remove(), 300);
    }, 5000);
}
```