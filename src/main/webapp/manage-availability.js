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
    fetch('/availability', {method: 'GET'}).then(response => response.json()).then((timeslots) => {
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
    deleteButtonElement.className = 'btn btn-default btn-lg';
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

    fetch('/add-availability', {method: 'POST', body: params}).then((response) => {
        console.log(response);
        //if the tutor id is not the id of the current user
        if(response.redirected) {
            window.location.href = response.url
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
        //if the tutor id is not the id of the current user
        if(response.redirected) {
            window.location.href = response.url
            return;
        }
    });
}
