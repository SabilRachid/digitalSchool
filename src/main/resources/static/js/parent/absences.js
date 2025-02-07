// Gestion des absences
document.addEventListener('DOMContentLoaded', function() {
    initializeJustificationForm();
});

// Initialisation du formulaire de justification
function initializeJustificationForm() {
    const form = document.getElementById('justificationForm');
    if (!form) return;

    form.addEventListener('submit', async function(e) {
        e.preventDefault();

        const formData = new FormData(this);
        const absenceId = document.getElementById('absenceId').value;

        try {
            const response = await fetch(`/parent/absences/${absenceId}/justify`, {
                method: 'POST',
                headers: {
                    'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                },
                body: formData
            });

            if (!response.ok) throw new Error('Erreur lors de la soumission du justificatif');

            closeJustificationModal();
            showNotification('Justificatif soumis avec succès', 'success');
            setTimeout(() => location.reload(), 1500);
        } catch (error) {
            showNotification(error.message, 'error');
        }
    });
}

// Soumission d'une justification
function submitJustification(absenceId) {
    document.getElementById('absenceId').value = absenceId;
    document.getElementById('justificationModal').classList.add('show');
}

// Visualisation d'un justificatif
async function viewJustification(absenceId) {
    try {
        const response = await fetch(`/parent/absences/${absenceId}/justification`);
        if (!response.ok) throw new Error('Erreur lors du chargement du justificatif');

        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        window.open(url, '_blank');
        window.URL.revokeObjectURL(url);
    } catch (error) {
        showNotification(error.message, 'error');
    }
}

function closeJustificationModal() {
    document.getElementById('justificationModal').classList.remove('show');
    document.getElementById('justificationForm').reset();
}

// Notifications
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
