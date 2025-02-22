document.addEventListener('DOMContentLoaded', function() {
    initializeSubjectCharts();
    initializeProgressionChart();
});

// Initialisation des graphiques par matière
function initializeSubjectCharts() {
    document.querySelectorAll('.grades-chart canvas').forEach(canvas => {
        // Ici, on suppose que l'id du canvas est au format "chart-Matière" (sans espaces)
        const subject = canvas.id.replace('chart-', '');
        const grades = getSubjectGrades(subject);
        console.log("Données pour la matière", subject, grades);

        if (!grades || grades.length === 0) {
            console.warn("Aucune donnée pour", subject);
            return;
        }

        new Chart(canvas, {
            type: 'line',
            data: {
                labels: grades.map(g => formatDate(g.date)),
                datasets: [{
                    label: 'Mes notes',
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

// Initialisation du graphique de progression
function initializeProgressionChart() {
    const progressionCanvas = document.getElementById('progressionChart');
    if (!progressionCanvas) {
        console.warn("Canvas de progression introuvable");
        return;
    }
    fetch('/student/api/grades/progression')
        .then(response => response.json())
        .then(data => {
            const ctx = progressionCanvas.getContext('2d');
            new Chart(ctx, {
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
}

// Récupère les notes d'une matière depuis window.gradesData
function normalize(str) {
    return str.toLowerCase().normalize("NFD").replace(/[\u0300-\u036f]/g, "");
}

function getSubjectGrades(subject) {
    if (!window.gradesData) {
        console.warn("gradesData is not defined, returning an empty array");
        return [];
    }
    // Normalisez les valeurs pour comparer
    const normalizedSubject = normalize(subject);
    const subjectData = window.gradesData.find(s => normalize(s.subject) === normalizedSubject);
    return subjectData ? subjectData.grades : [];
}

// Formate une date en "dd MMM"
function formatDate(date) {
    return new Date(date).toLocaleDateString('fr-FR', {
        day: '2-digit',
        month: 'short'
    });
}

// Génère des étiquettes de date pour le graphique de progression
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

// Retourne une couleur selon la matière
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

// Téléchargement du bulletin
function downloadBulletin() {
    fetch('/student/api/grades/report')
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

// Affichage des notifications
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
