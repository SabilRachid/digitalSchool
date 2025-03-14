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
                ${exam.status === 'UPCOMING' ? `<span class="status-badge in-progress">En cours</span>` : ''}
                ${exam.status === 'UPCOMING'  && now > examDate ? `<span class="status-badge in-progress">A noter</span>` : ''}
                ${exam.status === 'COMPLETED' ? `<span class="status-badge completed">Terminé</span>` : ''}
            </div>
            <div class="exam-content">
                <h3>${exam.title}</h3>
                <p>${exam.description || ''}</p>
                <div class="exam-details">
                    <span class="detail-item"><i class="fas fa-clock"></i> ${exam.duration} minutes</span>
                    <span class="detail-item"><i class="fas fa-users"></i> ${exam.classeName}</span>
                </div>
                ${exam.status === 'UPCOMING' && now > examDate ?
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
                ${exam.status === 'UPCOMING' && exam.graded ?
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
        } else if (exam.status === 'UPCOMING' && now <= examDate) {
            inProgressSection.appendChild(examCard);
        } else if (exam.status === 'UPCOMING' && now > examDate) {
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

// Ouvre le modal et charge les notes existantes pour l'examen donné
function openGradeEntryExamModal(examId) {
    console.log("Ouverture du modal pour l'examen :", examId);
    // Stocker l'ID de l'examen dans le champ caché
    document.getElementById('examId').value = examId;

    // Ouvrir le modal
    const modal = document.getElementById('gradeEntryExamModal');
    modal.classList.add('show');

    // Requête pour récupérer la liste des étudiants de l'examen et leurs notes existantes (si elles existent)
    fetch(`/professor/api/exams/${examId}/grades`)
        .then(response => {
            if (!response.ok) {
                throw new Error("Erreur lors de la récupération des notes existantes");
            }
            return response.json();
        })
        .then(data => {
            // data est un tableau d'objets contenant l'ID de l'étudiant, la note et le commentaire
            // Par exemple : [{ studentId: 1, firstName: "Alice", lastName: "Dupont", grade: 15.0, comment: "Très bien" }, ...]
            populateGradesContainer(data);
        })
        .catch(error => {
            console.error(error);
            showNotification(error.message, 'error');
        });
}

// Remplit le conteneur avec une ligne par étudiant
function populateGradesContainer(studentsData) {
    const container = document.getElementById('studentsGradesContainer');
    container.innerHTML = ''; // Vider le conteneur
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


// Initialisation du formulaire de saisie des notes pour l'examen
function initializeExamGradeEntryForm() {
    const form = document.getElementById('gradeEntryExamForm');
    if (!form) return;
    form.addEventListener('submit', async function(e) {
        e.preventDefault();
        // Appeler la fonction de sauvegarde partielle, puis notifier
        await saveOrPublishGrades(false);
    });
}

// Fonction de sauvegarde partielle des notes (sans publier)
async function savePartialGrades() {
    await saveOrPublishGrades(false);
}

// Ferme le modal de saisie des notes pour l'examen
function closeGradeEntryExamModal() {
    const modal = document.getElementById('gradeEntryExamModal');
    modal.classList.remove('show');
    document.getElementById('gradeEntryExamForm').reset();
}

function viewResults(examId) {
    // Récupère les résultats de l'examen via l'API
    fetch(`/professor/api/exams/${examId}/results`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Erreur lors du chargement des résultats: ' + response.status);
            }
            return response.json();
        })
        .then(data => {
            // Remplissage du contenu du modal avec les données reçues
            document.getElementById('resultsExamTitle').innerText = data.examTitle;
            document.getElementById('resultsAverage').innerText = data.average;
            document.getElementById('resultsMin').innerText = data.min;
            document.getElementById('resultsMax').innerText = data.max;
            document.getElementById('resultsTotalSubmissions').innerText = data.totalSubmissions;

            // Vous pouvez également ajouter d'autres statistiques ici...

            // Ouvrir le modal
            document.getElementById('resultsModal').classList.add('show');
        })
        .catch(error => {
            showNotification(error.message, 'error');
        });
}

// Fonction pour fermer le modal des résultats
function closeResultsModal() {
    document.getElementById('resultsModal').classList.remove('show');
}


// Fonction de publication finale des notes
async function publishGrades() {
    // Vérifier que toutes les notes sont renseignées
    if (!areAllGradesFilled()) {
        showNotification("La saisie est incomplète. Veuillez renseigner toutes les notes avant de publier.", "warning");
        return;
    }
    await saveOrPublishGrades(true);
}

// Fonction commune pour sauvegarder (partiellement ou publier)
async function saveOrPublishGrades(publish) {
    const examId = document.getElementById('examId').value;
    const container = document.getElementById('studentsGradesContainer');
    const rows = container.querySelectorAll('.grade-entry-row');
    const gradesData = [];
    let incomplete = false;
    rows.forEach(row => {
        // On récupère l'ID de l'étudiant à partir du nom de l'input grade
        const gradeInput = row.querySelector('input[type="number"]');
        const commentInput = row.querySelector('input[type="text"]');
        const studentId = gradeInput.name.split('_')[1];
        const gradeValue = gradeInput.value ? parseFloat(gradeInput.value) : null;
        if (gradeValue === null) {
            incomplete = true;
        }
        const comment = commentInput.value || "";
        gradesData.push({
            studentId: studentId,
            evaluationId: examId, // Considéré comme l'ID de l'évaluation/examen
            gradeValue: gradeValue,
            comment: comment
        });
    });
    // Envoi des données vers l'API
    try {
        const response = await fetch('/professor/api/exams/grade-entry', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
            },
            body: JSON.stringify(gradesData)
        });
        if (!response.ok) throw new Error('Erreur lors de la saisie des notes');
        if (publish) {
            // Appeler l'endpoint pour publier les notes finales
            const pubResponse = await fetch(`/professor/api/exams/${examId}/publish-grades`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                }
            });
            if (!pubResponse.ok) throw new Error('Erreur lors de la publication des notes');
            showNotification('Notes publiées avec succès', 'success');
        } else {
            showNotification('Notes sauvegardées avec succès', 'success');
        }
        // Recharger la liste ou fermer le modal
        closeGradeEntryExamModal();
        // Optionnel : recharger la page ou mettre à jour l'interface
    } catch (error) {
        showNotification(error.message, 'error');
    }
}

// Vérifie si tous les champs de note sont renseignés
function areAllGradesFilled() {
    const container = document.getElementById('studentsGradesContainer');
    const gradeInputs = container.querySelectorAll('input[type="number"]');
    for (let input of gradeInputs) {
        if (!input.value || isNaN(parseFloat(input.value))) {
            return false;
        }
    }
    return true;
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
