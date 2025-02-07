// Gestion des absences
document.addEventListener('DOMContentLoaded', function() {
    initializeFilters();
    initializeAbsenceForm();
});

// Initialisation des filtres
function initializeFilters() {
    const searchInput = document.querySelector('.search-box input');
    const classFilter = document.getElementById('classFilter');
    const statusFilter = document.getElementById('statusFilter');
    const periodFilter = document.getElementById('periodFilter');

    const filters = [searchInput, classFilter, statusFilter, periodFilter];
    filters.forEach(filter => {
        filter.addEventListener('change', applyFilters);
        if (filter === searchInput) {
            filter.addEventListener('keyup', applyFilters);
        }
    });
}

// Application des filtres
function applyFilters() {
    const search = document.querySelector('.search-box input').value.toLowerCase();
    const classId = document.getElementById('classFilter').value;
    const status = document.getElementById('statusFilter').value;
    const period = document.getElementById('periodFilter').value;

    document.querySelectorAll('.absence-card').forEach(card => {
        const cardStatus = card.dataset.status;
        const cardClass = card.dataset.class;
        const cardText = card.textContent.toLowerCase();

        const matchesSearch = search === '' || cardText.includes(search);
        const matchesClass = classId === '' || cardClass === classId;
        const matchesStatus = status === '' || cardStatus === status;

        card.style.display =
            matchesSearch && matchesClass && matchesStatus ? 'block' : 'none';
    });
}

// Initialisation du formulaire d'absence
function initializeAbsenceForm() {
    const form = document.getElementById('absenceForm');
    if (!form) return;

    // Mise à jour dynamique des cours disponibles selon l'élève sélectionné
    document.getElementById('student').addEventListener('change', async function(e) {
        const studentId = e.target.value;
        if (!studentId) return;

        try {
            const response = await fetch(`/secretary/absences/courses/${studentId}`);
            if (!response.ok) throw new Error('Erreur lors du chargement des cours');

            const courses = await response.json();
            updateCourseSelect(courses);
        } catch (error) {
            showNotification(error.message, 'error');
        }
    });

    form.addEventListener('submit', async function(e) {
        e.preventDefault();

        try {
            const formData = new FormData(this);
            const response = await fetch('/secretary/absences', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                },
                body: JSON.stringify(Object.fromEntries(formData))
            });

            if (!response.ok) throw new Error('Erreur lors de l\'enregistrement de l\'absence');

            closeAbsenceModal();
            showNotification('Absence enregistrée avec succès', 'success');
            setTimeout(() => location.reload(), 1500);
        } catch (error) {
            showNotification(error.message, 'error');
        }
    });
}

// Mise à jour du select des cours
function updateCourseSelect(courses) {
    const select = document.getElementById('course');
    select.innerHTML = `
        <option value="">Sélectionner un cours</option>
        ${courses.map(course => `
            <option value="${course.id}">
                ${course.subject.name} - ${formatTime(course.startTime)}
            </option>
        `).join('')}
    `;
}

// Validation d'une justification
async function validateJustification(id) {
    try {
        const response = await fetch(`/secretary/absences/${id}/validate`, {
            method: 'POST',
            headers: {
                'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
            }
        });

        if (!response.ok) throw new Error('Erreur lors de la validation');

        showNotification('Justification validée avec succès', 'success');
        setTimeout(() => location.reload(), 1500);
    } catch (error) {
        showNotification(error.message, 'error');
    }
}

// Rejet d'une justification
async function rejectJustification(id) {
    if (!confirm('Êtes-vous sûr de vouloir refuser cette justification ?')) return;

    try {
        const response = await fetch(`/secretary/absences/${id}/reject`, {
            method: 'POST',
            headers: {
                'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
            }
        });

        if (!response.ok) throw new Error('Erreur lors du rejet');

        showNotification('Justification refusée', 'success');
        setTimeout(() => location.reload(), 1500);
    } catch (error) {
        showNotification(error.message, 'error');
    }
}

// Visualisation d'un justificatif
async function viewJustification(id) {
    try {
        const response = await fetch(`/secretary/absences/${id}/justification`);
        if (!response.ok) throw new Error('Erreur lors du chargement du justificatif');

        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        window.open(url, '_blank');
        window.URL.revokeObjectURL(url);
    } catch (error) {
        showNotification(error.message, 'error');
    }
}

// Envoi d'un rappel
async function sendReminder(id) {
    try {
        const response = await fetch(`/secretary/absences/${id}/remind`, {
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

// Gestion des modales
function openAbsenceModal() {
    document.getElementById('absenceModal').classList.add('show');
}

function closeAbsenceModal() {
    document.getElementById('absenceModal').classList.remove('show');
    document.getElementById('absenceForm').reset();
}

// Utilitaires
function formatTime(time) {
    return new Date(time).toLocaleTimeString('fr-FR', {
        hour: '2-digit',
        minute: '2-digit'
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