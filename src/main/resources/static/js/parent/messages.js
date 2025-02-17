// Gestion de la messagerie parent
document.addEventListener('DOMContentLoaded', function() {
    initializeSearch();
    initializeChildSelector();
    initializeMessageForm();
    initializeMeetingForm();
});

// Initialisation de la recherche
function initializeSearch() {
    const searchInput = document.querySelector('.search-box input');
    if (!searchInput) return;

    searchInput.addEventListener('input', function(e) {
        const query = e.target.value.toLowerCase();

        // Filtrer les professeurs
        document.querySelectorAll('.teacher-item').forEach(item => {
            const name = item.querySelector('.teacher-name').textContent.toLowerCase();
            const subject = item.querySelector('.teacher-subject').textContent.toLowerCase();

            item.style.display =
                name.includes(query) || subject.includes(query) ? 'flex' : 'none';
        });
    });
}

// Initialisation du sélecteur d'enfant
function initializeChildSelector() {
    document.querySelectorAll('.child-item').forEach(item => {
        item.addEventListener('click', function() {
            // Mettre à jour la sélection active
            document.querySelectorAll('.child-item').forEach(child => {
                child.classList.remove('active');
            });
            this.classList.add('active');

            // Charger les professeurs de l'enfant
            loadTeachers(this.dataset.childId);
        });
    });
}

// Chargement des professeurs
async function loadTeachers(childId) {
    try {
        const response = await fetch(`/parent/messages/teachers/${childId}`);
        if (!response.ok) throw new Error('Erreur lors du chargement des professeurs');

        const teachers = await response.json();
        updateTeachersList(teachers);
    } catch (error) {
        showNotification(error.message, 'error');
    }
}

// Mise à jour de la liste des professeurs
function updateTeachersList(teachers) {
    const container = document.querySelector('.teachers-list');
    container.innerHTML = teachers.map(teacher => `
        <div class="teacher-item" data-teacher-id="${teacher.id}">
            <div class="teacher-avatar">
                <i class="fas fa-chalkboard-teacher"></i>
            </div>
            <div class="teacher-info">
                <span class="teacher-name">${teacher.firstName} ${teacher.lastName}</span>
                <span class="teacher-subject">${teacher.subject}</span>
            </div>
            ${teacher.unreadCount > 0 ?
        `<div class="unread-badge">${teacher.unreadCount}</div>` :
        ''}
        </div>
    `).join('');

    // Réinitialiser les événements
    document.querySelectorAll('.teacher-item').forEach(item => {
        item.addEventListener('click', function() {
            openConversation(this.dataset.teacherId);
        });
    });
}

// Ouverture d'une conversation
async function openConversation(teacherId) {
    try {
        const childId = document.querySelector('.child-item.active').dataset.childId;
        const response = await fetch(`/parent/messages/conversation/${childId}/${teacherId}`);
        if (!response.ok) throw new Error('Erreur lors du chargement de la conversation');

        const data = await response.json();

        // Mettre à jour l'interface
        document.getElementById('emptyState').style.display = 'none';
        document.getElementById('activeConversation').style.display = 'flex';

        // Mettre à jour les informations du professeur
        const teacherName = document.querySelector('.teacher-name');
        const teacherSubject = document.querySelector('.teacher-subject');
        teacherName.textContent = data.teacher.name;
        teacherSubject.textContent = data.teacher.subject;

        // Afficher les messages
        displayMessages(data.messages);

        // Activer le professeur dans la liste
        document.querySelectorAll('.teacher-item').forEach(item => {
            item.classList.remove('active');
        });
        document.querySelector(`.teacher-item[data-teacher-id="${teacherId}"]`).classList.add('active');

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
            const response = await fetch('/parent/messages', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                },
                body: JSON.stringify({
                    content: content,
                    childId: getCurrentChildId(),
                    teacherId: getCurrentTeacherId()
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
            childId: getCurrentChildId(),
            teacherId: getCurrentTeacherId(),
            startTime: formData.get('startTime'),
            description: formData.get('description'),
            online: formData.get('online') === 'on'
        };

        try {
            const response = await fetch('/parent/messages/meeting', {
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
function getCurrentChildId() {
    return document.querySelector('.child-item.active').dataset.childId;
}

function getCurrentTeacherId() {
    return document.querySelector('.teacher-item.active').dataset.teacherId;
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
