document.addEventListener('DOMContentLoaded', function() {
    loadHomeworks(); // Charge initialement tous les devoirs via GET
    initializeHomeworkForm();
    initializeHomeworkGradeEntryForm();
    initializeHomeworkFilters();
    initializeHomeworkCharts();
});

// Charge les devoirs via GET, en passant éventuellement une URL filtrée
async function loadHomeworks(filterUrl = '/professor/homeworks') {
    try {
        const response = await fetch(filterUrl, {
            method: 'GET',
            headers: { 'Accept': 'application/json' }
        });
        if (!response.ok) throw new Error('Erreur lors du chargement des devoirs : ' + response.status);
        const homeworks = await response.json();
        updateHomeworksUI(homeworks);
    } catch (error) {
        showNotification(error.message, 'error');
    }
}

// Met à jour l'interface en répartissant les devoirs dans les sections "À venir", "En cours" et "Terminés"
function updateHomeworksUI(homeworks) {
    const upcomingSection = document.getElementById('upcomingHomework').querySelector('.homework-list');
    const inProgressSection = document.getElementById('inProgressHomework').querySelector('.homework-list');
    const completedSection = document.getElementById('completedHomework').querySelector('.homework-list');

    upcomingSection.innerHTML = '';
    inProgressSection.innerHTML = '';
    completedSection.innerHTML = '';

    homeworks.forEach(hw => {
        const hwCard = document.createElement('div');
        hwCard.className = 'homework-card';
        hwCard.innerHTML = `
            <div class="homework-header">
                <span class="subject-badge">${hw.subject.name}</span>
                ${hw.status === 'SCHEDULED' ? `<span class="due-date"><i class="fas fa-calendar"></i> ${formatHomeworkDate(hw.dueDate)}</span>` : ''}
                ${hw.status === 'IN_PROGRESS' ? `<span class="status-badge in-progress">En cours</span>` : ''}
                ${hw.status === 'COMPLETED' ? `<span class="status-badge completed">Terminé</span>` : ''}
            </div>
            <div class="homework-content">
                <h3>${hw.title}</h3>
                <p>${hw.description || ''}</p>
                <div class="homework-details">
                    <span class="detail-item"><i class="fas fa-clock"></i> ${formatHomeworkDate(hw.dueDate)}</span>
                    <span class="detail-item"><i class="fas fa-users"></i> ${hw.classe.name}</span>
                </div>
                ${hw.status === 'IN_PROGRESS' ? `<button class="btn btn-info mt-2" onclick="openHomeworkGradeEntryModal(${hw.submissionId})"><i class="fas fa-edit"></i> Saisir notes</button>` : ''}
            </div>
            <div class="homework-footer">
                ${hw.status === 'SCHEDULED' ? `
                    <button class="btn btn-primary" onclick="publishHomework(${hw.id})"><i class="fas fa-share"></i> Publier</button>
                    <button class="btn btn-secondary" onclick="editHomework(${hw.id})"><i class="fas fa-edit"></i> Modifier</button>
                ` : ''}
                ${hw.status === 'IN_PROGRESS' ? `<button class="btn btn-warning" onclick="endHomework(${hw.id})"><i class="fas fa-stop"></i> Terminer</button>` : ''}
                ${hw.status === 'COMPLETED' ? `
                    <button class="btn btn-primary" onclick="viewHomeworkResults(${hw.id})"><i class="fas fa-chart-bar"></i> Résultats</button>
                    <button class="btn btn-secondary" onclick="downloadHomeworkReport(${hw.id})"><i class="fas fa-download"></i> Rapport</button>
                ` : ''}
            </div>
        `;
        if (hw.status === 'SCHEDULED') {
            upcomingSection.appendChild(hwCard);
        } else if (hw.status === 'IN_PROGRESS') {
            inProgressSection.appendChild(hwCard);
        } else if (hw.status === 'COMPLETED') {
            completedSection.appendChild(hwCard);
        }
    });
}

// Formatte la date d'un devoir
function formatHomeworkDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('fr-FR', {
        day: '2-digit',
        month: 'short',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

// INITIALISATION DU FORMULAIRE DE CRÉATION DE DEVOIRS
function initializeHomeworkForm() {
    const form = document.getElementById('homeworkForm');
    if (!form) return;
    form.addEventListener('submit', async function(e) {
        e.preventDefault();
        try {
            const formData = new FormData(this);
            const data = {
                title: formData.get('title'),
                subject: { id: formData.get('subject') },
                classe: { id: formData.get('classe') },
                dueDate: formData.get('dueDate'),
                description: formData.get('description')
            };
            const response = await fetch('/professor/homeworks', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                },
                body: JSON.stringify(data)
            });
            if (!response.ok) throw new Error('Erreur lors de la création du devoir');
            closeHomeworkModal();
            showNotification('Devoir créé avec succès', 'success');
            setTimeout(() => location.reload(), 1500);
        } catch (error) {
            showNotification(error.message, 'error');
        }
    });
}

