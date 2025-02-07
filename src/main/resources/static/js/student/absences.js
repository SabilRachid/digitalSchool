// Gestion des absences
document.addEventListener('DOMContentLoaded', function() {
    initializePeriodFilter();
    initializeJustificationForm();
});

// Initialisation du filtre de période
function initializePeriodFilter() {
    const buttons = document.querySelectorAll('.period-filter .btn-outline');
    buttons.forEach(button => {
        button.addEventListener('click', function() {
            buttons.forEach(btn => btn.classList.remove('active'));
            this.classList.add('active');
            loadAbsences(this.textContent.toLowerCase());
        });
    });
}

// Chargement des absences
async function loadAbsences(period) {
    const now = new Date();
    let start = new Date();
    
    switch(period) {
        case 'mois':
            start.setMonth(now.getMonth() - 1);
            break;
        case 'trimestre':
            start.setMonth(now.getMonth() - 3);
            break;
        case 'année':
            start.setMonth(8); // Septembre
            start.setDate(1);
            break;
        default:
            start.setMonth(now.getMonth() - 1);
    }

    try {
        const response = await fetch(`/student/absences/data?start=${start.toISOString()}&end=${now.toISOString()}`);
        if (!response.ok) throw new Error('Erreur lors du chargement des absences');

        const absences = await response.json();
        updateAbsencesList(absences);
        updateStatistics();
    } catch (error) {
        showNotification(error.message, 'error');
    }
}

// Mise à jour de la liste des absences
function updateAbsencesList(absences) {
    const container = document.querySelector('.absences-list');
    container.innerHTML = absences.map(absence => `
        <div class="absence-card">
            <div class="absence-date">
                <div class="date-day">${formatDay(absence.recordedAt)}</div>
                <div class="date-month">${formatMonth(absence.recordedAt)}</div>
            </div>
            <div class="absence-details">
                <div class="course-info">
                    <h3>${absence.course.subject.name}</h3>
                    <p>
                        <i class="fas fa-clock"></i>
                        ${formatTime(absence.course.startTime)} - ${formatTime(absence.course.endTime)}
                    </p>
                    <p>
                        <i class="fas fa-chalkboard-teacher"></i>
                        ${absence.course.professor.firstName} ${absence.course.professor.lastName}
                    </p>
                </div>
                <div class="absence-status ${absence.status.toLowerCase()}">
                    ${getStatusLabel(absence.status)}
                </div>
            </div>
            <div class="absence-actions">
                ${absence.status === 'ABSENT' ? `
                    <button class="btn btn-primary" onclick="submitJustification(${absence.id})">
                        <i class="fas fa-upload"></i> Justifier
                    </button>
                ` : absence.status === 'EXCUSED' ? `
                    <button class="btn btn-secondary" onclick="viewJustification(${absence.id})">
                        <i class="fas fa-eye"></i> Voir justificatif
                    </button>
                ` : ''}
            </div>
        </div>
    `).join('');
}

// Mise à jour des statistiques
async function updateStatistics() {
    try {
        const response = await fetch('/student/absences/statistics');
        if (!response.ok) throw new Error('Erreur lors du chargement des statistiques');

        const stats = await response.json();
        
        document.querySelector('.stat-value.total').textContent = stats.totalAbsences;
        document.querySelector('.stat-value.justified').textContent = stats.justifiedAbsences;
        document.querySelector('.stat-value.unjustified').textContent = stats.unjustifiedAbsences;
        document.querySelector('.stat-value.rate').textContent = stats.absenceRate.toFixed(1) + '%';
    } catch (error) {
        showNotification(error.message, 'error');
    }
}

// Soumission d'une justification
function submitJustification(absenceId) {
    document.getElementById('absenceId').value = absenceId;
    document.getElementById('justificationModal').classList.add('show');
}

// Initialisation du formulaire de justification
function initializeJustificationForm() {
    const form = document.getElementById('justificationForm');
    if (!form) return;

    form.addEventListener('submit', async function(e) {
        e.preventDefault();
        
        const formData = new FormData(this);
        const absenceId = document.getElementById('absenceId').value;
        
        try {
            const response = await fetch(`/student/absences/${absenceId}/justify`, {
                method: 'POST',
                headers: {
                    'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                },
                body: formData
            });

            if (!response.ok) throw new Error('Erreur lors de la soumission du justificatif');

            closeJustificationModal();
            showNotification('Justificatif soumis avec succès', 'success');
            setTimeout(() => location.reload(), 1500);
        } catch (error) {
            showNotification(error.message, 'error');
        }
    });
}

// Visualisation d'un justificatif
async function viewJustification(absenceId) {
    try {
        const response = await fetch(`/student/absences/${absenceId}/justification`);
        if (!response.ok) throw new Error('Erreur lors du chargement du justificatif');

        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        window.open(url, '_blank');
        window.URL.revokeObjectURL(url);
    } catch (error) {
        showNotification(error.message, 'error');
    }
}

// Utilitaires
function formatDay(date) {
    return new Date(date).getDate().toString().padStart(2, '0');
}

function formatMonth(date) {
    return new Date(date).toLocaleString('fr-FR', { month: 'short' });
}

function formatTime(date) {
    return new Date(date).toLocaleTimeString('fr-FR', { 
        hour: '2-digit', 
        minute: '2-digit' 
    });
}

function getStatusLabel(status) {
    switch (status) {
        case 'EXCUSED': return 'Justifiée';
        case 'ABSENT': return 'Non justifiée';
        default: return 'En attente';
    }
}

function closeJustificationModal() {
    document.getElementById('justificationModal').classList.remove('show');
    document.getElementById('justificationForm').reset();
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