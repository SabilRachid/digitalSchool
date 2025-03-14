document.addEventListener('DOMContentLoaded', function() {
    loadCourses();              // Charge initialement tous les cours via GET
    initializeCourseForm();       // Pour la création d'un cours
    initializeAttendanceForm();   // Pour la saisie de présence
    initializeCourseFilters();    // Pour le filtrage dynamique
});

// Charge les cours via GET, éventuellement filtrés
async function loadCourses(filterUrl = '/professor/api/courses') {
    try {
        const response = await fetch(filterUrl, {
            method: 'GET',
            headers: { 'Accept': 'application/json' }
        });
        if (!response.ok) throw new Error('Erreur lors du chargement des cours : ' + response.status);
        const courses = await response.json();
        updateCoursesUI(courses);
    } catch (error) {
        showNotification(error.message, 'error');
    }
}

// Met à jour l'affichage des cours dans les différentes sections
function updateCoursesUI(courses) {
    const upcomingCourseSection = document.getElementById('upcomingCourseList');
    const inProgressCourseSection = document.getElementById('inProgressCourseList');
    const toBeAttendCourseSection = document.getElementById('toBeAttendCourseList');
    const completedCourseSection = document.getElementById('completedCourseList');

    upcomingCourseSection.innerHTML = '';
    inProgressCourseSection.innerHTML = '';
    toBeAttendCourseSection.innerHTML = '';
    completedCourseSection.innerHTML = '';

    courses.forEach(course => {
        // Utiliser course.subjectName si c'est la propriété qui contient le nom de la matière
        console.log(`Course ${course.id} - Subject: ${course.subjectName}, Status: ${course.status}, StartTime: ${course.startTime}`);
        const now = new Date();
        const courseCard = document.createElement('div');
        courseCard.className = 'course-card';
        courseCard.innerHTML = `
        <div class="course-header">
            <span class="subject-badge">${course.subjectName}</span>
            ${course.status === 'SCHEDULED' ? `<span class="exam-date"><i class="fas fa-calendar"></i> ${formatCourseDate(course.startTime)}</span>` : ''}
            ${course.status === 'UPCOMING' ? `<span class="status-badge in-progress">En cours</span>` : ''}
            ${course.status === 'COMPLETED' ? `<span class="status-badge completed">Terminé</span>` : ''}
        </div>
        <div class="course-content">
            <h3>${course.title}</h3>
            <div class="course-details">
                <span class="detail-item"><i class="fas fa-clock"></i> ${formatTime(course.startTime)} - ${formatTime(course.endTime)}</span>
                <span class="detail-item"><i class="fas fa-users"></i> ${course.classe}</span>
            </div>
            ${course.status === 'UPCOMING' ? `<button class="btn btn-info mt-2" onclick="openCourseAttendanceModal(${course.id})"><i class="fas fa-edit"></i> Saisir présence</button>` : ''}
        </div>
        <div class="course-footer">
            ${course.status === 'SCHEDULED' ? `
                <button class="btn btn-primary" onclick="publishCourse(${course.id})"><i class="fas fa-share"></i> Publier</button>
                <button class="btn btn-secondary" onclick="editCourse(${course.id})"><i class="fas fa-edit"></i> Modifier</button>
            ` : ''}
            ${course.status === 'UPCOMING' ? `
                <button class="btn btn-warning" onclick="finalizeCourse(${course.id})"><i class="fas fa-stop"></i> Terminer</button>
            ` : ''}
            ${course.status === 'COMPLETED' ? `
                <button class="btn btn-primary" onclick="viewCourseDetails(${course.id})">
                    <i class="fas fa-chart-bar"></i> Consulter
                </button>
            ` : ''}
        </div>
    `;
        // Répartition des cours selon leur statut
        if (course.status === 'SCHEDULED') {
            upcomingCourseSection.appendChild(courseCard);
        } else if (course.status === 'UPCOMING') {
            inProgressCourseSection.appendChild(courseCard);
        } else if (course.status === 'COMPLETED') {
            completedCourseSection.appendChild(courseCard);
        }
    });

}


// Formatage de la date d'un cours
function formatCourseDate(dateTimeString) {
    const date = new Date(dateTimeString);
    return date.toLocaleDateString('fr-FR', { day: '2-digit', month: 'short', year: 'numeric' });
}

// Formatage de l'heure
function formatTime(timeString) {
    const date = new Date(timeString);
    return date.toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' });
}

