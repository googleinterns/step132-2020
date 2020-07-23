// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

function getMyStudents() {
    return getMyStudentsHelper(window);
}

async function getMyStudentsHelper(window) {
    await fetch('/my-students', {method: 'GET'}).then((response) => {
        //if the tutor id is not the id of the current user
        if(response.redirected) {
            window.location.href = response.url;
            alert("You must be signed in to view your students.");
            return [];
        }
        return response.json();
    }).then((students) => {
        if(students.error) {
            var message = document.createElement("p");
            p.innerText = students.error;
            document.getElementById('students').appendChild(message);
            return;
        }

        if (Object.keys(students).length != 0) {
            students.forEach((student) => {
                document.getElementById('students').appendChild(createStudentBox(student));
            });
        } else {
            var studentsContainer = document.getElementById('students');
            var errorMessage = document.createElement("p");
            errorMessage.innerText = "This user does not have any students.";
            studentsContainer.appendChild(errorMessage);
            return;
        }
    });
}

/** Creates a div element containing information about a student. */
function createStudentBox(student) {
    console.log(student);
    const studentContainer = document.createElement("div");
    const name = document.createElement("h3");
    const email = document.createElement("h6");
    const learning = document.createElement("p");
    const progressLink = document.createElement("a");
    name.innerText = student.name;
    email.innerText = student.email;
    learning.innerText = "Learning: " + student.learning.join(", ");
    progressLink.innerText = "Track Progress";

    progressLink.href = "/profile.html?userID=" + student.userId;

    studentContainer.classList.add("student-result");
    studentContainer.classList.add("list-group-item");
    learning.style.textTransform = "capitalize";
    studentContainer.appendChild(name);
    studentContainer.appendChild(email);
    studentContainer.appendChild(learning);
    studentContainer.appendChild(progressLink);
    return studentContainer;
} 
