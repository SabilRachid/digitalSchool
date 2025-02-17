document.addEventListener('DOMContentLoaded', function() {
    async function loadSettings() {
        try {
            const response = await fetch('/admin/api/settings');
            if (!response.ok) throw new Error('Erreur lors du chargement des paramètres');
            const settings = await response.json();
            console.log("Paramètres chargés :", settings);
            populateForm(settings);
        } catch (error) {
            console.error("Erreur lors du chargement des paramètres :", error);
            alert("Erreur lors du chargement des paramètres : " + error.message);
        }
    }

    function populateForm(settings) {
        const form = document.getElementById('settingsForm');
        // On suppose que le champ "id" est présent dans le formulaire pour stocker l'identifiant
        if(form.elements['id']) {
            form.elements['id'].value = settings.id || '';
        }
        form.elements['name'].value = settings.name || '';
        form.elements['code'].value = settings.code || '';
        form.elements['address'].value = settings.address || '';
        form.elements['groupeScolaire'].value = settings.groupeScolaire || '';
        form.elements['city'].value = settings.city || '';
        form.elements['dirigeant1'].value = settings.dirigeant1 || '';
        form.elements['phoneDirigeant1'].value = settings.phoneDirigeant1 || '';
        form.elements['emailDirigeant1'].value = settings.emailDirigeant1 || '';
        form.elements['dirigeant2'].value = settings.dirigeant2 || '';
        form.elements['phoneDirigeant2'].value = settings.phoneDirigeant2 || '';
        form.elements['emailDirigeant2'].value = settings.emailDirigeant2 || '';
        form.elements['directeurScolaire'].value = settings.directeurScolaire || '';
        form.elements['phoneDirecteurScolaire'].value = settings.phoneDirecteurScolaire || '';
        form.elements['emailDirecteurScolaire'].value = settings.emailDirecteurScolaire || '';
        form.elements['establishmentPhone'].value = settings.establishmentPhone || '';
        form.elements['establishmentEmail'].value = settings.establishmentEmail || '';
        form.elements['academicYear'].value = settings.academicYear || '';
        form.elements['tuitionFee'].value = settings.tuitionFee || '';
        form.elements['gradingScale'].value = settings.gradingScale || '';
        form.elements['maxStudentsPerClass'].value = settings.maxStudentsPerClass || '';
        form.elements['openingHours'].value = settings.openingHours || '';
        form.elements['websiteUrl'].value = settings.websiteUrl || '';
        form.elements['logoUrl'].value = settings.logoUrl || '';
        form.elements['themeColor'].value = settings.themeColor || '#ffffff';
        form.elements['socialMediaLinks'].value = settings.socialMediaLinks || '';
        form.elements['libraryLoanPeriod'].value = settings.libraryLoanPeriod || '';
        form.elements['examDuration'].value = settings.examDuration || '';
        form.elements['attendancePolicy'].value = settings.attendancePolicy || '';
        form.elements['parentMeetingFrequency'].value = settings.parentMeetingFrequency || '';
    }

    document.getElementById('settingsForm').addEventListener('submit', async function(e) {
        e.preventDefault();
        const formData = new FormData(this);
        const data = {
            name: formData.get('name'),
            code: formData.get('code'),
            address: formData.get('address'),
            groupeScolaire: formData.get('groupeScolaire'),
            city: formData.get('city'),
            dirigeant1: formData.get('dirigeant1'),
            phoneDirigeant1: formData.get('phoneDirigeant1'),
            emailDirigeant1: formData.get('emailDirigeant1'),
            dirigeant2: formData.get('dirigeant2'),
            phoneDirigeant2: formData.get('phoneDirigeant2'),
            emailDirigeant2: formData.get('emailDirigeant2'),
            directeurScolaire: formData.get('directeurScolaire'),
            phoneDirecteurScolaire: formData.get('phoneDirecteurScolaire'),
            emailDirecteurScolaire: formData.get('emailDirecteurScolaire'),
            establishmentPhone: formData.get('establishmentPhone'),
            establishmentEmail: formData.get('establishmentEmail'),
            academicYear: formData.get('academicYear'),
            tuitionFee: formData.get('tuitionFee'),
            gradingScale: formData.get('gradingScale'),
            maxStudentsPerClass: formData.get('maxStudentsPerClass'),
            openingHours: formData.get('openingHours'),
            websiteUrl: formData.get('websiteUrl'),
            logoUrl: formData.get('logoUrl'),
            themeColor: formData.get('themeColor'),
            socialMediaLinks: formData.get('socialMediaLinks'),
            libraryLoanPeriod: formData.get('libraryLoanPeriod'),
            examDuration: formData.get('examDuration'),
            attendancePolicy: formData.get('attendancePolicy'),
            parentMeetingFrequency: formData.get('parentMeetingFrequency')
        };

        try {
            const csrfToken = document.querySelector('meta[name="_csrf"]').content;
            // Ajout de l'ID dans l'URL PUT si présent
            const id = formData.get('id');
            console.log("School id", id);
            const url = id ? `/admin/api/settings/${id}` : '/admin/api/settings';
            const response = await fetch(url, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': csrfToken
                },
                body: JSON.stringify(data)
            });
            if (!response.ok) {
                const error = await response.text();
                throw new Error(error || 'Erreur lors de la mise à jour des paramètres');
            }
            alert("Paramètres mis à jour avec succès !");
        } catch (error) {
            console.error("Erreur lors de la mise à jour :", error);
            alert("Erreur lors de la mise à jour des paramètres : " + error.message);
        }
    });

    loadSettings();
});