// Initialisation du formulaire de création d'un cours
function initializeCourseForm() {
    const form = document.getElementById('courseForm');
    if (!form) {
        console.error("Le formulaire courseForm n'a pas été trouvé.");
        return;
    }
    form.addEventListener('submit', async function(e) {
        e.preventDefault();
        console.log("Formulaire soumis !");
        try {
            const formData = new FormData(this);
            // Récupérer les valeurs du formulaire
            const dateCourse = formData.get("courseDate");    // ex. "2025-03-13"
            const timeStr = formData.get("startTime");          // ex. "15:00"
            const timeEndStr = formData.get("endTime");         // ex. "17:00"

            // Construire les datetime ISO
            const v_startTime = dateCourse + "T" + timeStr + ":00";
            const v_endTime = dateCourse + "T" + timeEndStr + ":00";

            const data = {
                title: formData.get('title'),
                subjectId: formData.get('subject'),
                classeId: formData.get('classe'),
                startTime: v_startTime,
                endTime: v_endTime,
                description: formData.get('description')
            };
            console.log("📌 JSON envoyé :", JSON.stringify(data, null, 2));
            const response = await fetch('/professor/api/courses', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                },
                body: JSON.stringify(data)
            });
            if (!response.ok) {
                throw new Error('Erreur lors de la création du cours');
            }
            closeCourseModal();
            showNotification('Cours créé avec succès', 'success');
            setTimeout(() => location.reload(), 1500);
        } catch (error) {
            console.error("Erreur lors de la soumission du cours :", error);
            showNotification(error.message, 'error');
        }
    });
}

// Fonction pour publier un cours : change le statut en "published" (ou autre selon votre logique)
async function publishCourse(courseId) {
    try {
        const response = await fetch(`/professor/api/courses/${courseId}/publish`, {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
            }
        });
        if (!response.ok) {
            throw new Error('Erreur lors de la publication du cours');
        }
        showNotification('Cours publié avec succès', 'success');
        // Recharge la liste des cours pour mettre à jour l'affichage
        loadCourses();
    } catch (error) {
        showNotification(error.message, 'error');
    }
}

// Fonction pour finaliser un cours : change le statut en COMPLETED (ou autre statut défini pour un cours terminé)
async function finalizeCourse(courseId) {
    try {
        const response = await fetch(`/professor/api/courses/${courseId}/finalize`, {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
            }
        });
        if (!response.ok) {
            throw new Error('Erreur lors de la finalisation du cours');
        }
        showNotification('Cours finalisé avec succès', 'success');
        // Rechargez la liste des cours pour mettre à jour l'affichage
        loadCourses();
    } catch (error) {
        showNotification(error.message, 'error');
    }
}



// Initialisation du formulaire de saisie de présence
function initializeAttendanceForm() {
    const form = document.getElementById('courseAttendanceForm');
    if (!form) return;
    form.addEventListener('submit', async function(e) {
        e.preventDefault();
        try {
            const formData = new FormData(this);
            const attendanceData = {};
            formData.forEach((value, key) => {
                attendanceData[key] = value;
            });
            console.log("📌 JSON envoyé pour présence :", JSON.stringify(attendanceData, null, 2));
            const response = await fetch('/professor/api/courses/attendance', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                },
                body: JSON.stringify(attendanceData)
            });
            if (!response.ok) throw new Error('Erreur lors de la saisie de présence');
            closeCourseAttendanceModal();
            showNotification('Présence enregistrée avec succès', 'success');
            setTimeout(() => location.reload(), 1500);
        } catch (error) {
            showNotification(error.message, 'error');
        }
    });
}


function openCourseModal() {
    document.getElementById('courseModal').classList.add('show');
}

function closeCourseModal() {
    const modal = document.getElementById('courseModal');
    if (modal) {
        modal.classList.remove('show');
    }
}

// Initialisation du modal de saisie de présence
function initializeCourseAttendanceModal() {
    const modal = document.getElementById('courseAttendanceModal');
    if (!modal) {
        console.error("Le modal de présence n'a pas été trouvé.");
        return;
    }
    const attendanceBody = document.getElementById('courseAttendanceBody');
    if (attendanceBody) {
        attendanceBody.innerHTML = ''; // Vider le conteneur
    } else {
        console.error("Le conteneur des présences (courseAttendanceBody) est introuvable.");
    }
}

// Ajout d'un écouteur sur chaque checkbox de présence dans le modal
document.querySelectorAll('.attendance-input input[type="checkbox"]').forEach(checkbox => {
    checkbox.addEventListener('change', function() {
        const row = this.closest('.grade-entry-row');
        const statusSelect = row.querySelector('.status-select select');
        if (this.checked) {
            statusSelect.value = "PRESENT";
            statusSelect.disabled = true;
        } else {
            statusSelect.disabled = false;
            // Remettre la valeur par défaut à "ABSENT" ou laisser libre
            statusSelect.value = "ABSENT";
        }
    });
});


