
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
    fetch('http://localhost:80/api/auth/register', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(formData)
    })
        .then(response => response.json())
        .then(data => {
            // Handle the response from the server, e.g., show a success message or handle errors
            console.log(data);
        })
        .catch(error => {
            // Handle any errors that occurred during the fetch.
            console.error('Error:', error);
        });
}
