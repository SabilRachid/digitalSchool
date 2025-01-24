document.addEventListener('DOMContentLoaded', function() {
    // Gestion des groupes de navigation
    document.querySelectorAll('.nav-group-header').forEach(header => {
        header.addEventListener('click', () => {
            const navGroup = header.parentElement;
            const chevron = header.querySelector('.fa-chevron-down');
            
            // Fermer les autres groupes
            document.querySelectorAll('.nav-group').forEach(group => {
                if (group !== navGroup) {
                    group.classList.remove('open');
                    group.querySelector('.fa-chevron-down').classList.add('collapsed');
                }
            });
            
            // Basculer le groupe actuel
            navGroup.classList.toggle('open');
            chevron.classList.toggle('collapsed');
        });
    });

    // Garder le groupe ouvert si un élément est actif
    const activeItem = document.querySelector('.nav-item.active');
    if (activeItem) {
        const parentGroup = activeItem.closest('.nav-group');
        if (parentGroup) {
            parentGroup.classList.add('open');
            parentGroup.querySelector('.fa-chevron-down').classList.remove('collapsed');
        }
    }
    
    
    const parentGroup = item.closest('.nav-group');
	if (parentGroup) {
	    parentGroup.classList.add('open');
	    const chevron = parentGroup.querySelector('.fa-chevron-down');
	    if (chevron) {
	        chevron.classList.remove('collapsed');
	    }
	}
});
