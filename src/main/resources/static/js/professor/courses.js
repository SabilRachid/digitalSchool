// Initialisation des graphiques et fonctionnalités du tableau de bord étudiant
document.addEventListener('DOMContentLoaded', function() {
    // Graphique de progression
    const progressCtx = document.getElementById('progressChart').getContext('2d');
    new Chart(progressCtx, {
        type: 'line',
        data: {
            labels: ['Sept', 'Oct', 'Nov', 'Déc', 'Jan', 'Fév'],
            datasets: [{
                label: 'Moyenne Générale',
                data: [12, 13, 13.5, 14, 14.5, 15],
                borderColor: '#4F46E5',
                tension: 0.4,
                fill: false
            }, {
                label: 'Moyenne Classe',
                data: [11.5, 12, 12.5, 13, 13.5, 14],
                borderColor: '#9CA3AF',
                tension: 0.4,
                fill: false,
                borderDash: [5, 5]
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom'
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    max: 20,
                    ticks: {
                        stepSize: 5
                    }
                }
            }
        }
    });

    // Animation des cartes statistiques
    const statCards = document.querySelectorAll('.stat-card');
    statCards.forEach((card, index) => {
        setTimeout(() => {
            card.classList.add('animate-slide-up');
        }, index * 100);
    });

    // Gestion des filtres de période
    const periodButtons = document.querySelectorAll('.period-selector .btn-outline');
    periodButtons.forEach(button => {
        button.addEventListener('click', () => {
            periodButtons.forEach(btn => btn.classList.remove('active'));
            button.classList.add('active');
            // TODO: Mettre à jour les données selon la période
        });
    });

    // Gestion des ressources
    document.querySelectorAll('.resource-item').forEach(item => {
        item.addEventListener('click', async function(e) {
            e.preventDefault();
            const resourceId = this.dataset.id;
            const resourceType = this.dataset.type;

            try {
                const response = await fetch(`/student/resources/${resourceId}`);
                if (!response.ok) throw new Error('Erreur lors du chargement de la ressource');

                const resource = await response.json();

                if (resourceType === 'PDF') {
                    window.open(resource.url, '_blank');
                } else if (resourceType === 'VIDEO') {
                    showVideoModal(resource);
                } else {
                    window.open(resource.url, '_blank');
                }
            } catch (error) {
                showNotification(error.message, 'error');
            }
        });
    });
});

// Fonction pour afficher une notification
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

// Fonction pour afficher une vidéo dans une modal
function showVideoModal(resource) {
    const modal = document.createElement('div');
    modal.className = 'modal video-modal';
    modal.innerHTML = `
        <div class="modal-content">
            <div class="modal-header">
                <h3>${resource.title}</h3>
                <button class="modal-close">&times;</button>
            </div>
            <div class="modal-body">
                <div class="video-container">
                    <video controls>
                        <source src="${resource.url}" type="video/mp4">
                        Votre navigateur ne supporte pas la lecture de vidéos.
                    </video>
                </div>
            </div>
        </div>
    `;

    document.body.appendChild(modal);
    setTimeout(() => modal.classList.add('show'), 100);

    modal.querySelector('.modal-close').addEventListener('click', () => {
        modal.classList.remove('show');
        setTimeout(() => modal.remove(), 300);
    });
}