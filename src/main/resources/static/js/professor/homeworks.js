document.addEventListener('DOMContentLoaded', function() {
    loadHomeworks(); // Charge initialement tous les devoirs via GET
    initializeHomeworkForm();
    initializeHomeworkGradeEntryForm();
    initializeHomeworkFilters();
    initializeHomeworkCharts();
});

// Charge les devoirs via GET, éventuellement filtrés
async function loadHomeworks(filterUrl = '/professor/api/homeworks') {
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

// Met à jour l'affichage des devoirs dans les sections "À venir", "En cours" et "Terminés"
function updateHomeworksUI(homeworks) {
    const upcomingSection = document.getElementById('upcomingHomework');
    const inProgressSection = document.getElementById('inProgressHomework');
    const toBeGradedSection = document.getElementById('toBeGradedHomework');
    const completedSection = document.getElementById('completedHomework');


    upcomingSection.innerHTML = '';
    inProgressSection.innerHTML = '';
    toBeGradedSection.innerHTML = '';
    completedSection.innerHTML = '';


    homeworks.forEach(hw => {
        const dueDate = new Date(hw.dueDate + "T00:00:00");
        const now = new Date();
        now.setHours(0, 0, 0, 0);
        console.log("Homework:", hw);
        const hwCard = document.createElement('div');
        hwCard.className = 'homework-card';
        hwCard.innerHTML = `
            <div class="homework-header">
                <span class="subject-badge">${hw.subjectId}</span>
                ${hw.status === 'SCHEDULED' ? `<span class="due-date"><i class="fas fa-calendar"></i> ${formatHomeworkDate(hw.dueDate)}</span>` : ''}
                ${hw.status === 'PUBLISHED' ? `<span class="status-badge in-progress">En cours</span>` : ''}
                ${hw.status === 'COMPLETED' ? `<span class="status-badge completed">Terminé</span>` : ''}
            </div>
            <div class="homework-content">
                <h3>${hw.title}</h3>
                <p>${hw.description || '' || hw.dueDate}</p>
                <div class="homework-details">
                    <span class="detail-item"><i class="fas fa-clock"></i> ${formatHomeworkDate(hw.dueDate)}</span>
                    <span class="detail-item"><i class="fas fa-users"></i> ${hw.classeId}</span>
                </div>
                ${hw.status === 'PUBLISHED' && dueDate.getTime() < now.getTime() ?
            `            <button class="btn btn-info mt-2" onclick="openGradeEntryModal('1')">
                            <i class="fas fa-edit"></i> Saisir notes
                         </button>` : ''}
            </div>
            <div class="homework-footer">
                ${hw.status === 'SCHEDULED' ? `
                    <button class="btn btn-primary" onclick="publishHomework(${hw.id})">
                        <i class="fas fa-share"></i> Publier
                    </button>
                    <button class="btn btn-secondary" onclick="editHomework(${hw.id})">
                        <i class="fas fa-edit"></i> Modifier
                    </button>
                ` : ''}
                ${hw.status === 'PUBLISHED' && hw.graded ? `
                    <button class="btn btn-warning" onclick="endHomework(${hw.id})">
                        <i class="fas fa-stop"></i> Terminer
                    </button>
                ` : ''}
                ${hw.status === 'COMPLETED' ? `
                    <button class="btn btn-primary" onclick="viewHomeworkResults(${hw.id})">
                        <i class="fas fa-chart-bar"></i> Résultats
                    </button>
                    <button class="btn btn-secondary" onclick="downloadHomeworkReport(${hw.id})">
                        <i class="fas fa-download"></i> Rapport
                    </button>
                ` : ''}
            </div>
        `;

        if (hw.status === 'SCHEDULED') {
            upcomingSection.appendChild(hwCard);
        } else if (hw.status === 'PUBLISHED' && dueDate.getTime() >= now.getTime()) {
            inProgressSection.appendChild(hwCard);
        } else if (hw.status === 'PUBLISHED' && dueDate.getTime() < now.getTime()) {
            toBeGradedSection.appendChild(hwCard);
        } else if (hw.status === 'COMPLETED') {
            completedSection.appendChild(hwCard);
        }
    });
}

// INITIALISATION DES GRAPHIQUES POUR DEVOIRS (si nécessaire)
function initializeHomeworkCharts() {
    // Ajoutez ici l'initialisation de graphiques spécifiques si besoin.
}

// Convertit une chaîne de date en format lisible pour l'affichage
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
                subjectId: formData.get('subject'),
                classeId: formData.get('classe'),
                dueDate: formData.get('dueDate'),
                description: formData.get('description')
            };
            console.log("Form Data:", JSON.stringify(data));
            const response = await fetch('/professor/api/homeworks', {
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

// Fonctions d'ouverture/fermeture des modales pour devoirs
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

    const filterInputs = filterForm.querySelectorAll('input, select');
    filterInputs.forEach(input => {
        input.addEventListener('change', () => {
            const formData = new FormData(filterForm);
            let url = '/professor/api/homeworks?';
            const month = formData.get('month');
            const classe = formData.get('classe');
            const subject = formData.get('subject');
            if (month) url += 'month=' + encodeURIComponent(month) + '&';
            if (classe) url += 'classe=' + encodeURIComponent(classe) + '&';
            if (subject) url += 'subject=' + encodeURIComponent(subject) + '&';
            if (url.endsWith('&') || url.endsWith('?')) {
                url = url.slice(0, -1);
            }
            loadHomeworks(url);
        });
    });

    filterForm.addEventListener('submit', function(e) {
        e.preventDefault();
        const url = generateFilterUrl(filterForm);
        loadHomeworks(url);
    });
}

// Génère l'URL de filtrage à partir du formulaire
function generateFilterUrl(form) {
    const formData = new FormData(form);
    let url = '/professor/api/homeworks?';
    const month = formData.get('month');
    const classe = formData.get('classe');
    const subject = formData.get('subject');
    if (month) url += 'month=' + encodeURIComponent(month) + '&';
    if (classe) url += 'classe=' + encodeURIComponent(classe) + '&';
    if (subject) url += 'subject=' + encodeURIComponent(subject) + '&';
    if (url.endsWith('&') || url.endsWith('?')) {
        url = url.slice(0, -1);
    }
    return url;
}

// Fonction utilitaire pour formater une date de type LocalDate (sans heures ni minutes)
function formatHomeworkDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('fr-FR', {
        day: '2-digit',
        month: 'short',
        year: 'numeric'
    });
}

// Fonction pour publier un devoir : change le statut en PUBLISHED
async function publishHomework(homeworkId) {
    try {
        const response = await fetch(`/professor/api/homeworks/${homeworkId}/publish`, {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
            }
        });
        if (!response.ok) throw new Error('Erreur lors de la publication du devoir');
        showNotification('Devoir publié avec succès', 'success');
        loadHomeworks();
    } catch (error) {
        showNotification(error.message, 'error');
    }
}

// Fonction pour terminer un devoir : change le statut à COMPLETED
async function endHomework(homeworkId) {
    try {
        const response = await fetch(`/professor/api/homeworks/${homeworkId}/end`, {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
            }
        });
        if (!response.ok) throw new Error('Erreur lors de la clôture du devoir');
        showNotification('Devoir terminé avec succès', 'success');
        loadHomeworks();
    } catch (error) {
        showNotification(error.message, 'error');
    }
}

// Téléchargement du rapport PDF d'un devoir
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
