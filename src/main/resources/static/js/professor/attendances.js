class AttendancesPage extends AdminPage {
    constructor() {
        super({
            tableId: 'groupedAttendanceTable',
            modalId: 'addAttendanceModal',
            formId: 'attendanceForm',
            apiEndpoint: '/professor/api/attendances',
            columns: [
                { data: 'course' },
                { data: 'classe' },
                { data: 'date' },
                { data: 'count' },
                {
                    data: null
                }
            ],
            ajaxConfig: {
                url: "/professor/api/attendances/data",
                type: "GET",
                dataSrc: "data"  // ✅ Extraction correcte des données
            }
        });

        // Chargement des données nécessaires
        this.loadClasses();
        this.initEventListeners();
    }

    // Charger la liste des classes pour les filtres et le formulaire
    async loadClasses() {
        try {
            const response = await fetch('/professor/api/attendances/classes/list');
            if (!response.ok) throw new Error('Erreur lors du chargement des classes');

            const classes = await response.json();
            console.log("📌 Classes chargées :", classes);

            const selectFilter = document.getElementById('classFilter');
            const selectForm = document.getElementById('attendanceClass');

            if (selectFilter && selectForm) {
                const options = classes.map(classe => `<option value="${classe.id}">${classe.name}</option>`).join('');
                selectFilter.innerHTML += options;
                selectForm.innerHTML += options;
            }
        } catch (error) {
            console.error('🚨 Erreur:', error);
        }
    }

    // Charger les étudiants d'une classe sélectionnée
    async loadStudents(classId) {
        if (!classId) return;
        try {
            const response = await fetch(`/professor/api/attendances/students/${classId}`);
            if (!response.ok) throw new Error('Erreur lors du chargement des étudiants');

            const students = await response.json();
            console.log("📌 Étudiants chargés :", students);

            const tbody = document.getElementById('studentsTableBody');
            if (tbody) {
                tbody.innerHTML = students.map(student => `
                    <tr>
                        <td>${student.firstName} ${student.lastName}</td>
                        <td>
                            <select name="attendance[${student.id}]" class="form-select">
                                <option value="">Absent</option>
                                <option value="PRESENT">Présent</option>
                                <option value="RETARD">Retard</option>
                            </select>
                        </td>
                    </tr>
                `).join('');
            }
        } catch (error) {
            console.error('🚨 Erreur:', error);
        }
    }

    // Initialisation des événements
    initEventListeners() {
        // Appliquer les filtres
        document.getElementById("applyFilters")?.addEventListener("click", () => {
            let classId = document.getElementById("classFilter")?.value;
            let startDate = document.getElementById("startDateFilter")?.value;
            let endDate = document.getElementById("endDateFilter")?.value;

            this.table.ajax.url(`/professor/api/attendances/data?classId=${classId}&startDate=${startDate}&endDate=${endDate}`).load();
        });

        // Suppression d'une feuille de présence
        document.addEventListener("click", (event) => {
            if (event.target.closest(".delete-attendance")) {
                const id = event.target.closest(".delete-attendance").dataset.id;
                if (confirm("Êtes-vous sûr de vouloir supprimer cette feuille de présence ?")) {
                    this.deleteAttendance(id);
                }
            }
        });

        // Changement de classe dans le formulaire (charge la liste des étudiants)
        document.getElementById("attendanceClass")?.addEventListener("change", (event) => {
            this.loadStudents(event.target.value);
        });

        // Soumission du formulaire d'ajout de présence
        document.getElementById("attendanceForm")?.addEventListener("submit", (event) => {
            event.preventDefault();
            this.saveAttendance();
        });
    }

    // Ouvrir le modal pour ajouter une présence
    openNewAttendanceModal() {
        const modal = new bootstrap.Modal(document.getElementById(this.modalId));
        modal.show();

        document.getElementById("studentsTableBody").innerHTML = ""; // Vider la liste avant chargement
        let classId = document.getElementById("attendanceClass").value;
        if (classId) this.loadStudents(classId);
    }

    // Sauvegarder une feuille de présence
    async saveAttendance() {
        const form = document.getElementById(this.formId);
        if (!form) return;

        const classId = document.getElementById("attendanceClass").value;
        const date = document.getElementById("attendanceDate").value;
        let attendances = {};

        document.querySelectorAll("#studentsTableBody select").forEach(select => {
            let studentId = select.name.match(/\d+/)[0];
            let status = select.value;
            attendances[studentId] = status || "ABSENT";
        });

        const data = { classId, date, attendances };

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

            new bootstrap.Modal(document.getElementById(this.modalId)).hide();
            this.table.ajax.reload();
            this.showNotification("Feuille de présence ajoutée avec succès", "success");

        } catch (error) {
            console.error("🚨 Erreur:", error);
            this.showNotification(error.message, "error");
        }
    }

    // Supprimer une feuille de présence
    async deleteAttendance(id) {
        try {
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

            this.table.ajax.reload();
            this.showNotification("Feuille de présence supprimée avec succès", "success");
        } catch (error) {
            console.error("🚨 Erreur:", error);
            this.showNotification(error.message, "error");
        }
    }
}

// Initialisation de la page
document.addEventListener("DOMContentLoaded", function() {
    window.attendancePage = new AttendancesPage();
});
