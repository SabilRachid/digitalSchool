document.addEventListener('DOMContentLoaded', function() {
    // Initialisation de la DataTable pour les salles
    const roomsTable = $('#roomsTable').DataTable({
        ajax: {
            url: '/admin/api/rooms/data',
            data: function(d) {
                // Ajout des paramètres de filtrage à la requête AJAX
                d.buildingFilter = $('#buildingFilter').val();
                d.floorFilter = $('#floorFilter').val();
                d.statusFilter = $('#statusFilter').val();
                d.equipmentFilter = $('#equipmentFilter').val();
            },
            dataSrc: ''
        },
        columns: [
            { data: 'name' },
            { data: 'buildingName' },
            { data: 'floorNumber' },
            { data: 'maxCapacity' },
            {
                data: 'equipment',
                render: data => data.join(', ')
            },
            { data: 'status' },
            {
                data: null,
                render: data => `
                    <button class="btn btn-sm btn-primary" onclick="roomsPage.editRoom(${data.id})">
                        <i class="fas fa-edit"></i>
                    </button>
                    <button class="btn btn-sm btn-danger" onclick="roomsPage.deleteRoom(${data.id})">
                        <i class="fas fa-trash"></i>
                    </button>
                `
            }
        ],
        language: {
            url: '/js/datatables/fr-FR.json'
        },
        responsive: true,
        order: [[0, 'asc']]
    });

    // Recharge la DataTable dès que l'un des filtres change
    $('#buildingFilter, #floorFilter, #statusFilter, #equipmentFilter').on('change', function() {
        console.log("🔄 Filtre modifié, rechargement de la DataTable...");
        roomsTable.ajax.reload();
    });

    // Initialisation du modal et du formulaire
    const roomModalEl = document.getElementById('roomModal');
    const roomModal = new bootstrap.Modal(roomModalEl);
    const roomForm = document.getElementById('roomForm');

    // Gestion de la soumission du formulaire pour ajout ou modification
    roomForm.addEventListener('submit', async function(e) {
        e.preventDefault();
        const formData = new FormData(roomForm);
        const data = {
            id: formData.get('id'),
            name: formData.get('name'),
            maxCapacity: parseInt(formData.get('maxCapacity')),
            buildingName: formData.get('buildingName'),
            floorNumber: parseInt(formData.get('floorNumber')),
            equipment: Array.from(formData.getAll('equipment')),
            status: formData.get('status'),
            accessible: formData.get('accessible') === 'on',
            description: formData.get('description'),
            maintenanceNotes: formData.get('maintenanceNotes')
        };

        try {
            const csrfToken = document.querySelector('meta[name="_csrf"]').content;
            console.log("💾 Sauvegarde des données :", data);
            const url = data.id ? `/admin/api/rooms/${data.id}` : '/admin/api/rooms';
            const method = data.id ? 'PUT' : 'POST';
            const response = await fetch(url, {
                method: method,
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': csrfToken
                },
                body: JSON.stringify(data)
            });

            if (!response.ok) {
                let error;
                try {
                    error = await response.json();
                } catch (e) {
                    error = { message: 'Erreur inconnue' };
                }
                throw new Error(error.message || 'Erreur lors de la sauvegarde');
            }

            console.log("✅ Sauvegarde réussie !");
            roomModal.hide();
            roomsTable.ajax.reload();
            showNotification('Opération réussie', 'success');
        } catch (error) {
            console.error('🚨 Erreur lors de la sauvegarde :', error);
            showNotification(error.message, 'error');
        }
    });

    // Objet global pour les actions sur les salles
    window.roomsPage = {
        openModal: function() {
            roomForm.reset();
            document.getElementById('id').value = '';
            roomModal.show();
        },
        editRoom: async function(id) {
            try {
                console.log("📌 Édition de la salle ID :", id);
                const response = await fetch(`/admin/api/rooms/${id}`);
                if (!response.ok) throw new Error('Erreur lors du chargement des données');
                const room = await response.json();
                populateForm(room);
                roomModal.show();
            } catch (error) {
                showNotification(error.message, 'error');
            }
        },
        deleteRoom: async function(id) {
            if (!confirm('Are you sure you want to delete this room?')) return;
            try {
                console.log("🗑 Suppression de la salle ID :", id);
                const csrfToken = document.querySelector('meta[name="_csrf"]').content;
                const response = await fetch(`/admin/api/rooms/${id}`, {
                    method: 'DELETE',
                    headers: {
                        'X-CSRF-TOKEN': csrfToken
                    }
                });
                if (!response.ok) throw new Error('Erreur lors de la suppression');
                console.log("✅ Suppression réussie !");
                roomsTable.ajax.reload();
                showNotification('Room deleted successfully', 'success');
            } catch (error) {
                showNotification(error.message, 'error');
            }
        }
    };

    function populateForm(room) {
        roomForm.elements['id'].value = room.id;
        roomForm.elements['name'].value = room.name;
        roomForm.elements['maxCapacity'].value = room.maxCapacity;
        roomForm.elements['buildingName'].value = room.buildingName;
        roomForm.elements['floorNumber'].value = room.floorNumber;
        roomForm.elements['status'].value = room.status;
        roomForm.elements['accessible'].checked = room.accessible;
        roomForm.elements['description'].value = room.description;
        roomForm.elements['maintenanceNotes'].value = room.maintenanceNotes;
        // Mise à jour de la sélection d'équipements
        const equipmentSelect = roomForm.elements['equipment'];
        Array.from(equipmentSelect.options).forEach(option => {
            option.selected = room.equipment.includes(option.value);
        });
    }

    function showNotification(message, type) {
        // Implémentation simple pour afficher la notification
        alert(message);
    }
});
