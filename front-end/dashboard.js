document.addEventListener("DOMContentLoaded", async () => {
    const authToken = localStorage.getItem("authToken");
    if (!authToken) {
        window.location.href = "/login.html";
        return;
    }

    const logoutLink = document.querySelector('nav a:nth-child(3)');
    logoutLink.addEventListener('click', (e) => {
        e.preventDefault();
        localStorage.removeItem("authToken");
        window.location.href = "/login.html";
    });

    try {
        const response = await fetch("http://localhost:8080/api/santas-lists/get-overviews", {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${authToken}`,
                "Content-Type": "application/json"
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const santasLists = await response.json();
        displaySantasLists(santasLists);

    } catch (error) {
        console.error("Error fetching santas lists:", error);
        alert("Failed to load Santas Lists. Please try again later.");
    }
});

function displaySantasLists(lists) {
    const dashboardContent = document.querySelector(".dashboard-content");
    
    // Remove all cards except the add-card-button
    Array.from(dashboardContent.children).forEach(child => {
        if (child.id !== "add-card-button") {
            child.remove();
        }
    });

    lists.forEach(list => {
        const card = createListCard(list);
        dashboardContent.appendChild(card);
    });
}

function createListCard(list) {
    const card = document.createElement('div');
    card.className = 'card';
    
    const statusDisplay = list.status.replace('_', ' ');

    card.innerHTML = `
        <div class="card-left">
            <h2>${list.name}</h2>
            <p>${list.message}</p>
        </div>
        <div class="card-middle">
            <h2>${statusDisplay}</h2>
        </div>
        <div class="card-right">
            ${getSendButton(list.status)}
            <div class="card-actions">
                <button class="edit-button">EDIT</button>
                <button class="delete-button">DELETE</button>
            </div>
        </div>
    `;

    return card;
}

function getSendButton(status) {
    // Only show send button for CREATED status
    if (status === 'CREATED') {
        return '<button class="send-button">SEND</button>';
    }
    return '';
}