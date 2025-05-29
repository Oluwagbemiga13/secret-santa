document.addEventListener("DOMContentLoaded", () => {
  const loginButton = document.querySelector("button[type='submit']");
  const emailInput = document.getElementById("login-email");
  const passwordInput = document.getElementById("login-password");

  loginButton.addEventListener("click", async (event) => {
    event.preventDefault();

    const loginData = {
      username: emailInput.value,
      password: passwordInput.value,
    };

    try {
      const response = await fetch("http://localhost:8080/api/auth/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(loginData),
      });

      if (response.ok) {
        const data = await response.json();
        alert("Login successful! Token: " + data.token);
        localStorage.setItem("authToken", data.token);
        window.location.href = "/dashboard.html"; // Redirect to a dashboard page
      } else {
        const errorData = await response.json();
        alert("Login failed: " + errorData.message);
      }
    } catch (error) {
      console.error("Error during login:", error);
      alert("An error occurred. Please try again later.");
    }
  });
});