class AdminPage {
    constructor(options) {
        this.tableId = options.tableId;
        this.modalId = options.modalId;
        this.formId = options.formId;
        this.apiEndpoint = options.apiEndpoint;
        this.columns = options.columns;
        
        this.table = null;
        this.modal = null;
        
        this.init();
    }

    init() {
        this.initDataTable();
        this.initModal();
        this.initForm();
    }

    initDataTable() {
        // Vérifier si la table existe déjà
        if ($.fn.DataTable.isDataTable(`#${this.tableId}`)) {
            $(`#${this.tableId}`).DataTable().destroy();
        }

        this.table = $(`#${this.tableId}`).DataTable({
            ajax: {
                url: `${this.apiEndpoint}/data`,
                dataSrc: ''
            },
            columns: this.columns,
            language: {
                url: '/js/datatables/fr-FR.json'
            },
            responsive: true,
            dom: 'Bfrtip',
            buttons: ['copy', 'excel', 'pdf', 'print']
        });
    }

    initModal() {
        this.modal = new bootstrap.Modal(document.getElementById(this.modalId));
    }

    initForm() {
        const form = document.getElementById(this.formId);
        if (form) {
            form.addEventListener('submit', (e) => {
                e.preventDefault();
                this.save();
            });
        }
    }

    openModal(id = null) {
        const form = document.getElementById(this.formId);
        if (!form) return;

        form.reset();
        
        const modalTitle = document.querySelector(`#${this.modalId} .modal-title`);
        if (id) {
            modalTitle.textContent = 'Modifier';
            this.loadData(id);
        } else {
            modalTitle.textContent = 'Ajouter';
            document.getElementById('id').value = '';
        }
        
        this.modal.show();
    }

    closeModal() {
        this.modal.hide();
    }

    async loadData(id) {
        try {
            const response = await fetch(`${this.apiEndpoint}/${id}`);
            if (!response.ok) throw new Error('Erreur lors du chargement des données');
            
            const data = await response.json();
            this.populateForm(data);
        } catch (error) {
            console.error('Erreur:', error);
            alert('Erreur lors du chargement des données');
        }
    }

    populateForm(data) {
        const form = document.getElementById(this.formId);
        if (!form) return;

        Object.keys(data).forEach(key => {
            const input = form.elements[key];
            if (input) {
                if (input.type === 'checkbox') {
                    input.checked = data[key];
                } else if (input.type === 'select-one' || input.type === 'select-multiple') {
                    if (data[key] && typeof data[key] === 'object') {
                        input.value = data[key].id;
                    } else {
                        input.value = data[key];
                    }
                } else {
                    input.value = data[key];
                }
            }
        });
    }

    async save() {
        const form = document.getElementById(this.formId);
        if (!form) return;

        const formData = new FormData(form);
        const data = Object.fromEntries(formData.entries());

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

    async delete(id) {
        if (!confirm('Êtes-vous sûr de vouloir supprimer cet élément ?')) return;
        
        try {
            const csrfToken = document.querySelector('meta[name="_csrf"]');
            if (!csrfToken) {
                throw new Error('Token CSRF non trouvé');
            }

            const response = await fetch(`${this.apiEndpoint}/${id}`, {
                method: 'DELETE',
                headers: {
                    'X-CSRF-TOKEN': csrfToken.content
                }
            });

            if (!response.ok) throw new Error('Erreur lors de la suppression');

            this.table.ajax.reload();
            this.showNotification('Élément supprimé avec succès', 'success');
        } catch (error) {
            console.error('Erreur:', error);
            this.showNotification('Erreur lors de la suppression', 'error');
        }
    }

    edit(id) {
        this.openModal(id);
    }

    showNotification(message, type = 'info') {
        alert(message);
    }

}

