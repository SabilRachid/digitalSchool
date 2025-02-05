// Configuration spécifique pour la page des cours
class CoursesPage extends AdminPage {
    constructor() {
        super({
            tableId: 'coursesTable',
            modalId: 'courseModal',
            formId: 'courseForm',
            apiEndpoint: '/admin/api/courses',
            columns: [
                {
                    data: 'subject',
                    render: function(data) {
                        return data ? data.name : '-';
                    }
                },
                {
                    data: 'professor',
                    render: function(data) {
                        return data ? `${data.firstName} ${data.lastName}` : '-';
                    }
                },
                {
                    data: 'class',
                    render: function(data) {
                        return data ? data.name : '-';
                    }
                },
                {
                    data: null,
                    render: function(data) {
                        const start = new Date(data.startTime).toLocaleTimeString('fr-FR', {
                            hour: '2-digit',
                            minute: '2-digit'
                        });
                        const end = new Date(data.endTime).toLocaleTimeString('fr-FR', {
                            hour: '2-digit',
                            minute: '2-digit'
                        });
                        return `${start} - ${end}`;
                    }
                },
                { data: 'room' },
                {
                    data: null,
                    render: function(data) {
                        return `
                            <div class="action-buttons">
                                <button class="btn btn-sm btn-primary" onclick="coursesPage.edit(${data.id})">
                                    <i class="fas fa-edit"></i>
                                </button>
                                <button class="btn btn-sm btn-danger" onclick="coursesPage.delete(${data.id})">
                                    <i class="fas fa-trash"></i>
                                </button>
                            </div>`;
                    }
                }
            ]
        });

        // Chargement des données complémentaires
        this.loadSubjects();
        this.loadProfessors();
        this.loadClasses();
    }

    // Fonctions pour charger les données complémentaires
    async loadSubjects() {
        try {
            const response = await fetch('/admin/api/subjects/list');
            if (!response.ok) throw new Error('Erreur lors du chargement des matières');

            const subjects = await response.json();
            console.log("📌 Matières chargées :", subjects);

            const select = document.getElementById('subject');
            if (select) {
                select.innerHTML = `
                    <option value="">Sélectionner une matière</option>
                    ${subjects.map(subject =>
                    `<option value="${subject.id}">${subject.name}</option>`
                ).join('')}
                `;
            }
        } catch (error) {
            console.error('Erreur:', error);
        }
    }

    async loadProfessors() {
        try {
            const response = await fetch('/admin/api/users/professors');
            if (!response.ok) throw new Error('Erreur lors du chargement des professeurs');

            const professors = await response.json();
            console.log("📌 Professeurs chargés :", professors);

            const select = document.getElementById('professor');
            if (select) {
                select.innerHTML = `
                    <option value="">Sélectionner un professeur</option>
                    ${professors.map(professor =>
                    `<option value="${professor.id}">${professor.firstName} ${professor.lastName}</option>`
                ).join('')}
                `;
            }
        } catch (error) {
            console.error('Erreur:', error);
        }
    }

    async loadClasses() {
        try {
            const response = await fetch('/admin/api/classes/list');
            if (!response.ok) throw new Error('Erreur lors du chargement des classes');

            const classes = await response.json();
            console.log("📌 Classes chargées :", classes);

            const select = document.getElementById('class');
            if (select) {
                select.innerHTML = `
                    <option value="">Sélectionner une classe</option>
                    ${classes.map(classe =>
                    `<option value="${classe.id}">${classe.name}</option>`
                ).join('')}
                `;
            }
        } catch (error) {
            console.error('Erreur:', error);
        }
    }

    async save() {
        const form = document.getElementById(this.formId);
        if (!form) return;

        const formData = new FormData(form);
        console.log("📌 FormData :", formData);
        let data = Object.fromEntries(formData.entries());

        // Vérification et transformation des valeurs en objets
        if (data.subject) {
            data.subject = { id: parseInt(data.subject, 10) };
        }
        if (data.professor) {
            data.professor = { id: parseInt(data.professor, 10) };
        }
        if (data.class) {
            data.class = { id: parseInt(data.class, 10) };
        }

        // Vérifier les champs obligatoires
        if (!data.subject.id || !data.professor.id || !data.class.id) {
            console.error("🚨 ERREUR : ID manquant !", data);
            this.showNotification("Erreur : Les ID de la matière, du professeur et de la classe sont obligatoires", "error");
            return;
        }

        console.log("📌 JSON envoyé :", JSON.stringify(data, null, 2)); // Vérification JSON

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
            this.showNotification('Cours ajouté avec succès', 'success');
        } catch (error) {
            console.error('🚨 Erreur:', error);
            this.showNotification(error.message, 'error');
        }
    }

//
}

// Initialisation de la page
document.addEventListener('DOMContentLoaded', function() {
    window.coursesPage = new CoursesPage();
});
