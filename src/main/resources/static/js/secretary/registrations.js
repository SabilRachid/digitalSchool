// Gestion des inscriptions
document.addEventListener('DOMContentLoaded', function() {
    initializeStats();
    initializeFilters();
    initializeForms();
});

// Initialisation des statistiques
function initializeStats() {
    fetch('/secretary/registrations/stats')
        .then(response => response.json())
        .then(stats => {
            document.getElementById('pendingCount').textContent = stats.pending;
            document.getElementById('approvedCount').textContent = stats.approved;
            document.getElementById('paymentPending').textContent = stats.paymentPending;
            document.getElementById('missingDocs').textContent = stats.missingDocs;
        })
        .catch(error => showNotification(error.message, 'error'));
}

// Initialisation des filtres
function initializeFilters() {
    const searchInput = document.querySelector('.search-box input');
    const statusFilter = document.getElementById('statusFilter');
    const classFilter = document.getElementById('classFilter');
    const paymentFilter = document.getElementById('paymentFilter');

    const filters = [searchInput, statusFilter, classFilter, paymentFilter];
    filters.forEach(filter => {
        filter.addEventListener('change', applyFilters);
        if (filter === searchInput) {
            filter.addEventListener('keyup', applyFilters);
        }
    });
}

// Application des filtres
function applyFilters() {
    const search = document.querySelector('.search-box input').value.toLowerCase();
    const status = document.getElementById('statusFilter').value;
    const classId = document.getElementById('classFilter').value;
    const payment = document.getElementById('paymentFilter').value;

    document.querySelectorAll('.registration-card').forEach(card => {
        const cardStatus = card.dataset.status;
        const cardPayment = card.dataset.payment;
        const cardClass = card.dataset.class;
        const cardText = card.textContent.toLowerCase();

        const matchesSearch = search === '' || cardText.includes(search);
        const matchesStatus = status === '' || cardStatus === status;
        const matchesClass = classId === '' || cardClass === classId;
        const matchesPayment = payment === '' || cardPayment === payment;

        card.style.display = 
            matchesSearch && matchesStatus && matchesClass && matchesPayment ? 
            'block' : 'none';
    });
}

// Initialisation des formulaires
function initializeForms() {
    initializeRegistrationForm();
    initializeRejectForm();
}

// Initialisation du formulaire d'inscription
function initializeRegistrationForm() {
    const form = document.getElementById('registrationForm');
    if (!form) return;

    form.addEventListener('submit', async function(e) {
        e.preventDefault();
        
        try {
            const formData = new FormData(this);
            const data = {
                student: {
                    firstName: formData.get('student.firstName'),
                    lastName: formData.get('student.lastName'),
                    birthDate: formData.get('student.birthDate'),
                    gender: formData.get('student.gender')
                },
                parent: {
                    firstName: formData.get('parent.firstName'),
                    lastName: formData.get('parent.lastName'),
                    email: formData.get('parent.email'),
                    phone: formData.get('parent.phone')
                },
                classe: {
                    id: formData.get('classe.id')
                },
                schoolYear: formData.get('schoolYear')
            };

            const response = await fetch('/secretary/registrations', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                },
                body: JSON.stringify(data)
            });

            if (!response.ok) throw new Error('Erreur lors de la création de l\'inscription');

            closeRegistrationModal();
            showNotification('Inscription créée avec succès', 'success');
            setTimeout(() => location.reload(), 1500);
        } catch (error) {
            showNotification(error.message, 'error');
        }
    });
}

    // Initialisation du formulaire de rejet
    function initializeRejectForm() {
        const form = document.getElementById('rejectForm');
        if (!form) return;

        form.addEventListener('submit', async function(e) {
            e.preventDefault();

            try {
                const registrationId = this.dataset.registrationId;
                const reason = document.getElementById('rejectReason').value;

                const response = await fetch(`/secretary/registrations/${registrationId}/reject`, {
        method: 'POST',
            headers: {
            'Content-Type': 'application/json',
                'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
        },
        body: JSON.stringify({ reason })
    });

    if (!response.ok) throw new Error('Erreur lors du rejet de l\'inscription');

    closeRejectModal();
    showNotification('Inscription rejetée', 'success');
    setTimeout(() => location.reload(), 1500);
    } catch (error) {
        showNotification(error.message, 'error');
}
});
}

