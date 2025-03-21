// Initialisation des graphiques du tableau de bord administrateur
document.addEventListener('DOMContentLoaded', function() {

    // les statistiques du tableau de bord
    fetchDashboardStats();

    // 📊 Répartition des utilisateurs
    initUserDistributionChart();

    // 📈 Évolution des inscriptions
    initRegistrationTrendChart();

    // 📊 Répartition des professeurs par matière (Dynamique via API)
    loadProfessorDistributionChart();

    // 📊 Performance par niveau scolaire
    initLevelPerformanceChart();

    // 📊 Taux de réussite par matière
    initSuccessRateChart();

    // 📊 Activité récente
    initRecentEvents();
});

/**
 * Récupère les statistiques du tableau de bord depuis l'API et les met à jour dynamiquement
 */
/**
 * Récupère les statistiques du tableau de bord et met à jour l'affichage
 */
async function fetchDashboardStats() {
    try {
        const response = await fetch('/admin/api/dashboard/stats');

        if (!response.ok) {
            throw new Error('Erreur lors de la récupération des statistiques.');
        }

        const data = await response.json();
        console.log("📌 Statistiques reçues :", data);

        // Mise à jour dynamique des valeurs dans la page
        updateElementText("totalStudents", data.totalStudents);
        updateElementText("studentsActive", data.studentsActive);
        updateElementText("studentsPending", data.studentsPending);

        updateElementText("totalProfessors", data.totalProfessors);
        updateElementText("fullTimeProfessors", data.fullTimeProfessors);
        updateElementText("partTimeProfessors", data.partTimeProfessors);

        updateElementText("totalClasses", data.totalClasses);
        updateElementText("occupancyRate", parseInt(data.occupancyRate) + "%");
        updateElementText("availableSeats", data.availableSeats);

        updateElementText("attendanceRate", data.attendanceRate + "%");
        updateElementText("justifiedAbsencesRate", data.justifiedAbsencesRate + "%");
        updateElementText("unjustifiedAbsencesRate", data.unjustifiedAbsencesRate + "%");

        // Mise à jour des tendances
        updateTrend("studentTrend", data.studentTrend || 12);
        updateTrend("professorTrend", data.professorTrend || 5);
        updateTrend("classTrend", data.classTrend || 8);
        updateTrend("attendanceTrend", -2); // Exemple de tendance négative de 2%



    } catch (error) {
        console.error("🚨 Erreur chargement stats :", error);
    }
}

/**
 * Met à jour le texte d'un élément HTML si présent
 * @param {string} elementId ID de l'élément HTML
 * @param {string|number} value Nouvelle valeur à afficher
 */
function updateElementText(elementId, value) {
    const element = document.getElementById(elementId);
    if (element) {
        element.textContent = value;
    }
}



/**
 * Met à jour la tendance d'un élément HTML
 * @param {string} elementId ID de l'élément HTML
 * @param {number} percent Pourcentage d'évolution
 */
function updateTrend(elementId, percent) {
    let trendElement = document.getElementById(elementId);
    if (!trendElement) return;

    trendElement.innerHTML = `
        <i class="fas ${percent >= 0 ? 'fa-arrow-up text-success' : 'fa-arrow-down text-danger'}"></i>
        <span>${Math.abs(percent)}% vs mois dernier</span>
    `;
}







/**
 * Répartition des utilisateurs (Administrateurs, Professeurs, Étudiants)
 */
