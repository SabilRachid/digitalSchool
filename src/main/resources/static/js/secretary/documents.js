// Gestion des documents
document.addEventListener('DOMContentLoaded', function() {
    initializeFilters();
    initializeDocumentForm();
    initializeSendForm();
});

// Initialisation des filtres
function initializeFilters() {
    const searchInput = document.querySelector('.search-box input');
    const classFilter = document.getElementById('classFilter');
    const typeFilter = document.getElementById('typeFilter');
    const periodFilter = document.getElementById('periodFilter');

    const filters = [searchInput, classFilter, typeFilter, periodFilter];
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
    const classId = document.getElementById('classFilter').value;
    const type = document.getElementById('typeFilter').value;
    const period = document.getElementById('periodFilter').value;

    document.querySelectorAll('.document-card').forEach(card => {
        const cardType = card.dataset.type;
        const cardClass = card.dataset.class;
        const cardPeriod = card.dataset.period;
        const cardText = card.textContent.toLowerCase();

        const matchesSearch = search === '' || cardText.includes(search);
        const matchesClass = classId === '' || cardClass === classId;
        const matchesType = type === '' || cardType === type;
        const matchesPeriod = period === '' || cardPeriod === period;

        card.style.display =
            matchesSearch && matchesClass && matchesType && matchesPeriod ?
                'flex' : 'none';
    });
}

// Initialisation du formulaire de document
function initializeDocumentForm() {
    const form = document.getElementById('documentForm');
    if (!form) return;

    // Afficher/masquer les champs selon le type de document
    document.getElementById('documentType').addEventListener('change', function(e) {
        const type = e.target.value;
        document.getElementById('bulletinFields').style.display =
            type === 'BULLETIN' ? 'block' : 'none';
        document.getElementById('studentFields').style.display =
            type === 'BULLETIN' ? 'none' : 'block';
    });

    form.addEventListener('submit', async function(e) {
        e.preventDefault();

        try {
            const formData = new FormData(this);
            const response = await fetch('/secretary/documents/generate', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                },
                body: JSON.stringify(Object.fromEntries(formData))
            });

            if (!response.ok) throw new Error('Erreur lors de la génération du document');

            closeDocumentModal();
            showNotification('Document généré avec succès', 'success');
            setTimeout(() => location.reload(), 1500);
        } catch (error) {
            showNotification(error.message, 'error');
        }
    });
}

// Initialisation du formulaire d'envoi
function initializeSendForm() {
    const form = document.getElementById('sendForm');
    if (!form) return;

    form.addEventListener('submit', async function(e) {
        e.preventDefault();

        try {
            const formData = new FormData(this);
            const documentId = this.dataset.documentId;

            const response = await fetch(`/secretary/documents/${documentId}/send`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                },
                body: JSON.stringify(Object.fromEntries(formData))
            });

            if (!response.ok) throw new Error('Erreur lors de l\'envoi du document');

            closeSendModal();
            showNotification('Document envoyé avec succès', 'success');
        } catch (error) {
            showNotification(error.message, 'error');
        }
    });
}

// Génération des bulletins
async function generateBulletins() {
    try {
        const response = await fetch('/secretary/documents/bulletins/generate', {
            method: 'POST',
            headers: {
                'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
            }
        });

        if (!response.ok) throw new Error('Erreur lors de la génération des bulletins');

        showNotification('Bulletins en cours de génération', 'success');
        setTimeout(() => location.reload(), 3000);
    } catch (error) {
        showNotification(error.message, 'error');
    }
}

// Téléchargement d'un document
async function downloadDocument(id) {
    try {
        const response = await fetch(`/secretary/documents/${id}/download`);
        if (!response.ok) throw new Error('Erreur lors du téléchargement');

        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = getDocumentFileName(id);
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
    } catch (error) {
        showNotification(error.message, 'error');
    }
}

// Aperçu d'un document
async function previewDocument(id) {
    try {
        const response = await fetch(`/secretary/documents/${id}/preview`);
        if (!response.ok) throw new Error('Erreur lors du chargement de l\'aperçu');

        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        window.open(url, '_blank');
        window.URL.revokeObjectURL(url);
    } catch (error) {
        showNotification(error.message, 'error');
    }
}

// Envoi d'un document
function sendDocument(id) {
    document.getElementById('sendForm').dataset.documentId = id;
    document.getElementById('sendModal').classList.add('show');
}

// Gestion des modales
function openDocumentModal() {
    document.getElementById('documentModal').classList.add('show');
}

function closeDocumentModal() {
    document.getElementById('documentModal').classList.remove('show');
    document.getElementById('documentForm').reset();
}

function closeSendModal() {
    document.getElementById('sendModal').classList.remove('show');
    document.getElementById('sendForm').reset();
}

// Utilitaires
function getDocumentFileName(id) {
    const card = document.querySelector(`.document-card[data-id="${id}"]`);
    if (!card) return 'document.pdf';

    const title = card.querySelector('h3').textContent;
    return title.toLowerCase().replace(/\s+/g, '-') + '.pdf';
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
