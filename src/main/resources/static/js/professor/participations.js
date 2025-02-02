// Configuration spécifique pour la page des participations
class ParticipationsPage extends AdminPage {
    constructor() {
        super({
            tableId: 'participationsTable',
            modalId: 'participationModal',
            formId: 'participationForm',
            apiEndpoint: '/admin/participations',
            columns: [
                {
                    data: 'student',
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
                    data: 'subject',
                    render: function(data) {
                        return data ? data.name : '-';
                    }
                },
                {
                    data: 'date',
                    render: function(data) {
                        return new Date(data).toLocaleDateString('fr-FR');
                    }
                },
                {
                    data: 'participationScore'
                },
                {
                    data: null,
                    render: function(data) {
                        return `
                            <div class="action-buttons">
                                <button class="btn btn-sm btn-primary" onclick="participationsPage.edit(${data.id})">
                                    <i class="fas fa-edit"></i>
                                </button>
                                <button class="btn btn-sm btn-danger" onclick="participationsPage.delete(${data.id})">
                                    <i class="fas fa-trash"></i>
                                </button>
                            </div>`;
                    }
                }
            ]
        });

        // Chargement des données complémentaires
        this.loadClasses();
        this.loadSubjects();
        this.initClassChangeEvent();
    }

    // Charger les classes
    async loadClasses() {
        try {
            const response = await fetch('/admin/classes/list');
            if (!response.ok) throw new Error('Erreur lors du chargement des classes');

            const classes = await response.json();
            console.log("📌 Classes chargées :", classes);

            const select = document.getElementById('class');
            if (select) {
                select.innerHTML = `
                    <option value="">Sélectionner une classe</option>
                    ${classes.map(classe =>
                    `<option value="${classe.id}">${classe.name}</option>`).join('')}
                `;
            }
        } catch (error) {
            console.error('Erreur:', error);
        }
    }

    // Charger les matières
    async loadSubjects() {
        try {
            const response = await fetch('/admin/subjects/list');
            if (!response.ok) throw new Error('Erreur lors du chargement des matières');

            const subjects = await response.json();
            console.log("📌 Matières chargées :", subjects);

            const select = document.getElementById('subject');
            if (select) {
                select.innerHTML = `
                    <option value="">Sélectionner une matière</option>
                    ${subjects.map(subject =>
                    `<option value="${subject.id}">${subject.name}</option>`).join('')}
                `;
            }
        } catch (error) {
            console.error('Erreur:', error);
        }
    }

    // Charger les élèves d'une classe sélectionnée
    async loadStudents(classId) {
        if (!classId) return;
        try {
            const response = await fetch(`/admin/students/by-class/${classId}`);
            if (!response.ok) throw new Error('Erreur lors du chargement des élèves');

            const students = await response.json();
            console.log("📌 Élèves chargés :", students);

            const select = document.getElementById('student');
            if (select) {
                select.innerHTML = `
                    <option value="">Sélectionner un élève</option>
                    ${students.map(student =>
                    `<option value="${student.id}">${student.firstName} ${student.lastName}</option>`).join('')}
                `;
            }
        } catch (error) {
            console.error('Erreur:', error);
        }
    }

    // Écouteur de changement de classe pour charger les élèves
    initClassChangeEvent() {
        document.getElementById('class').addEventListener('change', (event) => {
            this.loadStudents(event.target.value);
        });
    }

    async save() {
        const form = document.getElementById(this.formId);
        if (!form) return;

        const formData = new FormData(form);
        let data = Object.fromEntries(formData.entries());

        // Vérification et transformation des valeurs en objets
        if (data.student) {
            data.student = { id: parseInt(data.student, 10) };
        }
        if (data.class) {
            data.class = { id: parseInt(data.class, 10) };
        }
        if (data.subject) {
            data.subject = { id: parseInt(data.subject, 10) };
        }

        // Vérifier les champs obligatoires
        if (!data.student.id || !data.class.id || !data.subject.id) {
            console.error("🚨 ERREUR : ID manquant !", data);
            this.showNotification("Erreur : Les ID de l'élève, de la classe et de la matière sont obligatoires", "error");
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
            this.showNotification('Participation enregistrée avec succès', 'success');
        } catch (error) {
            console.error('🚨 Erreur:', error);
            this.showNotification(error.message, 'error');
        }
    }
}

// Initialisation de la page
document.addEventListener('DOMContentLoaded', function() {
    window.participationsPage = new ParticipationsPage();
});
