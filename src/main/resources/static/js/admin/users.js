class UserManagement {
    constructor() {
        this.table = this.initializeTable();
        this.modal = new bootstrap.Modal(document.getElementById('userModal'));
        this.form = document.getElementById('userForm');
        this.initializeEventListeners();

        // Expose global functions
        window.openAddUserModal = (type) => this.openAddUserModal(type);
    }

    initializeTable() {
        return new DataTable('#usersTable', {
            ajax: {
                url: '/admin/users/data',
                dataSrc: ''
            },
            columns: [
                {
                    data: null,
                    render: data => `${data.firstName} ${data.lastName}`
                },
                { data: 'email' },
                { data: 'username' },
                {
                    data: 'roles',
                    render: data => this.renderRoles(data)
                },
                {
                    data: 'classe',
                    render: data => data ? data.name : 'N/A'
                },
                {
                    data: 'enabled',
                    render: data => this.renderStatus(data)
                },
                {
                    data: null,
                    render: data => this.renderActions(data)
                }
            ],
            language: {
                url: '/js/datatables/fr-FR.json'
            },
            responsive: true,
            order: [[0, 'asc']]
        });
    }

    initializeEventListeners() {
        this.form.addEventListener('submit', e => this.handleSubmit(e));

        // Gestion de l'affichage du champ classe
        const roleCheckboxes = document.querySelectorAll('input[name="roles"]');
        roleCheckboxes.forEach(checkbox => {
            checkbox.addEventListener('change', () => this.toggleClasseField());
        });
    }

    renderRoles(roles) {
        return roles.map(role => {
            const roleName = role.replace('ROLE_', '');
            return `<span class="badge bg-primary">${roleName}</span>`;
        }).join(' ');
    }

    renderStatus(enabled) {
        return enabled ?
            '<span class="status-badge status-active">Actif</span>' :
            '<span class="status-badge status-inactive">Inactif</span>';
    }

    renderActions(data) {
        return `
            <div class="action-buttons">
                <button class="btn btn-sm btn-primary" onclick="userManagement.editUser(${data.id})">
                    <i class="fas fa-edit"></i>
                </button>
                <button class="btn btn-sm btn-danger" onclick="userManagement.deleteUser(${data.id})">
                    <i class="fas fa-trash"></i>
                </button>
            </div>
        `;
    }

    async handleSubmit(e) {
        e.preventDefault();
        const formData = new FormData(this.form);
        const data = {
            id: formData.get('id'),
            username: formData.get('username'),
            email: formData.get('email'),
            password: formData.get('password'),
            firstName: formData.get('firstName'),
            lastName: formData.get('lastName'),
            enabled: formData.get('enabled') === 'on',
            roles: Array.from(document.querySelectorAll('input[name="roles"]:checked'))
                .map(cb => cb.value),
            classeId: formData.get('classe')
        };

        try {
            const response = await fetch(data.id ? `/admin/users/${data.id}` : '/admin/users', {
                method: data.id ? 'PUT' : 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                },
                body: JSON.stringify(data)
            });

            if (!response.ok) {
                const error = await response.json();
                throw new Error(error.message);
            }

            this.modal.hide();
            this.table.ajax.reload();
            this.showNotification('Opération réussie', 'success');
        } catch (error) {
            this.showNotification(error.message, 'error');
        }
    }

    async editUser(id) {
        try {
            const response = await fetch(`/admin/users/${id}`);
            if (!response.ok) throw new Error('Erreur lors du chargement des données');

            const user = await response.json();
            this.populateForm(user);
            this.modal.show();
        } catch (error) {
            this.showNotification(error.message, 'error');
        }
    }

    async deleteUser(id) {
        if (!confirm('Êtes-vous sûr de vouloir supprimer cet utilisateur ?')) return;

        try {
            const response = await fetch(`/admin/users/${id}`, {
                method: 'DELETE',
                headers: {
                    'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                }
            });

            if (!response.ok) throw new Error('Erreur lors de la suppression');

            this.table.ajax.reload();
            this.showNotification('Utilisateur supprimé avec succès', 'success');
        } catch (error) {
            this.showNotification(error.message, 'error');
        }
    }

    populateForm(user) {
        const form = this.form;
        form.elements['id'].value = user.id;
        form.elements['username'].value = user.username;
        form.elements['email'].value = user.email;
        form.elements['firstName'].value = user.firstName;
        form.elements['lastName'].value = user.lastName;
        form.elements['enabled'].checked = user.enabled;

        // Réinitialiser le mot de passe
        form.elements['password'].value = '';

        // Gérer les rôles
        document.querySelectorAll('input[name="roles"]').forEach(cb => {
            cb.checked = user.roles.includes(cb.value);
        });

        // Gérer la classe
        if (user.classe) {
            form.elements['classe'].value = user.classe.id;
        }

        this.toggleClasseField();
    }

    toggleClasseField() {
        const isStudent = document.getElementById('roleStudent').checked;
        const classeField = document.getElementById('classeField');
        classeField.style.display = isStudent ? 'block' : 'none';

        if (!isStudent) {
            document.getElementById('classe').value = '';
        }
    }

    showNotification(message, type) {
        // Implémenter selon votre système de notification
        alert(message);
    }

    openAddUserModal(type) {
        this.form.reset();
        document.getElementById('userId').value = '';

        // Sélectionner le rôle approprié
        document.querySelectorAll('input[name="roles"]').forEach(cb => {
            cb.checked = cb.value === `ROLE_${type.toUpperCase()}`;
        });

        this.toggleClasseField();
        this.modal.show();
    }
}

// Initialisation
let userManagement;
document.addEventListener('DOMContentLoaded', () => {
    userManagement = new UserManagement();
});