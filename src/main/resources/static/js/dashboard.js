// Fonctions communes pour tous les tableaux de bord
function initializeCharts(userType) {
    switch(userType) {
        case 'admin':
            initAdminCharts();
            break;
        case 'professor':
            initProfessorCharts();
            break;
        case 'student':
            initStudentCharts();
            break;
    }
}

// Graphiques Admin
function initAdminCharts() {
    // Graphique de répartition des étudiants
    new Chart(document.getElementById('studentDistributionChart'), {
        type: 'doughnut',
        data: {
            labels: ['6ème', '5ème', '4ème', '3ème', '2nde', '1ère', 'Terminale'],
            datasets: [{
                data: [300, 280, 250, 260, 270, 290, 280],
                backgroundColor: [
                    '#4C51BF',
                    '#48BB78',
                    '#4299E1',
                    '#ED8936',
                    '#F56565',
                    '#9F7AEA',
                    '#667EEA'
                ]
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom'
                }
            }
        }
    });

    // Graphique d'évolution des inscriptions
    new Chart(document.getElementById('registrationTrendChart'), {
        type: 'line',
        data: {
            labels: ['Sept', 'Oct', 'Nov', 'Déc', 'Jan', 'Fév', 'Mar'],
            datasets: [{
                label: 'Inscriptions',
                data: [1000, 1050, 1100, 1150, 1200, 1220, 1234],
                borderColor: '#4C51BF',
                tension: 0.4,
                fill: false
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: {
                    beginAtZero: true
                }
            }
        }
    });
}

// Graphiques Professeur
function initProfessorCharts() {
    // Graphique de performance par classe
    new Chart(document.getElementById('classPerformanceChart'), {
        type: 'bar',
        data: {
            labels: ['3ème A', '3ème B', '4ème A', '4ème B', '5ème A'],
            datasets: [{
                label: 'Moyenne de classe',
                data: [14.5, 13.8, 15.2, 14.7, 13.9],
                backgroundColor: '#4C51BF'
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

    // Graphique de répartition des notes
    new Chart(document.getElementById('gradesDistributionChart'), {
        type: 'bar',
        data: {
            labels: ['0-5', '5-8', '8-10', '10-12', '12-15', '15-18', '18-20'],
            datasets: [{
                label: 'Nombre d\'élèves',
                data: [2, 5, 8, 15, 25, 12, 3],
                backgroundColor: '#48BB78'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: {
                    beginAtZero: true
                }
            }
        }
    });
}

// Graphiques Étudiant
function initStudentCharts() {
    // Graphique des notes par matière
    new Chart(document.getElementById('subjectGradesChart'), {
        type: 'bar',
        data: {
            labels: ['Maths', 'Français', 'Histoire', 'Physique', 'Anglais', 'SVT'],
            datasets: [{
                label: 'Notes',
                data: [16, 15, 14, 17, 16, 15],
                backgroundColor: '#4C51BF'
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

    // Graphique d'évolution des moyennes
    new Chart(document.getElementById('gradesProgressChart'), {
        type: 'line',
        data: {
            labels: ['Sept', 'Oct', 'Nov', 'Déc', 'Jan', 'Fév'],
            datasets: [{
                label: 'Moyenne générale',
                data: [14, 14.5, 15, 15.2, 15.5, 15.5],
                borderColor: '#48BB78',
                tension: 0.4,
                fill: false
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
}