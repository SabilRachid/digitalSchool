
// Gestion des dropdowns
function toggleDropdown(id) {
    const dropdown = document.getElementById(id);
    const allDropdowns = document.querySelectorAll('.dropdown-content');
    
    // Fermer tous les autres dropdowns
    allDropdowns.forEach(d => {
        if (d.id !== id) {
            d.classList.remove('show');
        }
    });
    
    // Basculer le dropdown actuel
    dropdown.classList.toggle('show');
}

// Gestion du menu profil
document.addEventListener('DOMContentLoaded', function() {
    // Fermer les dropdowns au clic en dehors
    document.addEventListener('click', (e) => {
        if (!e.target.matches('.notification-icon') && 
            !e.target.matches('.user-profile') && 
            !e.target.matches('.fas')) {
            const dropdowns = document.querySelectorAll('.dropdown-content');
            dropdowns.forEach(dropdown => {
                dropdown.classList.remove('show');
            });
        }
    });

    // Fermer les dropdowns avec la touche Echap
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') {
            const dropdowns = document.querySelectorAll('.dropdown-content');
            dropdowns.forEach(dropdown => {
                dropdown.classList.remove('show');
            });
        }
    });
});
