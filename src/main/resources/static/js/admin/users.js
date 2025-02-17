class UserManagement {
    constructor() {
        this.table = this.initializeTable();
        this.modal = new bootstrap.Modal(document.getElementById('userModal'));
        this.form = document.getElementById('userForm');
        this.initializeEventListeners();

        // Exposer la fonction globale pour ouvrir le modal
        window.openAddUserModal = (type) => this.openAddUserModal(type);
    }

    initializeTable() {
        return new DataTable('#usersTable', {
            ajax: {
                url: '/admin/api/users/data',
                dataSrc: ''
            },
            columns: [
                { data: null, render: data => `${data.firstName} ${data.lastName}` },
                { data: 'email' },
                { data: 'username' },
                { data: 'roles', render: data => this.renderRoles(data) },
                { data: 'classe', render: data => data ? data.name : 'N/A' },
                { data: 'enabled', render: data => this.renderStatus(data) },
                { data: null, render: data => this.renderActions(data) }
            ],
            language: { url: '/js/datatables/fr-FR.json' },
            responsive: true,
            order: [[0, 'asc']]
        });
    }

    initializeEventListeners() {
        this.form.addEventListener('submit', e => this.handleSubmit(e));

        // Gestion de l'affichage des sections spécifiques selon les rôles
        const roleCheckboxes = document.querySelectorAll('input[name="roles"]');
        roleCheckboxes.forEach(checkbox => {
            checkbox.addEventListener('change', () => this.toggleRoleFields());
        });
    }

    renderRoles(roles) {
        return roles.map(role => `<span class="badge bg-primary">${role.replace('ROLE_', '')}</span>`).join(' ');
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
                .map(cb => cb.value)
        };

        // Si l'utilisateur est un étudiant, ajouter classeId
        if (data.roles.includes('ROLE_STUDENT')) {
            data.classeId = formData.get('classe');
        }
        // Pour un professeur, ajouter l'employmentType et la multi-sélection de subjects
        if (data.roles.includes('ROLE_PROFESSOR')) {
            data.employmentType = formData.get('employmentType');
            data.subjectIds = Array.from(document.getElementById('subjects').selectedOptions)
                .map(opt => opt.value);
        }

        try {
            const response = await fetch(data.id ? `/admin/api/users/${data.id}` : '/admin/api/users', {
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
            const response = await fetch(`/admin/api/users/${id}`);
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
            const response = await fetch(`/admin/api/users/${id}`, {
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
        form.elements['password'].value = ''; // Ne pas pré-remplir pour la sécurité

        // Gestion des rôles
        document.querySelectorAll('input[name="roles"]').forEach(cb => {
            cb.checked = user.roles.includes(cb.value);
        });

        // Afficher les sections spécifiques selon les rôles
        this.toggleRoleFields();

        // Si l'utilisateur a une classe, l'afficher
        if (user.classe) {
            form.elements['classe'].value = user.classe.id;
        }

        // Pour les professeurs, remplir employmentType et les subjects
        if (user.roles.includes('ROLE_PROFESSOR') && user.employmentType) {
            form.elements['employmentType'].value = user.employmentType;
        }
        if (user.roles.includes('ROLE_PROFESSOR') && user.subjects) {
            const subjectsSelect = document.getElementById('subjects');
            // Réinitialiser la sélection
            Array.from(subjectsSelect.options).forEach(option => option.selected = false);
            user.subjects.forEach(subject => {
                let option = subjectsSelect.querySelector(`option[value="${subject.id}"]`);
                if (option) {
                    option.selected = true;
                }
            });
        }
    }

    toggleRoleFields() {
        // Masquer toutes les sections spécifiques
        document.getElementById('studentFields').style.display = 'none';
        document.getElementById('professorFields').style.display = 'none';
        // Afficher la section "studentFields" si le rôle étudiant est sélectionné
        const selectedRoles = Array.from(document.querySelectorAll('input[name="roles"]:checked')).map(cb => cb.value);
        if (selectedRoles.includes('ROLE_STUDENT')) {
            document.getElementById('studentFields').style.display = 'block';
        }
        if (selectedRoles.includes('ROLE_PROFESSOR')) {
            document.getElementById('professorFields').style.display = 'block';
        }
    }

    showNotification(message, type) {
        alert(message);
    }

    openAddUserModal(type) {
        this.form.reset();
        document.getElementById('userId').value = '';

        // Réinitialiser les sections spécifiques
        document.getElementById('studentFields').style.display = 'none';
        document.getElementById('professorFields').style.display = 'none';

        // Sélectionner le rôle approprié
        document.querySelectorAll('input[name="roles"]').forEach(cb => {
            cb.checked = cb.value === `ROLE_${type.toUpperCase()}`;
        });

        this.toggleRoleFields();
        this.modal.show();
    }
}

document.addEventListener('DOMContentLoaded', () => {
    window.userManagement = new UserManagement();
});
