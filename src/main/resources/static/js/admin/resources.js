// Configuration spécifique pour la gestion des ressources pédagogiques
class ResourcesPage extends AdminPage {
    constructor() {
        super({
            tableId: 'resourcesTable',
            modalId: 'resourceModal',
            formId: 'resourceForm',
            apiEndpoint: '/admin/api/resources',

            columns: [
                { data: 'title' },
                { data: 'type' },
                {
                    data: 'courseName',
                    render: function (data) {
                        return data ? data : '-'; // ✅ Gérer les valeurs nulles ou undefined
                    }

                },
                {
                    data: null,
                    render: function(data) {
                        return `
                            <div class="action-buttons">
                                <button class="btn btn-sm btn-primary" onclick="window.resourcesPage.edit(${data.id})">
                                    <i class="fas fa-edit"></i>
                                </button>
                                <button class="btn btn-sm btn-danger" onclick="window.resourcesPage.delete(${data.id})">
                                    <i class="fas fa-trash"></i>
                                </button>
                                <a href="${data.url}" target="_blank" class="btn btn-sm btn-info">
                                    <i class="fas fa-file-pdf text-success"></i>
                                </a>
                            </div>`;
                    }
                }
            ]
        });

        // Charger les cours pour la sélection
        this.loadCourses();
    }

    // Chargement des cours disponibles pour l'association
    async loadCourses() {
        try {
            const response = await fetch('/admin/api/courses/list');
            if (!response.ok) throw new Error('Erreur lors du chargement des cours');

            const courses = await response.json();
            console.log("📌 Cours chargés :", courses);

            const select = document.getElementById('course');
            if (select) {
                select.innerHTML = `
                    <option value="">Sélectionner un cours</option>
                    ${courses.map(course =>
                    `<option value="${course.id}">${course.name}</option>`
                ).join('')}
                `;
            }
        } catch (error) {
            console.error('Erreur:', error);
        }
    }

    // Remplissage du formulaire lors de l'édition
    populateForm(data) {
        super.populateForm(data);
        if (data.course) {
            document.getElementById('course').value = data.course.id;
        }
    }

    // Surcharge de la méthode save pour inclure le cours lié
    async save() {
        const form = document.getElementById(this.formId);
        if (!form) return;

        const formData = new FormData(form);
        let data = Object.fromEntries(formData.entries());

        // Assure que courseId est bien envoyé sous forme de JSON
        // Correction : S'assurer que courseId est bien ajouté comme paramètre et sous le bon nom
        if (formData.get("course")) {
            formData.set("courseId", formData.get("course")); // Renomme correctement en "courseId"
            formData.delete("course"); // Supprime l'ancien champ "course"
        }

        console.log("📌 Données envoyées :", [...formData.entries()]);

        // Ajout du fichier dans FormData
        const fileInput = document.getElementById('file');
        if (fileInput.files.length > 0) {
            formData.append('file', fileInput.files[0]);
        }

        // Suppression de l'ancien champ `url`
        formData.delete('url');

        try {
            const response = await fetch(
                data.id ? `${this.apiEndpoint}/${data.id}` : `${this.apiEndpoint}/upload`,
                {
                    method: data.id ? 'PUT' : 'POST',
                    headers: {
                        'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                    },
                    body: formData
                }
            );

            if (!response.ok) {
                const error = await response.json();
                throw new Error(error.message || 'Erreur lors de la sauvegarde');
            }

            this.closeModal();
            this.table.ajax.reload();
            this.showNotification('Fichier ajouté avec succès', 'success');
        } catch (error) {
            console.error('🚨 Erreur:', error);
            this.showNotification(error.message, 'error');
        }
    }


}

// Initialisation de la page après chargement du DOM
document.addEventListener('DOMContentLoaded', function() {
    window.resourcesPage = new ResourcesPage();


});

document.getElementById('resourceForm').addEventListener('submit', function(event) {
    event.preventDefault();
    const formData = new FormData(this);
    const data = Object.fromEntries(formData.entries());

    console.log("📌 Données du formulaire envoyées :", data); // Vérifie le contenu
});

fetch('/admin/api/resources')
    .then(response => response.json())
    .then(data => console.log("📌 Données reçues :", data));