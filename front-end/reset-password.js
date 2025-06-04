// Helper to get query param from URL
function getQueryParam(param) {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(param);
}

// Helper to validate UUID (v4)
function isValidUUID(uuid) {
    return /^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i.test(uuid);
}

window.addEventListener("DOMContentLoaded", function () {
    const tokenFromUrl = getQueryParam("token");
    const responseMessage = document.getElementById("responseMessage");
    const form = document.getElementById("resetPasswordForm");

    if (!tokenFromUrl || !isValidUUID(tokenFromUrl)) {
        form.style.display = "none";
        responseMessage.innerHTML = `<div class="alert alert-danger">Invalid or missing reset link. Please check your email and try again.</div>`;
        return;
    }

    // Store token in a hidden field or variable if needed
    form.addEventListener("submit", async function (event) {
        event.preventDefault();

        const newPassword = document.getElementById("newPassword").value;

        const requestBody = {
            token: tokenFromUrl,
            newPassword: newPassword
        };

        try {
            const response = await fetch("http://localhost:8080/auth/password/reset-password", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(requestBody)
            });

            if (response.ok) {
                responseMessage.innerHTML = `<div class="alert alert-success">Password reset successfully!</div>`;
            } else {
                const errorData = await response.json();
                responseMessage.innerHTML = `<div class="alert alert-danger">Error: ${errorData.message || "Failed to reset password"}</div>`;
            }
        } catch (error) {
            responseMessage.innerHTML = `<div class="alert alert-danger">Error: ${error.message}</div>`;
        }
    });
});