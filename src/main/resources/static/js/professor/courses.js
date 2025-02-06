
    class ProfessorCoursesPage extends AdminPage {
        constructor() {
            super({
                tableId: 'professorCoursesTable',
                apiEndpoint: '/professor/api/courses/my-courses',
                columns: [
                    {data: 'name'},
                    {data: 'subject'},
                    {data: 'class'},
                    {data: 'startTime'},
                    {data: 'endTime'},
                    {data: 'room'}
                ]
            });
        }

}

// Initialisation
    document.addEventListener("DOMContentLoaded", function() {
        window.professorCoursesPage = new ProfessorCoursesPage();
    });

