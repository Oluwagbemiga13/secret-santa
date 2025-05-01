class SantaListManager {
    constructor() {
        this.personCount = 3;
        this.initializeEventListeners();
        this.loadInitialCards();
    }

    initializeEventListeners() {
        document.getElementById('add-person-button').addEventListener('click', () => this.addPersonCard());
        document.getElementById('save-list-button').addEventListener('click', () => this.saveList());
    }

    async loadInitialCards() {
        const cardsContainer = document.querySelector('.cards-container');
        cardsContainer.innerHTML = ''; // Clear existing cards
        
        try {
            const response = await fetch('http://localhost:8080/api/users/info', {
                headers: {
                    'Authorization': `Bearer ${this.getAuthToken()}`
                }
            });

            if (response.ok) {
                const userInfo = await response.json();
                // Create first card with user info
                const firstCard = this.createPersonCard(1);
                cardsContainer.appendChild(firstCard);
                
                // Fill the first card with user info
                firstCard.querySelector('input[id^="name"]').value = userInfo.username;
                firstCard.querySelector('input[id^="email"]').value = userInfo.email;
                
                // Add remaining empty cards
                for (let i = 2; i <= 3; i++) {
                    cardsContainer.appendChild(this.createPersonCard(i));
                }
            } else {
                this.createInitialCardsWithMessage();
            }
        } catch (error) {
            console.error('Error fetching user info:', error);
            this.createInitialCardsWithMessage();
        }
    }

    createInitialCardsWithMessage() {
        const cardsContainer = document.querySelector('.cards-container');
        const firstCard = this.createPersonCard(1);
        
        // Add a message above the first card's inputs
        const messageDiv = document.createElement('div');
        messageDiv.classList.add('info-message');
        messageDiv.style.color = '#666';
        messageDiv.style.marginBottom = '10px';
        messageDiv.textContent = 'Please enter your information';
        
        firstCard.insertBefore(messageDiv, firstCard.firstChild);
        cardsContainer.appendChild(firstCard);
        
        // Add remaining empty cards
        for (let i = 2; i <= 3; i++) {
            cardsContainer.appendChild(this.createPersonCard(i));
        }
    }

    createPersonCard(index) {
        const newPerson = document.createElement('div');
        newPerson.classList.add('person-card');
        
        // Only add delete button if it's not the first card
        const deleteButton = index === 1 ? '' : '<button class="delete-button" onclick="santaListManager.deletePerson(this)">×</button>';
        
        newPerson.innerHTML = `                
            ${deleteButton}
            <div class="inputs-container">
                <div class="input-group">
                    <label for="name-${index}">Name:</label>
                    <input type="text" id="name-${index}" name="name-${index}" placeholder="Enter name" required />
                </div>
                <div class="input-group">
                    <label for="email-${index}">Email:</label>
                    <input type="email" id="email-${index}" name="email-${index}" placeholder="Enter email" required />
                </div>
            </div>`;
        return newPerson;
    }

    deletePerson(button) {
        button.closest('.person-card').remove();
    }

    addPersonCard() {
        this.personCount++;
        const cardsContainer = document.querySelector('.cards-container');
        const newCard = this.createPersonCard(this.personCount);
        
        // Insert at the beginning instead of appending
        cardsContainer.insertBefore(newCard, cardsContainer.firstChild);
        
        // Scroll to top
        cardsContainer.scrollTop = 0;
    }

    validateForm() {
        const cards = document.querySelectorAll('.person-card');
        if (cards.length < 2) {
            alert('At least 2 people are required for a Secret Santa list.');
            return false;
        }

        let isValid = true;
        cards.forEach(card => {
            const nameInput = card.querySelector('input[type="text"][id^="name"]');
            const emailInput = card.querySelector('input[type="email"]');
            
            if (!nameInput.value.trim()) {
                nameInput.classList.add('error');
                isValid = false;
            }
            if (!emailInput.value.trim() || !emailInput.validity.valid) {
                emailInput.classList.add('error');
                isValid = false;
            }
        });

        return isValid;
    }

    collectFormData() {
        const listName = document.getElementById('list-name').value.trim();
        const dueDate = document.getElementById('due-date').value;

        if (!listName || !dueDate) {
            alert('Please fill in both list name and due date.');
            return null;
        }

        if (!this.isValidDate(dueDate)) {
            alert('Please enter a valid date.');
            return null;
        }

        const persons = Array.from(document.querySelectorAll('.person-card')).map(card => ({
            name: card.querySelector('input[id^="name"]').value.trim(),
            email: card.querySelector('input[id^="email"]').value.trim(),
            desiredGift: null
        }));

        return {
            name: listName,
            dueDate: dueDate,
            persons: persons
        };
    }

    isValidDate(dateString) {
        const regex = /^\d{4}-\d{2}-\d{2}$/;
        if (!regex.test(dateString)) return false;
        const date = new Date(dateString);
        return date instanceof Date && !isNaN(date);
    }

    async saveList() {
        if (!this.validateForm()) {
            alert('Please fill in all required fields correctly.');
            return;
        }

        const formData = this.collectFormData();
        if (!formData) return;

        try {
            const response = await fetch('http://localhost:8080/api/santas-lists', {  // Update this line
                method: 'POST',
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
            alert('Santa\'s list created successfully!');
            window.location.href = 'dashboard.html';

        } catch (error) {
            console.error('Error saving Santa\'s list:', error);
            alert('Failed to save Santa\'s list. Please try again.');
        }
    }

    getAuthToken() {
        return localStorage.getItem('authToken');
    }
}

class CreateList extends SantaListManager {
    constructor() {
        super();
        this._initializeEventListeners();
    }
}

// Initialize the manager
const santaListManager = new CreateList();