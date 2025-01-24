// Initialisation des graphiques du tableau de bord professeur
document.addEventListener('DOMContentLoaded', function() {
    // Performance par classe
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

    // Distribution des notes
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

    // Taux de participation
    new Chart(document.getElementById('participationRateChart'), {
        type: 'doughnut',
        data: {
            labels: ['Participation active', 'Participation moyenne', 'Participation faible'],
            datasets: [{
                data: [60, 30, 10],
                backgroundColor: ['#48BB78', '#ED8936', '#F56565']
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

    // Progression des moyennes
    new Chart(document.getElementById('averageProgressionChart'), {
        type: 'line',
        data: {
            labels: ['Sept', 'Oct', 'Nov', 'Déc', 'Jan', 'Fév'],
            datasets: [{
                label: 'Moyenne générale',
                data: [13.5, 14.2, 14.8, 14.5, 15.1, 14.9],
                borderColor: '#4299E1',
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