document.addEventListener('DOMContentLoaded', function() {
    loadGroupedEvaluations();
    attachFilterListeners();
});

//////////////////////////////
// Attach listeners aux filtres
//////////////////////////////
function attachFilterListeners() {
    const filters = ['classeFilter', 'subjectFilter', 'evaluationTypeFilter', 'evaluationDateFilter'];
    filters.forEach(id => {
        const element = document.getElementById(id);
        if (element) element.addEventListener('change', loadGroupedEvaluations);
    });
}

//////////////////////////////
// Chargement des évaluations groupées
//////////////////////////////
async function loadGroupedEvaluations() {
    const classeId = document.getElementById('classeFilter').value;
    const subjectId = document.getElementById('subjectFilter').value;
    const evaluationType = document.getElementById('evaluationTypeFilter').value;
    const evaluationDate = document.getElementById('evaluationDateFilter').value; // format YYYY-MM-DD

    let url = `/professor/api/evaluations/grouped?`;
    const params = new URLSearchParams();
    if (classeId) params.append('classeId', classeId);
    if (subjectId) params.append('subjectId', subjectId);
    if (evaluationType) params.append('evaluationType', evaluationType);
    if (evaluationDate) params.append('evaluationDate', evaluationDate);
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

function displayGroupedEvaluations(evaluations) {
    const tbody = document.querySelector('#evaluationsTable tbody');
    tbody.innerHTML = evaluations.map(ev => `
    <tr data-evaluation-id="${ev.id}">
      <td>${ev.subjectName}</td>
      <td>${ev.evaluationType}</td>
      <td>${ev.classeName}</td>
      <td>${ev.eventDate}</td>
      <td>${ev.completed ? 'Complété' : 'Non complété'}</td>
      <td>
        <button class="btn btn-sm btn-primary" onclick="openGradesModal(${ev.id}, '${ev.classeId}')">
          <i class="fas fa-edit"></i> Saisir les notes
        </button>
      </td>
    </tr>
  `).join('');
}

//////////////////////////////
// Ouverture du modal de saisie des notes
//////////////////////////////
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

//////////////////////////////
// Sauvegarde des notes (bulk update)
//////////////////////////////
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

//////////////////////////////
// Notifications
//////////////////////////////
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
