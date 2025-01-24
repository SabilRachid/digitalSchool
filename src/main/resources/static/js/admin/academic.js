/* Styles communs pour la gestion académique */
:root {
    --primary-color: #4C51BF;
    --secondary-color: #48BB78;
    --warning-color: #ED8936;
    --danger-color: #F56565;
    --info-color: #4299E1;
    --text-primary: #2D3748;
    --text-secondary: #718096;
    --bg-primary: #F7FAFC;
    --bg-secondary: #EDF2F7;
}

/* Conteneur principal */
.content-card {
    background: white;
    border-radius: 1rem;
    padding: 1.5rem;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

/* Tableau */
.table {
    width: 100%;
    border-collapse: separate;
    border-spacing: 0;
}

.table th {
    background-color: var(--bg-secondary);
    padding: 1rem;
    font-weight: 600;
    text-align: left;
    color: var(--text-primary);
}

.table td {
    padding: 1rem;
    border-bottom: 1px solid var(--bg-secondary);
    vertical-align: middle;
}

/* Grilles */
.levels-grid,
.subjects-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 1rem;
    margin-top: 1rem;
}

/* Cases à cocher personnalisées */
.checkbox-card {
    display: flex;
    align-items: center;
    padding: 1rem;
    background-color: var(--bg-secondary);
    border-radius: 0.5rem;
    cursor: pointer;
    transition: all 0.3s;
}

.checkbox-card:hover {
    background-color: #e2e8f0;
}

.checkbox-card input[type="checkbox"] {
    margin-right: 0.5rem;
}

/* Badges */
.badge {
    padding: 0.25rem 0.75rem;
    border-radius: 9999px;
    font-size: 0.75rem;
    font-weight: 500;
}

.badge-primary {
    background-color: rgba(76, 81, 191, 0.1);
    color: var(--primary-color);
}

.badge-secondary {
    background-color: rgba(72, 187, 120, 0.1);
    color: var(--secondary-color);
}

.badge-warning {
    background-color: rgba(237, 137, 54, 0.1);
    color: var(--warning-color);
}

.badge-info {
    background-color: rgba(66, 153, 225, 0.1);
    color: var(--info-color);
}

/* Boutons d'action */
.action-buttons {
    display: flex;
    gap: 0.5rem;
}

.icon-button {
    padding: 0.5rem;
    border: none;
    border-radius: 0.375rem;
    cursor: pointer;
    transition: background-color 0.3s;
}

.icon-button.edit {
    background-color: #ebf4ff;
    color: #3182ce;
}

.icon-button.edit:hover {
    background-color: #bee3f8;
}

.icon-button.delete {
    background-color: #fff5f5;
    color: #e53e3e;
}

.icon-button.delete:hover {
    background-color: #fed7d7;
}

/* Modal */
.modal {
    display: none;
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    z-index: 1000;
}

.modal.show {
    display: flex;
    align-items: center;
    justify-content: center;
}

.modal-content {
    background: white;
    border-radius: 1rem;
    padding: 2rem;
    width: 100%;
    max-width: 800px;
    max-height: 90vh;
    overflow-y: auto;
}

/* Formulaire */
.form-section {
    margin-bottom: 2rem;
    padding: 1.5rem;
    background-color: var(--bg-secondary);
    border-radius: 0.5rem;
}

.form-section h3 {
    font-size: 1.125rem;
    font-weight: 600;
    color: var(--text-primary);
    margin-bottom: 1rem;
}

.form-row {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 1rem;
    margin-bottom: 1rem;
}

.form-group {
    margin-bottom: 1rem;
}

.form-group label {
    display: block;
    margin-bottom: 0.5rem;
    color: var(--text-primary);
    font-weight: 500;
}

.form-input,
.form-select {
    width: 100%;
    padding: 0.75rem;
    border: 1px solid #e2e8f0;
    border-radius: 0.5rem;
    font-size: 0.875rem;
    transition: all 0.3s;
}

.form-input:focus,
.form-select:focus {
    border-color: var(--primary-color);
    box-shadow: 0 0 0 3px rgba(76, 81, 191, 0.1);
    outline: none;
}

/* Responsive Design */
@media (max-width: 768px) {
    .form-row {
        grid-template-columns: 1fr;
    }

    .levels-grid,
    .subjects-grid {
        grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    }

    .modal-content {
        margin: 1rem;
        padding: 1rem;
    }
}