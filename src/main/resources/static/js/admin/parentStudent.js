// Configuration spécifique pour la page des associations Parent–Élève
class ParentStudentPage extends AdminPage {
    constructor() {
        super({
            tableId: 'associationTable',
            modalId: 'associationModal',
            formId: 'associationForm',
            // L'endpoint API pour la création/mise à jour des associations
            apiEndpoint: '/admin/api/parentStudent/associations',
            columns: [
                { data: 'studentName' },
                { data: 'className' },
                { data: 'parentName' },
                { data: 'relationship' },
                {
                    data: 'primaryContact',
                    render: function(data) {
                        return data
                            ? '<i class="fas fa-check text-success"></i>'
                            : '<i class="fas fa-times text-danger"></i>';
                    }
                },
                { data: 'createdAt' },
                {
                    data: "id",
                    render: function(data, type, row) {
                        return `
                            <div class="action-buttons">
                                <button class="btn btn-sm btn-primary" onclick="window.parentStudentPage.edit(${data})">
                                    <i class="fas fa-edit"></i>
                                </button>
                                <button class="btn btn-sm btn-danger" onclick="window.parentStudentPage.delete(${data})">
                                    <i class="fas fa-trash"></i>
                                </button>
                            </div>`;
                    }
                }
            ]
        });

        // Initialisation du filtre par classe
        this.initClassFilterHandler();
    }

    // Méthode pour gérer le filtrage par classe via l'élément select "classFilter"
    initClassFilterHandler() {
        const classFilter = document.getElementById('classFilter');
        if (classFilter) {
            classFilter.addEventListener('change', () => {
                this.table.ajax.reload();
            });
        }
    }

    // Surcharge de la méthode save pour gérer la soumission du formulaire du modal
    async save() {
        const form = document.getElementById(this.formId);
        if (!form) return;

        const formData = new FormData(form);
        const data = Object.fromEntries(formData.entries());

        // Conversion de la case à cocher en booléen
        data.primaryContact = data.primaryContact === 'on' ? true : false;

        try {
            const csrfToken = document.querySelector('meta[name="_csrf"]').content;
            const response = await fetch(
                data.id ? `${this.apiEndpoint}/${data.id}` : this.apiEndpoint,
                {
                    method: data.id ? 'PUT' : 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'X-CSRF-TOKEN': csrfToken
                    },
                    body: JSON.stringify(data)
                }
            );
            if (!response.ok) {
                const error = await response.json();
                throw new Error(error.message || 'Error saving association');
            }
            this.closeModal();
            this.table.ajax.reload();
        } catch (error) {
            console.error('Erreur:', error);
            alert('Une erreur est survenue lors de la sauvegarde : ' + error.message);
        }
    }

    // Méthode pour ouvrir le modal en mode modification et charger les données de l'association sélectionnée
    async edit(id) {
        try {
            const response = await fetch(`/admin/api/parentStudent/association/${id}`);
            if (!response.ok) throw new Error('Erreur lors de la récupération des données');
            const data = await response.json();
            // Remplir le formulaire du modal avec les données récupérées
            document.getElementById('associationId').value = data.id;
            document.getElementById('studentSelect').value = data.studentId;
            document.getElementById('parentSelect').value = data.parentId;
            document.getElementById('relationship').value = data.relationship;
            document.getElementById('primaryContact').checked = data.primaryContact;
            // Ouvrir le modal
            const modal = new bootstrap.Modal(document.getElementById(this.modalId));
            modal.show();
        } catch (error) {
            console.error('Erreur:', error);
            alert('Erreur lors de la récupération des données : ' + error.message);
        }
    }

    // Méthode pour supprimer une association
    async delete(id) {
        if (confirm("Êtes-vous sûr de vouloir supprimer cette association ?")) {
            try {
                const csrfToken = document.querySelector('meta[name="_csrf"]').content;
                const response = await fetch(`${this.apiEndpoint}/${id}`, {
                    method: "DELETE",
                    headers: {
                        "X-CSRF-TOKEN": csrfToken
                    }
                });
                if (!response.ok) throw new Error('Erreur lors de la suppression');
                this.table.ajax.reload();
            } catch (error) {
                console.error('Erreur:', error);
                alert('Erreur lors de la suppression : ' + error.message);
            }
        }
    }
    openNewAttendanceModal() {
        // Charger les étudiants et les parents depuis l'API
        //this.loadStudents();
        //this.loadParents();

        const modal = new bootstrap.Modal(document.getElementById(this.modalId));
        modal.show();
    }

    async loadStudents() {
        try {
            const response = await fetch("/admin/api/students");
            if (!response.ok) throw new Error("Erreur lors du chargement des étudiants");
            const students = await response.json();
            let options = `<option value="" disabled selected>Choisissez un élève</option>`;
            students.forEach(student => {
                options += `<option value="${student.id}">${student.firstName} ${student.lastName}</option>`;
            });
            document.getElementById("studentSelect").innerHTML = options;
        } catch (error) {
            console.error("Erreur:", error);
        }
    }

    async loadParents() {
        try {
            const response = await fetch("/admin/api/parents");
            if (!response.ok) throw new Error("Erreur lors du chargement des parents");
            const parents = await response.json();
            let options = `<option value="" disabled selected>Choisissez un parent</option>`;
            parents.forEach(parent => {
                options += `<option value="${parent.id}">${parent.firstName} ${parent.lastName}</option>`;
            });
            document.getElementById("parentSelect").innerHTML = options;
        } catch (error) {
            console.error("Erreur:", error);
        }
    }
}

// Initialisation de la page lorsque le DOM est prêt
document.addEventListener('DOMContentLoaded', function() {
    window.parentStudentPage = new ParentStudentPage();
});
