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
    var queryString = new Array();
    window.onload = readTutorID(queryString, window);
    const userID = queryString["userID"];

    fetch('/my-students?tutorID=' + userID, {method: 'GET'}).then(response => response.json()).then((students) => {
        if(students.error) {
            var message = document.createElement("p");
            p.innerText = students.error;
            document.getElementById('students').appendChild(message);
            return;
        }
        students.forEach((student) => {
            document.getElementById('students').appendChild(createStudentBox(student));
        })
    });
}
// Referenced to https://www.aspsnippets.com/Articles/Redirect-to-another-Page-on-Button-Click-using-JavaScript.aspx#:~:text=Redirecting%
// 20on%20Button%20Click%20using%20JavaScript&text=Inside%20the%20Send%20JavaScript%20function,is%20redirected%20to%20the%20URL on June 23rd.
// This function reads the id of the tutor that the student has selected, which is passed as an URI component, and add it to the queryString array..
function readTutorID(queryString, window) {
    if (queryString.length == 0) {
        if (window.location.search.split('?').length > 1) {
            var params = window.location.search.split('?')[1].split('&');
            for (var i = 0; i < params.length; i++) {
                var key = params[i].split('=')[0];
                var value = decodeURIComponent(params[i].split('=')[1]);
                queryString[key] = value;
            }
        }
    }
}

/** Creates a div element containing information about a student. */
function createStudentBox(student) {
    const studentContainer = document.createElement("div");
    const name = document.createElement("h3");
    const email = document.createElement("h6");
    const learning = document.createElement("p");
    const progressLink = document.createElement("a");
    name.innerText = student.name;
    email.innerText = student.email;
    learning.innerText = "Learning: " + student.learning.join(", ");
    progressLink.innerText = "Track Progress";

    progressLink.href = "/progress.html?studentID=" + student.userId;

    studentContainer.classList.add("student-result");
    studentContainer.classList.add("list-group-item");
    learning.style.textTransform = "capitalize";
    studentContainer.appendChild(name);
    studentContainer.appendChild(email);
    studentContainer.appendChild(learning);
    studentContainer.appendChild(progressLink);
    return studentContainer;
} 
