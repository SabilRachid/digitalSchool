// Configuration spécifique pour la page des niveaux
class LevelsPage extends AdminPage {
    constructor() {
        super({
            tableId: 'levelsTable',
            modalId: 'levelModal',
            formId: 'levelForm',
            apiEndpoint: '/admin/api/levels',
            columns: [
                { data: 'code' },
                { data: 'name' },
                { data: 'cycle' },
                { data: 'order' },
                {
                    data: null,
                    render: function(data) {
                        return `
                            <div class="action-buttons">
                                <button class="btn btn-sm btn-primary" onclick="window.levelsPage.edit(${data.id})">
                                    <i class="fas fa-edit"></i>
                                </button>
                                <button class="btn btn-sm btn-danger" onclick="window.levelsPage.delete(${data.id})">
                                    <i class="fas fa-trash"></i>
                                </button>
                            </div>`;
                    }
                }
            ]
        });
        
        this.initCycleChangeHandler();
    }

    initCycleChangeHandler() {
        const cycleSelect = document.getElementById('cycle');
        const orderInput = document.getElementById('order');

        cycleSelect.addEventListener('change', async () => {
            const cycle = cycleSelect.value;
            if (cycle) {
                try {
                    const response = await fetch(`/admin/api/levels/maxOrder?cycle=${cycle}`);
                    if (!response.ok) throw new Error('Erreur lors de la récupération de l\'ordre maximum');
                    
                    const maxOrder = await response.json();
                    orderInput.value = maxOrder + 1;
                } catch (error) {
                    console.error('Erreur:', error);
                }
            }
        });
    }

    // Surcharge de la méthode save
    async save() {
        const form = document.getElementById(this.formId);
        if (!form) return;

        const formData = new FormData(form);
        const data = Object.fromEntries(formData.entries());

        // Conversion des valeurs numériques
        if (data.order) {
            data.order = parseInt(data.order, 10);
        }

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
    window.levelsPage = new LevelsPage();
});