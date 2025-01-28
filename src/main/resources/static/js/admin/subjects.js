// Configuration spécifique pour la page des matières
class SubjectsPage extends AdminPage {
    constructor() {
        super({
            tableId: 'subjectsTable',
            modalId: 'subjectModal',
            formId: 'subjectForm',
            apiEndpoint: '/admin/subjects',
            columns: [
                { data: 'code' },
                { data: 'name' },
                { data: 'description' },
                { data: 'coefficient' },
                { 
                    data: 'optional',
                    render: function(data) {
                        return data ? 
                            '<span class="badge bg-info">Optionnelle</span>' : 
                            '<span class="badge bg-primary">Obligatoire</span>';
                    }
                },
                { 
                    data: 'levels',
                    render: function(data) {
                        return data ? data.map(level => 
                            `<span class="badge bg-secondary">${level.name}</span>`
                        ).join(' ') : '';
                    }
                },
                {
                    data: null,
                    render: function(data) {
                        return `
                            <div class="action-buttons">
                                <button class="btn btn-sm btn-primary" onclick="window.subjectsPage.edit(${data.id})">
                                    <i class="fas fa-edit"></i>
                                </button>
                                <button class="btn btn-sm btn-danger" onclick="window.subjectsPage.delete(${data.id})">
                                    <i class="fas fa-trash"></i>
                                </button>
                            </div>`;
                    }
                }
            ]
        });
        
        this.initializeSelect2();
    }

    initializeSelect2() {
        $('#levels').select2({
            theme: 'bootstrap-5',
            width: '100%',
            placeholder: 'Sélectionner les niveaux',
            allowClear: true
        });
    }

    // Surcharge de la méthode populateForm pour gérer les niveaux
    populateForm(data) {
        super.populateForm(data);

        // Mise à jour des niveaux sélectionnés
        if (data.levels) {
            const levelNames = data.levels.map(level => level.levelName);
            $('#levels').val(levelNames).trigger('change');
        }
    }

    // Surcharge de la méthode save pour inclure les niveaux
    async save() {
        const form = document.getElementById(this.formId);
        if (!form) return;

        const formData = new FormData(form);
        const data = Object.fromEntries(formData.entries());

        // Conversion des valeurs numériques
        if (data.coefficient) {
            data.coefficient = parseFloat(data.coefficient);
        }

        // Conversion des booléens
        data.optional = !!data.optional;

        // Récupérer les niveaux sélectionnés
        const selectedLevels = Array.from($('#levels').select2('data'))
            .map(item => ({ levelName: item.id }));
        data.levels = selectedLevels;

        try {
            const response = await fetch(
                data.id ? `${this.apiEndpoint}/${data.id}` : this.apiEndpoint,
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

            this.closeModal();
            this.table.ajax.reload();
        } catch (error) {
            console.error('Erreur:', error);
            alert('Une erreur est survenue lors de la sauvegarde: ' + error.message);
        }
    }
}

// Initialisation de la page
document.addEventListener('DOMContentLoaded', function() {
    window.subjectsPage = new SubjectsPage();
});