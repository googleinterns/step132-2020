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

function scheduleTutorSession() {
    return scheduleTutorSessionHelper(window);
}

async function scheduleTutorSessionHelper(window) {
    var queryString = new Array();
    window.onload = readComponents(queryString, window);
    const tutorID = queryString["tutorID"];
    const start = queryString["start"];
    const end = queryString["end"];
    const year = queryString["year"];
    const month = queryString["month"];
    const day = queryString["day"];

    var subtopics = document.getElementById("topics").value;
    var questions = document.getElementById("questions").value;

    const params = new URLSearchParams();
    params.append('tutorID', tutorID);
    params.append('start', start);
    params.append('end', end);
    params.append('year', year);
    params.append('month', month);
    params.append('day', day);
    params.append('subtopics', subtopics);
    params.append('questions', questions);

    // Redirect user to confirmation
    await fetch('/scheduling', {method: 'POST', body: params}).then((response) => {
        //if the student is not the current user or not signed in
        if(response.redirected) {
            window.location.href = response.url
            alert("You must be signed in to schedule a tutoring session.");
            return null;
        }
        return response.json();
    }).then((tutors) => {
        if(tutors !== null && tutors.error) {
            var message = document.createElement("p");
            message.innerText = tutors.error;
            document.body.appendChild(message);
            return;
        }

        if(tutors !== null) {
            redirectToConfirmation(window);
        }
    });
}
  
/** A function that adds event listeners to a DOM objects. */
function addEventListeners() {
    document.getElementById("scheduling-form").addEventListener('submit', event => {
        event.preventDefault();
        scheduleTutorSession(window);
    });
}

// Redirects the user to the confirmation page and passes down the student ID.
function redirectToConfirmation(window) {
    var url = "confirmation.html";
    window.location.href = url;
}