// INITIALISATION DU FORMULAIRE DE SAISIE DES NOTES POUR DEVOIRS
function initializeHomeworkGradeEntryForm() {
    const form = document.getElementById('gradeEntryHomeworkForm');
    if (!form) return;
    form.addEventListener('submit', async function(e) {
        e.preventDefault();
        try {
            const formData = new FormData(this);
            const data = {
                submissionId: formData.get('homeworkSubmissionId'),
                gradeValue: parseFloat(formData.get('homeworkGradeValue')),
                comment: formData.get('homeworkComment')
            };
            const response = await fetch('/professor/api/homeworks/grade-entry', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                },
                body: JSON.stringify(data)
            });
            if (!response.ok) throw new Error('Erreur lors de la saisie des notes');
            closeHomeworkGradeEntryModal();
            showNotification('Notes enregistrées et élève notifié(e)', 'success');
            setTimeout(() => location.reload(), 1500);
        } catch (error) {
            showNotification(error.message, 'error');
        }
    });
}

// Ouverture/fermeture des modales pour devoirs
function openHomeworkModal() {
    document.getElementById('homeworkModal').classList.add('show');
}
function closeHomeworkModal() {
    document.getElementById('homeworkModal').classList.remove('show');
    document.getElementById('homeworkForm').reset();
}
function openHomeworkGradeEntryModal(submissionId) {
    document.getElementById('gradeEntryHomeworkModal').classList.add('show');
    document.getElementById('homeworkSubmissionId').value = submissionId;
}
function closeHomeworkGradeEntryModal() {
    document.getElementById('gradeEntryHomeworkModal').classList.remove('show');
    document.getElementById('gradeEntryHomeworkForm').reset();
}

// INITIALISATION DU FORMULAIRE DE FILTRAGE POUR DEVOIRS
function initializeHomeworkFilters() {
    const filterForm = document.getElementById('homeworkFilterForm');
    if (!filterForm) return;
    filterForm.addEventListener('submit', function(e) {
        e.preventDefault();
        const formData = new FormData(filterForm);
        const month = formData.get('month');
        const classe = formData.get('classe');
        const subject = formData.get('subject');
        let url = '/professor/homeworks?';
        if (month) url += 'month=' + encodeURIComponent(month) + '&';
        if (classe) url += 'classe=' + encodeURIComponent(classe) + '&';
        if (subject) url += 'subject=' + encodeURIComponent(subject) + '&';
        if (url.endsWith('&') || url.endsWith('?')) {
            url = url.slice(0, -1);
        }
        // Redirection via GET pour que le contrôleur recharge la page avec les devoirs filtrés
        window.location.href = url;
    });
}

// INITIALISATION DES GRAPHIQUES POUR DEVOIRS (si nécessaire)
function initializeHomeworkCharts() {
    // Ajoutez ici l'initialisation de graphiques spécifiques si besoin.
}

// Utilitaires
function formatDate(date) {
    return new Date(date).toLocaleDateString('fr-FR', {
        day: '2-digit',
        month: 'short'
    });
}
function generateDateLabels() {
    const labels = [];
    const now = new Date();
    for (let i = 5; i >= 0; i--) {
        const date = new Date(now);
        date.setMonth(now.getMonth() - i);
        labels.push(date.toLocaleDateString('fr-FR', { month: 'short' }));
    }
    return labels;
}
function getSubjectColor(subject) {
    const colors = {
        'Mathématiques': '#4F46E5',
        'Français': '#059669',
        'Histoire-Géo': '#D97706',
        'Anglais': '#DC2626',
        'SVT': '#4299E1',
        'Physique-Chimie': '#8B5CF6'
    };
    return colors[subject] || '#6B7280';
}
async function downloadHomeworkReport(homeworkId) {
    try {
        const response = await fetch(`/professor/api/homeworks/${homeworkId}/report`);
        if (!response.ok) throw new Error('Erreur lors du téléchargement du rapport');
        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `homework-report-${homeworkId}.pdf`;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
    } catch (error) {
        showNotification(error.message, 'error');
    }
}
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
