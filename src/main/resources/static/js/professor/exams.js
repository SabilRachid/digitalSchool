```javascript
// Gestion des examens
document.addEventListener('DOMContentLoaded', function() {
    initializeForm();
    initializeCharts();
});

// Initialisation du formulaire
function initializeForm() {
    const form = document.getElementById('examForm');
    if (!form) return;

    form.addEventListener('submit', async function(e) {
        e.preventDefault();
        
        try {
            const formData = new FormData(this);
            const data = {
                name: formData.get('name'),
                subject: { id: formData.get('subject') },
                classe: { id: formData.get('classe') },
                examDate: formData.get('examDate'),
                duration: parseInt(formData.get('duration')),
                description: formData.get('description'),
                maxScore: parseFloat(formData.get('maxScore'))
            };

            const response = await fetch('/professor/exams', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                },
                body: JSON.stringify(data)
            });

            if (!response.ok) throw new Error('Erreur lors de la création de l\'examen');

            closeExamModal();
            showNotification('Examen créé avec succès', 'success');
            setTimeout(() => location.reload(), 1500);
        } catch (error) {
            showNotification(error.message, 'error');
        }
    });
}

// Gestion des modales
function openExamModal() {
    document.getElementById('examModal').classList.add('show');
}

function closeExamModal() {
    document.getElementById('examModal').classList.remove('show');
    document.getElementById('examForm').reset();
}

// Actions sur les examens
async function publishExam(examId) {
    try {
        const response = await fetch(`/professor/exams/${examId}/publish`, {
            method: 'POST',
            headers: {
                'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
            }
        });

        if (!response.ok) throw new Error('Erreur lors de la publication');

        showNotification('Examen publié avec succès', 'success');
        setTimeout(() => location.reload(), 1500);
    } catch (error) {
        showNotification(error.message, 'error');
    }
}

async function endExam(examId) {
    if (!confirm('Êtes-vous sûr de vouloir terminer cet examen ?')) return;

    try {
        const response = await fetch(`/professor/exams/${examId}/end`, {
            method: 'POST',
            headers: {
                'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
            }
        });

        if (!response.ok) throw new Error('Erreur lors de la fin de l\'examen');

        showNotification('Examen terminé avec succès', 'success');
        setTimeout(() => location.reload(), 1500);
    } catch (error) {
        showNotification(error.message, 'error');
    }
}

async function viewResults(examId) {
    try {
        const response = await fetch(`/professor/exams/${examId}/results`);
        if (!response.ok) throw new Error('Erreur lors du chargement des résultats');

        const results = await response.json();
        displayResults(results);
        document.getElementById('resultsModal').classList.add('show');
    } catch (error) {
        showNotification(error.message, 'error');
    }
}

function displayResults(results) {
    // Mettre à jour les statistiques
    document.querySelector('#resultsModal .stat-value:nth-child(1)').textContent = 
        results.average.toFixed(2) + '/20';
    document.querySelector('#resultsModal .stat-value:nth-child(2)').textContent = 
        results.highest.toFixed(2) + '/20';
    document.querySelector('#resultsModal .stat-value:nth-child(3)').textContent = 
        results.passRate.toFixed(1) + '%';

    // Mettre à jour le graphique
    updateDistributionChart(results.distribution);

    // Mettre à jour le tableau des résultats
    const tbody = document.getElementById('resultsTableBody');
    tbody.innerHTML = results.studentGrades.map(grade => `
        <tr>
            <td>${grade.studentName}</td>
            <td>${grade.score}/20</td>
            <td>${grade.comments || '-'}</td>
        </tr>
    `).join('');
}

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

async function downloadReport(examId) {
    try {
        const response = await fetch(`/professor/exams/${examId}/report`);
        if (!response.ok) throw new Error('Erreur lors du téléchargement du rapport');

        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `exam-report-${examId}.pdf`;
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