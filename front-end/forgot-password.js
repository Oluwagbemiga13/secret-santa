document.getElementById("forgotPasswordForm").addEventListener("submit", async function (event) {
    event.preventDefault();

    const email = document.getElementById("email").value;

    try {
        const response = await fetch("http://localhost:8080/auth/password/forgot-password?email=" + encodeURIComponent(email), {
            method: "POST"
        });

        const responseMessage = document.getElementById("forgotResponseMessage");
        if (response.ok) {
            responseMessage.innerHTML = `<div class="alert alert-success">If the email exists, a reset link has been sent.</div>`;
        } else {
            const errorData = await response.json();
            responseMessage.innerHTML = `<div class="alert alert-danger">Error: ${errorData.message || "Failed to send reset link"}</div>`;
        }
    } catch (error) {
        document.getElementById("forgotResponseMessage").innerHTML = `<div class="alert alert-danger">Error: ${error.message}</div>`;
    }
});