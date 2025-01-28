x²// Initialisation des graphiques du tableau de bord administrateur
document.addEventListener('DOMContentLoaded', function() {
    // Distribution des utilisateurs
    new Chart(document.getElementById('userDistributionChart'), {
        type: 'doughnut',
        data: {
            labels: ['Administrateurs', 'Professeurs', 'Étudiants'],
            datasets: [{
                data: [5, 50, 500],
                backgroundColor: ['#4C51BF', '#48BB78', '#4299E1']
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

    // Évolution des inscriptions
    new Chart(document.getElementById('registrationTrendChart'), {
        type: 'line',
        data: {
            labels: ['Sept', 'Oct', 'Nov', 'Déc', 'Jan', 'Fév'],
            datasets: [{
                label: 'Nouvelles inscriptions',
                data: [120, 150, 180, 190, 210, 250],
                borderColor: '#4C51BF',
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
            }
        }
    });

    // Performance par niveau
    new Chart(document.getElementById('levelPerformanceChart'), {
        type: 'bar',
        data: {
            labels: ['6ème', '5ème', '4ème', '3ème', '2nde', '1ère', 'Terminale'],
            datasets: [{
                label: 'Moyenne générale',
                data: [14.5, 13.8, 14.2, 15.1, 14.7, 15.3, 15.8],
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

    // Taux de réussite
    new Chart(document.getElementById('successRateChart'), {
        type: 'radar',
        data: {
            labels: ['Maths', 'Français', 'Histoire', 'Sciences', 'Langues', 'Sport'],
            datasets: [{
                label: 'Taux de réussite',
                data: [85, 90, 88, 92, 87, 95],
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
    
   
});