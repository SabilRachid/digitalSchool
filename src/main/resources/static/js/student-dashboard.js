// Initialisation des graphiques du tableau de bord étudiant
document.addEventListener('DOMContentLoaded', function() {
    // Notes par matière
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

    // Évolution des moyennes
    new Chart(document.getElementById('gradesProgressChart'), {
        type: 'line',
        data: {
            labels: ['Sept', 'Oct', 'Nov', 'Déc', 'Jan', 'Fév'],
            datasets: [{
                label: 'Moyenne générale',
                data: [14, 14.5, 15, 15.2, 15.5, 15.5],
                borderColor: '#48BB78',
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

    // Assiduité
    new Chart(document.getElementById('attendanceChart'), {
        type: 'pie',
        data: {
            labels: ['Présent', 'Absent', 'Retard'],
            datasets: [{
                data: [95, 3, 2],
                backgroundColor: ['#48BB78', '#F56565', '#ED8936']
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

    // Temps d'étude
    new Chart(document.getElementById('studyTimeChart'), {
        type: 'radar',
        data: {
            labels: ['Maths', 'Français', 'Histoire', 'Physique', 'Anglais', 'SVT'],
            datasets: [{
                label: 'Heures par semaine',
                data: [4, 3, 2, 3, 2, 2],
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
                    max: 5
                }
            }
        }
    });
});