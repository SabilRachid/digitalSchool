
document.addEventListener('DOMContentLoaded', function() {
    // Configuration du calendrier principal
    const calendarEl = document.getElementById('calendar');
    const calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'timeGridWeek',
        headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: 'dayGridMonth,timeGridWeek,timeGridDay,listWeek'
        },
        locale: 'fr',
        firstDay: 1,
        slotMinTime: '07:00:00',
        slotMaxTime: '20:00:00',
        allDaySlot: true,
        height: '100%',
        nowIndicator: true,
        selectable: true,
        selectMirror: true,
        editable: true,
        dayMaxEvents: true,

        // Gestion des événements
        select: function(info) {
            openEventModal(info);
        },
        eventClick: function(info) {
            editEvent(info.event);
        },
        eventDrop: function(info) {
            updateEvent(info.event);
        },
        eventResize: function(info) {
            updateEvent(info.event);
        },

        // Style des événements
        eventDidMount: function(info) {
            const event = info.event;
            const eventEl = info.el;

            // Ajouter des icônes selon le type
            const titleEl = eventEl.querySelector('.fc-event-title');
            if (titleEl) {
                let icon = '';
                switch (event.extendedProps.type) {
                    case 'COURSE':
                        icon = '<i class="fas fa-book"></i> ';
                        break;
                    case 'EXAM':
                        icon = '<i class="fas fa-file-alt"></i> ';
                        break;
                    case 'EVENT':
                        icon = '<i class="fas fa-calendar-day"></i> ';
                        break;
                    case 'MEETING':
                        icon = '<i class="fas fa-users"></i> ';
                        break;
                }
                titleEl.innerHTML = icon + event.title;
            }
        },

        // Chargement des événements
        events: function(info, successCallback, failureCallback) {
            const token = document.querySelector('meta[name="_csrf"]').content;
            const header = document.querySelector('meta[name="_csrf_header"]').content;

            fetch(`/api/events?start=${info.startStr}&end=${info.endStr}`, {
                method: 'GET',
                headers: {
                    'Accept': 'application/json',
                    [header]: token
                }
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Erreur réseau');
                }
                return response.json();
            })
            .then(data => {
                const events = Array.isArray(data) ? data : [];
                const formattedEvents = events.map(event => ({
                    id: event.id,
                    title: event.title,
                    start: event.startTime,
                    end: event.endTime,
                    allDay: event.allDay,
                    extendedProps: {
                        type: event.type,
                        location: event.location,
                        description: event.description
                    },
                    backgroundColor: getEventColor(event.type),
                    textColor: '#FFFFFF',
                    className: event.type.toLowerCase()
                }));
                successCallback(formattedEvents);
            })
            .catch(error => {
                console.error('Erreur lors du chargement des événements:', error);
                failureCallback(error);
            });
        }
    });

    calendar.render();

    // Configuration du mini-calendrier
    const miniCalendarEl = document.getElementById('miniCalendar');
    const miniCalendar = new FullCalendar.Calendar(miniCalendarEl, {
        initialView: 'dayGridMonth',
        headerToolbar: {
            left: 'prev',
            center: 'title',
            right: 'next'
        },
        locale: 'fr',
        firstDay: 1,
        height: 'auto',
        selectable: true,
        dateClick: function(info) {
            calendar.gotoDate(info.date);
        }
    });

    miniCalendar.render();

    // Gestion des filtres
    const filters = {
        'showCourses': 'COURSE',
        'showEvents': 'EVENT',
        'showExams': 'EXAM'
    };

    Object.keys(filters).forEach(filterId => {
        document.getElementById(filterId).addEventListener('change', function(e) {
            toggleEventType(filters[filterId], e.target.checked);
        });
    });

    function toggleEventType(type, show) {
        const events = calendar.getEvents();
        events.forEach(event => {
            if (event.extendedProps.type === type) {
                event.setProp('display', show ? 'auto' : 'none');
            }
        });
    }

    // Gestion du modal d'événement
    function openEventModal(info = null) {
        const modal = document.getElementById('eventModal');
        const form = document.getElementById('eventForm');
        form.reset();

        if (info) {
            document.getElementById('startDate').value = formatDateTime(info.start);
            document.getElementById('endDate').value = formatDateTime(info.end || info.start);
        }

        modal.classList.add('show');
    }

    function closeEventModal() {
        const modal = document.getElementById('eventModal');
        modal.classList.remove('show');
    }

    // Gestion du formulaire d'événement
    document.getElementById('eventForm').addEventListener('submit', async function(e) {
        e.preventDefault();
        
        const formData = new FormData(this);
        const data = {
            title: formData.get('title'),
            type: formData.get('type'),
            startTime: formData.get('startDate'),
            endTime: formData.get('endDate'),
            location: formData.get('location'),
            description: formData.get('description'),
            allDay: formData.get('allDay') === 'on'
        };

        const eventId = formData.get('id');
        if (eventId) {
            data.id = eventId;
        }

        try {
            const token = document.querySelector('meta[name="_csrf"]').content;
            const header = document.querySelector('meta[name="_csrf_header"]').content;

            const response = await fetch('/api/events', {
                method: eventId ? 'PUT' : 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json',
                    [header]: token
                },
                body: JSON.stringify(data)
            });

            if (!response.ok) {
                throw new Error('Erreur lors de la sauvegarde');
            }

            closeEventModal();
            calendar.refetchEvents();
            showNotification('Événement sauvegardé avec succès', 'success');
        } catch (error) {
            console.error('Erreur:', error);
            showNotification(error.message, 'error');
        }
    });

    // Fonctions utilitaires
    function formatDateTime(date) {
        return date.toISOString().slice(0, 16);
    }

    function getEventColor(type) {
        switch (type) {
            case 'COURSE':
                return '#4C51BF';
            case 'EXAM':
                return '#ED8936';
            case 'EVENT':
                return '#48BB78';
            case 'MEETING':
                return '#4299E1';
            default:
                return '#718096';
        }
    }

    function showNotification(message, type = 'info') {
        const notification = document.createElement('div');
        notification.className = `notification ${type}`;
        notification.innerHTML = `
            <div class="notification-content">
                <i class="fas ${type === 'success' ? 'fa-check-circle' : 'fa-exclamation-circle'}"></i>
                <span>${message}</span>
            </div>
            <button class="notification-close">×</button>
        `;

        document.body.appendChild(notification);

        setTimeout(() => {
            notification.classList.add('show');
        }, 100);

        setTimeout(() => {
            notification.classList.remove('show');
            setTimeout(() => {
                notification.remove();
            }, 300);
        }, 5000);

        notification.querySelector('.notification-close').addEventListener('click', () => {
            notification.classList.remove('show');
            setTimeout(() => {
                notification.remove();
            }, 300);
        });
    }

    // Mise à jour des événements à venir
    function updateUpcomingEvents() {
        const upcomingList = document.getElementById('upcomingEventsList');
        const events = calendar.getEvents()
            .filter(event => event.start >= new Date())
            .sort((a, b) => a.start - b.start)
            .slice(0, 5);

        upcomingList.innerHTML = events.map(event => `
            <div class="upcoming-event ${event.extendedProps.type.toLowerCase()}">
                <div class="event-time">
                    ${formatEventTime(event)}
                </div>
                <div class="event-details">
                    <div class="event-title">${event.title}</div>
                    ${event.extendedProps.location ? 
                        `<div class="event-location">
                            <i class="fas fa-map-marker-alt"></i> ${event.extendedProps.location}
                        </div>` : ''}
                </div>
            </div>
        `).join('');
    }

    function formatEventTime(event) {
        if (event.allDay) {
            return 'Toute la journée';
        }
        return event.start.toLocaleTimeString('fr-FR', {
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    // Mettre à jour la liste des événements à venir lors des changements
    calendar.on('eventAdd', updateUpcomingEvents);
    calendar.on('eventChange', updateUpcomingEvents);
    calendar.on('eventRemove', updateUpcomingEvents);
    updateUpcomingEvents();
});
