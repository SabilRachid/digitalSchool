document.addEventListener("DOMContentLoaded", function() {
    // Initialisation du calendrier principal
    const calendarEl = document.getElementById("calendar");
    const calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: "timeGridWeek",
        headerToolbar: {
            left: "prev,next today",
            center: "title",
            right: "timeGridWeek,timeGridDay,listWeek"
        },
        locale: "fr",
        firstDay: 1,
        slotMinTime: "08:00:00",
        slotMaxTime: "20:00:00",
        contentHeight: "auto",
        hiddenDays: [0], // Supprime le dimanche
        allDaySlot: true,
        nowIndicator: true,
        selectable: hasEditPermission(),
        selectMirror: true,
        editable: hasEditPermission(),
        dayMaxEvents: true,

        // Création d'un événement via la sélection
        select: function(info) {
            if (hasEditPermission()) {
                document.getElementById("startDate").value = formatDateTime(info.start);
                document.getElementById("endDate").value = formatDateTime(info.end || info.start);
                openEventModal(info);
            }
        },

        // Au clic sur un événement, affiche ses détails dans un popover compact
        eventClick: function(info) {
            viewEventDetails(info.event, info.jsEvent);
        },

        // Mise à jour par drag & drop ou redimensionnement
        eventDrop: function(info) {
            if (hasEditPermission()) {
                updateEvent(info.event);
            }
        },
        eventResize: function(info) {
            if (hasEditPermission()) {
                updateEvent(info.event);
            }
        },

        // Personnalisation de l'affichage des événements
        eventDidMount: function(info) {
            const titleEl = info.el.querySelector(".fc-event-title");
            if (titleEl) {
                let icon = "";
                switch (info.event.extendedProps.type) {
                    case "COURSE":
                        icon = '<i class="fas fa-book"></i> ';
                        break;
                    case "EXAM":
                        icon = '<i class="fas fa-file-alt"></i> ';
                        break;
                    case "EVENT":
                        icon = '<i class="fas fa-calendar-day"></i> ';
                        break;
                    case "MEETING":
                        icon = '<i class="fas fa-users"></i> ';
                        break;
                    default:
                        icon = "";
                }
                titleEl.innerHTML = icon + info.event.title;
            }
        },

        // Chargement des événements via l'API REST
        events: function(info, successCallback, failureCallback) {
            const startEncoded = encodeURIComponent(info.startStr);
            const endEncoded = encodeURIComponent(info.endStr);
            const url = `/calendar/api/events?start=${startEncoded}&end=${endEncoded}`;

            fetch(url, {
                method: "GET",
                headers: {
                    "Accept": "application/json"
                    // Si nécessaire, ajoutez ici le token CSRF
                }
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`Erreur lors du chargement des événements: ${response.status}`);
                    }
                    return response.json();
                })
                .then(data => {
                    const events = data.map(event => ({
                        id: event.id,
                        // Concaténation du titre, du nom de la matière et du nom de la classe
                        title: event.title + " - " + event.subjectName + " - " + event.classeName,
                        start: event.startTime,
                        end: event.endTime,
                        allDay: event.allDay || false,
                        extendedProps: {
                            roomName: event.roomName,
                            status: event.status,
                            description: event.description,
                            type: event.type
                        },
                        backgroundColor: getEventColor(event.type),
                        textColor: "#FFFFFF"
                    }));
                    successCallback(events);
                })
                .catch(error => {
                    console.error("Erreur lors du chargement des événements:", error);
                    failureCallback(error);
                });
        }
    });
    calendar.render();
    window.calendarInstance = calendar;

    // Initialisation du mini-calendrier
    const miniCalendarEl = document.getElementById("miniCalendar");
    const miniCalendar = new FullCalendar.Calendar(miniCalendarEl, {
        initialView: "dayGridMonth",
        headerToolbar: {
            left: "prev",
            center: "title",
            right: "next"
        },
        locale: "fr",
        firstDay: 1,
        height: "auto",
        selectable: true,
        dateClick: function(info) {
            calendar.gotoDate(info.date);
        }
    });
    miniCalendar.render();

    // Attacher les écouteurs sur les filtres
    attachFilterListeners();

    // Gestion du formulaire de création/édition d'événement
    const eventForm = document.getElementById("eventForm");
    if (eventForm) {
        console.debug("eventForm: existe");
        eventForm.addEventListener("submit", async function(e) {
            e.preventDefault();
            console.debug("eventForm submit Event");
            if (!hasEditPermission()) {
                console.debug("Permissions insuffisantes pour créer/modifier un événement");
            }

            const formData = new FormData(this);
            const data = {
                title: formData.get("title"),
                type: formData.get("type"),
                startTime: formData.get("startDate"),
                endTime: formData.get("endDate"),
                location: formData.get("location"),
                description: formData.get("description"),
                allDay: formData.get("allDay") === "on"
            };

            // Champs spécifiques pour COURSES et EXAMS
            if (["COURSE", "EXAM"].includes(data.type)) {
                data.subject = { id: formData.get("subject") };
                data.classe = { id: formData.get("classe") };
                data.room = { id: formData.get("room") };
                if (data.type === "EXAM") {
                    data.duration = parseInt(formData.get("duration"));
                }
            } else if (data.type === "MEETING") {
                data.participants = Array.from(formData.getAll("participants"));
                data.online = formData.get("online") === "on";
            }

            try {
                const csrfToken = document.querySelector('meta[name="_csrf"]').content;
                console.debug("📌 Données avant fetch:", data);
                const response = await fetch("/calendar/api/events/create", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        "Accept": "application/json",
                        "X-CSRF-TOKEN": csrfToken
                    },
                    body: JSON.stringify(data)
                });
                if (!response.ok) {
                    throw new Error("Erreur lors de la création de l'événement");
                }
                closeEventModal();
                console.log("📌 Événement créé avant refetch:", data);
                calendar.refetchEvents();
                showNotification("Événement créé avec succès", "success");
            } catch (error) {
                console.error("Erreur:", error);
                showNotification(error.message, "error");
            }
        });
    }
});

