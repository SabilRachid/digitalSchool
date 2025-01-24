document.addEventListener('DOMContentLoaded', function() {
    // Configuration des graphiques
    Chart.defaults.font.family = "'Inter', sans-serif";
    Chart.defaults.color = '#718096';

    // Graphique des notes par matière
    const gradesCtx = document.getElementById('gradesChart').getContext('2d');
    new Chart(gradesCtx, {
        type: 'bar',
        data: {
            labels: ['Mathématiques', 'Français', 'Histoire', 'Physique', 'Anglais', 'SVT'],
            datasets: [{
                label: 'Mes notes',
                data: [16, 15, 14, 17, 16, 15],
                backgroundColor: '#4C51BF',
                borderRadius: 8
            }, {
                label: 'Moyenne de la classe',
                data: [14, 13.5, 13, 15, 14, 14.5],
                backgroundColor: '#E9D8FD',
                borderRadius: 8
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
                    grid: {
                        display: false
                    }
                },
                x: {
                    grid: {
                        display: false
                    }
                }
            }
        }
    });

    // Graphique d'assiduité
    const attendanceCtx = document.getElementById('attendanceChart').getContext('2d');
    new Chart(attendanceCtx, {
        type: 'line',
        data: {
            labels: ['Lun', 'Mar', 'Mer', 'Jeu', 'Ven', 'Lun', 'Mar', 'Mer', 'Jeu', 'Ven'],
            datasets: [{
                label: 'Présence',
                data: [1, 1, 1, 0.5, 1, 1, 1, 1, 1, 1],
                borderColor: '#48BB78',
                backgroundColor: 'rgba(72, 187, 120, 0.1)',
                fill: true,
                tension: 0.4
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
                    max: 1,
                    ticks: {
                        callback: function(value) {
                            return value === 1 ? 'Présent' : 
                                   value === 0.5 ? 'Retard' : 
                                   value === 0 ? 'Absent' : '';
                        }
                    },
                    grid: {
                        display: false
                    }
                },
                x: {
                    grid: {
                        display: false
                    }
                }
            }
        }
    });

    // Gestion des filtres de période
    document.querySelectorAll('.card-actions .btn-outline').forEach(button => {
        button.addEventListener('click', function() {
            const parent = this.closest('.card-actions');
            parent.querySelectorAll('.btn-outline').forEach(btn => ```javascript
            parent.querySelectorAll('.btn-outline').forEach(btn => 
                btn.classList.remove('active'));
            this.classList.add('active');
            // TODO: Mettre à jour les données du graphique selon la période
        });
    });

    // Animation des cartes de statistiques
    const statCards = document.querySelectorAll('.stat-card');
    statCards.forEach((card, index) => {
        setTimeout(() => {
            card.classList.add('animate');
        }, index * 100);
    });

    // Gestion des devoirs
    document.querySelectorAll('.homework-item .btn-primary').forEach(button => {
        button.addEventListener('click', async function(e) {
            e.preventDefault();
            const homeworkId = this.closest('.homework-item').dataset.id;
            try {
                const response = await fetch(`/student/homework/${homeworkId}/submit`, {
                    method: 'GET',
                    headers: {
                        'Accept': 'application/json',
                        'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                    }
                });

                if (!response.ok) {
                    throw new Error('Erreur lors de la récupération du formulaire');
                }

                const data = await response.json();
                showSubmissionModal(data);
            } catch (error) {
                console.error('Erreur:', error);
                showNotification(error.message, 'error');
            }
        });
    });

    // Modal de soumission de devoir
    function showSubmissionModal(data) {
        const modal = document.createElement('div');
        modal.className = 'modal';
        modal.innerHTML = `
            <div class="modal-content">
                <div class="modal-header">
                    <h3>Soumettre le devoir</h3>
                    <button class="modal-close">&times;</button>
                </div>
                <form id="homeworkSubmissionForm">
                    <div class="modal-body">
                        <div class="form-group">
                            <label>Titre</label>
                            <input type="text" name="title" required>
                        </div>
                        <div class="form-group">
                            <label>Description</label>
                            <textarea name="description" rows="3"></textarea>
                        </div>
                        <div class="form-group">
                            <label>Fichier</label>
                            <input type="file" name="file" required>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary">Annuler</button>
                        <button type="submit" class="btn btn-primary">Soumettre</button>
                    </div>
                </form>
            </div>
        `;

        document.body.appendChild(modal);
        setTimeout(() => modal.classList.add('show'), 100);

        // Gestion de la fermeture
        const closeModal = () => {
            modal.classList.remove('show');
            setTimeout(() => modal.remove(), 300);
        };

        modal.querySelector('.modal-close').addEventListener('click', closeModal);
        modal.querySelector('.btn-secondary').addEventListener('click', closeModal);

        // Gestion de la soumission
        modal.querySelector('form').addEventListener('submit', async function(e) {
            e.preventDefault();
            const formData = new FormData(this);

            try {
                const response = await fetch(`/student/homework/${data.id}/submit`, {
                    method: 'POST',
                    headers: {
                        'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                    },
                    body: formData
                });

                if (!response.ok) {
                    throw new Error('Erreur lors de la soumission');
                }

                closeModal();
                showNotification('Devoir soumis avec succès', 'success');
                // Recharger la liste des devoirs
                location.reload();
            } catch (error) {
                console.error('Erreur:', error);
                showNotification(error.message, 'error');
            }
        });
    }

    // Système de notification
    function showNotification(message, type = 'info') {
        const notification = document.createElement('div');
        notification.className = `notification ${type}`;
        notification.innerHTML = `
            <div class="notification-content">
                <i class="fas ${type === 'success' ? 'fa-check-circle' : 'fa-exclamation-circle'}"></i>
                <span>${message}</span>
            </div>
            <button class="notification-close">×</button>
        `;

        document.body.appendChild(notification);

        setTimeout(() => {
            notification.classList.add('show');
        }, 100);

        setTimeout(() => {
            notification.classList.remove('show');
            setTimeout(() => {
                notification.remove();
            }, 300);
        }, 5000);

        notification.querySelector('.notification-close').addEventListener('click', () => {
            notification.classList.remove('show');
            setTimeout(() => {
                notification.remove();
            }, 300);
        });
    }

    // Gestion des ressources pédagogiques
    document.querySelectorAll('.resource-item').forEach(item => {
        item.addEventListener('click', async function(e) {
            e.preventDefault();
            const resourceId = this.dataset.id;

            try {
                const response = await fetch(`/student/resources/${resourceId}`, {
                    headers: {
                        'Accept': 'application/json',
                        'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                    }
                });

                if (!response.ok) {
                    throw new Error('Erreur lors du chargement de la ressource');
                }

                const resource = await response.json();
                
                if (resource.type === 'PDF') {
                    window.open(resource.url, '_blank');
                } else if (resource.type === 'VIDEO') {
                    showVideoModal(resource);
                } else {
                    window.open(resource.url, '_blank');
                }
            } catch (error) {
                console.error('Erreur:', error);
                showNotification(error.message, 'error');
            }
        });
    });

    // Modal vidéo
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
});