class PillSelect {
    constructor(containerId) {
        this.container = document.getElementById(containerId);
        this.selectedPills = new Set();
        this.init();
    }

    init() {
        const container = this.container.closest('.pill-select-container');
        const pillOptions = container.querySelectorAll('.pill-option');
        const hiddenInput = container.querySelector('input[type="hidden"]');

        pillOptions.forEach(option => {
            option.addEventListener('click', () => {
                const id = option.dataset.id;
                const name = option.textContent.trim();

                if (option.classList.contains('selected')) {
                    this.removePill(id);
                    option.classList.remove('selected');
                } else {
                    this.addPill(id, name);
                    option.classList.add('selected');
                }

                // Mettre à jour l'input caché
                this.updateHiddenInput(hiddenInput);
            });
        });
    }

    addPill(id, name) {
        if (this.selectedPills.has(id)) return;

        const pillsContainer = this.container.closest('.pill-select-container')
            .querySelector('.selected-pills');

        const pill = document.createElement('div');
        pill.className = 'selected-pill';
        pill.dataset.id = id;
        pill.innerHTML = `
            ${name}
            <button type="button" class="remove-pill" aria-label="Remove">×</button>
        `;

        pill.querySelector('.remove-pill').addEventListener('click', () => {
            this.removePill(id);
            this.container.closest('.pill-select-container')
                .querySelector(`.pill-option[data-id="${id}"]`)
                .classList.remove('selected');
        });

        pillsContainer.appendChild(pill);
        this.selectedPills.add(id);
    }

    removePill(id) {
        const pillsContainer = this.container.closest('.pill-select-container')
            .querySelector('.selected-pills');
        const pill = pillsContainer.querySelector(`.selected-pill[data-id="${id}"]`);
        
        if (pill) {
            pill.remove();
            this.selectedPills.delete(id);
            
            // Mettre à jour l'input caché
            const hiddenInput = this.container.closest('.pill-select-container')
                .querySelector('input[type="hidden"]');
            this.updateHiddenInput(hiddenInput);
        }
    }

    updateHiddenInput(hiddenInput) {
        hiddenInput.value = Array.from(this.selectedPills).join(',');
        // Déclencher un événement change pour notifier les écouteurs
        hiddenInput.dispatchEvent(new Event('change', { bubbles: true }));
    }

    // Pour pré-sélectionner des valeurs
    setValues(values) {
        // Réinitialiser la sélection actuelle
        this.selectedPills.clear();
        const pillsContainer = this.container.closest('.pill-select-container')
            .querySelector('.selected-pills');
        pillsContainer.innerHTML = '';

        // Désélectionner toutes les options
        const options = this.container.closest('.pill-select-container')
            .querySelectorAll('.pill-option');
        options.forEach(option => option.classList.remove('selected'));

        // Ajouter les nouvelles valeurs
        values.forEach(value => {
            const option = this.container.closest('.pill-select-container')
                .querySelector(`.pill-option[data-id="${value.id}"]`);
            if (option) {
                this.addPill(value.id, option.textContent.trim());
                option.classList.add('selected');
            }
        });

        // Mettre à jour l'input caché
        const hiddenInput = this.container.closest('.pill-select-container')
            .querySelector('input[type="hidden"]');
        this.updateHiddenInput(hiddenInput);
    }
}

// Initialisation
document.addEventListener('DOMContentLoaded', () => {
    const pillSelects = document.querySelectorAll('[data-pill-select]');
    pillSelects.forEach(select => {
        new PillSelect(select.id);
    });
});