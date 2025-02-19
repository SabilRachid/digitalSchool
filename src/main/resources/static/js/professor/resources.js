document.addEventListener('DOMContentLoaded', function() {
    loadResources();
    initializeResourceForm();
});

// Chargement des ressources pour le professeur
async function loadResources() {
    try {
        const response = await fetch('/professor/api/resources/data');
        if (!response.ok) throw new Error('Erreur lors du chargement des ressources');
        const resources = await response.json();
        displayResources(resources);
    } catch (error) {
        showNotification(error.message, 'error');
    }
}

function displayResources(resources) {
    const tbody = document.querySelector('#resourcesTable tbody');
    tbody.innerHTML = resources.map(resource => `
    <tr data-resource-id="${resource.id}">
      <td>${resource.title}</td>
      <td>${resource.type}</td>
      <td>${resource.courseName || '-'}</td>
      <td>
        <button class="btn btn-sm btn-primary" onclick="resourcesPage.editResource(${resource.id})">
          <i class="fas fa-edit"></i> Modifier
        </button>
        <button class="btn btn-sm btn-danger" onclick="resourcesPage.deleteResource(${resource.id})">
          <i class="fas fa-trash"></i> Supprimer
        </button>
      </td>
    </tr>
  `).join('');
}

// Initialisation du formulaire de ressource
function initializeResourceForm() {
    const form = document.getElementById('resourceForm');
    form.addEventListener('submit', async function(e) {
        e.preventDefault();
        const formData = new FormData(form);
        // Pour les fichiers, vous pouvez utiliser FormData et l'envoyer via multipart/form-data,
        // ici on suppose une logique simplifiée avec JSON
        const data = {
            id: formData.get('id'),
            title: formData.get('title'),
            type: formData.get('type'),
            // Si un fichier est sélectionné, vous devrez gérer son upload différemment (par exemple via un autre endpoint)
            // file: formData.get('file'),
            course: { id: formData.get('course') },
            description: formData.get('description')
        };

        try {
            const csrfToken = document.querySelector('meta[name="_csrf"]').content;
            const url = data.id ? `/professor/api/resources/${data.id}` : '/professor/api/resources';
            const method = data.id ? 'PUT' : 'POST';
            const response = await fetch(url, {
                method: method,
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': csrfToken
                },
                body: JSON.stringify(data)
            });
            if (!response.ok) throw new Error('Erreur lors de l\'enregistrement de la ressource');
            closeResourceModal();
            loadResources();
            showNotification('Ressource enregistrée avec succès', 'success');
        } catch (error) {
            showNotification(error.message, 'error');
        }
    });
}

// Fonctions d'ouverture et fermeture du modal
function openResourceModal() {
    document.getElementById('resourceForm').reset();
    document.getElementById('id').value = '';
    const modal = new bootstrap.Modal(document.getElementById('resourceModal'));
    modal.show();
}

function closeResourceModal() {
    const modalEl = document.getElementById('resourceModal');
    const modalInstance = bootstrap.Modal.getInstance(modalEl);
    if (modalInstance) {
        modalInstance.hide();
    }
}

// Objets globaux pour l'usage dans le DOM
window.resourcesPage = {
    openModal: openResourceModal,
    editResource: async function(id) {
        try {
            const response = await fetch(`/professor/api/resources/${id}`);
            if (!response.ok) throw new Error('Erreur lors du chargement de la ressource');
            const resource = await response.json();
            populateResourceForm(resource);
            const modal = new bootstrap.Modal(document.getElementById('resourceModal'));
            modal.show();
        } catch (error) {
            showNotification(error.message, 'error');
        }
    },
    deleteResource: async function(id) {
        if (!confirm('Êtes-vous sûr de vouloir supprimer cette ressource ?')) return;
        try {
            const csrfToken = document.querySelector('meta[name="_csrf"]').content;
            const response = await fetch(`/professor/api/resources/${id}`, {
                method: 'DELETE',
                headers: { 'X-CSRF-TOKEN': csrfToken }
            });
            if (!response.ok) throw new Error('Erreur lors de la suppression de la ressource');
            loadResources();
            showNotification('Ressource supprimée avec succès', 'success');
        } catch (error) {
            showNotification(error.message, 'error');
        }
    }
};

function populateResourceForm(resource) {
    const form = document.getElementById('resourceForm');
    form.elements['id'].value = resource.id || '';
    form.elements['title'].value = resource.title || '';
    form.elements['type'].value = resource.type || '';
    if(resource.course) {
        form.elements['course'].value = resource.course.id;
    }
    form.elements['description'].value = resource.description || '';
}

// Fonction de notification
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
