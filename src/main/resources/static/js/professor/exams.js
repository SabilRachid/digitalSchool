document.addEventListener('DOMContentLoaded', function() {
    loadExams(); // Charge initialement tous les examens via GET
    initializeForm();           // Pour la création d'un examen
    initializeGradeEntryForm(); // Pour la saisie des notes
    initializeCharts();         // Pour l'initialisation de graphiques si besoin
    initializeFilters();        // Pour le filtrage dynamique
});

// Charge les examens via GET, éventuellement filtrés
async function loadExams(filterUrl = '/professor/api/exams') {
    try {
        const response = await fetch(filterUrl, {
            method: 'GET',
            headers: { 'Accept': 'application/json' }
        });
        if (!response.ok) throw new Error('Erreur lors du chargement des examens : ' + response.status);
        const exams = await response.json();
        updateExamsUI(exams);
    } catch (error) {
        showNotification(error.message, 'error');
    }
}

// Met à jour l'affichage des examens selon leur statut
function updateExamsUI(exams) {

    const upcomingSection = document.getElementById('upcomingExamList');
    const inProgressSection = document.getElementById('inProgressExamList');
    const toBeGradedSection = document.getElementById('toBeGradedExamList');
    const completedSection = document.getElementById('completedExamList');


    upcomingSection.innerHTML = '';
    inProgressSection.innerHTML = '';
    toBeGradedSection.innerHTML = '';
    completedSection.innerHTML = '';


    exams.forEach(exam => {
        const examDate = new Date(exam.examDate);
        const now = new Date();
        const examCard = document.createElement('div');
        examCard.className = 'exam-card';
        examCard.innerHTML = `
            <div class="exam-header">
                <span class="subject-badge">${exam.subjectName}</span>
                ${exam.status === 'SCHEDULED' ? `<span class="exam-date"><i class="fas fa-calendar"></i> ${formatExamDate(exam.startTime)}</span>` : ''}
                ${exam.status === 'PUBLISHED' ? `<span class="status-badge in-progress">En cours</span>` : ''}
                ${exam.status === 'COMPLETED' ? `<span class="status-badge completed">Terminé</span>` : ''}
            </div>
            <div class="exam-content">
                <h3>${exam.title}</h3>
                <p>${exam.description || '' || exam.examDate}</p>
                <div class="exam-details">
                    <span class="detail-item"><i class="fas fa-clock"></i> ${exam.duration} minutes</span>
                    <span class="detail-item"><i class="fas fa-users"></i> ${exam.classeName}</span>
                </div>
                ${exam.status === 'PUBLISHED' && now > examDate ?
                `<button class="btn btn-info mt-2" onclick="openGradeEntryModal('1')">
                            <i class="fas fa-edit"></i> Saisir notes
                         </button>` : ''}
            </div>
            <div class="exam-footer">
                ${exam.status === 'SCHEDULED' ? `
                    <button class="btn btn-primary" onclick="publishExam(${exam.id})">
                        <i class="fas fa-share"></i> Publier
                    </button>
                    <button class="btn btn-secondary" onclick="editExam(${exam.id})">
                        <i class="fas fa-edit"></i> Modifier
                    </button>
                ` : ''}
                ${exam.status === 'PUBLISHED' && exam.graded ?
            `<button class="btn btn-warning" onclick="endExam(${exam.id})">
                        <i class="fas fa-stop"></i> Terminer
                    </button>` : ''}
                ${exam.status === 'COMPLETED' ? `
                    <button class="btn btn-primary" onclick="viewResults(${exam.id})">
                        <i class="fas fa-chart-bar"></i> Résultats
                    </button>
                    <button class="btn btn-secondary" onclick="downloadReport(${exam.id})">
                        <i class="fas fa-download"></i> Rapport
                    </button>
                ` : ''}
            </div>
        `;
        if (exam.status === 'SCHEDULED') {
            upcomingSection.appendChild(examCard);
        } else if (exam.status === 'PUBLISHED' && now <= examDate) {
            inProgressSection.appendChild(examCard);
        } else if (exam.status === 'PUBLISHED' && now > examDate) {
            toBeGradedSection.appendChild(examCard);
        } else if (exam.status === 'COMPLETED') {
            completedSection.appendChild(examCard);
        }
    });
}

// Formate la date d'un examen à partir de startTime
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

// INITIALISATION DU FORMULAIRE DE CRÉATION D'EXAMEN
function initializeForm() {
    const form = document.getElementById('examForm');
    if (!form) return;
    form.addEventListener('submit', async function(e) {
        e.preventDefault();
        try {
            const formData = new FormData(this);
            const data = {
                title: formData.get('title'),
                subjectId: formData.get('subject'),
                classeId: formData.get('classe'),
                startTime: formData.get('examDate'),
                duration: parseInt(formData.get('duration')),
                description: formData.get('description'),
                maxScore: parseFloat(formData.get('maxScore'))
                // roomId peut être ajouté si vous utilisez un select pour la salle
            };
            console.log("📌 JSON envoyé :", JSON.stringify(data, null, 2));
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

// Fonction d'ouverture/fermeture des modales
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
function initializeFilters() {
    const filterForm = document.getElementById('examFilterForm');
    if (!filterForm) return;

    // Ajoute un écouteur sur chaque changement de champ
    const filterInputs = filterForm.querySelectorAll('input, select');
    filterInputs.forEach(input => {
        input.addEventListener('change', () => {
            const url = generateFilterUrl(filterForm);
            loadExams(url);
        });
    });

    // Écoute de la soumission du formulaire
    filterForm.addEventListener('submit', function(e) {
        e.preventDefault();
        const url = generateFilterUrl(filterForm);
        loadExams(url);
    });
}

// Génère l'URL de filtrage à partir des valeurs du formulaire
function generateFilterUrl(form) {
    const formData = new FormData(form);
    let url = '/professor/api/exams?';
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

// Fonction utilitaire pour formater la date d'un examen
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

// Fonction pour publier un examen : change le statut en PUBLISHED
async function publishExam(examId) {
    try {
        const response = await fetch(`/professor/api/exams/${examId}/publish`, {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
            }
        });
        if (!response.ok) throw new Error('Erreur lors de la publication de l\'examen');
        showNotification('Examen publié avec succès', 'success');
        loadExams();
    } catch (error) {
        showNotification(error.message, 'error');
    }
}

async function endExam(examId) {
    try {
        const response = await fetch(`/professor/api/exams/${examId}/end`, {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
            }
        });
        if (!response.ok) throw new Error('Erreur lors de la clôture de l\'examen');
        showNotification('Examen terminé avec succès', 'success');
        // Recharge la liste des examens pour actualiser l'affichage
        loadExams();
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