function initUserDistributionChart() {
    fetch('/admin/api/dashboard/user-distribution')
        .then(response => response.json())
        .then(data => {
            const labels = data.map(item => item.role);
            const values = data.map(item => item.count);
            const colors = ['#4C51BF', '#48BB78', '#4299E1'];

            new Chart(document.getElementById('userDistributionChart'), {
                type: 'doughnut',
                data: {
                    labels: labels,
                    datasets: [{
                        data: values,
                        backgroundColor: colors
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: { position: 'bottom' }
                    }
                }
            });
        })
        .catch(error => console.error('Erreur lors du chargement des données :', error));
}


/**
 * Évolution des inscriptions sur les derniers mois
 */
function initRegistrationTrendChart() {
    fetch('/admin/api/dashboard/registrations-trend')
        .then(response => response.json())
        .then(data => {
            const labels = data.map(item => item.month);
            const values = data.map(item => item.count);

            new Chart(document.getElementById('registrationTrendChart'), {
                type: 'line',
                data: {
                    labels: labels,
                    datasets: [{
                        label: 'Inscriptions Étudiants',
                        data: values,
                        borderColor: '#4C51BF',
                        backgroundColor: 'rgba(76, 81, 191, 0.2)',
                        borderWidth: 2,
                        tension: 0.4
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    scales: {
                        y: {
                            beginAtZero: true
                        }
                    },
                    plugins: {
                        legend: {
                            position: 'top'
                        }
                    }
                }
            });
        })
        .catch(error => console.error('Erreur lors du chargement des données :', error));
}

/**
 * Chargement dynamique de la répartition des professeurs par matière depuis l'API
 */
function loadProfessorDistributionChart() {
    fetch('/admin/api/dashboard/professors-per-subject')
        .then(response => {
            if (!response.ok) throw new Error('Erreur réseau');
            return response.json();
        })
        .then(data => {
            const labels = data.map(item => item.subject);
            const values = data.map(item => item.count);

            new Chart(document.getElementById('distributionProfChart'), {
                type: 'bar',
                data: {
                    labels: labels,
                    datasets: [{
                        label: 'Nombre de Professeurs par Matière',
                        data: values,
                        backgroundColor: 'rgba(75, 192, 192, 0.2)',
                        borderColor: 'rgba(75, 192, 192, 1)',
                        borderWidth: 1
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    scales: { y: { beginAtZero: true } }
                }
            });
        })
        .catch(error => console.error('Erreur lors du chargement des données :', error));
}

/**
 * Moyenne des performances par niveau scolaire
 */

// 📊 Performance par Niveau scolaire
function initLevelPerformanceChart() {
    fetch('/admin/api/dashboard/level-performance')
        .then(response => response.json())
        .then(data => {
            const labels = data.map(item => item.level);
            const values = data.map(item => item.average);

            new Chart(document.getElementById('levelPerformanceChart'), {
                type: 'bar',
                data: {
                    labels: labels,
                    datasets: [{
                        label: 'Moyenne générale',
                        data: values,
                        backgroundColor: '#48BB78'
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
        })
        .catch(error => console.error('Erreur lors du chargement des données :', error));
}


/**
 * Taux de réussite par matière en radar chart
 */
function initSuccessRateChart() {
    fetch('/admin/api/dashboard/success-rate')
        .then(response => response.json())
        .then(data => {
            const labels = data.map(item => item.subject);
            const values = data.map(item => item.successRate);

            new Chart(document.getElementById('successRateChart'), {
                type: 'radar',
                data: {
                    labels: labels,
                    datasets: [{
                        label: 'Taux de réussite',
                        data: values,
                        backgroundColor: 'rgba(76, 81, 191, 0.2)',
                        borderColor: '#4C51BF'
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    scales: {
                        r: {
                            beginAtZero: true,
                            max: 100
                        }
                    }
                }
            });
        })
        .catch(error => console.error('Erreur lors du chargement des données :', error));
}





//donne moi le jpa repository

function initRecentEvents() {
    fetch('/admin/api/dashboard/last-events')
        .then(response => response.json())
        .then(data => {
            const activityList = document.getElementById('activityList');
            data.forEach(event => {
                const eventHtml = `
                    <div class="activity-item">
                        <div class="activity-icon ${getEventColor(event.eventType)}">
                            <i class="${getEventIcon(event.eventType)}"></i>
                        </div>
                        <div class="activity-content">
                            <p class="activity-text">${event.title}</p>
                            <span class="activity-time">${event.timeAgo}</span>
                        </div>
                        <div class="activity-actions">
                            <button class="action-button view">Voir</button>
                        </div>
                    </div>`;
                activityList.innerHTML += eventHtml; // ✅ Correction ici
            });
        })
        .catch(error => console.error('Error fetching activities:', error));

}


function getEventColor(eventType) {
    switch (eventType) {
        case 'COURSE':
            return 'bg-primary'; // Bleu pour les cours
        case 'EXAM':
            return 'bg-danger'; // Rouge pour les examens
        case 'EVENT':
            return 'bg-success'; // Vert pour les événements
        case 'MEETING':
            return 'bg-warning'; // Jaune pour les réunions
        default:
            return 'bg-secondary'; // Gris par défaut
    }
}


function getEventIcon(eventType) {
    switch (eventType) {
        case 'COURSE':
            return 'fas fa-book-open'; // Icône pour un cours
        case 'EXAM':
            return 'fas fa-pencil-alt'; // Icône pour un examen
        case 'EVENT':
            return 'fas fa-calendar-check'; // Icône pour un événement
        case 'MEETING':
            return 'fas fa-users'; // Icône pour une réunion
        default:
            return 'fas fa-info-circle'; // Icône par défaut
    }
}
