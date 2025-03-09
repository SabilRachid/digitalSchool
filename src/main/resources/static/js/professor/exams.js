document.addEventListener('DOMContentLoaded', function() {
    initializeExamGradeEntryForm();
    loadExams(); // Charge initialement tous les examens via GET
    initializeForm();           // Pour la création d'un examen
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
                <p>${exam.description || ''}</p>
                <div class="exam-details">
                    <span class="detail-item"><i class="fas fa-clock"></i> ${exam.duration} minutes</span>
                    <span class="detail-item"><i class="fas fa-users"></i> ${exam.classeName}</span>
                </div>
                ${exam.status === 'PUBLISHED' && now > examDate ?
            `<button class="btn btn-info mt-2" onclick="openGradeEntryExamModal(${exam.id})">
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


// Fonctions d'ouverture/fermeture des modals
function openExamModal() {
    document.getElementById('examModal').classList.add('show');
}

function closeExamModal() {
    document.getElementById('examModal').classList.remove('show');
    document.getElementById('examForm').reset();
}

// Ouvre le modal de saisie des notes pour l'examen et remplit dynamiquement la liste des étudiants
function openGradeEntryExamModal(examId) {
    console.log("Ouverture du modal de saisie des notes pour l'examen :", examId);
    // Affiche le modal
    const modal = document.getElementById('gradeEntryExamModal');
    modal.classList.add('show');

    // Stocke l'examId dans le champ caché
    document.getElementById('examId').value = examId;

    // Récupère la liste des étudiants de la classe pour cet examen.
    // Cette partie est à adapter selon votre méthode d'obtention des étudiants.
    // Par exemple, vous pouvez effectuer une requête AJAX pour obtenir ces données.
    fetch(`/professor/api/exams/${examId}/students`)
        .then(response => {
            if (!response.ok) {
                throw new Error("Erreur lors de la récupération des étudiants");
            }
            return response.json();
        })
        .then(students => {
            const container = document.getElementById('studentsGradesContainer');
            container.innerHTML = ''; // Vider le conteneur
            students.forEach(student => {
                // Créez une ligne pour chaque étudiant
                const div = document.createElement('div');
                div.className = 'grade-entry-row';
                div.innerHTML = `
                    <span>${student.firstName} ${student.lastName}</span>
                    <input type="number" name="grade_${student.id}" placeholder="Note" min="0" max="20" step="0.1" required>
                    <input type="text" name="comment_${student.id}" placeholder="Commentaire (optionnel)">
                `;
                container.appendChild(div);
            });
        })
        .catch(error => {
            console.error(error);
            showNotification(error.message, 'error');
        });
}

// Ferme le modal de saisie des notes pour l'examen
function closeGradeEntryExamModal() {
    const modal = document.getElementById('gradeEntryExamModal');
    modal.classList.remove('show');
    document.getElementById('gradeEntryExamForm').reset();
}

// Initialisation du formulaire de saisie des notes pour l'examen
function initializeExamGradeEntryForm() {
    const form = document.getElementById('gradeEntryExamForm');
    if (!form) return;
    form.addEventListener('submit', async function(e) {
        e.preventDefault();
        try {
            // Récupère l'examId
            const examId = document.getElementById('examId').value;
            // Rassemble les notes et commentaires pour chaque étudiant
            const container = document.getElementById('studentsGradesContainer');
            const inputs = container.querySelectorAll('input[type="number"]');
            const gradesData = [];
            inputs.forEach(input => {
                const studentId = input.name.split('_')[1]; // suppose que le nom est "grade_{studentId}"
                const gradeValue = parseFloat(input.value);
                const commentInput = container.querySelector(`input[name="comment_${studentId}"]`);
                const comment = commentInput ? commentInput.value : "";
                gradesData.push({
                    studentId: studentId,
                    evaluationId: examId, // Utilisé ici comme l'ID de l'évaluation
                    gradeValue: gradeValue,
                    comment: comment
                });
            });
            console.log("JSON envoyé pour la note d'examen :", JSON.stringify(gradesData, null, 2));
            // Envoi via fetch (vous pouvez adapter l'URL et le format selon votre API)
            const response = await fetch('/professor/api/exams/grade-entry', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                },
                body: JSON.stringify(gradesData)
            });
            if (!response.ok) throw new Error('Erreur lors de la saisie des notes');
            closeGradeEntryExamModal();
            showNotification('Notes enregistrées et élèves notifiés', 'success');
            setTimeout(() => location.reload(), 1500);
        } catch (error) {
            showNotification(error.message, 'error');
        }
    });
}

// INITIALISATION DES GRAPHIQUES (si nécessaire)
function initializeCharts() {
    // Ajoutez ici l'initialisation de graphiques spécifiques si besoin.
}

// INITIALISATION DU FORMULAIRE DE FILTRAGE
function initializeFilters() {
    const filterForm = document.getElementById('examFilterForm');
    if (!filterForm) return;
    const filterInputs = filterForm.querySelectorAll('input, select');
    filterInputs.forEach(input => {
        input.addEventListener('change', () => {
            const url = generateFilterUrl(filterForm);
            loadExams(url);
        });
    });
    filterForm.addEventListener('submit', function(e) {
        e.preventDefault();
        const url = generateFilterUrl(filterForm);
        loadExams(url);
    });
}

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
        loadExams();
    } catch (error) {
        showNotification(error.message, 'error');
    }
}

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
