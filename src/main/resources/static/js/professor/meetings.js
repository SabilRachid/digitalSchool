// Gestion des réunions
document.addEventListener('DOMContentLoaded', function() {
    // Initialisation des composants
    initializeParticipantsSelect();
    initializeForm();
    
    // Mise à jour automatique du statut des réunions
    setInterval(updateMeetingStatuses, 60000); // Toutes les minutes
});

// Initialisation du select des participants
function initializeParticipantsSelect() {
    const participantsSelect = document.getElementById('participants');
    if (!participantsSelect) return;

    // Charger les participants potentiels selon le type de réunion
    document.getElementById('type').addEventListener('change', function(e) {
        loadParticipants(e.target.value);
    });
}

// Chargement des participants selon le type de réunion
async function loadParticipants(meetingType) {
    try {
        let endpoint;
        switch (meetingType) {
            case 'PARENT_TEACHER':
                endpoint = '/api/parents';
                break;
            case 'STAFF':
                endpoint = '/api/staff';
                break;
            case 'CLASS_COUNCIL':
                endpoint = '/api/class-council-members';
                break;
            default:
                return;
        }

        const response = await fetch(endpoint);
        if (!response.ok) throw new Error('Erreur lors du chargement des participants');
        
        const participants = await response.json();
        updateParticipantsSelect(participants);
    } catch (error) {
        showNotification(error.message, 'error');
    }
}

// Mise à jour du select des participants
function updateParticipantsSelect(participants) {
    const select = document.getElementById('participants');
    select.innerHTML = participants.map(p => 
        `<option value="${p.id}">${p.lastName} ${p.firstName}</option>`
    ).join('');
}

// Initialisation du formulaire
function initializeForm() {
    const form = document.getElementById('meetingForm');
    if (!form) return;

    form.addEventListener('submit', async function(e) {
        e.preventDefault();
        
        try {
            const formData = new FormData(this);
            const data = {
                title: formData.get('title'),
                type: formData.get('type'),
                startTime: formData.get('startTime'),
                duration: parseInt(formData.get('duration')),
                location: formData.get('location'),
                description: formData.get('description'),
                participants: Array.from(formData.getAll('participants')),
                online: formData.get('online') === 'on'
            };

            const response = await fetch('/professor/api/meetings', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                },
                body: JSON.stringify(data)
            });

            if (!response.ok) throw new Error('Erreur lors de la création de la réunion');

            closeMeetingModal();
            showNotification('Réunion programmée avec succès', 'success');
            setTimeout(() => location.reload(), 1500);
        } catch (error) {
            showNotification(error.message, 'error');
        }
    });
}

// Gestion des modales
function openMeetingModal() {
    document.getElementById('meetingModal').classList.add('show');
}

function closeMeetingModal() {
    document.getElementById('meetingModal').classList.remove('show');
    document.getElementById('meetingForm').reset();
}

// Actions sur les réunions
async function joinMeeting(meetingLink) {
    window.open(meetingLink, '_blank');
}

async function sendReminders(meetingId) {
    try {
        const response = await fetch(`/professor/api/meetings/${meetingId}/notify`, {
            method: 'POST',
            headers: {
                'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
            }
        });

        if (!response.ok) throw new Error('Erreur lors de l\'envoi des rappels');

        showNotification('Rappels envoyés avec succès', 'success');
    } catch (error) {
        showNotification(error.message, 'error');
    }
}

async function cancelMeeting(meetingId) {
    if (!confirm('Êtes-vous sûr de vouloir annuler cette réunion ?')) return;

    try {
        const response = await fetch(`/professor/api/meetings/${meetingId}`, {
            method: 'DELETE',
            headers: {
                'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
            }
        });

        if (!response.ok) throw new Error('Erreur lors de l\'annulation de la réunion');

        showNotification('Réunion annulée avec succès', 'success');
        setTimeout(() => location.reload(), 1500);
    } catch (error) {
        showNotification(error.message, 'error');
    }
}

// Mise à jour du statut des réunions
function updateMeetingStatuses() {
    const now = new Date();
    
    document.querySelectorAll('.meeting-card').forEach(card => {
        const startTime = new Date(card.dataset.startTime);
        const endTime = new Date(card.dataset.endTime);
        
        if (now >= startTime && now <= endTime) {
            card.classList.add('in-progress');
        } else if (now > endTime) {
            card.classList.add('past');
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
