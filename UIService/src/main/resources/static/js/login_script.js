function submitLoginForm() {
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    const data = {
        email: email,
        password: password
    };

    const errorMessageElement = document.getElementById("errorMessage");

    // Clear any previous error messages
    errorMessageElement.textContent = "";

    // Send the login request
    fetch("http://localhost/api/auth/authenticate", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
    })
        .then(response => {
            if (response.ok) {
                // Redirect to the home page upon successful login
                window.location.href = "http://localhost/api/ui/home";
            } else {
                // Display an error message when login fails
                errorMessageElement.textContent = "Login failed. Please try again.";
            }
        })
        .catch(error => {
            // Handle network or other errors
            errorMessageElement.textContent = "An error occurred. Please try again later.";
        });
}