//
// Fonctions utilitaires et de gestion des événements
//

function formatDateTime(date) {
    // Retourne la date au format "YYYY-MM-DDTHH:MM:SS" (sans fraction de seconde)
    return date.toISOString().slice(0, 19);
}

function hasEditPermission() {
    // Vérifie si le body contient la classe "role-professor"
    return document.body.classList.contains("role-professor");
}

function getEventColor(type) {
    switch (type) {
        case "COURSE": return "#4C51BF";
        case "EXAM": return "#ED8936";
        case "EVENT": return "#48BB78";
        case "MEETING": return "#4299E1";
        default: return "#718096";
    }
}

function attachFilterListeners() {
    const filters = ["classFilter", "subjectFilter", "startDateFilter", "endDateFilter"];
    filters.forEach(id => {
        const element = document.getElementById(id);
        if (element) {
            element.addEventListener("change", function() {
                window.calendarInstance.refetchEvents();
            });
        }
    });
}

/**
 * Affiche les détails d'un événement dans un popover compact lors d'un clic.
 * Le contenu affiche les horaires sans secondes, avec une police réduite et un fond très clair.
 * @param {Object} event - L'objet événement FullCalendar.
 * @param {Event} jsEvent - L'événement JS du clic.
 */
function viewEventDetails(event, jsEvent) {
    console.log("📌 Détails de l'événement:", event);
    const startFormatted = new Date(event.start).toLocaleTimeString("fr-FR", {
        hour: "2-digit",
        minute: "2-digit"
    });
    const endFormatted = event.end ? new Date(event.end).toLocaleTimeString("fr-FR", {
        hour: "2-digit",
        minute: "2-digit"
    }) : "-";

    const content = `
    <div style="font-size: var(--compact-font-size); background-color: rgba(222,237,239,0.9); padding: var(--compact-padding); border-radius: 0.25rem;">
      <p style="margin: 0;"><strong>Titre :</strong> ${event.title}</p>
      <p style="margin: 0;"><strong>Horaires :</strong> ${startFormatted} - ${endFormatted}</p>
      <p style="margin: 0;"><strong>Salle :</strong> ${event.extendedProps.roomName || "-"}</p>
      <p style="margin: 0;"><strong>Description :</strong> ${event.extendedProps.description || "-"}</p>
    </div>
  `;

    const popoverTemplate = `
    <div class="popover custom-popover" role="tooltip">
      <div class="arrow"></div>
      <h3 class="popover-header"></h3>
      <div class="popover-body"></div>
    </div>
  `;

    const popover = new bootstrap.Popover(jsEvent.target, {
        container: "body",
        content: content,
        html: true,
        trigger: "manual",
        placement: "top",
        template: popoverTemplate
    });
    popover.show();

    setTimeout(() => { popover.hide(); }, 5000);

    document.addEventListener("click", function onDocClick(e) {
        if (!jsEvent.target.contains(e.target)) {
            popover.hide();
            document.removeEventListener("click", onDocClick);
        }
    });
}

