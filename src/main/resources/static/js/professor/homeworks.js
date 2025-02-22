document.addEventListener('DOMContentLoaded', function () {
    // Initialisation de la DataTable pour les devoirs

    const homeworksTable = $('#homeworksTable').DataTable({
        ajax: {
            url: '/professor/api/homeworks/data',
            data: function (d) {
                d.classId = $('#classFilter').val();
                // Les valeurs de year et month doivent être des nombres ou null
                d.year = $('#yearFilter').val() ? parseInt($('#yearFilter').val()) : null;
                d.month = $('#monthFilter').val() ? parseInt($('#monthFilter').val()) : null;
            },
            dataSrc: ''
        },
        columns: [
            { data: 'title' },
            { data: 'subjectName' },
            { data: 'classeName' },
            { data: 'dueDate', render: data => new Date(data).toLocaleDateString() },
            { data: 'status' },
            {
                data: null,
                render: function (data) {
                    return `
                        <div class="action-buttons">
                            <button class="btn btn-sm btn-primary" onclick="homeworksPage.edit(${data.id})">
                                <i class="fas fa-edit"></i>
                            </button>
                            <button class="btn btn-sm btn-danger" onclick="homeworksPage.delete(${data.id})">
                                <i class="fas fa-trash"></i>
                            </button>
                        </div>
                    `;
                }
            }
        ],
        language: {
            url: '/js/datatables/fr-FR.json'
        },
        responsive: true,
        order: [[3, 'asc']]
    });

    // Recharge la DataTable dès qu'un des filtres change
    $('#subjectFilter, #classFilter, #yearFilter, #monthFilter').on('change', function () {
        homeworksTable.ajax.reload();
    });

    // Exemple de fonction de notification simple
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

    // Exposer l'objet global pour les actions (édition, suppression, etc.)
    window.homeworksPage = {
        openModal: function () {
            // Implémentez ici l'ouverture du modal d'ajout/modification en utilisant l'API Bootstrap 5 si nécessaire
            const modalId='homeworkModal';
            const modal = new bootstrap.Modal(document.getElementById(modalId));
            modal.show();
        },
        edit: async function (id) {
            try {
                const response = await fetch(`/professor/api/homeworks/${id}`);
                if (!response.ok) throw new Error('Erreur lors du chargement des données');
                const homework = await response.json();
                populateForm(homework);
                // Utilisation de l'API native de Bootstrap 5 pour afficher le modal
                const modalElement = document.getElementById('homeworkModal');
                const modal = new bootstrap.Modal(modalElement);
                modal.show();
            } catch (error) {
                showNotification(error.message, 'error');
            }
        },
        delete: async function (id) {
            if (!confirm('Êtes-vous sûr de vouloir supprimer ce devoir ?')) return;
            try {
                const csrfToken = document.querySelector('meta[name="_csrf"]').content;
                const response = await fetch(`/professor/api/homeworks/${id}`, {
                    method: 'DELETE',
                    headers: { 'X-CSRF-TOKEN': csrfToken }
                });
                if (!response.ok) throw new Error('Erreur lors de la suppression');
                homeworksTable.ajax.reload();
                showNotification('Devoir supprimé avec succès', 'success');
            } catch (error) {
                showNotification(error.message, 'error');
            }
        }
    };

    // Exemple de fonction pour pré-remplir le formulaire du modal
    function populateForm(homework) {
        const form = document.getElementById('homeworkForm');
        form.elements['id'].value = homework.id;
        form.elements['title'].value = homework.title;
        form.elements['subjectId'].value = homework.subject.id;
        form.elements['classeId'].value = homework.student.classe.id;
        form.elements['dueDate'].value = homework.dueDate;
        form.elements['description'].value = homework.description;
        form.elements['status'].value = homework.status;
    }
});
