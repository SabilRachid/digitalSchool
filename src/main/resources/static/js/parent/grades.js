// Gestion des notes
document.addEventListener('DOMContentLoaded', function() {
    initializeSubjectCharts();
    initializeProgressionCharts();
});

// Initialisation des graphiques par matière
function initializeSubjectCharts() {
    document.querySelectorAll('.grades-chart canvas').forEach(canvas => {
        const [childId, subject] = canvas.id.replace('chart-', '').split('-');
        const grades = getSubjectGrades(childId, subject);
        console.log("Données pour la matière", subject, grades);
        new Chart(canvas, {
            type: 'line',
            data: {
                labels: grades.map(g => formatDate(g.date)),
                datasets: [{
                    label: 'Notes',
                    data: grades.map(g => g.value),
                    borderColor: '#4F46E5',
                    tension: 0.4
                }, {
                    label: 'Moyenne classe',
                    data: grades.map(g => g.classAverage),
                    borderColor: '#9CA3AF',
                    borderDash: [5, 5],
                    tension: 0.4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true,
                        max: 20
                    }
                }
            }
        });
    });
}

// Initialisation des graphiques de progression
function initializeProgressionCharts() {
    document.querySelectorAll('.progression-chart canvas').forEach(canvas => {
        const childId = canvas.id.replace('progression-', '');

        fetch(`/parent/grades/child/${childId}/progression`)
            .then(response => response.json())
            .then(data => {
                new Chart(canvas, {
                    type: 'line',
                    data: {
                        labels: generateDateLabels(),
                        datasets: Object.entries(data).map(([subject, grades]) => ({
                            label: subject,
                            data: grades,
                            borderColor: getSubjectColor(subject),
                            tension: 0.4
                        }))
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        scales: {
                            y: {
                                beginAtZero: true,
                                max: 20
                            }
                        }
                    }
                });
            })
            .catch(error => showNotification(error.message, 'error'));
    });
}

// Téléchargement du bulletin
function downloadBulletin(childId) {
    fetch(`/parent/grades/report/${childId}`)
        .then(response => {
            if (!response.ok) throw new Error('Erreur lors du téléchargement');
            return response.blob();
        })
        .then(blob => {
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = 'bulletin.pdf';
            document.body.appendChild(a);
            a.click();
            window.URL.revokeObjectURL(url);
            document.body.removeChild(a);
        })
        .catch(error => showNotification(error.message, 'error'));
}

// Utilitaires
function getSubjectGrades(childId, subject) {
    const childData = window.gradesData.find(c => c.childId === parseInt(childId));
    if (!childData) return [];

    const subjectData = childData.subjectGrades.find(s => s.subject === subject);
    return subjectData ? subjectData.grades : [];
}

function formatDate(date) {
    return new Date(date).toLocaleDateString('fr-FR', {
        day: '2-digit',
        month: 'short'
    });
}

function generateDateLabels() {
    const labels = [];
    const now = new Date();
    for (let i = 5; i >= 0; i--) {
        const date = new Date(now);
        date.setMonth(now.getMonth() - i);
        labels.push(date.toLocaleDateString('fr-FR', { month: 'short' }));
    }
    return labels;
}

function getSubjectColor(subject) {
    const colors = {
        'Mathématiques': '#4F46E5',
        'Français': '#059669',
        'Histoire-Géo': '#D97706',
        'Anglais': '#DC2626',
        'SVT': '#4299E1',
        'Physique-Chimie': '#8B5CF6'
    };
    return colors[subject] || '#6B7280';
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
