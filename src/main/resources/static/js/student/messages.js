
// Gestion de la messagerie
document.addEventListener('DOMContentLoaded', function() {
    initializeSearch();
    initializeMessageForm();
    initializeMeetingForm();
});

// Initialisation de la recherche
function initializeSearch() {
    const searchInput = document.querySelector('.search-box input');
    if (!searchInput) return;

    searchInput.addEventListener('input', function(e) {
        const query = e.target.value.toLowerCase();
        document.querySelectorAll('.professor-item').forEach(item => {
            const name = item.querySelector('.professor-name').textContent.toLowerCase();
            const subject = item.querySelector('.professor-subject').textContent.toLowerCase();
            
            item.style.display = 
                name.includes(query) || subject.includes(query) ? 'flex' : 'none';
        });
    });
}

// Ouverture d'une conversation
async function openConversation(professorId) {
    try {
        // Charger la conversation
        const response = await fetch(`/student/messages/conversation/${professorId}`);
        if (!response.ok) throw new Error('Erreur lors du chargement de la conversation');
        
        const data = await response.json();
        
        // Mettre à jour l'interface
        document.getElementById('emptyState').style.display = 'none';
        document.getElementById('activeConversation').style.display = 'flex';
        
        // Mettre à jour les informations du professeur
        const professorName = document.querySelector('.professor-name');
        const professorSubject = document.querySelector('.professor-subject');
        professorName.textContent = data.professor.name;
        professorSubject.textContent = data.professor.subject;
        
        // Afficher les messages
        displayMessages(data.messages);
        
        // Activer le professeur dans la liste
        document.querySelectorAll('.professor-item').forEach(item => {
            item.classList.remove('active');
        });
        document.querySelector(`.professor-item[data-id="${professorId}"]`).classList.add('active');
        
        // Scroll vers le bas
        scrollToBottom();
    } catch (error) {
        showNotification(error.message, 'error');
    }
}

// Affichage des messages
function displayMessages(messages) {
    const container = document.getElementById('messagesList');
    container.innerHTML = messages.map(message => `
        <div class="message ${message.sent ? 'sent' : 'received'}">
            <div class="message-content">${message.content}</div>
            <div class="message-time">
                ${formatDateTime(message.sentAt)}
            </div>
        </div>
    `).join('');
}

// Initialisation du formulaire de message
function initializeMessageForm() {
    const form = document.getElementById('messageForm');
    if (!form) return;

    form.addEventListener('submit', async function(e) {
        e.preventDefault();
        
        const textarea = this.querySelector('textarea');
        const content = textarea.value.trim();
        if (!content) return;
        
        try {
            const response = await fetch('/student/messages', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                },
                body: JSON.stringify({
                    content: content,
                    recipientId: getCurrentProfessorId()
                })
            });

            if (!response.ok) throw new Error('Erreur lors de l\'envoi du message');

            const message = await response.json();
            appendMessage(message);
            textarea.value = '';
            scrollToBottom();
        } catch (error) {
            showNotification(error.message, 'error');
        }
    });
}

// Demande de rendez-vous
function requestMeeting() {
    document.getElementById('meetingModal').classList.add('show');
}

// Initialisation du formulaire de rendez-vous
function initializeMeetingForm() {
    const form = document.getElementById('meetingForm');
    if (!form) return;

    form.addEventListener('submit', async function(e) {
        e.preventDefault();
        
        const formData = new FormData(this);
        const data = {
            professorId: getCurrentProfessorId(),
            startTime: formData.get('startTime'),
            description: formData.get('description'),
            online: formData.get('online') === 'on'
        };

        try {
            const response = await fetch('/student/messages/meeting', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                },
                body: JSON.stringify(data)
            });

            if (!response.ok) throw new Error('Erreur lors de la demande de rendez-vous');

            closeMeetingModal();
            showNotification('Demande de rendez-vous envoyée avec succès', 'success');
        } catch (error) {
            showNotification(error.message, 'error');
        }
    });
}

// Utilitaires
function getCurrentProfessorId() {
    return document.querySelector('.professor-item.active').dataset.id;
}

function formatDateTime(date) {
    return new Date(date).toLocaleString('fr-FR', {
        hour: '2-digit',
        minute: '2-digit',
        day: '2-digit',
        month: 'short'
    });
}

function appendMessage(message) {
    const container = document.getElementById('messagesList');
    const messageElement = document.createElement('div');
    messageElement.className = `message ${message.sent ? 'sent' : 'received'}`;
    messageElement.innerHTML = `
        <div class="message-content">${message.content}</div>
        <div class="message-time">${formatDateTime(message.sentAt)}</div>
    `;
    container.appendChild(messageElement);
}

function scrollToBottom() {
    const container = document.getElementById('messagesList');
    container.scrollTop = container.scrollHeight;
}

function closeMeetingModal() {
    document.getElementById('meetingModal').classList.remove('show');
    document.getElementById('meetingForm').reset();
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
