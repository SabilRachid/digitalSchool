document.addEventListener('DOMContentLoaded', function() {
    loadHomeworks();               // Charge initialement tous les devoirs via GET
    initializeHomeworkForm();      // Pour la création d'un devoir
    initializeHomeworkGradeEntryForm();  // Pour la saisie des notes
    initializeHomeworkFilters();   // Pour le filtrage dynamique
    initializeHomeworkCharts();    // Pour les graphiques si nécessaire
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

// Met à jour l'affichage des devoirs dans les différentes sections
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
        const dueDate = new Date(hw.dueDate + "T00:00:00"); // Conversion de la date (sans heure)
        const now = new Date();
        now.setHours(0, 0, 0, 0);
        console.log("Homework:", hw);
        const hwCard = document.createElement('div');
        hwCard.className = 'homework-card';
        hwCard.innerHTML = `
            <div class="homework-header">
                <span class="subject-badge">${hw.subjectName || hw.subjectId}</span>
                ${hw.status === 'SCHEDULED' ? `<span class="due-date"><i class="fas fa-calendar"></i> ${formatHomeworkDate(hw.dueDate)}</span>` : ''}
                ${hw.status === 'UPCOMING' ? `<span class="status-badge in-progress">En cours</span>` : ''}
                ${hw.status === 'UPCOMING' && dueDate.getTime() < now.getTime() ? `<span class="status-badge in-progress">À noter</span>` : ''}
                ${hw.status === 'COMPLETED' ? `<span class="status-badge completed">Terminé</span>` : ''}
            </div>
            <div class="homework-content">
                <h3>${hw.title}</h3>
                <p>${hw.description || ''}</p>
                <div class="homework-details">
                    <span class="detail-item"><i class="fas fa-clock"></i> ${formatHomeworkDate(hw.dueDate)}</span>
                    <span class="detail-item"><i class="fas fa-users"></i> ${hw.classeName || hw.classeId}</span>
                </div>
                ${hw.status === 'UPCOMING' && dueDate.getTime() < now.getTime() ?
            `<button class="btn btn-info mt-2" onclick="openGradeEntryHomeworkModal(${hw.id})">
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
                ${hw.status === 'UPCOMING' && hw.graded ? `
                    <button class="btn btn-warning" onclick="endHomework(${hw.id})">
                        <i class="fas fa-stop"></i> Terminer
                    </button>` : ''}
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
        } else if (hw.status === 'UPCOMING' && dueDate.getTime() >= now.getTime()) {
            inProgressSection.appendChild(hwCard);
        } else if (hw.status === 'UPCOMING' && dueDate.getTime() < now.getTime()) {
            toBeGradedSection.appendChild(hwCard);
        } else if (hw.status === 'COMPLETED') {
            completedSection.appendChild(hwCard);
        }
    });
}

// Formatte la date pour l'affichage (sans heures)
function formatHomeworkDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('fr-FR', { day: '2-digit', month: 'short', year: 'numeric' });
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
            console.log("Form Data:", JSON.stringify(data, null, 2));
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

function openHomeworkModal() {
    document.getElementById('homeworkModal').classList.add('show');
}
function closeHomeworkModal() {
    document.getElementById('homeworkModal').classList.remove('show');
    document.getElementById('homeworkForm').reset();
}

// Fonction pour publier un devoir : change le statut en UPCOMING
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


// Ouvre le modal de saisie des notes pour un devoir
function openGradeEntryHomeworkModal(homeworkId) {
    console.log("Ouverture du modal pour le devoir :", homeworkId);
    // Stocker l'ID du devoir dans le champ caché
    document.getElementById('homeworkSubmissionId').value = homeworkId;
    // Ouvrir le modal
    const modal = document.getElementById('gradeEntryHomeworkModal');
    modal.classList.add('show');
    // Requête pour récupérer la liste des étudiants et leurs notes existantes (si elles existent)
    fetch(`/professor/api/homeworks/${homeworkId}/grades`)
        .then(response => {
            if (!response.ok) {
                throw new Error("Erreur lors de la récupération des notes existantes");
            }
            return response.json();
        })
        .then(data => {
            populateHomeworkGradesContainer(data);
        })
        .catch(error => {
            console.error(error);
            showNotification(error.message, 'error');
        });
}

// Remplit le conteneur avec une ligne par étudiant
function populateHomeworkGradesContainer(studentsData) {
    const container = document.getElementById('studentsGradesContainer');
    container.innerHTML = '';
    studentsData.forEach(student => {
        const row = document.createElement('div');
        row.className = 'grade-entry-row';
        row.innerHTML = `
            <div class="student-info">
                <span>${student.firstName} ${student.lastName}</span>
            </div>
            <div class="grade-input">
                <input type="number" name="grade_${student.studentId}" placeholder="Note (0-20)" min="0" max="20" step="0.1" value="${student.grade != null ? student.grade : ''}" required>
            </div>
            <div class="comment-input">
                <input type="text" name="comment_${student.studentId}" placeholder="Remarque" value="${student.comment || ''}">
            </div>
        `;
        container.appendChild(row);
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
            console.log("JSON envoyé pour les notes :", JSON.stringify(data, null, 2));
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

// Ferme le modal de saisie des notes pour un devoir
function closeHomeworkGradeEntryModal() {
    const modal = document.getElementById('gradeEntryHomeworkModal');
    modal.classList.remove('show');
    document.getElementById('gradeEntryHomeworkForm').reset();
}

// INITIALISATION DU FORMULAIRE DE FILTRAGE POUR DEVOIRS
function initializeHomeworkFilters() {
    const filterForm = document.getElementById('homeworkFilterForm');
    if (!filterForm) return;
    filterForm.addEventListener('submit', function(e) {
        e.preventDefault();
        const url = generateFilterUrl(filterForm);
        loadHomeworks(url);
    });
    const filterInputs = filterForm.querySelectorAll('input, select');
    filterInputs.forEach(input => {
        input.addEventListener('change', () => {
            const url = generateFilterUrl(filterForm);
            loadHomeworks(url);
        });
    });
}

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

// Fonction utilitaire pour afficher une notification
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

// Fonction pour télécharger le rapport PDF d'un devoir
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
