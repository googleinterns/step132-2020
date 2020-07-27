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
        //if the student is not the current user or not signed in
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

        if (Object.keys(scheduledSessions).length != 0) {
            scheduledSessions.forEach((scheduledSession) => {
                document.getElementById('scheduledSessions').appendChild(createScheduledSessionBoxManage(scheduledSession));
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

function createScheduledSessionBoxManage(scheduledSession) {
    var months = [ "January", "February", "March", "April", "May", "June", 
           "July", "August", "September", "October", "November", "December" ];

    const scheduledSessionElement = document.createElement('li');
    scheduledSessionElement.className = 'list-group-item';

    const tutorElement = document.createElement('h3');
    tutorElement.style.textAlign = 'left';
    tutorElement.style.display = 'inline';

    setTutorName(tutorElement, scheduledSession.tutorID);

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

//Helper function for testing purposes
//Sets the tutor element's name field to the tutor name
function setTutorName(tutorElement, tutorID) {
    var tutor;
    return getUser(tutorID).then(user => tutor = user).then(() => {
        tutorElement.innerText = "Tutoring Session with " + tutor.name;
    });
}

/** Gets information about the given user from the server. */
function getUser(userID) {
    return fetch('/profile?userId='+userID).then(response => response.json()).then((user) => {
        if(user.error) {
            var message = document.createElement("p");
            p.innerText = user.error;
            document.body.appendChild(message);
            return;
        }
        return user;
    });
}


function cancelTutorSession(window, scheduledSession) {
    const params = new URLSearchParams();
    params.append('id', scheduledSession.id);

    fetch('/delete-tutor-session', {method: 'POST', body: params}).then((response) => {
        //if the student is not the current user or not signed in
        if(response.redirected) {
            window.location.href = response.url;
            alert("You must be signed in to cancel a tutoring session.");
            return;
        }
    });
}

/** Creates a calendar with the Charts API and renders it on the page  */
function createCalendar() {
    fetch('/confirmation', {method: 'GET'}).then(response => response.json()).then(async (scheduledSessions) => {
        // Don't create a calendar if there are no scheduled sessions
        if (scheduledSessions === undefined || scheduledSessions.length == 0) {
            return;
        }

        // There are available timeslots, display header and calendar
        document.getElementById('calendar-header').style.display = 'block';
        
        const container = document.getElementById('calendar');
        const chart = new google.visualization.Timeline(container);

        const dataTable = new google.visualization.DataTable();
        dataTable.addColumn({type: 'string', id: 'Date'});
        dataTable.addColumn({type: 'string', id: 'Description'});
        dataTable.addColumn({type: 'date', id: 'Start'});
        dataTable.addColumn({type: 'date', id: 'End'});
        
        for (var session of scheduledSessions) {
            // Wait for this promise to resolve so tutor is defined when making calendar row
            var tutor = await getUser(session.tutorID);
            // Add 1 to the month so it displays correctly (January's default value is 0, February's is 1, etc.)
            var date = (session.timeslot.date.month+1) + '/' + session.timeslot.date.dayOfMonth + '/' + session.timeslot.date.year;
            var description = session.subtopics + " with " + tutor.name;
            dataTable.addRow([
                date, description, asDate(session.timeslot.start), asDate(session.timeslot.end)
            ]);
        }

        const options = {
            'width':1000,
            'height':200,
        };

        chart.draw(dataTable, options);
    });
}

/**
 * Converts "minutes since midnight" into a JavaScript Date object.
 * Code used from the week 5 unit testing walkthrough of Google's STEP internship trainings
 */
function asDate(minutes) {
  const date = new Date();
  date.setHours(Math.floor(minutes / 60));
  date.setMinutes(minutes % 60);
  return date;
}
   
google.charts.load('current', {'packages': ['timeline']});
google.charts.setOnLoadCallback(createCalendar);
