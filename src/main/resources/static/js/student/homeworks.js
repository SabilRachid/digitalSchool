// Gestion des devoirs étudiants
document.addEventListener('DOMContentLoaded', function() {
// Filtres
const subjectFilter = document.getElementById('subjectFilter');
    const statusFilter = document.getElementById('statusFilter');

    function applyFilters() {
    const subject = subjectFilter.value;
    const status = statusFilter.value;

document.querySelectorAll('.homework-card').forEach(card => {
    let show = true;

    if (subject && card.dataset.subject !== subject) {
    show = false;
}

    if (status && card.dataset.status !== status) {
    show = false;
}

card.style.display = show ? 'block' : 'none';
});
}

subjectFilter.addEventListener('change', applyFilters);
statusFilter.addEventListener('change', applyFilters);

// Gestion du formulaire de soumission
const submissionForm = document.getElementById('submissionForm');
submissionForm.addEventListener('submit', async function(e) {
e.preventDefault();

    const formData = new FormData(this);
    const homeworkId = document.getElementById('homeworkId').value;

try {
    const response = await fetch(`/student/homework/${homeworkId}/submit`, {
    method: 'POST',
    body: formData
});

if (!response.ok) throw new Error('Erreur lors de la soumission');

closeSubmissionModal();
showNotification('Devoir soumis avec succès', 'success');
setTimeout(() => location.reload(), 1500);
} catch (error) {
showNotification(error.message, 'error');
}
});
});

// Fonction pour ouvrir la modal de soumission
   function submitHomework(homeworkId) {
document.getElementById('homeworkId').value = homeworkId;
document.getElementById('submissionModal').classList.add('show');
}

// Fonction pour fermer la modal de soumission
   function closeSubmissionModal() {
document.getElementById('submissionModal').classList.remove('show');
document.getElementById('submissionForm').reset();
}

// Fonction pour voir une soumission
   function viewSubmission(homeworkId) {
window.location.href = `/student/homework/${homeworkId}/view`;
}

// Fonction pour afficher une notification
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
 