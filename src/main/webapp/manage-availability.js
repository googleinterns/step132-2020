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

/** A function that adds event listeners to a DOM objects. */
function addEventListeners() {
    document.getElementById("timeslot-form").addEventListener('submit', event => {
        event.preventDefault();
        addTimeSlot();
    });
}

function getAvailabilityManage() {
    return getAvailabilityManageHelper(window);
}

function getAvailabilityManageHelper(window) {
    fetch('/manage-availability', {method: 'GET'}).then((response) => {
        //if the student is not the current user/signed in
        if(response.redirected) {
            window.location.href = response.url;
            alert("You must be signed in to manage availability.");
            return [];
        }
        return response.json();
        
    }).then((timeslots) => {
        if(timeslots.error) {
            var message = document.createElement("p");
            p.innerText = timeslots.error;
            document.getElementById('timeslots').appendChild(message);
            return;
        }

        timeslots.forEach((timeslot) => {
            document.getElementById('timeslots').appendChild(createTimeSlotBoxManage(timeslot));
        });
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

function createTimeSlotBoxManage(timeslot) {
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
    deleteButtonElement.className = 'btn btn-lg';
    deleteButtonElement.addEventListener('click', () => {
        deleteTimeSlot(window, timeslot);

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

/** Tells the server to add a timeslot for a tutor. */
function addTimeSlot() {
    return addTimeSlotHelper(window);
}

//Helper function for addTimeSlot, used for testing.
function addTimeSlotHelper(window) {
    const params = new URLSearchParams();

    params.append('startHour', document.getElementById('startHour').value);
    params.append('startMinute', document.getElementById('startMinute').value);
    params.append('endHour', document.getElementById('endHour').value);
    params.append('endMinute', document.getElementById('endMinute').value);
    params.append('day', document.getElementById('day').value);
    params.append('month', document.getElementById('month').value);
    params.append('year', document.getElementById('year').value);

    fetch('/manage-availability', {method: 'POST', body: params}).then((response) => {
        //if the tutor is not the current user/signed in
        if(response.redirected) {
            window.location.href = response.url;
            alert("You must be signed in to edit availability.");
            return;
        }
        window.location.href = "/manage-availability.html";
    });
}

/** Function to tell the server to delete a time slot for tutor.  */
function deleteTimeSlot(window, timeslot) {
    const params = new URLSearchParams();
    params.append('year', timeslot.date.year);
    params.append('month', timeslot.date.month);
    params.append('day', timeslot.date.dayOfMonth);
    params.append('start', timeslot.start);
    params.append('end', timeslot.end);

    fetch('/delete-availability', {method: 'POST', body: params}).then((response) => {
        //if the tutor is not the current user/signed in
        if(response.redirected) {
            window.location.href = response.url;
            alert("You must be signed in to edit availability.");
            return;
        }
    });
}
