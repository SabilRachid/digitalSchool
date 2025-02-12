document.addEventListener("DOMContentLoaded", function () {
    fetchClassPerformance();
    fetchGradesDistribution();
    fetchParticipationRate();
    fetchAverageProgression();
});

function fetchClassPerformance() {
    fetch('/professor/api/dashboard/class-performance')
        .then(response => response.json())
        .then(data => {
            const labels = data.map(item => item.className);
            const values = data.map(item => item.average);

            new Chart(document.getElementById('classPerformanceChart'), {
                type: 'bar',
                data: {
                    labels: labels,
                    datasets: [{
                        label: 'Moyenne de classe',
                        data: values,
                        backgroundColor: '#4C51BF'
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    scales: { y: { beginAtZero: true, max: 20 } }
                }
            });
        })
        .catch(error => console.error("🚨 Erreur chargement performance des classes :", error));
}

function fetchGradesDistribution() {
    fetch('/professor/api/dashboard/grades-distribution')
        .then(response => response.json())
        .then(data => {
            const labels = data.map(item => item.range);
            const values = data.map(item => item.count);

            new Chart(document.getElementById('gradesDistributionChart'), {
                type: 'bar',
                data: {
                    labels: labels,
                    datasets: [{
                        label: 'Nombre d\'élèves',
                        data: values,
                        backgroundColor: '#48BB78'
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    scales: { y: { beginAtZero: true } }
                }
            });
        })
        .catch(error => console.error("🚨 Erreur chargement distribution des notes :", error));
}

function fetchParticipationRate() {
    fetch('/professor/api/dashboard/participation-rate')
        .then(response => response.json())
        .then(data => {
            new Chart(document.getElementById('participationRateChart'), {
                type: 'doughnut',
                data: {
                    labels: ['Participation active', 'Participation moyenne', 'Participation faible'],
                    datasets: [{
                        data: [data.high, data.medium, data.low],
                        backgroundColor: ['#48BB78', '#ED8936', '#F56565']
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: { legend: { position: 'bottom' } }
                }
            });
        })
        .catch(error => console.error("🚨 Erreur chargement taux de participation :", error));
}

function fetchAverageProgression() {
    fetch('/professor/api/dashboard/average-progression')
        .then(response => response.json())
        .then(data => {
            const labels = data.map(item => item.month);
            const values = data.map(item => item.average);

            new Chart(document.getElementById('averageProgressionChart'), {
                type: 'line',
                data: {
                    labels: labels,
                    datasets: [{
                        label: 'Moyenne générale',
                        data: values,
                        borderColor: '#4299E1',
                        tension: 0.4
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    scales: { y: { beginAtZero: true, max: 20 } }
                }
            });
        })
        .catch(error => console.error("🚨 Erreur chargement progression des moyennes :", error));
}