// Approbation d'une inscription
async function approveRegistration(id) {
    if (!confirm('Êtes-vous sûr de vouloir approuver cette inscription ?')) return;

    try {
        const response = await fetch(`/secretary/registrations/${id}/approve`, {
            method: 'POST',
            headers: {
                'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
            }
        });

        if (!response.ok) throw new Error('Erreur lors de l\'approbation');

        showNotification('Inscription approuvée avec succès', 'success');
        setTimeout(() => location.reload(), 1500);
    } catch (error) {
        showNotification(error.message, 'error');
    }
}

// Rejet d'une inscription
function rejectRegistration(id) {
    const form = document.getElementById('rejectForm');
    form.dataset.registrationId = id;
    document.getElementById('rejectModal').classList.add('show');
}

// Envoi d'un rappel
async function sendReminder(id) {
    try {
        const response = await fetch(`/secretary/registrations/${id}/payment/remind`, {
            method: 'POST',
            headers: {
                'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
            }
        });

        if (!response.ok) throw new Error('Erreur lors de l\'envoi du rappel');

        showNotification('Rappel envoyé avec succès', 'success');
    } catch (error) {
        showNotification(error.message, 'error');
    }
}

// Affichage des détails
async function viewDetails(id) {
    try {
        const response = await fetch(`/secretary/registrations/${id}`);
        if (!response.ok) throw new Error('Erreur lors du chargement des détails');

        const registration = await response.json();
        displayDetails(registration);
        document.getElementById('detailsModal').classList.add('show');
    } catch (error) {
        showNotification(error.message, 'error');
    }
}

// Affichage des détails dans la modal
function displayDetails(registration) {
    // Informations générales
    document.getElementById('detailRegistrationNumber').textContent = registration.registrationNumber;
    document.getElementById('detailRegistrationDate').textContent =
        formatDate(registration.registrationDate);
    document.getElementById('detailStatus').textContent = formatStatus(registration.status);

    // Informations de l'élève
    document.getElementById('detailStudentName').textContent =
        `${registration.student.firstName} ${registration.student.lastName}`;
    document.getElementById('detailStudentBirthDate').textContent =
        formatDate(registration.student.birthDate);
    document.getElementById('detailStudentClass').textContent = registration.classe.name;

    // Informations du parent
    document.getElementById('detailParentName').textContent =
        `${registration.parent.firstName} ${registration.parent.lastName}`;
    document.getElementById('detailParentEmail').textContent = registration.parent.email;
    document.getElementById('detailParentPhone').textContent = registration.parent.phone;

    // Documents
    const documentsContainer = document.getElementById('detailDocuments');
    documentsContainer.innerHTML = registration.documents.map(doc => `
        <div class="document-item">
            <i class="fas fa-file-alt"></i>
            <span>${doc.type}</span>
            <button class="btn btn-sm btn-secondary" onclick="viewDocument('${doc.id}')">
                <i class="fas fa-eye"></i>
            </button>
        </div>
    `).join('');

    // Paiement
    document.getElementById('detailPaymentStatus').textContent =
        formatPaymentStatus(registration.paymentStatus);
    document.getElementById('detailPaymentDueDate').textContent =
        formatDate(registration.paymentDueDate);
    document.getElementById('detailReminderCount').textContent = registration.reminderCount;
}

// Visualisation d'un document
async function viewDocument(id) {
    try {
        const response = await fetch(`/secretary/registrations/documents/${id}`);
        if (!response.ok) throw new Error('Erreur lors du chargement du document');

        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        window.open(url, '_blank');
        window.URL.revokeObjectURL(url);
    } catch (error) {
        showNotification(error.message, 'error');
    }
}

// Gestion des modales
function openRegistrationModal() {
    document.getElementById('registrationModal').classList.add('show');
}

function closeRegistrationModal() {
    document.getElementById('registrationModal').classList.remove('show');
    document.getElementById('registrationForm').reset();
}

function closeRejectModal() {
    document.getElementById('rejectModal').classList.remove('show');
    document.getElementById('rejectForm').reset();
}

function closeDetailsModal() {
    document.getElementById('detailsModal').classList.remove('show');
}

// Utilitaires
function formatDate(date) {
    return new Date(date).toLocaleDateString('fr-FR', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric'
    });
}

function formatStatus(status) {
    switch (status) {
        case 'PENDING': return 'En attente';
        case 'APPROVED': return 'Approuvée';
        case 'REJECTED': return 'Refusée';
        default: return status;
    }
}

function formatPaymentStatus(status) {
    switch (status) {
        case 'PENDING': return 'En attente';
        case 'PAID': return 'Payé';
        case 'PARTIAL': return 'Partiel';
        default: return status;
    }
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
