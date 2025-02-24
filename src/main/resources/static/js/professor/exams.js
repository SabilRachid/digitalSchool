document.addEventListener('DOMContentLoaded', function() {
    initializeForm();
    initializeGradeEntryForm();
    initializeCharts();
    initializeFilters();
});

// INITIALISATION DU FORMULAIRE DE CRÉATION D'EXAMEN
function initializeForm() {
    const form = document.getElementById('examForm');
    if (!form) return;
    form.addEventListener('submit', async function(e) {
        e.preventDefault();
        try {
            const formData = new FormData(this);
            const data = {
                name: formData.get('name'),
                subject: { id: formData.get('subject') },
                classe: { id: formData.get('classe') },
                examDate: formData.get('examDate'),
                duration: parseInt(formData.get('duration')),
                description: formData.get('description'),
                maxScore: parseFloat(formData.get('maxScore'))
            };
            const response = await fetch('/professor/api/exams', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                },
                body: JSON.stringify(data)
            });
            if (!response.ok) throw new Error('Erreur lors de la création de l\'examen');
            closeExamModal();
            showNotification('Examen créé avec succès', 'success');
            setTimeout(() => location.reload(), 1500);
        } catch (error) {
            showNotification(error.message, 'error');
        }
    });
}

// INITIALISATION DU FORMULAIRE DE SAISIE DES NOTES
function initializeGradeEntryForm() {
    const form = document.getElementById('gradeEntryForm');
    if (!form) return;
    form.addEventListener('submit', async function(e) {
        e.preventDefault();
        try {
            const formData = new FormData(this);
            const data = {
                submissionId: formData.get('submissionId'),
                gradeValue: parseFloat(formData.get('gradeValue')),
                comment: formData.get('comment')
            };
            const response = await fetch('/professor/api/exams/grade-entry', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                },
                body: JSON.stringify(data)
            });
            if (!response.ok) throw new Error('Erreur lors de la saisie des notes');
            closeGradeEntryModal();
            showNotification('Notes enregistrées et élève notifié(e)', 'success');
            setTimeout(() => location.reload(), 1500);
        } catch (error) {
            showNotification(error.message, 'error');
        }
    });
}

// Ouverture/fermeture des modales
function openExamModal() {
    document.getElementById('examModal').classList.add('show');
}
function closeExamModal() {
    document.getElementById('examModal').classList.remove('show');
    document.getElementById('examForm').reset();
}
function openGradeEntryModal(submissionId) {
    document.getElementById('gradeEntryModal').classList.add('show');
    document.getElementById('submissionId').value = submissionId;
}
function closeGradeEntryModal() {
    document.getElementById('gradeEntryModal').classList.remove('show');
    document.getElementById('gradeEntryForm').reset();
}

// INITIALISATION DES GRAPHIQUES (si nécessaire)
function initializeCharts() {
    // Ajoutez ici l'initialisation de graphiques spécifiques si besoin.
}

// INITIALISATION DU FORMULAIRE DE FILTRAGE
// Au clic sur le bouton de filtre, on récupère les valeurs et on redirige vers une URL GET.
function initializeFilters() {
    const filterForm = document.getElementById('examFilterForm');
    if (!filterForm) return;
    filterForm.addEventListener('submit', function(e) {
        e.preventDefault();
        const formData = new FormData(filterForm);
        const month = formData.get('month');
        const classe = formData.get('classe');
        const subject = formData.get('subject');
        let url = '/professor/exams?';
        if (month) url += 'month=' + encodeURIComponent(month) + '&';
        if (classe) url += 'classe=' + encodeURIComponent(classe) + '&';
        if (subject) url += 'subject=' + encodeURIComponent(subject) + '&';
        // Supprime le dernier caractère '&' ou '?' si présent
        if (url.endsWith('&') || url.endsWith('?')) {
            url = url.slice(0, -1);
        }
        // Redirection via GET, le contrôleur récupère les paramètres et recharge la page
        window.location.href = url;
    });
}

// Fonction utilitaire pour formater une date d'examen
function formatExamDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('fr-FR', {
        day: '2-digit',
        month: 'short',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

// Utilitaires pour le graphique de progression (si besoin)
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

// Téléchargement du rapport PDF d'un examen
async function downloadReport(examId) {
    try {
        const response = await fetch(`/professor/api/exams/${examId}/report`);
        if (!response.ok) throw new Error('Erreur lors du téléchargement du rapport');
        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `exam-report-${examId}.pdf`;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
    } catch (error) {
        showNotification(error.message, 'error');
    }
}

// Affichage des notifications
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
