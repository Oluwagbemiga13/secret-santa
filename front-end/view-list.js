class SantaListViewer {
    constructor() {
        // Get the list ID from the URL parameters
        const urlParams = new URLSearchParams(window.location.search);
        this.listId = urlParams.get('id');
        
        // Check if dashboard-header exists
        const dashboardHeader = document.querySelector('.dashboard-header');
        if (!dashboardHeader) {
            console.error('Required DOM elements not found');
            return;
        }
        
        this.loadList();
    }

    async loadList() {
        try {
            // Use the actual endpoint for list details
            const response = await fetch(`http://localhost:8080/api/santas-lists/${this.listId}/details`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('authToken')}`,
                    'Content-Type': 'application/json'
                }
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const list = await response.json();
            console.log("Fetched list:", list);
            this.displayListDetails(list);
        } catch (error) {
            console.error('Error loading Santa\'s list:', error);
            alert('Failed to load Santa\'s list. Redirecting to dashboard...');
            window.location.href = 'dashboard.html';
        }
    }

    displayListDetails(list) {
        this.displayListHeader(list);
        this.displayListStatus(list);
        this.displayParticipants(list);
    }

    displayListHeader(list) {
        const header = document.createElement('div');
        header.className = 'list-header';
        header.innerHTML = `
            <h2>${list.name}</h2>
            <div class="list-dates">
                <span>Created: ${new Date(list.creationDate).toLocaleDateString()}</span>
                <span>Due: ${new Date(list.dueDate).toLocaleDateString()}</span>
            </div>
        `;
        
        document.getElementById('list-header').appendChild(header);
    }

    displayListStatus(list) {
        const statusBadge = document.createElement('div');
        statusBadge.className = `status-badge ${list.status.toLowerCase()}`;
        statusBadge.innerHTML = `
            <span class="status-text">${list.status}</span>
            <span class="lock-status">${list.isLocked ? '🔒' : '🔓'}</span>
        `;
        
        document.querySelector('.list-header').appendChild(statusBadge);
    }

    displayParticipants(list) {
        const participantsList = document.getElementById('participants-list');
        participantsList.innerHTML = '';

        if (!list.persons || list.persons.length === 0) {
            this.displayEmptyMessage(participantsList);
            return;
        }

        const cardsContainer = document.createElement('div');
        cardsContainer.className = 'cards-container';

        list.persons.forEach((person, index) => {
            cardsContainer.appendChild(this.createParticipantCard(person, index));
        });

        participantsList.appendChild(cardsContainer);
    }

    createParticipantCard(person, index) {
        const card = document.createElement('div');
        card.className = 'person-status-card';  // Changed from 'person-card'
        
        const giftStatusIcon = person.hasSelectedGift 
            ? '✅' 
            : '<span style="color: #FFD700; font-weight: bold;">❌</span>';

        card.innerHTML = `
            <div class="card-header">
                <h3>${person.name}</h3>
            </div>
            <div class="card-body">
                <div class="info-row">
                    <label>Email:</label>
                    <span>${person.email || 'Not specified'}</span>
                </div>
                <div class="info-row">
                    <label>Gift Selected:</label>
                    <span class="gift-status">${giftStatusIcon}</span>
                </div>
            </div>
        `;

        return card;
    }

    displayEmptyMessage(container) {
        container.innerHTML = `
            <div class="empty-list-message">
                <p>No participants added to this list yet.</p>
            </div>
        `;
    }
}

// Initialize viewer only after DOM is fully loaded
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initializeViewer);
} else {
    initializeViewer();
}

function initializeViewer() {
    try {
        window.santaListViewer = new SantaListViewer();
    } catch (error) {
        console.error('Error initializing SantaListViewer:', error);
    }
}