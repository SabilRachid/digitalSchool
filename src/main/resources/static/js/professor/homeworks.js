class HomeworksPage extends AdminPage {
    constructor() {
        super({
            tableId: 'homeworksTable',
            modalId: 'homeworkModal',
            formId: 'homeworkForm',
            apiEndpoint: '/professor/api/homeworks',
            columns: [
                { data: 'title' },
                { data: 'course.name' },
                { data: 'dueDate', render: (data) => new Date(data).toLocaleDateString() },
                { data: 'status', render: (data) => `<span class="badge ${this.getStatusClass(data)}">${data}</span>` },
                {
                    data: null,
                    render: function (data) {
                        return `
                            <div class="action-buttons">
                                <button class="btn btn-sm btn-primary" onclick="window.homeworksPage.edit(${data.id})">
                                    <i class="fas fa-edit"></i>
                                </button>
                                <button class="btn btn-sm btn-danger" onclick="window.homeworksPage.delete(${data.id})">
                                    <i class="fas fa-trash"></i>
                                </button>
                            </div>`;
                    }
                }
            ]
        });

        this.initFilters();
    }

    getStatusClass(status) {
        switch (status) {
            case "PENDING": return "badge-warning";
            case "SUBMITTED": return "badge-info";
            case "GRADED": return "badge-success";
            default: return "badge-secondary";
        }
    }

    initFilters() {
        document.getElementById('courseFilter').addEventListener('change', () => this.table.ajax.reload());
        document.getElementById('statusFilter').addEventListener('change', () => this.table.ajax.reload());

        $.fn.dataTable.ext.search.push((settings, data, dataIndex) => {
            const courseFilter = document.getElementById('courseFilter').value;
            const statusFilter = document.getElementById('statusFilter').value;
            const course = data[1]; // Nom du cours
            const status = data[3]; // Statut

            return (!courseFilter || course === courseFilter) && (!statusFilter || status.includes(statusFilter));
        });
    }

    async save() {
        const form = document.getElementById(this.formId);
        if (!form) return;

        const formData = new FormData(form);
        const data = Object.fromEntries(formData.entries());

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
                throw new Error('Erreur lors de la sauvegarde');
            }

            this.closeModal();
            this.table.ajax.reload();
        } catch (error) {
            console.error('Erreur:', error);
            alert('Une erreur est survenue: ' + error.message);
        }
    }
}

// Initialisation de la page
document.addEventListener('DOMContentLoaded', function () {
    window.homeworksPage = new HomeworksPage();
});
