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

/** Gets a list of the user's scheduled sessions from the server and displays them on the page. */
function getScheduledSessions() {
    return getScheduledSessionsHelper(window);
}

//Helper function for getScheduledSessions, used for testing
async function getScheduledSessionsHelper(window) {
    await fetch('/confirmation', {method: 'GET'}).then((response) => {
        //if the student is not the current user/signed in
        if(response.redirected) {
            window.location.href = response.url;
            alert("You must be signed in to view upcoming session.");
            return [];
        }
        return response.json();
        
    }).then((scheduledSessions) => {
        if(scheduledSessions.error) {
            var message = document.createElement("p");
            p.innerText = scheduledSessions.error;
            document.getElementById('timeslots').appendChild(message);
            return;
        }

        if (Object.keys(scheduledSessions).length != 0) {
            scheduledSessions.forEach((scheduledSession) => {
                document.getElementById('scheduledSessions').appendChild(createScheduledSessionBox(scheduledSession));
            });
        } else {
            var sessionsContainer = document.getElementById('scheduledSessions');
            var errorMessage = document.createElement("p");
            errorMessage.innerText = "This user does not have any scheduled tutoring sessions.";
            sessionsContainer.appendChild(errorMessage);
            return;
        }
    });
}


function createScheduledSessionBox(scheduledSession) {

    var months = [ "January", "February", "March", "April", "May", "June", 
           "July", "August", "September", "October", "November", "December" ];

    const scheduledSessionElement = document.createElement('li');
    scheduledSessionElement.className = 'list-group-item';

    const tutorElement = document.createElement('h3');
    tutorElement.style.textAlign = 'left';
    tutorElement.style.display = 'inline';

    setTutorEmail(tutorElement, scheduledSession.tutorID);

    const tutorLineElement = document.createElement('div');
    tutorLineElement.className = 'd-flex w-100 justify-content-between';
    tutorLineElement.appendChild(tutorElement);

    const dateElement = document.createElement('h3');
    dateElement.style.textAlign = 'left';
    dateElement.style.display = 'inline';
    var hour = Math.floor(parseInt(scheduledSession.timeslot.start) / 60);
    var amOrPm = "am";
    if (hour > 12) {
        hour = hour - 12;
        amOrPm = "pm"
    }
    var minute = parseInt(scheduledSession.timeslot.start) % 60;
    if (minute == 0) {
        minute = "00";
    }
    dateElement.innerText = hour + ":" + minute + amOrPm + " on " + months[scheduledSession.timeslot.date.month] +
                             " " + scheduledSession.timeslot.date.dayOfMonth + ", " + scheduledSession.timeslot.date.year;


    const dateLineElement = document.createElement('div');
    dateLineElement.className = 'd-flex w-100 justify-content-between';
    dateLineElement.appendChild(dateElement);

    scheduledSessionElement.appendChild(tutorLineElement);
    scheduledSessionElement.appendChild(dateLineElement);
    return scheduledSessionElement;
}

//Helper function for testing purposes
//Sets the tutor element's email field to the tutor email
function setTutorEmail(tutorElement, tutorID) {
    var tutor;
    return getUser(tutorID).then(user => tutor = user).then(() => {
        tutorElement.innerText = "Tutoring Session with " + tutor.name;
    });
}
