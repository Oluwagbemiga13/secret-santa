document.querySelector('button[type="submit"]').addEventListener('click', function(event) {
    event.preventDefault(); // Prevent form submission

    const username = document.getElementById('username').value;
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirm-password').value;

    // Check if passwords match
    if (password !== confirmPassword) {
        alert('Passwords do not match!');
        return;
    }

    // Create UserDTO object
    const userDTO = {
        username: username,
        email: email,
        password: password
    };

    // Send POST request to the backend
    fetch('http://localhost:8080/api/users', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(userDTO)
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(error => {
                throw new Error(error.message); // Extract the error message from the backend
            });
        }
        return response.json();
    })
    .then(data => {
        console.log('User registered successfully:', data);
        alert('Registration successful!');
        window.location.href = 'login.html'; 
    })
    .catch(error => {
        console.error('Error:', error.message);
        alert(`Error: ${error.message}`); // Display the error message to the user
    });
});