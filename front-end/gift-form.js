class GiftForm {
    constructor() {
        this.giftId = this.getIdFromUrl();
        this.initializeEventListeners();
    }

    getIdFromUrl() {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get('id');
    }

    getAuthToken() {
        return localStorage.getItem('authToken');
    }


    validateForm() {
        const name = document.getElementById('gift-name').value.trim();
        const description = document.getElementById('gift-description').value.trim();

        if (!name || !description) {
            alert('Please fill in all fields');
            return false;
        }
        return true;
    }

    async submitGift() {
        if (!this.validateForm()) return;

        const giftData = {
            name: document.getElementById('gift-name').value.trim(),
            description: document.getElementById('gift-description').value.trim()
        };

        try {
            // Changed URL to match backend's expected path variable name
            const response = await fetch(`http://localhost:8080/api/gifts/${this.giftId}`, {
                method: 'PUT',
                headers: {
                    'Authorization': `Bearer ${this.getAuthToken()}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(giftData)
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            alert('Gift updated successfully!');
            window.location.href = 'dashboard.html';
        } catch (error) {
            console.error('Error updating gift:', error);
            alert('Failed to update gift. Please try again.');
        }
    }

    getPersonIdFromUrl() {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get('personId');
    }

    initializeEventListeners() {
        document.getElementById('submit-gift-button').addEventListener('click', () => this.submitGift());
        document.getElementById('cancel-button').addEventListener('click', () => {
            window.location.href = 'dashboard.html';
        });
    }
}

// Initialize the gift form
window.addEventListener('DOMContentLoaded', () => {
    new GiftForm();
});