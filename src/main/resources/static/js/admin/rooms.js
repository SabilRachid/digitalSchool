document.addEventListener('DOMContentLoaded', function() {
    const roomsTable = $('#roomsTable').DataTable({
        ajax: {
            url: '/admin/api/rooms/data',
            dataSrc: ''
        },
        columns: [
            { data: 'name' },
            { data: 'buildingName' },
            { data: 'floorNumber' },
            { data: 'maxCapacity' },
            { data: 'equipment', render: data => data.join(', ') },
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

    const roomModal = new bootstrap.Modal(document.getElementById('roomModal'));
    const roomForm = document.getElementById('roomForm');

    roomForm.addEventListener('submit', async function(e) {
        e.preventDefault();
        const formData = new FormData(roomForm);
        const data = {
            id: formData.get('id'),
            name: formData.get('name'),
            maxCapacity: formData.get('maxCapacity'),
            buildingName: formData.get('buildingName'),
            floorNumber: formData.get('floorNumber'),
            equipment: Array.from(formData.getAll('equipment')),
            status: formData.get('status'),
            accessible: formData.get('accessible') === 'on',
            description: formData.get('description'),
            maintenanceNotes: formData.get('maintenanceNotes')
        };

        try {
            const response = await fetch(data.id ? `/admin/api/rooms/${data.id}` : '/admin/rooms', {
                method: data.id ? 'PUT' : 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                },
                body: JSON.stringify(data)
            });

            if (!response.ok) {
                const error = await response.json();
                throw new Error(error.message);
            }

            roomModal.hide();
            roomsTable.ajax.reload();
            showNotification('Operation successful', 'success');
        } catch (error) {
            showNotification(error.message, 'error');
        }
    });

    window.roomsPage = {
        openModal: function() {
            roomForm.reset();
            document.getElementById('id').value = '';
            roomModal.show();
        },
        editRoom: async function(id) {
            try {
                const response = await fetch(`/admin/api/rooms/${id}`);
                if (!response.ok) throw new Error('Error loading data');

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
                const response = await fetch(`/admin/api/rooms/${id}`, {
                    method: 'DELETE',
                    headers: {
                        'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                    }
                });

                if (!response.ok) throw new Error('Error deleting room');

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

        // Set equipment
        const equipmentSelect = roomForm.elements['equipment'];
        Array.from(equipmentSelect.options).forEach(option => {
            option.selected = room.equipment.includes(option.value);
        });
    }

    function showNotification(message, type) {
        // Implement according to your notification system
        alert(message);
    }
});