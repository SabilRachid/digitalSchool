// Gestion de l'emploi du temps
document.addEventListener('DOMContentLoaded', function() {
    initializeCalendar();
    initializeFilters();
    initializeDistributionChart();
});

let calendar;

// Initialisation du calendrier
function initializeCalendar() {
    const calendarEl = document.getElementById('calendar');
    if (!calendarEl) return;

    calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'timeGridWeek',
        headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: 'timeGridDay,timeGridWeek,dayGridMonth'
        },
        locale: 'fr',
        firstDay: 1,
        slotMinTime: '07:00:00',
        slotMaxTime: '20:00:00',
        allDaySlot: true,
        height: '100%',
        nowIndicator: true,
        events: function(info, successCallback, failureCallback) {
            const childId = getCurrentChildId();
            if (!childId) {
                successCallback([]);
                return;
            }

            fetch(`/parent/schedule/child/${childId}?start=${info.startStr}&end=${info.endStr}`)
                .then(response => response.json())
                .then(events => {
                    const formattedEvents = events.map(event => ({
                        id: event.id,
                        title: event.title,
                        start: event.start,
                        end: event.end,
                        backgroundColor: getEventColor(event.type),
                        borderColor: getEventColor(event.type),
                        extendedProps: {
                            type: event.type,
                            location: event.location,
                            professor: event.professor,
                            description: event.description
                        }
                    }));
                    successCallback(formattedEvents);
                })
                .catch(error => {
                    console.error('Error fetching events:', error);
                    failureCallback(error);
                });
        },
        eventClick: function(info) {
            showEventDetails(info.event);
        }
    });

    calendar.render();
}

// Initialisation des filtres
function initializeFilters() {
    // Filtres de vue
    document.querySelectorAll('.view-filters button').forEach(button => {
        button.addEventListener('click', function() {
            const view = this.dataset.view;
            calendar.changeView(view);

            // Mettre à jour l'état actif
            document.querySelectorAll('.view-filters button').forEach(btn => {
                btn.classList.remove('active');
            });
            this.classList.add('active');
        });
    });

    // Filtres d'événements
    document.querySelectorAll('.event-filters input').forEach(input => {
        input.addEventListener('change', function() {
            const type = this.dataset.type;
            toggleEventType(type, this.checked);
        });
    });
}

// Sélection d'un enfant
function selectChild(element) {
    // Mettre à jour la sélection active
    document.querySelectorAll('.child-tab').forEach(tab => {
        tab.classList.remove('active');
    });
    element.classList.add('active');

    // Recharger le calendrier
    calendar.refetchEvents();

    // Charger les statistiques
    loadChildStats(element.dataset.childId);
}

// Chargement des statistiques
async function loadChildStats(childId) {
    try {
        const response = await fetch(`/parent/schedule/stats/${childId}`);
        if (!response.ok) throw new Error('Erreur lors du chargement des statistiques');

        const stats = await response.json();
        updateStats(stats);
        updateDistributionChart(stats.subjectDistribution);
    } catch (error) {
        showNotification(error.message, 'error');
    }
}

// Mise à jour des statistiques
function updateStats(stats) {
    document.getElementById('weeklyHours').textContent = stats.weeklyHours;
    document.getElementById('upcomingExams').textContent = stats.upcomingExams;
    document.getElementById('upcomingEvents').textContent = stats.upcomingEvents;
}

// Initialisation du graphique de répartition
function initializeDistributionChart() {
    const ctx = document.getElementById('subjectDistributionChart').getContext('2d');
    new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: [],
            datasets: [{
                data: [],
                backgroundColor: [
                    '#4F46E5',
                    '#059669',
                    '#DC2626',
                    '#D97706',
                    '#7C3AED',
                    '#2563EB'
                ]
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom'
                }
            }
        }
    });
}

// Mise à jour du graphique de répartition
function updateDistributionChart(distribution) {
    const chart = Chart.getChart('subjectDistributionChart');
    if (!chart) return;

    chart.data.labels = Object.keys(distribution);
    chart.data.datasets[0].data = Object.values(distribution);
    chart.update();
}

// Affichage des détails d'un événement
function showEventDetails(event) {
    const modal = document.getElementById('eventModal');
    const props = event.extendedProps;

    document.getElementById('eventTitle').textContent = event.title;
    document.getElementById('eventTime').textContent = formatEventTime(event);
    document.getElementById('eventLocation').textContent = props.location || 'Non spécifié';

    const teacherContainer = document.getElementById('eventTeacherContainer');
    if (props.professor) {
        teacherContainer.style.display = 'flex';
        document.getElementById('eventTeacher').textContent = props.professor;
    } else {
        teacherContainer.style.display = 'none';
    }

    const descriptionContainer = document.getElementById('eventDescriptionContainer');
    if (props.description) {
        descriptionContainer.style.display = 'flex';
        document.getElementById('eventDescription').textContent = props.description;
    } else {
        descriptionContainer.style.display = 'none';
    }

    modal.classList.add('show');
}

// Fermeture de la modal
function closeEventModal() {
    document.getElementById('eventModal').classList.remove('show');
}

// Utilitaires
function getCurrentChildId() {
    const activeTab = document.querySelector('.child-tab.active');
    return activeTab ? activeTab.dataset.childId : null;
}

function getEventColor(type) {
    switch (type) {
        case 'COURSE':
            return '#4F46E5';
        case 'EXAM':
            return '#DC2626';
        case 'EVENT':
            return '#059669';
        default:
            return '#6B7280';
    }
}

function formatEventTime(event) {
    const start = event.start.toLocaleTimeString('fr-FR', {
        hour: '2-digit',
        minute: '2-digit'
    });
    const end = event.end.toLocaleTimeString('fr-FR', {
        hour: '2-digit',
        minute: '2-digit'
    });
    return `${start} - ${end}`;
}

function toggleEventType(type, show) {
    const events = calendar.getEvents();
    events.forEach(event => {
        if (event.extendedProps.type === type.toUpperCase()) {
            event.setProp('display', show ? 'auto' : 'none');
        }
    });
}

// Notifications
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
