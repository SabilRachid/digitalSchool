// schedule.js

document.addEventListener('DOMContentLoaded', function() {
    console.log("Page Emploi du temps et devoirs chargée.");

    // Exemple: attach an event listener on "Trimestre" / "Année" buttons
    const periodButtons = document.querySelectorAll('.period-selector .btn-outline');
    periodButtons.forEach(button => {
        button.addEventListener('click', () => {
            // Retire la classe 'active' de tous les boutons
            periodButtons.forEach(b => b.classList.remove('active'));
            // Ajoute la classe 'active' au bouton cliqué
            button.classList.add('active');

            // Ici, on pourrait appeler une fonction pour recharger un graphique
            // ou filtrer la liste des devoirs selon la période sélectionnée
            console.log("Période sélectionnée :", button.textContent.trim());
        });
    });

    // Exemple: ajouter un événement sur un bouton "Voir plus" pour afficher plus de devoirs
    const seeMoreHomeworksButton = document.getElementById('seeMoreHomeworks');
    if (seeMoreHomeworksButton) {
        seeMoreHomeworksButton.addEventListener('click', () => {
            // Logique pour charger d'autres devoirs ou afficher plus de lignes
            console.log("Charger plus de devoirs...");
            // On pourrait faire un fetch vers /student/homeworks?offset=...
        });
    }
});

/**
 * Exemple d'une fonction pour recharger l'emploi du temps d'un jour particulier
 */
function loadScheduleForDay(day) {
    fetch(`/student/schedule?day=${day}`)
        .then(response => response.json())
        .then(data => {
            // Mettre à jour le tableau HTML avec les nouvelles données
            console.log("Emploi du temps pour", day, data);
            // logiques de mise à jour du DOM...
        })
        .catch(error => {
            console.error("Erreur lors du chargement de l'emploi du temps :", error);
        });
}
