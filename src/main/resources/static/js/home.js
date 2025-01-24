// Gestion du formulaire de contact
document.addEventListener('DOMContentLoaded', function() {
    const contactForm = document.querySelector('.contact-form');
    if (contactForm) {
        contactForm.addEventListener('submit', async function(e) {
            e.preventDefault();

            const formData = {
                schoolName: this.querySelector('input[placeholder="Nom de l\'établissement"]').value,
                email: this.querySelector('input[type="email"]').value,
                phone: this.querySelector('input[type="tel"]').value,
                message: this.querySelector('textarea').value
            };

            try {
                const response = await fetch('/contact', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                    },
                    body: JSON.stringify(formData)
                });

                if (!response.ok) {
                    throw new Error('Erreur lors de l\'envoi du formulaire');
                }

                const result = await response.json();
                showNotification(result.message, 'success');
                this.reset();
            } catch (error) {
                showNotification(error.message, 'error');
            }
        });
    }

    // Système de notification
    function showNotification(message, type = 'success') {
        const notification = document.createElement('div');
        notification.className = `notification ${type}`;
        notification.innerHTML = `
            <div class="notification-content">
                <i class="fas ${type === 'success' ? 'fa-check-circle' : 'fa-exclamation-circle'}"></i>
                <span>${message}</span>
            </div>
            <button class="notification-close">×</button>
        `;

        document.body.appendChild(notification);

        // Animation d'entrée
        setTimeout(() => {
            notification.classList.add('show');
        }, 100);

        // Fermeture automatique
        setTimeout(() => {
            notification.classList.remove('show');
            setTimeout(() => {
                notification.remove();
            }, 300);
        }, 5000);

        // Fermeture manuelle
        notification.querySelector('.notification-close').addEventListener('click', () => {
            notification.classList.remove('show');
            setTimeout(() => {
                notification.remove();
            }, 300);
        });
    }

    // Animation au scroll
    const animateOnScroll = () => {
        const elements = document.querySelectorAll('.feature-card, .pricing-card, .testimonial-card');
        
        elements.forEach(element => {
            const elementTop = element.getBoundingClientRect().top;
            const elementBottom = element.getBoundingClientRect().bottom;
            
            if (elementTop < window.innerHeight && elementBottom > 0) {
                element.classList.add('animate');
            }
        });
    };

    window.addEventListener('scroll', animateOnScroll);
    animateOnScroll(); // Initial check

    // Navigation fluide
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function(e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                target.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            }
        });
    });

    // Menu mobile
    const mobileMenuButton = document.createElement('button');
    mobileMenuButton.className = 'mobile-menu-button';
    mobileMenuButton.innerHTML = '<i class="fas fa-bars"></i>';
    document.querySelector('.nav-container').appendChild(mobileMenuButton);

    mobileMenuButton.addEventListener('click', () => {
        document.querySelector('.nav-links').classList.toggle('show');
    });
});
