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

function getTutorSessionsManage() {
    var queryString = new Array();
    window.onload = readTutorID(queryString, window);
    const userID = queryString["userID"];
    console.log(userID);

    fetch('/confirmation?studentEmail=' + userID, {method: 'GET'}).then(response => response.json()).then((scheduledSessions) => {
        scheduledSessions.forEach((scheduledSession) => {
            document.getElementById('scheduledSessions').appendChild(createScheduledSessionBoxManage(scheduledSession, userID));
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

function createScheduledSessionBoxManage(scheduledSession, userID) {
    var months = [ "January", "February", "March", "April", "May", "June", 
           "July", "August", "September", "October", "November", "December" ];

    const scheduledSessionElement = document.createElement('li');
    scheduledSessionElement.className = 'list-group-item';

    const tutorElement = document.createElement('h3');
    tutorElement.style.textAlign = 'left';
    tutorElement.style.display = 'inline';
    tutorElement.innerHTML = "Tutoring Session with " + scheduledSession.tutorEmail;

    const tutorLineElement = document.createElement('div');
    tutorLineElement.className = 'd-flex w-100 justify-content-between';
    tutorLineElement.appendChild(tutorElement);

    const dateElement = document.createElement('h3');
    dateElement.style.textAlign = 'left';
    dateElement.style.display = 'inline';
    var hour = parseInt(scheduledSession.timeslot.start) / 60;
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
        cancelTutorSession(userID, window, scheduledSession);

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

function cancelTutorSession(userID, window, scheduledSession) {
    const params = new URLSearchParams();
    params.append('tutorEmail', scheduledSession.tutorEmail);
    params.append('studentEmail', scheduledSession.studentEmail);
    params.append('year', scheduledSession.timeslot.date.year);
    params.append('month', scheduledSession.timeslot.date.month);
    params.append('day', scheduledSession.timeslot.date.dayOfMonth);
    params.append('start', scheduledSession.timeslot.start);
    params.append('end', scheduledSession.timeslot.end);
    params.append('subtopics', scheduledSession.subtopics);
    params.append('questions', scheduledSession.questions);
    params.append('rating', scheduledSession.rating);
    params.append('id', scheduledSession.id);

    fetch('/delete-tutor-session', {method: 'POST', body: params});
}
