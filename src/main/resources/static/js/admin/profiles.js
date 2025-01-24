class ProfilesPage {
    constructor() {
        this.initializeTables();
        this.initializeEventListeners();
    }

    initializeTables() {
        // Table des profils étudiants
        this.studentProfilesTable = $('#studentProfilesTable').DataTable({
            ajax: {
                url: '/admin/profiles/students/data',
                dataSrc: ''
            },
            columns: [
                {
                    data: null,
                    render: function(data) {
                        return `${data.student.lastName} ${data.student.firstName}`;
                    }
                },
                {
                    data: 'student.classe',
                    render: function(data) {
                        return data ? data.name : '';
                    }
                },
                {
                    data: 'birthDate',
                    render: function(data) {
                        return data ? new Date(data).toLocaleDateString('fr-FR') : '';
                    }
                },
                {
                    data: 'emergencyContact',
                    render: function(data) {
                        return data || '';
                    }
                },
                {
                    data: 'specialNeeds',
                    render: function(data) {
                        return data ? 
                            '<span class="badge bg-warning">Oui</span>' : 
                            '<span class="badge bg-success">Non</span>';
                    }
                },
                {
                    data: null,
                    render: function(data) {
                        return `
                            <div class="action-buttons">
                                <button class="icon-button edit" onclick="profilesPage.editStudentProfile(${data.id})">
                                    <i class="fas fa-edit"></i>
                                </button>
                            </div>`;
                    }
                }
            ],
            language: {
                url: '/js/datatables/fr-FR.json'
            }
        });

        // Table des profils parents
        this.parentProfilesTable = $('#parentProfilesTable').DataTable({
            ajax: {
                url: '/admin/profiles/parents/data',
                dataSrc: ''
            },
            columns: [
                {
                    data: null,
                    render: function(data) {
                        return `${data.parent.lastName} ${data.parent.firstName}`;
                    }
                },
                { data: 'profession' },
                { data: 'preferredContactMethod' },
                { data: 'preferredContactTime' },
                {
                    data: null,
                    render: function(data) {
                        return `
                            <div class="action-buttons">
                                <button class="icon-button edit" onclick="profilesPage.editParentProfile(${data.id})">
                                    <i class="fas fa-edit"></i>
                                </button>
                            </div>`;
                    }
                }
            ],
            language: {
                url: '/js/datatables/fr-FR.json'
            }
        });

        // Table des associations parent-élève
        this.associationsTable = $('#associationsTable').DataTable({
            ajax: {
                url: '/admin/profiles/associations/data',
                dataSrc: ''
            },
            columns: [
                {
                    data: 'parent',
                    render: function(data) {
                        return `${data.lastName} ${data.firstName}`;
                    }
                },
                {
                    data: 'student',
                    render: function(data) {
                        return `${data.lastName} ${data.firstName}`;
                    }
                },
                { data: 'relationship' },
                {
                    data: 'primaryContact',
                    render: function(data) {
                        return data ? 
                            '<span class="badge bg-success">Oui</span>' : 
                            '<span class="badge bg-secondary">Non</span>';
                    }
                },
                {
                    data: 'validated',
                    render: function(data) {
                        return data ? 
                            '<span class="badge bg-success">Validé</span>' : 
                            '<span class="badge bg-warning">En attente</span>';
                    }
                },
                {
                    data: null,
                    render: function(data) {
                        return `
                            <div class="action-buttons">
                                <button class="icon-button edit" onclick="profilesPage.editAssociation(${data.id})">
                                    <i class="fas fa-edit"></i>
                                </button>
                                ${!data.validated ? `
                                    <button class="icon-button validate" onclick="profilesPage.validateAssociation(${data.id})">
                                        <i class="fas fa-check"></i>
                                    </button>
                                ` : ''}
                                <button class="icon-button delete" onclick="profilesPage.deleteAssociation(${data.id})">
                                    <i class="fas fa-trash"></i>
                                </button>
                            </div>`;
                    }
                }
            ],
            language: {
                url: '/js/datatables/fr-FR.json'
            }
        });
    }

    initializeEventListeners() {
        // Gestion des allergies
        $('#hasAllergies').on('change', function() {
            $('#allergiesDetailsGroup').toggle(this.checked);
        });

        // Gestion des formulaires
        $('#studentProfileForm').on('submit', (e) => this.saveStudentProfile(e));
        $('#parentProfileForm').on('submit', (e) => this.saveParentProfile(e));
        $('#associationForm').on('submit', (e) => this.saveAssociation(e));

        // Gestion des filtres
        const filters = {
            'classFilter': this.studentProfilesTable,
            'specialNeedsFilter': this.studentProfilesTable,
            'contactMethodFilter': this.parentProfilesTable,
            'relationshipFilter': this.associationsTable,
            'validationStatusFilter': this.associationsTable
        };

        Object.entries(filters).forEach(([filterId, table]) => {
            $(`#${filterId}`).on('change', () => table.ajax.reload());
        });
    }

    async editStudentProfile(id) {
        try {
            const response = await fetch(`/admin/profiles/students/${id}`);
            if (!response.ok) throw new Error('Erreur lors du chargement du profil');
            
            const profile = await response.json();
            this.populateStudentProfileForm(profile);
            $('#studentProfileModal').modal('show');
        } catch (error) {
            console.error('Erreur:', error);
            this.showNotification('Erreur lors du chargement du profil', 'error');
        }
    }

    async editParentProfile(id) {
        try {
            const response = await fetch(`/admin/profiles/parents/${id}`);
            if (!response.ok) throw new Error('Erreur lors du chargement du profil');
            
            const profile = await response.json();
            this.populateParentProfileForm(profile);
            $('#parentProfileModal').modal('show');
        } catch (error) {
            console.error('Erreur:', error);
            this.showNotification('Erreur lors du chargement du profil', 'error');
        }
    }

    async editAssociation(id) {
        try {
            const response = await fetch(`/admin/profiles/associations/${id}`);
            if (!response.ok) throw new Error('Erreur lors du chargement de l\'association');
            
            const association = await response.json();
            this.populateAssociationForm(association);
            $('#associationModal').modal('show');
        } catch (error) {
            console.error('Erreur:', error);
            this.showNotification('Erreur lors du chargement de l\'association', 'error');
        }
    }

    async validateAssociation(id) {
        if (!confirm('Êtes-vous sûr de vouloir valider cette association ?')) return;
        
        try {
            const response = await fetch(`/admin/profiles/associations/${id}/validate`, {
                method: 'PUT',
                headers: {
                    'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                }
            });

            if (!response.ok) throw new Error('Erreur lors de la validation');
            
            this.associationsTable.ajax.reload();
            this.showNotification('Association validée avec succès', 'success');
        } catch (error) {
            console.error('Erreur:', error);
            this.showNotification('Erreur lors de la validation de l\'association', 'error');
        }
    }

    async deleteAssociation(id) {
        if (!confirm('Êtes-vous sûr de vouloir supprimer cette association ?')) return;
        
        try {
            const response = await fetch(`/admin/profiles/associations/${id}`, {
                method: 'DELETE',
                headers: {
                    'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                }
            });

            if (!response.ok) throw new Error('Erreur lors de la suppression');
            
            this.associationsTable.ajax.reload();
            this.showNotification('Association supprimée avec succès', 'success');
        } catch (error) {
            console.error('Erreur:', error);
            this.showNotification('Erreur lors de la suppression de l\'association', 'error');
        }
    }

    populateStudentProfileForm(profile) {
        const form = document.getElementById('studentProfileForm');
        form.reset();
        
        Object.entries(profile).forEach(([key, value]) => {
            const input = form.elements[key];
            if (input) {
                if (input.type === 'checkbox') {
                    input.checked = value;
                    if (key === 'hasAllergies') {
                        $('#allergiesDetailsGroup').toggle(value);
                    }
                } else if (key === 'birthDate') {
                    input.value = value ? value.split('T')[0] : '';
                } else {
                    input.value = value || '';
                }
            }
        });
    }

    populateParentProfileForm(profile) {
        const form = document.getElementById('parentProfileForm');
        form.reset();
        
        Object.entries(profile).forEach(([key, value]) => {
            const input = form.elements[key];
            if (input) {
                input.value = value || '';
            }
        });
    }

    populateAssociationForm(association) {
        const form = document.getElementById('associationForm');
        form.reset();
        
        Object.entries(association).forEach(([key, value]) => {
            const input = form.elements[key];
            if (input) {
                if (input.type === 'checkbox') {
                    input.checked = value;
                } else {
                    input.value = value || '';
                }
            }
        });
    }

    async saveStudentProfile(e) {
        e.preventDefault();
        const form = e.target;
        const formData = new FormData(form);
        const data = Object.fromEntries(formData.entries());

        try {
            const response = await fetch(
                data.id ? `/admin/profiles/students/${data.id}` : '/admin/profiles/students',
                {
                    method: data.id ? 'PUT' : 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                    },
                    body: JSON.stringify(data)
                }
            );

            if (!response.ok) {
                const error = await response.json();
                throw new Error(error.message || 'Erreur lors de la sauvegarde');
            }

            $('#studentProfileModal').modal('hide');
            this.studentProfilesTable.ajax.reload();
            this.showNotification('Profil étudiant sauvegardé avec succès', 'success');
        } catch (error) {
            console.error('Erreur:', error);
            this.showNotification(error.message, 'error');
        }
    }

    async saveParentProfile(e) {
        e.preventDefault();
        const form = e.target;
        const formData = new FormData(form);
        const data = Object.fromEntries(formData.entries());

        try {
            const response = await fetch(
                data.id ? `/admin/profiles/parents/${data.id}` : '/admin/profiles/parents',
                {
                    method: data.id ? 'PUT' : 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                    },
                    body: JSON.stringify(data)
                }
            );

            if (!response.ok) {
                const error = await response.json();
                throw new Error(error.message || 'Erreur lors de la sauvegarde');
            }

            $('#parentProfileModal').modal('hide');
            this.parentProfilesTable.ajax.reload();
            this.showNotification('Profil parent sauvegardé avec succès', 'success');
        } catch (error) {
            console.error('Erreur:', error);
            this.showNotification(error.message, 'error');
        }
    }

    async saveAssociation(e) {
        e.preventDefault();
        const form = e.target;
        const formData = new FormData(form);
        const data = Object.fromEntries(formData.entries());

        try {
            const response = await fetch(
                data.id ? `/admin/profiles/associations/${data.id}` : '/admin/profiles/associations',
                {
                    method: data.id ? 'PUT' : 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                    },
                    body: JSON.stringify(data)
                }
            );

            if (!response.ok) {
                const error = await response.json();
                throw new Error(error.message || 'Erreur lors de la sauvegarde');
            }

            $('#associationModal').modal('hide');
            this.associationsTable.ajax.reload();
            this.showNotification('Association sauvegardée avec succès', 'success');
        } catch (error) {
            console.error('Erreur:', error);
            this.showNotification(error.message, 'error');
        }
    }

    showNotification(message, type = 'info') {
        // Implémentation de la notification (à adapter selon votre système)
        alert(message);
    }
}

// Initialisation
let profilesPage;
document.addEventListener('DOMContentLoaded', () => {
    profilesPage = new ProfilesPage();
});