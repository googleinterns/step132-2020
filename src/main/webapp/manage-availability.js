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

function getAvailabilityManage() {
    var queryString = new Array();
    window.onload = readTutorID(queryString, window);
    const userID = queryString["userID"];

    document.getElementById("tutorEmail").value = userID;

    fetch('/availability?tutorID=' + userID, {method: 'GET'}).then(response => response.json()).then((timeslots) => {
        timeslots.forEach((timeslot) => {
            document.getElementById('timeslots').appendChild(createTimeSlotBoxManage(timeslot, userID));
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

function createTimeSlotBoxManage(timeslot, tutorID) {
    var months = [ "January", "February", "March", "April", "May", "June", 
           "July", "August", "September", "October", "November", "December" ];

    const timeslotElement = document.createElement('li');
    timeslotElement.className = 'list-group-item';

    const dateElement = document.createElement('h3');
    dateElement.style.textAlign = 'left';
    dateElement.style.display = 'inline';

    var hour = Math.floor(parseInt(timeslot.start) / 60);
    var amOrPm = "am";
    if (hour > 12) {
        hour = hour - 12;
        amOrPm = "pm"
    }
    var minute = parseInt(timeslot.start) % 60;
    if (minute == 0) {
        minute = "00";
    }
    dateElement.innerHTML = hour + ":" + minute + amOrPm + " on " + months[timeslot.date.month] + " " + timeslot.date.dayOfMonth + ", " + timeslot.date.year;

    const dateLineElement = document.createElement('div');
    dateLineElement.className = 'd-flex w-100 justify-content-between';
    dateLineElement.style.padding = '10px';
    dateLineElement.appendChild(dateElement);

    const deleteButtonElement = document.createElement('button');
    deleteButtonElement.innerText = 'Delete';
    deleteButtonElement.style.textAlign = 'right';
    deleteButtonElement.style.display = 'inline';
    deleteButtonElement.className = 'btn btn-default btn-lg';
    deleteButtonElement.addEventListener('click', () => {
        deleteTimeSlot(tutorID, window, timeslot);

        timeslotElement.remove();
    });

    const buttonLineElement = document.createElement('div');
    buttonLineElement.className = 'd-flex w-100 justify-content-between';
    buttonLineElement.style.padding = '10px';
    buttonLineElement.appendChild(deleteButtonElement);

    timeslotElement.appendChild(dateLineElement);
    timeslotElement.appendChild(buttonLineElement);
    return timeslotElement;
}

function deleteTimeSlot(tutorID, window, timeslot) {
    const params = new URLSearchParams();
    params.append('tutorID', tutorID);
    params.append('year', timeslot.date.year);
    params.append('month', timeslot.date.month);
    params.append('day', timeslot.date.dayOfMonth);
    params.append('start', timeslot.start);
    params.append('end', timeslot.end);

    fetch('/delete-availability', {method: 'POST', body: params});
}
