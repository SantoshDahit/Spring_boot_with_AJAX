    function fetchStudents() {
    var xhr = new XMLHttpRequest();
    xhr.open("GET", "/admin/studentsData");
    xhr.onreadystatechange = function() {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status === 200) {
                var data = JSON.parse(xhr.responseText);
                var tableBody = document.getElementById("studentsTableBody");
                tableBody.innerHTML = "";
                data.forEach(function(student) {
                    var row = "<tr>" +
                        "<td>" + student.id + "</td>" +
                        "<td>" + student.name + "</td>" +
                        "<td>" + student.lastName + "</td>" +
                        "<td>" + student.degree + "</td>" +
                        "<td>" + student.roles.map(role => role.role).join(", ") + "</td>" +
                        "<td><button class='btn btn-primary btn-sm editBtn' data-id='" + student.id + "'>Edit</button></td>" +
                        "<td><button class='btn btn-danger btn-sm deleteBtn' data-id='" + student.id + "'>Delete</button></td>" +
                        "</tr>";
                    tableBody.innerHTML += row;
                });
            } else {
                console.error("Error fetching data: " + xhr.statusText);
            }
        }
    };
    xhr.send();
}

function populatePredefinedRoles() {
    var predefinedRoles = ["ROLE_ADMIN", "ROLE_STUDENT"];
    var rolesSelect = document.getElementById("roles");
    rolesSelect.innerHTML = "";
    predefinedRoles.forEach(function(role) {
        var option = document.createElement("option");
        option.value = role;
        option.textContent = role;
        rolesSelect.appendChild(option);
    });
}

function fetchRolesAndPopulateSelect() {
    populatePredefinedRoles();
}

document.addEventListener("DOMContentLoaded", function() {
    fetchRolesAndPopulateSelect();
});

function createStudent() {
    var name = document.getElementById("name").value.trim();
    var lastName = document.getElementById("lastName").value.trim();
    var degree = document.getElementById("degree").value.trim();
    var password = document.getElementById("password").value.trim();
    var roles = Array.from(document.getElementById("roles").selectedOptions).map(option => option.value);

    if (name === '' || lastName === '' || degree === '' || password === '' || roles.length === 0) {
        alert('Please fill in all fields.');
        return;
    }

    var xhr = new XMLHttpRequest();
    xhr.open("POST", "/admin/student", true);
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4) {
            if (xhr.status === 201) {
                alert("Student added successfully");
                fetchStudents(); // Assuming you have a function to fetch students
                document.getElementById("name").value = "";
                document.getElementById("lastName").value = "";
                document.getElementById("degree").value = "";
                document.getElementById("password").value = "";
                document.getElementById("roles").value = "";
            } else {
                console.error("Error creating student: " + xhr.statusText);
            }
        }
    };
    var studentData = {
        name: name,
        lastName: lastName,
        degree: degree,
        password: password,
        roles: roles
    };
    xhr.send(JSON.stringify(studentData));
}

document.getElementById("createBtn").addEventListener("click", createStudent);

document.getElementById("cancelCreateBtn").addEventListener("click", function() {
    document.getElementById("name").value = "";
    document.getElementById("lastName").value = "";
    document.getElementById("degree").value = "";
    document.getElementById("password").value = "";
    document.getElementById("roles").value = "";
});

document.addEventListener("DOMContentLoaded", function() {
    fetchStudents();
});

document.addEventListener("click", function(event) {
    if (event.target.classList.contains("editBtn")) {
        var id = event.target.dataset.id;
        var xhr = new XMLHttpRequest();
        xhr.open("GET", "/admin/edit?id=" + id);
        xhr.onreadystatechange = function() {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                if (xhr.status === 200) {
                    var data = JSON.parse(xhr.responseText);
                    document.getElementById("editId").value = data.student.id;
                    document.getElementById("editName").value = data.student.name;
                    document.getElementById("editLastName").value = data.student.lastName;
                    document.getElementById("editDegree").value = data.student.degree;
                    document.getElementById("editPassword").value = data.student.password;

                    var editRolesSelect = document.getElementById("editRoles");
                    editRolesSelect.innerHTML = "";

                    // Specify predefined roles directly
                    var roles = ["ROLE_ADMIN", "ROLE_STUDENT"];

                    roles.forEach(function(role) {
                        var option = document.createElement("option");
                        option.value = role;
                        option.textContent = role;
                        editRolesSelect.appendChild(option);
                    });

                    document.getElementById("editSection").style.display = "block";
                } else {
                    console.error("Error fetching student data for editing: " + xhr.statusText);
                }
            }
        };
        xhr.send();
    }

    if (event.target.id === "cancelEditBtn") {
        document.getElementById("editName").value = "";
        document.getElementById("editLastName").value = "";
        document.getElementById("editDegree").value = "";
        document.getElementById("editPassword").value = "";
        document.getElementById("editRoles").value = "";
        document.getElementById("editSection").style.display = "none";
    }

    if (event.target.classList.contains("deleteBtn")) {
        var id = event.target.dataset.id;
        if (confirm("Do you want to delete this student?")) {
            var xhr = new XMLHttpRequest();
            xhr.open("DELETE", "/admin/student?id=" + id);
            xhr.onreadystatechange = function() {
                if (xhr.readyState === XMLHttpRequest.DONE) {
                    if (xhr.status === 204) {
                        fetchStudents();
                    } else {
                        console.error("Error deleting student: " + xhr.statusText);
                    }
                }
            };
            xhr.send();
        }
    }

    if (event.target.id === "updateBtn") {
        var id = document.getElementById("editId").value.trim();
        var name = document.getElementById("editName").value.trim();
        var lastName = document.getElementById("editLastName").value.trim();
        var degree = document.getElementById("editDegree").value.trim();
        var password = document.getElementById("editPassword").value.trim();
        var roles = Array.from(document.getElementById("editRoles").selectedOptions).map(option => option.value);

        // Perform field validation
        if (name === '' || lastName === '' || degree === '' || password === '' || roles.length === 0) {
            alert('Please fill in all fields.');
            return; // Stop execution if any field is empty
        }

        var xhr = new XMLHttpRequest();
        xhr.open("PUT", "/admin/student/update?id=" + id);
        xhr.setRequestHeader("Content-Type", "application/json");
        xhr.onreadystatechange = function() {
            if (xhr.readyState === 4) {
                if (xhr.status === 200) {
                    var response = JSON.parse(xhr.responseText);
                    alert("Student updated successfully: " + response.name);
                    fetchStudents(); // Assuming you have a function to fetch students

                    document.getElementById("editName").value = "";
                    document.getElementById("editLastName").value = "";
                    document.getElementById("editDegree").value = "";
                    document.getElementById("editPassword").value = "";
                    document.getElementById("editRoles").value = "";

                } else {
                    console.error("Error updating student: " + xhr.statusText);
                }
            }
        };
        var requestBody = JSON.stringify({
            id: id,
            name: name,
            lastName: lastName,
            degree: degree,
            password: password,
            roles: roles
        });
        xhr.send(requestBody);
    }

});