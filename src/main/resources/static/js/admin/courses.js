// Configuration spécifique pour la page des cours
class CoursesPage extends AdminPage {
    constructor() {
        super({
            tableId: 'coursesTable',
            modalId: 'courseModal',
            formId: 'courseForm',
            apiEndpoint: '/admin/courses',
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
                                <button class="icon-button edit" onclick="coursesPage.edit(${data.id})">
                                    <i class="fas fa-edit"></i>
                                </button>
                                <button class="icon-button delete" onclick="coursesPage.delete(${data.id})">
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
            const response = await fetch('/admin/subjects/list');
            if (!response.ok) throw new Error('Erreur lors du chargement des matières');
            
            const subjects = await response.json();
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
            const response = await fetch('/admin/users/professors');
            if (!response.ok) throw new Error('Erreur lors du chargement des professeurs');
            
            const professors = await response.json();
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
            const response = await fetch('/admin/classes/list');
            if (!response.ok) throw new Error('Erreur lors du chargement des classes');
            
            const classes = await response.json();
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
}

// Initialisation de la page
document.addEventListener('DOMContentLoaded', function() {
    window.coursesPage = new CoursesPage();
});