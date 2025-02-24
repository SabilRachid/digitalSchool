document.addEventListener('DOMContentLoaded', function() {
    loadGroupedEvaluations();
    attachFilterListeners();
    initializeDataTable();
});

// Initialisation de DataTable avec recherche activée
function initializeDataTable() {
    $('#evaluationsTable').DataTable({
        responsive: true,
        dom: 'lfrtip'
    });
}

// Attache les écouteurs sur les filtres
function attachFilterListeners() {
    const filters = ['classeFilter', 'subjectFilter', 'evaluationTypeFilter', 'startDateFilter', 'endDateFilter'];
    filters.forEach(id => {
        const element = document.getElementById(id);
        if (element) {
            element.addEventListener('change', loadGroupedEvaluations);
        }
    });
}

// Charge les évaluations groupées en fonction des filtres
async function loadGroupedEvaluations() {
    const classeId = document.getElementById('classeFilter').value;
    const subjectId = document.getElementById('subjectFilter').value;
    const evaluationType = document.getElementById('evaluationTypeFilter').value;
    const startDate = document.getElementById('startDateFilter').value; // Format YYYY-MM-DD
    const endDate = document.getElementById('endDateFilter').value;     // Format YYYY-MM-DD

    let url = `/professor/api/evaluations/grouped?`;
    const params = new URLSearchParams();
    if (classeId) params.append('classeId', classeId);
    if (subjectId) params.append('subjectId', subjectId);
    if (evaluationType) params.append('evaluationType', evaluationType);
    if (startDate) params.append('startDate', startDate);
    if (endDate) params.append('endDate', endDate);
    url += params.toString();

    try {
        const response = await fetch(url);
        if (!response.ok) throw new Error('Erreur lors du chargement des évaluations');
        const evaluations = await response.json();
        displayGroupedEvaluations(evaluations);
    } catch (error) {
        showNotification(error.message, 'error');
    }
}

// Affiche les évaluations groupées dans le tableau
function displayGroupedEvaluations(evaluations) {
    const tbody = document.querySelector('#evaluationsTable tbody');
    tbody.innerHTML = evaluations.map(ev => `
        <tr data-evaluation-id="${ev.id}">
          <td>${ev.subjectName}</td>
          <td>${ev.evaluationType}</td>
          <td>${ev.classeName}</td>
          <td>${ev.studentName}</td>
          <td>${ev.eventDate}</td>
          <td>${ev.completed ? 'Complété' : 'Non complété'}</td>
          <td>
            <button class="btn btn-sm btn-primary" onclick="openGradesModal(${ev.id}, '${ev.classeId}')">
              <i class="fas fa-edit"></i> Modifier
            </button>
          </td>
        </tr>
    `).join('');
}

// Ouvre le modal de modification des notes et charge les notes existantes
async function openGradesModal(evaluationId, classeId) {
    try {
        const response = await fetch(`/professor/api/evaluations/${evaluationId}/grades`);
        if (!response.ok) throw new Error('Erreur lors du chargement des notes');
        const gradesData = await response.json();

        const tbody = document.querySelector('#gradesEntryTable tbody');
        tbody.innerHTML = gradesData.map(item => `
            <tr data-student-id="${item.studentId}">
                <td>${item.studentName}</td>
                <td>
                    <input type="number" class="form-control grade-input" value="${item.value !== null ? item.value : ''}" min="0" max="20" step="0.5">
                </td>
                <td>
                    <input type="text" class="form-control comment-input" value="${item.comments ? item.comments : ''}">
                </td>
            </tr>
        `).join('');

        document.getElementById('gradesModal').dataset.evaluationId = evaluationId;
        const modal = new bootstrap.Modal(document.getElementById('gradesModal'));
        modal.show();
    } catch (error) {
        showNotification(error.message, 'error');
    }
}

// Sauvegarde les modifications des notes
async function saveGrades() {
    const evaluationId = document.getElementById('gradesModal').dataset.evaluationId;
    const rows = document.querySelectorAll('#gradesEntryTable tbody tr');
    const updates = [];

    rows.forEach(row => {
        const studentId = row.dataset.studentId;
        const gradeValue = parseFloat(row.querySelector('.grade-input').value);
        const commentValue = row.querySelector('.comment-input').value;
        if (!isNaN(gradeValue)) {
            updates.push({
                studentId: studentId,
                value: gradeValue,
                comments: commentValue
            });
        }
    });

    if (updates.length === 0) {
        showNotification("Aucune note à enregistrer", "warning");
        return;
    }

    try {
        const csrfToken = document.querySelector('meta[name="_csrf"]').content;
        const response = await fetch(`/professor/api/evaluations/${evaluationId}/grades`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': csrfToken
            },
            body: JSON.stringify(updates)
        });
        if (!response.ok) throw new Error('Erreur lors de l\'enregistrement des notes');
        showNotification("Notes enregistrées avec succès", "success");
        const modal = bootstrap.Modal.getInstance(document.getElementById('gradesModal'));
        modal.hide();
        loadGroupedEvaluations();
    } catch (error) {
        showNotification(error.message, "error");
    }
}

// Affiche une notification à l'écran
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
