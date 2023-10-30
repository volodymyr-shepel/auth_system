function submitForm() {
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    const data = {
        email: email,
        password: password
    };
    // TODO make so it sends via localhost:80
    fetch("http://localhost:80/api/auth/authenticate", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
    })
        .then(response => {
            if (response.ok) {
                // Extract the JWT token from the response body
                return response.text();
            } else {
                alert("Login failed. Please try again.");
                throw new Error("Login failed");
            }
        })
        .then(jwtToken => {
            // Check if a token was successfully obtained
            if (jwtToken) {
                // Store the JWT token in a secure way (e.g., localStorage or a secure cookie)
                // For this example, we'll display it in an alert
                alert("Login successful!\nJWT Token: " + jwtToken);
            } else {
                alert("Login successful, but no JWT token found in the response.");
            }
        })
        .catch(error => {
            alert("An error occurred. Please try again later.");
        });
}