function openEventModal(info = null) {
    const modal = document.getElementById("eventModal");
    const form = document.getElementById("eventForm");
    form.reset();
    if (info) {
        document.getElementById("startDate").value = formatDateTime(info.start);
        document.getElementById("endDate").value = formatDateTime(info.end || info.start);
    }
    handleEventTypeChange();
    modal.classList.add("show");
}

function closeEventModal() {
    const modal = document.getElementById("eventModal");
    modal.classList.remove("show");
}

function handleParticipantTypeChange() {
    const participantTypeSelect = document.getElementById("participantType");
    const specificParticipantsContainer = document.getElementById("specificParticipantsContainer");
    if (!participantTypeSelect || !specificParticipantsContainer) return;

    if (["SPECIFIC_PROFESSORS", "STUDENT_GROUP", "CLASS"].includes(participantTypeSelect.value)) {
        specificParticipantsContainer.style.display = "block";
        fetchParticipants(participantTypeSelect.value);
    } else {
        specificParticipantsContainer.style.display = "none";
    }
}

function fetchParticipants(type) {
    let url = "";
    switch (type) {
        case "SPECIFIC_PROFESSORS":
            url = "/api/professors";
            break;
        case "STUDENT_GROUP":
            url = "/api/student-groups";
            break;
        case "CLASS":
            url = "/api/classes";
            break;
    }
    fetch(url)
        .then(response => response.json())
        .then(data => {
            const participantsSelect = document.getElementById("participants");
            participantsSelect.innerHTML = "";
            data.forEach(item => {
                const option = document.createElement("option");
                option.value = item.id;
                option.textContent = item.name;
                participantsSelect.appendChild(option);
            });
        })
        .catch(error => console.error("Erreur lors du chargement des participants :", error));
}

function handleEventTypeChange() {
    const type = document.getElementById("type").value;
    document.getElementById("courseFields").style.display =
        ["COURSE", "EXAM"].includes(type) ? "block" : "none";
    document.getElementById("examFields").style.display =
        type === "EXAM" ? "block" : "none";
    document.getElementById("meetingFields").style.display =
        type === "MEETING" ? "block" : "none";
}

async function updateEvent(event) {
    const data = {
        id: event.id,
        startTime: event.start.toISOString(),
        endTime: event.end ? event.end.toISOString() : null
    };
    try {
        const csrfToken = document.querySelector('meta[name="_csrf"]').content;
        const response = await fetch(`/calendar/api/events/${event.id}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json",
                "X-CSRF-TOKEN": csrfToken
            },
            body: JSON.stringify(data)
        });
        if (!response.ok) {
            throw new Error("Erreur lors de la mise à jour de l'événement");
        }
        showNotification("Événement mis à jour avec succès", "success");
    } catch (error) {
        showNotification(error.message, "error");
    }
}

function showNotification(message, type = "info") {
    const notification = document.createElement("div");
    notification.className = `notification ${type}`;
    notification.innerHTML = `
    <div class="notification-content">
      <i class="fas ${type === "success" ? "fa-check-circle" : "fa-exclamation-circle"}"></i>
      <span>${message}</span>
    </div>
    <button class="notification-close">×</button>
  `;
    document.body.appendChild(notification);
    setTimeout(() => notification.classList.add("show"), 100);
    notification.querySelector(".notification-close").addEventListener("click", () => {
        notification.classList.remove("show");
        setTimeout(() => notification.remove(), 300);
    });
    setTimeout(() => {
        notification.classList.remove("show");
        setTimeout(() => notification.remove(), 300);
    }, 5000);
}
