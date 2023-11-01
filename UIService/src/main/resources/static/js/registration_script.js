function submitRegistrationForm() {
    // Get form elements
    const email = document.getElementById("email").value;
    const firstName = document.getElementById("firstName").value;
    const lastName = document.getElementById("lastName").value;
    const password = document.getElementById("password").value;
    const userRole = document.getElementById("userRole").value;

    // Create an object with the form data
    const formData = {
        email: email,
        firstName: firstName,
        lastName: lastName,
        password: password,
        userRole: userRole
    };

    // Send a POST request to the registration endpoint
    fetch('http://localhost/api/auth/register', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(formData)
    })
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                return response.json().then(errorData => {
                    throw new Error(errorData.message);
                });
            }
        })
        .then(data => {
            // Get the registrationMessage div element
            const registrationMessage = document.getElementById("registrationMessage");

            // Set the success message
            registrationMessage.textContent = "Registration successful. Confirmation email has been sent.";
            registrationMessage.style.color = "green";

            // Make the message div visible
            registrationMessage.style.display = "block";
        })
        .catch(error => {
            // Get the registrationMessage div element
            const registrationMessage = document.getElementById("registrationMessage");

            // Set the error message
            registrationMessage.textContent = "Registration failed. " + error.message; // Display the error message from the API
            registrationMessage.style.color = "red";

            // Make the message div visible
            registrationMessage.style.display = "block";

            console.error('Error:', error);
        });
}
