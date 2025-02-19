// Configuration spécifique pour la page des cours du professeur
class CoursesPage extends AdminPage {
    constructor() {
        super({
            tableId: 'professorCoursesTable',
            // Modal et formulaire si vous en utilisez pour ajouter/modifier un cours
            modalId: 'courseModal',
            formId: 'courseForm',
            // Endpoint API pour récupérer les cours du professeur
            apiEndpoint: '/professor/api/courses',
            columns: [
                {
                    data: 'name',
                    title: 'Nom'
                },
                {
                    data: 'subject',
                    title: 'Matière'
                },
                {
                    data: 'classe',
                    title: 'Classe'
                },
                {
                    data: 'startTime',
                    title: 'Début',
                    render: function(data) {
                        return data ? new Date(data).toLocaleString('fr-FR', {
                            hour: '2-digit',
                            minute: '2-digit',
                            day: '2-digit',
                            month: '2-digit',
                            year: 'numeric'
                        }) : '-';
                    }
                },
                {
                    data: 'endTime',
                    title: 'Fin',
                    render: function(data) {
                        return data ? new Date(data).toLocaleString('fr-FR', {
                            hour: '2-digit',
                            minute: '2-digit',
                            day: '2-digit',
                            month: '2-digit',
                            year: 'numeric'
                        }) : '-';
                    }
                },
                {
                    data: 'room',
                    title: 'Salle'
                }
            ]
        });

        console.log("📌 CoursesPage initialized");
        // Vous pouvez ajouter ici des initialisations complémentaires, par exemple pour charger des données complémentaires
        // this.loadSupplementaryData();
    }

    // Exemple de méthode pour éditer un cours (si nécessaire)
    async edit(id) {
        try {
            const response = await fetch(`${this.apiEndpoint}/${id}`);
            if (!response.ok) throw new Error('Erreur lors du chargement des données du cours');
            const course = await response.json();
            this.populateForm(course);
            const modal = new bootstrap.Modal(document.getElementById(this.modalId));
            modal.show();
        } catch (error) {
            console.error('🚨 Erreur lors de l’édition du cours :', error);
            alert('Erreur : ' + error.message);
        }
    }

    // Exemple de méthode pour supprimer un cours (si nécessaire)
    async delete(id) {
        if (!confirm("Êtes-vous sûr de vouloir supprimer ce cours ?")) return;
        try {
            const csrfToken = document.querySelector('meta[name="_csrf"]').content;
            const response = await fetch(`${this.apiEndpoint}/${id}`, {
                method: 'DELETE',
                headers: {
                    'X-CSRF-TOKEN': csrfToken
                }
            });
            if (!response.ok) throw new Error('Erreur lors de la suppression du cours');
            this.table.ajax.reload();
            alert("Cours supprimé avec succès !");
        } catch (error) {
            console.error('🚨 Erreur lors de la suppression du cours :', error);
            alert('Erreur : ' + error.message);
        }
    }

    // Exemple de méthode pour pré-remplir le formulaire d'édition (si vous utilisez un modal)
    populateForm(course) {
        const form = document.getElementById(this.formId);
        form.elements['id'].value = course.id || '';
        form.elements['name'].value = course.name || '';
        // Pour les objets imbriqués, on peut pré-remplir avec le nom
        if (course.subject) {
            form.elements['subject'].value = course.subject.id || '';
        }
        if (course.classe) {
            form.elements['classe'].value = course.classe.id || '';
        }
        form.elements['startTime'].value = course.startTime || '';
        form.elements['endTime'].value = course.endTime || '';
        form.elements['room'].value = course.room || '';
    }
}

// Initialisation de la page lorsque le DOM est chargé
document.addEventListener('DOMContentLoaded', function() {
    window.coursesPage = new CoursesPage();
});