// Fonction pour cocher tous les élèves ("Tous présents")
function markAllPresent() {
    const rows = document.querySelectorAll('.grade-entry-row');
    rows.forEach(row => {
        const checkbox = row.querySelector('input[type="checkbox"]');
        if (checkbox) {
            checkbox.checked = true;
        }
        // Mettre à jour le select pour "PRESENT" si disponible
        const statusSelect = row.querySelector('.status-select select');
        if (statusSelect) {
            statusSelect.value = "PRESENT";
            statusSelect.disabled = true;
        }
    });
}

// Alias pour éviter l'erreur si selectAllAttendance est appelé
function selectAllAttendance() {
    markAllPresent();
}

// Ouvre le modal de présence et charge la liste des étudiants du cours
function openCourseAttendanceModal(courseId) {
    fetch(`/professor/api/courses/${courseId}/students`)
        .then(response => response.json())
        .then(students => {
            const attendanceBody = document.getElementById('courseAttendanceBody');
            attendanceBody.innerHTML = '';
            students.forEach(student => {
                const row = document.createElement('div');
                row.className = 'grade-entry-row';
                row.innerHTML = `
                    <div class="student-info">
                        <span>${student.firstName} ${student.lastName}</span>
                    </div>
                    <div class="attendance-input">
                        <input type="checkbox" name="attendance_${student.id}" id="attendance_${student.id}">
                    </div>
                    <select name="status_${student.id}" id="status_${student.id}">
                        <option value="ABSENT">Absent</option>
                        <option value="PRESENT">Présent</option>
                        <option value="RETARD">Retard</option>
                        <option value="EXCUSE">Excusé</option>
                    </select>
                    <div class="justification-input">
                        <input type="text" name="justification_${student.id}" placeholder="Justification (optionnel)">
                    </div>
                `;
                attendanceBody.appendChild(row);
            });
            document.getElementById('courseId').value = courseId;
            document.getElementById('courseAttendanceModal').classList.add('show');
        })
        .catch(error => {
            showNotification("Erreur lors de la récupération des étudiants: " + error.message, "error");
        });
}

// Ferme le modal de présence
function closeCourseAttendanceModal() {
    document.getElementById('courseAttendanceModal').classList.remove('show');
    document.getElementById('courseAttendanceForm').reset();
}

async function viewCourseDetails(courseId) {
    try {
        const response = await fetch(`/professor/api/courses/${courseId}`);
        if (!response.ok) throw new Error("Erreur lors de la récupération des détails du cours.");

        const course = await response.json();

        // Sélection du modal et insertion des détails
        const modal = document.getElementById("courseDetailsModal");
        const modalBody = document.getElementById("courseDetailsBody");

        modalBody.innerHTML = `
            <h3>${course.title}</h3>
            <p><strong>Matière :</strong> ${course.subjectName}</p>
            <p><strong>Classe :</strong> ${course.classe}</p>
            <p><strong>Début :</strong> ${formatCourseDate(course.startTime)} à ${formatTime(course.startTime)}</p>
            <p><strong>Fin :</strong> ${formatCourseDate(course.endTime)} à ${formatTime(course.endTime)}</p>
            <p><strong>Description :</strong> ${course.description || "Aucune description"}</p>
        `;

        modal.classList.add("show");

    } catch (error) {
        showNotification(error.message, "error");
    }
}

// Fonction pour fermer le modal
function closeCourseDetailsModal() {
    document.getElementById("courseDetailsModal").classList.remove("show");
}


// Initialisation du formulaire de filtrage pour les cours
function initializeCourseFilters() {
    const filterForm = document.getElementById('courseFilterForm');
    if (!filterForm) return;
    filterForm.addEventListener('submit', function(e) {
        e.preventDefault();
        const url = generateCourseFilterUrl(filterForm);
        loadCourses(url);
    });
}

function generateCourseFilterUrl(form) {
    const formData = new FormData(form);
    let url = '/professor/api/courses?';
    const month = formData.get('month');
    const classe = formData.get('classe');
    const subject = formData.get('subject');
    if (month) url += 'month=' + encodeURIComponent(month) + '&';
    if (classe) url += 'classe=' + encodeURIComponent(classe) + '&';
    if (subject) url += 'subject=' + encodeURIComponent(subject) + '&';
    if (url.endsWith('&') || url.endsWith('?')) {
        url = url.slice(0, -1);
    }
    return url;
}

// Exemple de fonction de notification
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.innerHTML = `
        <div class="notification-content">
            <i class="fas ${type === 'success' ? 'fa-check-circle' : 'fa-exclamation-circle'}"></i>
            <span>${message}</span>
        </div>
        <button class="notification-close">&times;</button>
    `;
    document.body.appendChild(notification);
    setTimeout(() => notification.classList.add('show'), 100);
    notification.querySelector('.notification-close').addEventListener('click', () => {
        notification.classList.remove('show');
        setTimeout(() => notification.remove(), 300);
    });
    setTimeout(() => {
        notification.classList.remove('show');
        setTimeout(() => notification.remove(), 300);
    }, 5000);

}
