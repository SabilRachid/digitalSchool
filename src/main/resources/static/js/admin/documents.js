class DocumentsPage extends AdminPage {
    constructor() {
        super({
            tableId: 'documentsTable',
            modalId: 'documentModal',
            formId: 'documentForm',
            apiEndpoint: '/admin/api/documents',
            columns: [
                { 
                    data: 'name',
                    render: function(data) {
                        return data || '';
                    }
                },
                { 
                    data: 'type',
                    render: function(data) {
                        const types = {
                            'ADMINISTRATIVE': 'Administratif',
                            'MEDICAL': 'Médical',
                            'ACADEMIC': 'Académique',
                            'OTHER': 'Autre'
                        };
                        return types[data] || data;
                    }
                },
                { 
                    data: 'category',
                    render: function(data) {
                        const categories = {
                            'INSCRIPTION': 'Inscription',
                            'CERTIFICATE': 'Certificat',
                            'REPORT': 'Bulletin',
                            'OTHER': 'Autre'
                        };
                        return categories[data] || data;
                    }
                },
                {
                    data: 'uploadedBy',
                    render: function(data) {
                        return data ? `${data.firstName} ${data.lastName}` : '';
                    }
                },
                {
                    data: 'uploadedAt',
                    render: function(data) {
                        return data ? new Date(data).toLocaleDateString('fr-FR') : '';
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
                                <button class="icon-button view" onclick="documentsPage.viewDocument(${data.id})">
                                    <i class="fas fa-eye"></i>
                                </button>
                                ${!data.validated ? `
                                    <button class="icon-button validate" onclick="documentsPage.validateDocument(${data.id})">
                                        <i class="fas fa-check"></i>
                                    </button>
                                ` : ''}
                                <button class="icon-button delete" onclick="documentsPage.deleteDocument(${data.id})">
                                    <i class="fas fa-trash"></i>
                                </button>
                            </div>`;
                    }
                }
            ]
        });
        
        this.initializeFilters();
        this.loadUsers();
    }

    initializeFilters() {
        const filters = ['type', 'category', 'validation'];
        filters.forEach(filter => {
            $(`#${filter}Filter`).on('change', () => this.table.ajax.reload());
        });
    }

    async loadUsers() {
        try {
            // Charger les étudiants
            const studentsResponse = await fetch('/admin/api/users/students');
            const students = await studentsResponse.json();
            const studentSelect = document.getElementById('student');
            students.forEach(student => {
                const option = new Option(
                    `${student.lastName} ${student.firstName}`,
                    student.id
                );
                studentSelect.add(option);
            });

            // Charger les parents
            const parentsResponse = await fetch('/admin/api/users/parents');
            const parents = await parentsResponse.json();
            const parentSelect = document.getElementById('parent');
            parents.forEach(parent => {
                const option = new Option(
                    `${parent.lastName} ${parent.firstName}`,
                    parent.id
                );
                parentSelect.add(option);
            });
        } catch (error) {
            console.error('Erreur lors du chargement des utilisateurs:', error);
        }
    }

    async save() {
        const form = document.getElementById(this.formId);
        if (!form) return;

        const formData = new FormData(form);
        
        try {
            const response = await fetch(this.apiEndpoint + '/upload', {
                method: 'POST',
                headers: {
                    'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                },
                body: formData
            });

            if (!response.ok) {
                const error = await response.json();
                throw new Error(error.message || 'Erreur lors de l\'upload');
            }

            this.closeModal();
            this.table.ajax.reload();
            this.showNotification('Document uploadé avec succès', 'success');
        } catch (error) {
            console.error('Erreur:', error);
            this.showNotification(error.message, 'error');
        }
    }

    async viewDocument(id) {
        try {
            const response = await fetch(`${this.apiEndpoint}/download/${id}`);
            if (!response.ok) throw new Error('Erreur lors du téléchargement');
            
            // Créer un lien temporaire pour le téléchargement
            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = response.headers.get('Content-Disposition')
                ?.split('filename=')[1]
                ?.replace(/"/g, '') || 'document';
            document.body.appendChild(a);
            a.click();
            window.URL.revokeObjectURL(url);
            document.body.removeChild(a);
        } catch (error) {
            console.error('Erreur:', error);
            this.showNotification('Erreur lors du téléchargement du document', 'error');
        }
    }

    async validateDocument(id) {
        if (!confirm('Êtes-vous sûr de vouloir valider ce document ?')) return;
        
        try {
            const response = await fetch(`${this.apiEndpoint}/${id}/validate`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                }
            });

            if (!response.ok) throw new Error('Erreur lors de la validation');
            
            this.table.ajax.reload();
            this.showNotification('Document validé avec succès', 'success');
        } catch (error) {
            console.error('Erreur:', error);
            this.showNotification('Erreur lors de la validation du document', 'error');
        }
    }

    async deleteDocument(id) {
        if (!confirm('Êtes-vous sûr de vouloir supprimer ce document ?')) return;
        
        try {
            const response = await fetch(`${this.apiEndpoint}/${id}`, {
                method: 'DELETE',
                headers: {
                    'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                }
            });

            if (!response.ok) throw new Error('Erreur lors de la suppression');
            
            this.table.ajax.reload();
            this.showNotification('Document supprimé avec succès', 'success');
        } catch (error) {
            console.error('Erreur:', error);
            this.showNotification('Erreur lors de la suppression du document', 'error');
        }
    }
}

// Initialisation
let documentsPage;
document.addEventListener('DOMContentLoaded', () => {
    documentsPage = new DocumentsPage();
});