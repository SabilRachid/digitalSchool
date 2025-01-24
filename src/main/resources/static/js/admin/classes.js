class ClassesPage extends AdminPage {
    constructor() {
        super({
            tableId: 'classesTable',
            modalId: 'classModal',
            formId: 'classForm',
            apiEndpoint: '/admin/classes',
            columns: [
                { 
                    data: 'name',
                    render: function(data) {
                        return data || '';
                    }
                },
                { 
                    data: 'level',
                    render: function(data) {
                        return data ? data.name : '';
                    }
                },
                { 
                    data: 'maxStudents',
                    render: function(data) {
                        return data || '0';
                    }
                },
                { 
                    data: 'schoolYear',
                    render: function(data) {
                        return data || '';
                    }
                },
                {
                    data: null,
                    render: function(data) {
                        return `
                            <div class="action-buttons">
                                <button class="icon-button edit" onclick="classesPage.edit(${data.id})">
                                    <i class="fas fa-edit"></i>
                                </button>
                                <button class="icon-button delete" onclick="classesPage.delete(${data.id})">
                                    <i class="fas fa-trash"></i>
                                </button>
                            </div>`;
                    }
                }
            ]
        });
        
        // Chargement des données complémentaires
        this.loadLevels();
    }

    async loadLevels() {
        try {
            const response = await fetch('/admin/levels/list');
            if (!response.ok) throw new Error('Erreur lors du chargement des niveaux');
            
            const levels = await response.json();
            const select = document.getElementById('level');
            if (select) {
                select.innerHTML = `
                    <option value="">Sélectionner un niveau</option>
                    ${levels.map(level => 
                        `<option value="${level.id}">${level.name}</option>`
                    ).join('')}
                `;
            }
        } catch (error) {
            console.error('Erreur:', error);
        }
    }

    // Surcharge de la méthode save pour gérer les références d'objets
    async save() {
        const form = document.getElementById(this.formId);
        if (!form) return;

        const formData = new FormData(form);
        const data = Object.fromEntries(formData.entries());

        // Conversion des valeurs numériques
        if (data.maxStudents) {
            data.maxStudents = parseInt(data.maxStudents, 10);
        }

        // Ajout des références d'objets
        if (data.level) {
            data.level = { 
                id: parseInt(data.level, 10)
            };
        }

        try {
            const csrfToken = document.querySelector('meta[name="_csrf"]');
            if (!csrfToken) {
                throw new Error('Token CSRF non trouvé');
            }

            const response = await fetch(
                data.id ? `${this.apiEndpoint}/${data.id}` : this.apiEndpoint,
                {
                    method: data.id ? 'PUT' : 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'X-CSRF-TOKEN': csrfToken.content
                    },
                    body: JSON.stringify(data)
                }
            );

            if (!response.ok) {
                const error = await response.json();
                throw new Error(error.message || 'Erreur lors de la sauvegarde');
            }

            this.closeModal();
            this.table.ajax.reload();
            this.showNotification('Opération réussie', 'success');
        } catch (error) {
            console.error('Erreur:', error);
            this.showNotification(error.message, 'error');
        }
    }
}

// Initialisation de la page
window.classesPage = null;
document.addEventListener('DOMContentLoaded', function() {
    window.classesPage = new ClassesPage();
});