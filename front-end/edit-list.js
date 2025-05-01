class SantaListEditor extends SantaListManager {
    constructor() {
        super();
        this.listId = this.getListIdFromUrl();
        this.loadExistingList();
        // Remove this line since super() already initialized the listeners
        // this.initializeEventListeners();
    }

    getListIdFromUrl() {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get('id');
    }

    async loadExistingList() {
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
            this.populateForm(list);
        } catch (error) {
            console.error('Error loading Santa\'s list:', error);
            alert('Failed to load Santa\'s list. Redirecting to dashboard...');
            window.location.href = 'dashboard.html';
        }
    }

    async loadInitialCards() {
        const cardsContainer = document.querySelector('.cards-container');
        cardsContainer.innerHTML = ''; // Clear existing cards
        
        // Just create 3 empty cards without user info
        for (let i = 1; i <= 3; i++) {
            cardsContainer.appendChild(this.createPersonCard(i));
        }
    }

    populateForm(list) {
        // Set list details
        document.getElementById('list-name').value = list.name;
        document.getElementById('due-date').value = list.dueDate?.split('T')[0]; // Handle ISO date format
    
        // Clear and populate person cards
        const cardsContainer = document.querySelector('.cards-container');
        cardsContainer.innerHTML = '';
        
        if (list.persons && Array.isArray(list.persons)) {
            list.persons.forEach((person, index) => {
                const card = this.createPersonCard(index + 1);
                // Add person ID as data attribute
                card.dataset.personId = person.id || null;
                
                // Get the input fields
                const nameInput = card.querySelector('input[id^="name"]');
                const emailInput = card.querySelector('input[id^="email"]');
                
                // Set values if they exist
                if (nameInput && person.name) {
                    nameInput.value = person.name;
                }
                if (emailInput && person.email) {
                    emailInput.value = person.email;
                }
                
                cardsContainer.appendChild(card);
            });
    
            this.personCount = list.persons.length;
        } else {
            // Load default 3 cards if no persons exist
            this.loadInitialCards();
        }
    }

    collectFormData() {
        const listName = document.getElementById('list-name').value.trim();
        const dueDate = document.getElementById('due-date').value;

        if (!listName || !dueDate) {
            alert('Please fill in all required fields.');
            return null;
        }

        const personCards = document.querySelectorAll('.person-card');
        const persons = Array.from(personCards).map(card => ({
            id: card.dataset.personId || null, // Include existing ID if available
            name: card.querySelector('input[id^="name"]').value.trim(),
            email: card.querySelector('input[id^="email"]').value.trim()
        }));

        return {
            id: this.listId,
            name: listName,
            dueDate: dueDate,
            persons: persons
        };
    }

    async saveList() {
        if (!this.validateForm()) {
            alert('Please fill in all required fields correctly.');
            return;
        }

        const formData = this.collectFormData();
        if (!formData) return;

        try {
            const response = await fetch(`http://localhost:8080/api/santas-lists/${this.listId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${this.getAuthToken()}`
                },
                body: JSON.stringify(formData)
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const result = await response.json();
            alert('Santa\'s list updated successfully!');
            window.location.href = 'dashboard.html';

        } catch (error) {
            console.error('Error updating Santa\'s list:', error);
            alert('Failed to update Santa\'s list. Please try again.');
        }
    }

    initializeEventListeners() {
        // Override parent's method to use our saveList instead
        document.getElementById('add-person-button').addEventListener('click', () => this.addPersonCard());
        document.getElementById('save-list-button').addEventListener('click', () => this.saveList());
    }
}

// Initialize the editor - make sure we use the editor instance
window.santaListManager = new SantaListEditor();