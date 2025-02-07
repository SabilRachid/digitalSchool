
// Gestion du tableau de bord parent
document.addEventListener('DOMContentLoaded', function() {
    initializeAlerts();
    initializeChildrenCards();
    initializeEventFilters();
});

// Initialisation des alertes
function initializeAlerts() {
    const alertsBadge = document.querySelector('.alerts-badge');
    if (!alertsBadge) return;

    // Marquer les alertes comme lues
    document.querySelectorAll('.alert-item').forEach(alert => {
        alert.addEventListener('click', function() {
            if (!this.classList.contains('read')) {
                this.classList.add('read');
                updateAlertsCount();
            }
        });
    });
}

// Mise à jour du compteur d'alertes
function updateAlertsCount() {
    const unreadAlerts = document.querySelectorAll('.alert-item:not(.read)').length;
    const badge = document.querySelector('.alerts-badge .badge');
    
    if (badge) {
        badge.textContent = unreadAlerts;
        if (unreadAlerts === 0) {
            badge.style.display = 'none';
        }
    }
}

// Initialisation des cartes enfants
function initializeChildrenCards() {
    // Charger les statistiques pour chaque enfant
    document.querySelectorAll('.child-card').forEach(card => {
        const childId = card.dataset.childId;
        loadChildStats(childId);
    });
}

// Chargement des statistiques d'un enfant
async function loadChildStats(childId) {
    try {
        const response = await fetch(`/parent/dashboard/child/${childId}/stats`);
        if (!response.ok) throw new Error('Erreur lors du chargement des statistiques');
        
        const stats = await response.json();
        updateChildCard(childId, stats);
    } catch (error) {
        console.error('Erreur:', error);
    }
}

// Mise à jour de la carte d'un enfant
function updateChildCard(childId, stats) {
    const card = document.querySelector(`.child-card[data-child-id="${childId}"]`);
    if (!card) return;

    // Mettre à jour les statistiques rapides
    card.querySelector('.average-grade').textContent = stats.averageGrade.toFixed(2) + '/20';
    card.querySelector('.attendance-rate').textContent = stats.attendanceRate.toFixed(1) + '%';
    
    // Mettre à jour les informations supplémentaires
    card.querySelector('.pending-homework').textContent = stats.pendingHomework;
    card.querySelector('.upcoming-exams').textContent = stats.upcomingExams;
}

// Voir les détails d'un enfant
function viewChildDetails(childId) {
    window.location.href = `/parent/children/${childId}`;
}

// Contacter les professeurs
function contactTeachers(childId) {
    window.location.href = `/parent/messages?child=${childId}`;
}

// Voir les détails d'une alerte
function viewDetails(type, childId) {
    let url;
    switch (type) {
        case 'ABSENCE':
            url = `/parent/absences?child=${childId}`;
            break;
        case 'GRADE':
            url = `/parent/grades?child=${childId}`;
            break;
        case 'HOMEWORK':
            url = `/parent/homework?child=${childId}`;
            break;
        default:
            return;
    }
    window.location.href = url;
}

// Initialisation des filtres d'événements
function initializeEventFilters() {
    const filterButtons = document.querySelectorAll('.event-filter');
    filterButtons.forEach(button => {
        button.addEventListener('click', function() {
            const type = this.dataset.type;
            filterEvents(type);
            
            // Mettre à jour l'état actif des boutons
            filterButtons.forEach(btn => btn.classList.remove('active'));
            this.classList.add('active');
        });
    });
}

// Filtrage des événements
function filterEvents(type) {
    const events = document.querySelectorAll('.event-item');
    events.forEach(event => {
        if (type === 'all' || event.dataset.type === type) {
            event.style.display = 'flex';
        } else {
            event.style.display = 'none';
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