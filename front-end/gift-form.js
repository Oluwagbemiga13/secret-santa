class GiftForm {
    constructor() {
        this.giftId = this.getIdFromUrl();
        this.loadCreatorName();
        this.initializeEventListeners();
    }

    getIdFromUrl() {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get('id');
    }

    getAuthToken() {
        return localStorage.getItem('authToken');
    }

    async loadCreatorName() {
        try {
            const response = await fetch(`http://localhost:8080/api/gifts/${this.giftId}/creator`, {
                headers: {
                    'Authorization': `Bearer ${this.getAuthToken()}`
                }
            });
            
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();
            document.getElementById('creator-name').textContent = data.creatorName;
        } catch (error) {
            console.error('Error loading creator name:', error);
            document.getElementById('creator-name').textContent = 'your Secret Santa organizer';
        }
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
            const response = await fetch(`http://localhost:8080/api/gifts/${this.giftId}`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${this.getAuthToken()}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(giftData)
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            alert('Gift submitted successfully!');
            window.location.href = 'dashboard.html';
        } catch (error) {
            console.error('Error submitting gift:', error);
            alert('Failed to submit gift. Please try again.');
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