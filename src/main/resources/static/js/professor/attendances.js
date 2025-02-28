class AttendancesPage extends AdminPage {
    constructor() {
        super({
            tableId: 'groupedAttendanceTable',
            modalId: 'addAttendanceModal',
            formId: 'attendanceForm',
            apiEndpoint: '/professor/api/attendances',
            columns: [
                { data: 'courseName' },
                { data: 'className' },
                { data: 'date' },
                { data: 'count' },
                { data: null, defaultContent: '' }
            ],
            ajaxConfig: {
                url: "/professor/api/attendances/data",
                type: "GET",
                dataSrc: "data"
            }
        });
        console.log("DataTable instance:", this.table);
        console.debug("Configuration DataTable:", this.config);
        // Chargement initial de la liste des classes
        this.loadClasses();
        this.initEventListeners();
    }

    // Récupère et charge la liste des classes pour alimenter les sélecteurs
    async loadClasses() {
        try {
            console.debug("Appel à /professor/api/attendances/classes/list");
            const response = await fetch('/professor/api/attendances/classes/list');
            if (!response.ok) {
                throw new Error('Erreur lors du chargement des classes');
            }
            const classes = await response.json();
            console.log("📌 Classes chargées :", classes);
            const selectFilter = document.getElementById('classFilter');
            const selectForm = document.getElementById('attendanceClass');
            if (selectFilter && selectForm) {
                const options = classes.map(classe => `<option value="${classe.id}">${classe.name}</option>`).join('');
                selectFilter.innerHTML += options;
                selectForm.innerHTML += options;
                console.debug("Options ajoutées aux sélecteurs de classes");
            }
        } catch (error) {
            console.error('🚨 Erreur dans loadClasses:', error);
        }
    }

    // Ouvre le modal et pré-remplit la date, puis charge les étudiants et cours si une classe est sélectionnée
    openNewAttendanceModal() {
        console.debug("Ouverture du modal", this.modalId);
        const modalEl = document.getElementById(this.modalId);
        const modal = new bootstrap.Modal(modalEl);
        modal.show();
        // Affecter la date du jour (format yyyy-MM-dd)
        const today = new Date().toISOString().split("T")[0];
        document.getElementById("attendanceDate").value = today;
        console.debug("Date par défaut affectée :", today);
        // Vider la liste des étudiants avant chargement
        document.getElementById("studentsTableBody").innerHTML = "";
        const classId = document.getElementById("attendanceClass").value;
        console.debug("Classe sélectionnée lors de l'ouverture du modal :", classId);
        if (classId) {
            this.loadStudents(classId);
            this.loadCourses(classId, today);
        }
    }

    // Charge la liste des cours pour une classe et une date donnés
    async loadCourses(classId, date) {
        if (!classId || !date) {
            console.warn("loadCourses: classId ou date manquant");
            return;
        }
        try {
            const url = `/professor/api/attendances/courses?classId=${classId}&date=${date}`;
            console.debug("Appel à loadCourses avec URL :", url);
            const response = await fetch(url);
            if (!response.ok) {
                throw new Error('Erreur lors du chargement des cours');
            }
            const courses = await response.json();
            console.log("📌 Cours chargés :", courses);
            const selectCourse = document.getElementById('attendanceCourse');
            if (selectCourse) {
                selectCourse.innerHTML = `<option value="">Sélectionner un cours</option>`;
                if (courses.length === 0) {
                    this.showNotification("Aucun cours n'est disponible pour cette classe à cette date", "warning");
                    console.warn("Aucun cours disponible pour classId =", classId, "et date =", date);
                } else {
                    selectCourse.innerHTML += courses.map(course =>
                        `<option value="${course.id}">${course.name}</option>`
                    ).join('');
                    console.debug("Cours ajoutés au sélecteur");
                }
            }
        } catch (error) {
            console.error('🚨 Erreur dans loadCourses:', error);
        }
    }

    // Charge la liste des étudiants pour la classe sélectionnée
    async loadStudents(classId) {
        if (!classId) {
            console.warn("loadStudents: classId manquant");
            return;
        }
        try {
            const url = `/professor/api/attendances/students/${classId}`;
            console.debug("Appel à loadStudents avec URL :", url);
            const response = await fetch(url);
            if (!response.ok) {
                throw new Error('Erreur lors du chargement des étudiants');
            }
            const students = await response.json();
            console.log("📌 Étudiants chargés :", students);
            const tbody = document.getElementById('studentsTableBody');
            if (tbody) {
                tbody.innerHTML = students.map(student => `
                    <tr>
                        <td>${student.firstName} ${student.lastName}</td>
                        <td>
                            <!-- Utilisation de data-student-id pour éviter la sérialisation automatique -->
                            <select data-student-id="${student.id}" class="form-select">
                                <option value="">Absent</option>
                                <option value="PRESENT">Présent</option>
                                <option value="RETARD">Retard</option>
                            </select>
                        </td>
                    </tr>
                `).join('');
                console.debug("Liste des étudiants affichée dans le tableau");
            }
        } catch (error) {
            console.error('🚨 Erreur dans loadStudents:', error);
        }
    }

    // Bouton de saisie rapide : coche tous les élèves comme "Présent"
    quickMarkAll() {
        console.debug("Activation de la saisie rapide pour marquer tous les élèves comme présents");
        document.querySelectorAll("#studentsTableBody select").forEach(select => {
            select.value = "PRESENT";
        });
    }

    // Initialisation des écouteurs d'événements
    initEventListeners() {
        console.debug("Initialisation des écouteurs d'événements");

        // Filtres de la DataTable
        document.getElementById("applyFilters")?.addEventListener("click", () => {
            let classId = document.getElementById("classFilter")?.value;
            let startDate = document.getElementById("startDateFilter")?.value;
            let endDate = document.getElementById("endDateFilter")?.value;
            console.debug("Application des filtres avec classId:", classId, "startDate:", startDate, "endDate:", endDate);
            this.table.ajax.url(`/professor/api/attendances/data?classId=${classId}&startDate=${startDate}&endDate=${endDate}`).load();
        });

        // Suppression d'une feuille de présence
        document.addEventListener("click", (event) => {
            if (event.target.closest(".delete-attendance")) {
                const id = event.target.closest(".delete-attendance").dataset.id;
                console.debug("Demande de suppression pour l'id:", id);
                if (confirm("Êtes-vous sûr de vouloir supprimer cette feuille de présence ?")) {
                    this.deleteAttendance(id);
                }
            }
        });

        // Changement de la classe dans le formulaire
        document.getElementById("attendanceClass")?.addEventListener("change", (event) => {
            let classId = event.target.value;
            let date = document.getElementById("attendanceDate").value;
            console.debug("Changement de classe dans le formulaire :", classId, "avec date :", date);
            this.loadStudents(classId);
            this.loadCourses(classId, date);
        });

        // Changement de date pour recharger les cours
        document.getElementById("attendanceDate")?.addEventListener("change", (event) => {
            let date = event.target.value;
            let classId = document.getElementById("attendanceClass").value;
            console.debug("Changement de date :", date, "pour classId :", classId);
            this.loadCourses(classId, date);
        });

        // Bouton de saisie rapide pour marquer tous les élèves comme présents
        document.getElementById("quickMarkAll")?.addEventListener("click", () => {
            this.quickMarkAll();
        });

        // Soumission du formulaire d'ajout de présence
        document.getElementById("attendanceForm")?.addEventListener("submit", async (event) => {
            event.preventDefault();
            console.debug("Soumission du formulaire d'attendance");
            await this.saveAttendance();
        });
    }

    // Sauvegarde la feuille de présence via une requête POST
    async saveAttendance() {
        const form = document.getElementById(this.formId);
        if (!form) {
            console.warn("saveAttendance: formulaire introuvable");
            return;
        }

        // Récupération et conversion des valeurs
        const classId = Number(document.getElementById("attendanceClass").value);
        const courseField = document.getElementById("attendanceCourse");
        const courseIdValue = courseField.value;
        if (!courseIdValue) {
            this.showNotification("Veuillez sélectionner un cours", "error");
            console.warn("saveAttendance: aucun cours sélectionné");
            return;
        }
        const courseId = Number(courseIdValue);
        const date = document.getElementById("attendanceDate").value;
        console.debug("Valeurs récupérées:", { classId, courseId, date });

        let attendances = {};
        // Parcourir tous les selects et récupérer la valeur avec data-student-id
        document.querySelectorAll("#studentsTableBody select").forEach(select => {
            let studentId = select.dataset.studentId || (select.name && select.name.match(/\d+/)[0]);
            let status = select.value;
            attendances[studentId] = status || "ABSENT";
        });
        console.debug("Données attendances récupérées:", attendances);

        const data = { classId, courseId, date, attendances };
        console.log("📌 JSON envoyé :", JSON.stringify(data, null, 2));

        try {
            const csrfToken = document.querySelector('meta[name="_csrf"]').content;
            console.debug("Envoi de la requête POST vers", this.apiEndpoint);
            const response = await fetch(this.apiEndpoint, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "X-CSRF-TOKEN": csrfToken
                },
                body: JSON.stringify(data)
            });

            if (!response.ok) {
                const error = await response.json();
                console.error("Réponse d'erreur de l'API:", error);
                throw new Error(error.message || "Erreur lors de l'enregistrement");
            }

            console.debug("Requête POST réussie");
            // Fermer le modal en utilisant l'instance existante
            const modalEl = document.getElementById(this.modalId);
            const modalInstance = bootstrap.Modal.getInstance(modalEl);
            if (modalInstance) {
                modalInstance.hide();
                console.debug("Modal fermé via instance existante");
            } else {
                new bootstrap.Modal(modalEl).hide();
                console.debug("Modal fermé en créant une nouvelle instance");
            }
            this.table.ajax.reload();
            this.showNotification("Feuille de présence ajoutée avec succès", "success");
        } catch (error) {
            console.error("🚨 Erreur dans saveAttendance:", error);
            this.showNotification(error.message, "error");
        }
    }

    // Suppression d'une feuille de présence via une requête DELETE
    async deleteAttendance(id) {
        try {
            console.debug("Suppression de l'attendance avec id:", id);
            const csrfToken = document.querySelector('meta[name="_csrf"]').content;
            const response = await fetch(`${this.apiEndpoint}/${id}`, {
                method: "DELETE",
                headers: {
                    "X-CSRF-TOKEN": csrfToken
                }
            });
            if (!response.ok) {
                throw new Error("Erreur lors de la suppression");
            }
            console.debug("Suppression réussie");
            this.table.ajax.reload();
            this.showNotification("Feuille de présence supprimée avec succès", "success");
        } catch (error) {
            console.error("🚨 Erreur dans deleteAttendance:", error);
            this.showNotification(error.message, "error");
        }
    }
}

// Initialisation de la page une fois le DOM entièrement chargé
document.addEventListener("DOMContentLoaded", function() {
    window.attendancePage = new AttendancesPage();
    console.debug("AttendancesPage initialisée", window.attendancePage);
});
