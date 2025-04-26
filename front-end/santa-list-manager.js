class SantaListManager {
    constructor() {
        this.personCount = 3;
        this.loadInitialCards();
    }

    _initializeEventListeners() {
        document.getElementById('add-person-button').addEventListener('click', () => this.addPersonCard());
        document.getElementById('save-list-button').addEventListener('click', () => this.saveList());
    }

    loadInitialCards() {
        const cardsContainer = document.querySelector('.cards-container');
        cardsContainer.innerHTML = ''; // Clear existing cards
        for (let i = 1; i <= 3; i++) {
            cardsContainer.appendChild(this.createPersonCard(i));
        }
    }

    createPersonCard(index) {
        const newPerson = document.createElement('div');
        newPerson.classList.add('person-card');
        newPerson.innerHTML = `                
            <button class="delete-button" onclick="santaListManager.deletePerson(this)">×</button>
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

    validateEmail(email) {
        const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return re.test(email);
    }

    getAuthToken() {
        return localStorage.getItem('authToken');
    }
}