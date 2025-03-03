class AttendancesPage extends AdminPage {
    constructor() {
        // Récupérer la date d'aujourd'hui au format yyyy-MM-dd
        const today = new Date().toISOString().split("T")[0];
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
                { data: 'status' },
                {
                    data: null,
                    defaultContent: '<button class="btn btn-sm btn-primary btn-saisie">Saisir</button>',
                    orderable: false
                }
            ],
            ajaxConfig: {
                // Afficher par défaut les fiches dont la date est <= aujourd'hui
                url: `/professor/api/attendances/data?endDate=${today}`,
                type: "GET",
                dataSrc: "data"
            }
        });
        console.log("DataTable instance:", this.table);
        // Flag pour empêcher la double soumission
        this.isSubmitting = false;
        this.loadClasses();
        this.initEventListeners();
    }

    async loadClasses() {
        try {
            console.debug("Appel à /professor/api/attendances/classes/list");
            const response = await fetch('/professor/api/attendances/classes/list');
            if (!response.ok) throw new Error('Erreur lors du chargement des classes');
            const classes = await response.json();
            console.log("📌 Classes chargées :", classes);
            const selectFilter = document.getElementById('classFilter');
            if (selectFilter) {
                const options = classes.map(classe => `<option value="${classe.id}">${classe.name}</option>`).join('');
                selectFilter.innerHTML += options;
                console.debug("Options ajoutées aux sélecteurs de classes");
            }
        } catch (error) {
            console.error('🚨 Erreur dans loadClasses:', error);
        }
    }

    /**
     * Charge la liste des étudiants pour une classe donnée.
     */
    async loadStudents(classId) {
        if (!classId) {
            console.warn("loadStudents: classId manquant");
            return;
        }
        try {
            const url = `/professor/api/attendances/students/${classId}`;
            console.debug("Appel à loadStudents avec URL :", url);
            const response = await fetch(url);
            if (!response.ok) throw new Error('Erreur lors du chargement des étudiants');
            const students = await response.json();
            console.log("📌 Étudiants chargés :", students);
            const tbody = document.getElementById('studentsTableBody');
            if (tbody) {
                tbody.innerHTML = students.map(student => `
                    <tr>
                        <td>${student.firstName} ${student.lastName}</td>
                        <td>
                            <select data-student-id="${student.id}" class="form-select">
                                <option value="">Absent</option>
                                <option value="PRESENT">Présent</option>
                                <option value="RETARD">Retard</option>
                                <option value="EXCUSE">Excusé</option>
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

    /**
     * Charge les enregistrements existants de StudentAttendance pour une fiche d'attendance donnée.
     */
    async loadStudentAttendances(attendanceId) {
        try {
            const url = `/professor/api/attendances/${attendanceId}/student-attendance`;
            console.debug("Chargement des StudentAttendance via URL :", url);
            const response = await fetch(url);
            if (!response.ok) throw new Error('Erreur lors du chargement des StudentAttendance');
            const studentAttendances = await response.json();
            console.log("📌 StudentAttendance chargés :", studentAttendances);
            // Assurez-vous que les étudiants sont déjà chargés
            setTimeout(() => {
                studentAttendances.forEach(sa => {
                    const selector = `#studentsTableBody select[data-student-id="${sa.studentId}"]`;
                    const select = document.querySelector(selector);
                    if (select) {
                        console.debug(`Mise à jour du select pour studentId ${sa.studentId} avec la valeur ${sa.status}`);
                        select.value = sa.status;
                    } else {
                        console.warn(`Aucun select trouvé pour studentId ${sa.studentId} (sélecteur utilisé: ${selector}).`);
                    }
                });
            }, 100); // délai de 100ms (ajustable selon vos besoins)
        } catch (error) {
            console.error("🚨 Erreur dans loadStudentAttendances:", error);
        }
    }

    quickMarkAll() {
        console.debug("quickMarkAll() déclenché");
        document.querySelectorAll("#studentsTableBody select").forEach(select => {
            select.value = "PRESENT";
        });
    }

    initEventListeners() {
        console.debug("Initialisation des écouteurs d'événements");

        // Attacher un unique écouteur pour la soumission du formulaire
        const attendanceForm = document.getElementById("attendanceForm");
        if (attendanceForm) {
            attendanceForm.addEventListener("submit", async (event) => {
                event.preventDefault();
                if (this.isSubmitting) {
                    console.warn("Soumission déjà en cours, annulation de la nouvelle soumission");
                    return;
                }
                this.isSubmitting = true;
                const submitButton = attendanceForm.querySelector("button[type='submit']");
                if (submitButton) submitButton.disabled = true;
                console.debug("Form submit event déclenché");
                try {
                    await this.saveAttendance();
                } finally {
                    if (submitButton) submitButton.disabled = false;
                    this.isSubmitting = false;
                }
            });
        }

        // Appliquer automatiquement les filtres dès qu'ils changent
        const classFilter = document.getElementById("classFilter");
        const startDateFilter = document.getElementById("startDateFilter");
        const endDateFilter = document.getElementById("endDateFilter");
        if (classFilter) {
            classFilter.addEventListener("change", () => this.applyFilters());
        }
        if (startDateFilter) {
            startDateFilter.addEventListener("change", () => this.applyFilters());
        }
        if (endDateFilter) {
            endDateFilter.addEventListener("change", () => this.applyFilters());
        }

        // Bouton d'action dans chaque ligne pour ouvrir le modal de saisie
        $('#groupedAttendanceTable tbody').on('click', 'button.btn-saisie', (event) => {
            const rowData = this.table.row($(event.currentTarget).closest('tr')).data();
            console.debug("Ligne sélectionnée pour saisie:", rowData);
            this.openAttendanceModal(rowData);
        });

        // Attacher l'écouteur pour le bouton "Tous présents" dans le modal
        const quickMarkAllBtn = document.getElementById("quickMarkAll");
        if (quickMarkAllBtn) {
            quickMarkAllBtn.addEventListener("click", () => {
                console.debug("Bouton Tous présents cliqué");
                this.quickMarkAll();
            });
        }
    }

    applyFilters() {
        let classId = document.getElementById("classFilter")?.value;
        let startDate = document.getElementById("startDateFilter")?.value;
        let endDate = document.getElementById("endDateFilter")?.value;
        console.debug("Application automatique des filtres avec classId:", classId, "startDate:", startDate, "endDate:", endDate);
        this.table.ajax.url(`/professor/api/attendances/data?classId=${classId}&startDate=${startDate}&endDate=${endDate}`).load();
    }

    /**
     * Ouvre le modal pour saisir ou mettre à jour les StudentAttendance.
     * Les informations (courseId, courseName, className, date, attendanceId, classId) proviennent de la ligne sélectionnée.
     */
    async openAttendanceModal(rowData) {
        console.debug("Ouverture du modal pour saisie - courseId:", rowData.courseId, "date:", rowData.date);
        document.getElementById("modalCourseName").value = rowData.courseName;
        document.getElementById("modalCourseName").dataset.courseId = rowData.courseId;
        document.getElementById("modalClassName").value = rowData.className;
        document.getElementById("modalDate").value = rowData.date;
        if (rowData.attendanceId) {
            document.getElementById("modalAttendanceId").value = rowData.attendanceId;
            // Charger les StudentAttendance existants pour pré-sélectionner les statuts
            await this.loadStudentAttendances(rowData.attendanceId);
        } else {
            document.getElementById("modalAttendanceId").value = "";
        }
        if (rowData.classId) {
            await this.loadStudents(rowData.classId);
        } else {
            console.warn("classId non présent dans rowData");
        }
        const modalEl = document.getElementById(this.modalId);
        const modal = new bootstrap.Modal(modalEl);
        modal.show();
    }

    /**
     * Enregistre les StudentAttendance saisis dans le modal.
     * Récupère courseId, date, et attendanceId depuis le modal, et construit l'objet JSON.
     */
    async saveAttendance() {
        const form = document.getElementById(this.formId);
        if (!form) {
            console.warn("saveAttendance: formulaire introuvable");
            return;
        }
        const courseId = Number(document.getElementById("modalCourseName").dataset.courseId);
        const date = document.getElementById("modalDate").value;
        const attendanceIdValue = document.getElementById("modalAttendanceId").value;
        const attendanceId = attendanceIdValue ? Number(attendanceIdValue) : null;

        console.debug("Données globales du modal:", { attendanceId, courseId, date });

        let attendances = {};
        document.querySelectorAll("#studentsTableBody select").forEach(select => {
            let studentId = select.dataset.studentId;
            let status = select.value;
            attendances[studentId] = status || "ABSENT";
        });
        console.debug("Données attendances récupérées:", attendances);

        const data = { attendanceId, courseId, date, attendances };
        console.log("📌 JSON envoyé :", JSON.stringify(data, null, 2));

        try {
            const csrfToken = document.querySelector('meta[name="_csrf"]').content;
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
                throw new Error(error.message || "Erreur lors de l'enregistrement");
            }
            console.debug("Requête POST réussie");
            const modalEl = document.getElementById(this.modalId);
            const modalInstance = bootstrap.Modal.getInstance(modalEl);
            if (modalInstance) {
                modalInstance.hide();
            } else {
                new bootstrap.Modal(modalEl).hide();
            }
            this.table.ajax.reload();
            this.showNotification("Saisie des présences effectuée avec succès", "success");
        } catch (error) {
            console.error("🚨 Erreur dans saveAttendance:", error);
            this.showNotification(error.message, "error");
        }
    }
}

// Initialisation une fois le DOM entièrement chargé
document.addEventListener("DOMContentLoaded", function() {
    window.attendancePage = new AttendancesPage();
    console.debug("AttendancesPage initialisée", window.attendancePage);
});
