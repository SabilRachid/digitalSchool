// Gestion des devoirs
document.addEventListener('DOMContentLoaded', function() {
    initializeStats();
    initializeFilters();
});

// Initialisation des statistiques
function initializeStats() {
    // Mettre à jour les graphiques de progression pour chaque enfant
    document.querySelectorAll('.child-tab').forEach(tab => {
        const childId = tab.dataset.childId;
        loadChildStats(childId);
    });
}

// Chargement des statistiques d'un enfant
async function loadChildStats(childId) {
    try {
        const response = await fetch(`/parent/homework/stats/${childId}`);
        if (!response.ok) throw new Error('Erreur lors du chargement des statistiques');

        const stats = await response.json();
        updateChildStats(childId, stats);
    } catch (error) {
        showNotification(error.message, 'error');
    }
}

// Mise à jour des statistiques d'un enfant
function updateChildStats(childId, stats) {
    const tab = document.querySelector(`.child-tab[data-child-id="${childId}"]`);
    if (!tab) return;

    // Mettre à jour les compteurs
    tab.querySelector('.total .stat-value').textContent = stats.totalHomework;
    tab.querySelector('.pending .stat-value').textContent = stats.pendingHomework;
    tab.querySelector('.completed .stat-value').textContent = stats.completedHomework;
    tab.querySelector('.rate .stat-value').textContent = stats.completionRate.toFixed(1) + '%';

    // Mettre à jour les graphiques
    updateSubjectBreakdown(childId, stats.subjectBreakdown);
    //updateMonthlyTrend(childId, stats.monthlyTrend);
}

// Mise à jour de la répartition par matière
function updateSubjectBreakdown(childId, breakdown) {
    const container = document.querySelector(`.child-tab[data-child-id="${childId}"] .subjects-grid`);
    if (!container) return;

    container.innerHTML = Object.entries(breakdown).map(([subject, stats]) => `
        <div class="subject-card">
            <div class="subject-header">
                <h4>${subject}</h4>
                <div class="completion-rate">${stats.completionRate}%</div>
            </div>
            <div class="progress-bar">
                <div class="progress" style="width: ${stats.completionRate}%"></div>
            </div>
            <div class="subject-stats">
                <span>${stats.completed} terminés</span>
                <span>${stats.pending} en attente</span>
            </div>
        </div>
        `).join('');
}

// Envoi d'un rappel
async function sendReminder(homeworkId) {
    try {
        const response = await fetch(`/parent/homework/${homeworkId}/reminder`, {
            method: 'POST',
            headers: {
                'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
            }
        });

        if (!response.ok) throw new Error('Erreur lors de l\'envoi du rappel');

        showNotification('Rappel envoyé avec succès', 'success');
    } catch (error) {
        showNotification(error.message, 'error');
    }
}

// Visualisation d'un devoir
async function viewSubmission(homeworkId) {
    try {
        const response = await fetch(`/parent/homework/${homeworkId}/submission`);
        if (!response.ok) throw new Error('Erreur lors du chargement du devoir');

        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        window.open(url, '_blank');
        window.URL.revokeObjectURL(url);
    } catch (error) {
        showNotification(error.message, 'error');
    }
}

// Initialisation des filtres
function initializeFilters() {
    const filterButtons = document.querySelectorAll('.filter-button');
    filterButtons.forEach(button => {
        button.addEventListener('click', function() {
            const status = this.dataset.status;
            filterHomework(status);

            // Mettre à jour l'état actif des boutons
            filterButtons.forEach(btn => btn.classList.remove('active'));
            this.classList.add('active');
        });
    });
}

// Filtrage des devoirs
function filterHomework(status) {
    document.querySelectorAll('.homework-card').forEach(card => {
        if (status === 'all' || card.dataset.status === status) {
            card.style.display = 'block';
        } else {
            card.style.display = 'none';
        }
    });
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