async function loadRecipients() {
    try {
        const response = await fetch('/messages/recipients');
        const recipients = await response.json();
        const select = document.getElementById('recipient');
        
        if (select) {
            select.innerHTML = `
                <option value="">Sélectionner un destinataire</option>
                ${recipients.map(user => 
                    `<option value="${user.id}">${user.lastName} ${user.firstName} (${user.email})</option>`
                ).join('')}
            `;
        }
    } catch (error) {
        console.error('Erreur lors du chargement des destinataires:', error);
    }
}