class SendList {
    constructor() {
        this.listId = this.getListIdFromUrl();
        this.loadList();
        this.initializeEventListeners();
    }

    getListIdFromUrl() {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get('id');
    }

    getAuthToken() {
        return localStorage.getItem('authToken');
    }

    async loadList() {
        try {
            const response = await fetch(`http://localhost:8080/api/santas-lists/${this.listId}`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${this.getAuthToken()}`,
                    'Content-Type': 'application/json'
                }
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const list = await response.json();
            this.displayList(list);
        } catch (error) {
            console.error('Error loading list:', error);
            alert('Failed to load list. Redirecting to dashboard...');
            window.location.href = 'dashboard.html';
        }
    }

    displayList(list) {
        document.getElementById('list-name').value = list.name;
        document.getElementById('due-date').value = list.dueDate?.split('T')[0];

        const container = document.querySelector('.cards-container');
        container.innerHTML = '';

        list.persons.forEach(person => {
            const card = this.createPersonCard(person);
            container.appendChild(card);
        });
    }

    createPersonCard(person) {
        const card = document.createElement('div');
        card.className = 'card';
        card.innerHTML = `
            <div class="card-left">
                <h2>${person.name}</h2>
                <p>${person.email}</p>
            </div>
        `;
        return card;
    }

    async sendList() {
        try {
            const token = this.getAuthToken();
            if (!token) {
                alert('Not authenticated. Please login again.');
                window.location.href = 'login.html';
                return;
            }

            const response = await fetch(`http://localhost:8080/api/santas-lists/${this.listId}/send-emails`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });

            if (response.status === 401) {
                alert('Session expired. Please login again.');
                window.location.href = 'login.html';
                return;
            }

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            alert('Emails sent successfully!');
            window.location.href = 'dashboard.html';
        } catch (error) {
            console.error('Error sending emails:', error);
            alert('Failed to send emails. Please try again.');
        }
    }

    initializeEventListeners() {
        document.getElementById('send-list-button').addEventListener('click', () => this.sendList());
        document.getElementById('edit-list-button').addEventListener('click', () => {
            window.location.href = `edit-list.html?id=${this.listId}`;
        });
    }
}

// Initialize the send list page
window.addEventListener('DOMContentLoaded', () => {
    new SendList();
});