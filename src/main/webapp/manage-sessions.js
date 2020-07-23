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

/** Gets a list of scheduled sessions for the student and displays them on the page with a delete button. */
function getTutorSessionsManage() {
    return getTutorSessionsManageHelper(window);
}

//Helper function for getTutorSessionManage, used for testing
async function getTutorSessionsManageHelper(window) {
    await fetch('/confirmation', {method: 'GET'}).then((response) => {
        //if the student id is not the id of the current user
        if(response.redirected) {
            window.location.href = response.url;
            alert("You must be signed in to manage sessions.");
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
        scheduledSessions.forEach((scheduledSession) => {
            document.getElementById('scheduledSessions').appendChild(createScheduledSessionBoxManage(scheduledSession));
        });
    });
}

function createScheduledSessionBoxManage(scheduledSession) {
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
    dateElement.innerHTML = hour + ":" + minute + amOrPm + " on " + months[scheduledSession.timeslot.date.month] +
                             " " + scheduledSession.timeslot.date.dayOfMonth + ", " + scheduledSession.timeslot.date.year;

    const dateLineElement = document.createElement('div');
    dateLineElement.className = 'd-flex w-100 justify-content-between';
    dateLineElement.appendChild(dateElement);
    
    const cancelButtonElement = document.createElement('button');
    cancelButtonElement.innerText = 'Cancel';
    cancelButtonElement.style.textAlign = 'right';
    cancelButtonElement.style.display = 'inline';
    cancelButtonElement.className = 'btn btn-default btn-lg';
    cancelButtonElement.addEventListener('click', () => {
        cancelTutorSession(window, scheduledSession);

        scheduledSessionElement.remove();
    });

    const buttonLineElement = document.createElement('div');
    buttonLineElement.className = 'd-flex w-100 justify-content-between';
    buttonLineElement.style.padding = '10px';
    buttonLineElement.appendChild(cancelButtonElement);

    scheduledSessionElement.appendChild(tutorLineElement);
    scheduledSessionElement.appendChild(dateLineElement);
    scheduledSessionElement.appendChild(buttonLineElement);
    return scheduledSessionElement;
}

function cancelTutorSession(window, scheduledSession) {
    const params = new URLSearchParams();
    params.append('id', scheduledSession.id);

    fetch('/delete-tutor-session', {method: 'POST', body: params}).then((response) => {
        //if the student id is not the id of the current user
        if(response.redirected) {
            window.location.href = response.url;
            alert("You must be signed in to cancel a tutoring session.");
            return;
        }
    });
}
