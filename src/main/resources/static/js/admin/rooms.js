class RoomsPage extends AdminPage {
    constructor() {
        super({
            tableId: 'roomsTable',
            modalId: 'roomModal',
            formId: 'roomForm',
            apiEndpoint: '/admin/rooms',
            columns: [
                { 
                    data: 'name',
                    render: function(data) {
                        return data || '';
                    }
                },
                { 
                    data: 'buildingName',
                    render: function(data) {
                        return data || '';
                    }
                },
                { 
                    data: 'floorNumber',
                    render: function(data) {
                        return data !== null ? `${data}${data === 0 ? ' (RDC)' : 'ème'}` : '';
                    }
                },
                { 
                    data: 'maxCapacity',
                    render: function(data) {
                        return data || '0';
                    }
                },
                {
                    data: 'equipment',
                    render: function(data) {
                        if (!data || !data.length) return '';
                        return data.map(eq => `
                            <span class="badge bg-info">${eq}</span>
                        `).join(' ');
                    }
                },
                {
                    data: 'status',
                    render: function(data) {
                        const statusClasses = {
                            'AVAILABLE': 'success',
                            'OCCUPIED': 'warning',
                            'MAINTENANCE': 'danger'
                        };
                        const statusLabels = {
                            'AVAILABLE': 'Disponible',
                            'OCCUPIED': 'Occupée',
                            'MAINTENANCE': 'Maintenance'
                        };
                        return `<span class="badge bg-${statusClasses[data]}">${statusLabels[data]}</span>`;
                    }
                },
                {
                    data: null,
                    render: function(data) {
                        return `
                            <div class="action-buttons">
                                <button class="icon-button edit" onclick="roomsPage.edit(${data.id})">
                                    <i class="fas fa-edit"></i>
                                </button>
                                <button class="icon-button delete" onclick="roomsPage.delete(${data.id})">
                                    <i class="fas fa-trash"></i>
                                </button>
                            </div>`;
                    }
                }
            ]
        });
        
        this.initializeFilters();
        this.initializeSelect2();
    }

    initializeFilters() {
        const filters = ['building', 'floor', 'status'];
        filters.forEach(filter => {
            $(`#${filter}Filter`).on('change', () => this.table.ajax.reload());
        });
    }

    initializeSelect2() {
        $('#equipment').select2({
            theme: 'bootstrap-5',
            width: '100%',
            placeholder: 'Sélectionner les équipements',
            allowClear: true
        });
    }

    populateForm(data) {
        super.populateForm(data);
        
        // Mise à jour des équipements
        if (data.equipment) {
            $('#equipment').val(data.equipment).trigger('change');
        }
    }

    async save() {
        const form = document.getElementById(this.formId);
        if (!form) return;

        const formData = new FormData(form);
        const data = Object.fromEntries(formData.entries());

        // Conversion des valeurs numériques
        if (data.maxCapacity) {
            data.maxCapacity = parseInt(data.maxCapacity, 10);
        }
        if (data.floorNumber) {
            data.floorNumber = parseInt(data.floorNumber, 10);
        }

        // Conversion des booléens
        data.accessible = !!data.accessible;

        // Récupération des équipements
        data.equipment = $('#equipment').val() || [];

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
            this.showNotification('Salle sauvegardée avec succès', 'success');
        } catch (error) {
            console.error('Erreur:', error);
            this.showNotification(error.message, 'error');
        }
    }
}

// Initialisation
let roomsPage;
document.addEventListener('DOMContentLoaded', () => {
    roomsPage = new RoomsPage();
});