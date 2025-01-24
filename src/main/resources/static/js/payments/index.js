const paymentManager = {
    init() {
        this.initializeDataTable();
        this.initializeFilters();
        this.initializeForm();
    },

    initializeDataTable() {
        this.table = $('#paymentsTable').DataTable({
            language: {
                url: '/js/datatables/fr-FR.json'
            },
            order: [[1, 'desc']],
            responsive: true
        });
    },

    initializeFilters() {
        $('#statusFilter, #periodFilter').on('change', () => this.applyFilters());
        $('#searchInput').on('keyup', () => this.applyFilters());
    },

    initializeForm() {
        $('#student').on('change', (e) => this.loadParents(e.target.value));
        
        $('#paymentForm').on('submit', (e) => {
            e.preventDefault();
            this.processPayment();
        });
    },

    applyFilters() {
        const status = $('#statusFilter').val();
        const period = $('#periodFilter').val();
        const search = $('#searchInput').val();

        this.table.columns(5).search(status);
        this.table.search(search);
        this.table.draw();
    },

    async loadParents(studentId) {
        try {
            const response = await fetch(`/api/students/${studentId}/parents`);
            const parents = await response.json();
            
            const parentSelect = $('#parent');
            parentSelect.empty();
            parentSelect.append('<option value="">Sélectionner un parent</option>');
            
            parents.forEach(parent => {
                parentSelect.append(`
                    <option value="${parent.id}">
                        ${parent.firstName} ${parent.lastName}
                    </option>
                `);
            });
        } catch (error) {
            console.error('Error loading parents:', error);
            this.showNotification('Erreur lors du chargement des parents', 'error');
        }
    },

    async processPayment() {
        try {
            const formData = new FormData($('#paymentForm')[0]);
            const data = Object.fromEntries(formData.entries());

            const response = await fetch('/payments/initiate', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                },
                body: JSON.stringify(data)
            });

            if (!response.ok) {
                throw new Error('Erreur lors de l\'initialisation du paiement');
            }

            const result = await response.json();
            window.location.href = result.paymentUrl;
        } catch (error) {
            console.error('Payment error:', error);
            this.showNotification(error.message, 'error');
        }
    },

    async viewDetails(paymentId) {
        try {
            const response = await fetch(`/payments/${paymentId}`);
            const payment = await response.json();
            
            // Afficher les détails dans une modal
            // TODO: Implémenter l'affichage des détails
        } catch (error) {
            console.error('Error loading payment details:', error);
            this.showNotification('Erreur lors du chargement des détails', 'error');
        }
    },

    async downloadReceipt(paymentId) {
        try {
            const response = await fetch(`/payments/receipt/${paymentId}`);
            if (!response.ok) throw new Error('Erreur lors du téléchargement');
            
            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `recu-${paymentId}.pdf`;
            document.body.appendChild(a);
            a.click();
            window.URL.revokeObjectURL(url);
            document.body.removeChild(a);
        } catch (error) {
            console.error('Download error:', error);
            this.showNotification('Erreur lors du téléchargement du reçu', 'error');
        }
    },

    openPaymentModal() {
        $('#paymentModal').addClass('show');
    },

    closeModal() {
        $('#paymentModal').removeClass('show');
        $('#paymentForm')[0].reset();
    },

    showNotification(message, type = 'info') {
        const notification = $(`
            <div class="notification ${type}">
                <div class="notification-content">
                    <i class="fas ${type === 'success' ? 'fa-check-circle' : 'fa-exclamation-circle'}"></i>
                    <span>${message}</span>
                </div>
                <button class="notification-close">&times;</button>
            </div>
        `);

        $('body').append(notification);
        setTimeout(() => notification.addClass('show'), 100);

        notification.find('.notification-close').on('click', () => {
            notification.removeClass('show');
            setTimeout(() => notification.remove(), 300);
        });

        setTimeout(() => {
            notification.removeClass('show');
            setTimeout(() => notification.remove(), 300);
        }, 5000);
    }
};

// Initialisation
$(document).ready(() => {
    paymentManager.init();
});
C
