// Configuration spécifique pour la page des associations parent-élève
class ParentStudentPage extends AdminPage {
    constructor() {
        super({
            tableId: 'associationTable',
            modalId: 'associationModal',
            formId: 'associationForm',
            apiEndpoint: '/admin/api/parentStudent/association',
            columns: [
                { data: 'studentName', title: "Élève" },
                { data: 'className', title: "Classe" },
                { data: 'parentName', title: "Parent" },
                { data: 'relationship', title: "Relation" },
                {
                    data: 'primaryContact', title: "Contact Principal",
                    render: function(data) {
                        return data
                            ? '<i class="fas fa-check text-success"></i>'
                            : '<i class="fas fa-times text-danger"></i>';
                    }
                },
                { data: 'createdAt', title: "Date de Création" },
                {
                    data: null,
                    render: function(data) {
                        return `
                            <button class="btn btn-sm btn-primary" onclick="window.parentStudentPage.edit(${data.id})">
                                <i class="fas fa-edit"></i> Modifier
                            </button>
                            <button class="btn btn-sm btn-danger" onclick="window.parentStudentPage.delete(${data.id})">
                                <i class="fas fa-trash"></i> Supprimer
                            </button>
                        `;
                    }
                }
            ]

        });

        console.log("📌 Initialisation de ParentStudentPage...");
        this.initClassFilterHandler();
    }

    // Gérer le filtrage par classe
    initClassFilterHandler() {
        const classFilter = document.getElementById('classFilter');
        if (classFilter) {
            classFilter.addEventListener('change', () => {
                console.log("🔄 Filtrage appliqué : Classe ID =", classFilter.value);
                const newUrl = `/admin/api/parentStudent/association/data?classId=${classFilter.value}`;
                console.log("📌 Nouvelle URL DataTables :", newUrl);
                this.table.ajax.url(newUrl).load();
            });
        }
    }


    // Méthode pour recharger les données
    reloadDataTable() {
        console.log("🔄 Rechargement manuel des données DataTable...");
        this.table.ajax.reload();
    }

    // Méthode pour soumettre le formulaire
    async save() {
        const form = document.getElementById(this.formId);
        if (!form) return;

        const formData = new FormData(form);
        const data = Object.fromEntries(formData.entries());

        // Conversion des cases à cocher
        data.primaryContact = data.primaryContact === 'on';

        console.log("📌 Données du formulaire avant envoi :", data);

        try {
            const csrfToken = document.querySelector('meta[name="_csrf"]').content;
            const response = await fetch(
                data.associationId ? `${this.apiEndpoint}/${data.associationId}` : this.apiEndpoint,
                {
                    method: data.associationId ? 'PUT' : 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'X-CSRF-TOKEN': csrfToken
                    },
                    body: JSON.stringify(data)
                }
            );

            if (!response.ok) {
                const error = await response.json();
                throw new Error(error.message || 'Erreur lors de la sauvegarde');
            }

            console.log("✅ Sauvegarde réussie !");
            this.closeModal();
            this.table.ajax.reload();
        } catch (error) {
            console.error('🚨 Erreur lors de la sauvegarde :', error);
            alert('Erreur : ' + error.message);
        }
    }

    // Méthode pour modifier une association
    async edit(id) {
        try {
            console.log("📌 Édition de l'association ID :", id);
            const response = await fetch(`/admin/api/parentStudent/association/${id}`);
            if (!response.ok) throw new Error('Erreur lors de la récupération des données');
            const data = await response.json();

            console.log("📊 Données reçues pour édition :", data);

            document.getElementById('associationId').value = data.id;
            document.getElementById('studentSelect').value = data.studentId;
            document.getElementById('parentSelect').value = data.parentId;
            document.getElementById('relationship').value = data.relationship;
            document.getElementById('primaryContact').checked = data.primaryContact;

            const modal = new bootstrap.Modal(document.getElementById(this.modalId));
            modal.show();
        } catch (error) {
            console.error('🚨 Erreur lors de l\'édition :', error);
            alert('Erreur lors de la récupération des données : ' + error.message);
        }
    }

    // Méthode pour supprimer une association
    async delete(id) {
        if (!confirm("Êtes-vous sûr de vouloir supprimer cette association ?")) return;

        try {
            console.log("🗑 Suppression de l'association ID :", id);
            const csrfToken = document.querySelector('meta[name="_csrf"]').content;
            const response = await fetch(`${this.apiEndpoint}/${id}`, {
                method: "DELETE",
                headers: {
                    "X-CSRF-TOKEN": csrfToken
                }
            });

            if (!response.ok) throw new Error('Erreur lors de la suppression');

            console.log("✅ Suppression réussie !");
            this.reloadDataTable();
        } catch (error) {
            console.error('🚨 Erreur lors de la suppression :', error);
            alert('Erreur : ' + error.message);
        }
    }

    // Méthode pour ouvrir le modal pour une nouvelle association
    openNewAssociationModal() {
        console.log("📌 Ouverture du modal pour une nouvelle association");
        document.getElementById(this.formId).reset();
        const modal = new bootstrap.Modal(document.getElementById(this.modalId));
        modal.show();
    }

    closeModal() {
        console.log("📌 Fermeture du modal");
        const modalEl = document.getElementById(this.modalId);
        let modalInstance = bootstrap.Modal.getInstance(modalEl);
        if (!modalInstance) {
            modalInstance = new bootstrap.Modal(modalEl);
        }
        modalInstance.hide();
    }
}



// Initialisation de la page
document.addEventListener('DOMContentLoaded', function() {
    window.parentStudentPage = new ParentStudentPage();
});