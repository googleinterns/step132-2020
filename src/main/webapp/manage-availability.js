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
        //if the student is not the current user or not signed in
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

        if (Object.keys(timeslots).length != 0) {
            timeslots.forEach((timeslot) => {
                document.getElementById('timeslots').appendChild(createTimeSlotBoxManage(timeslot));
            });
        } else {
            var timeslotsContainer = document.getElementById('timeslots');
            var errorMessage = document.createElement("p");
            errorMessage.innerText = "This user has not set any available timeslots";
            timeslotsContainer.appendChild(errorMessage);
            return;
        }
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
    // Display minutes regularly (e.g. 1:05pm rather than 1:5pm)
    if (minute < 10 && minute != 0) {
        minute = "0" + minute;
    }
    dateElement.innerText = hour + ":" + minute + amOrPm + " on " + months[timeslot.date.month] + " " + timeslot.date.dayOfMonth + ", " + timeslot.date.year;

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

    params.append('startTime', document.getElementById('startTime').value);
    params.append('endTime', document.getElementById('endTime').value);
    params.append('date', document.getElementById('date').value);

    fetch('/manage-availability', {method: 'POST', body: params}).then((response) => {
        //if the tutor is not the current user or not signed in
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
        //if the tutor is not the current user or not signed in
        if(response.redirected) {
            window.location.href = response.url;
            alert("You must be signed in to edit availability.");
            return;
        }
    });
}

/** Creates a calendar with the Charts API and renders it on the page  */
function createCalendar() {
    fetch('/manage-availability', {method: 'GET'}).then(response => response.json()).then((timeslots) => {  
        // Don't create a calendar if there are no available timeslots
        if (timeslots === undefined || timeslots.length == 0) {
            return;
        }

        // There are available timeslots, display header and calendar
        document.getElementById('calendar-header').style.display = 'block';
        
        const container = document.getElementById('calendar');
        const chart = new google.visualization.Timeline(container);

        const dataTable = new google.visualization.DataTable();
        dataTable.addColumn({type: 'string', id: 'Date'});
        dataTable.addColumn({type: 'date', id: 'Start'});
        dataTable.addColumn({type: 'date', id: 'End'});
        
        for (var slot of timeslots) {
            // Add 1 to the month so it displays correctly (January's default value is 0, February's is 1, etc.)
            var date = (slot.date.month+1) + '/' + slot.date.dayOfMonth + '/' + slot.date.year;
            dataTable.addRow([
                date, asDate(slot.start), asDate(slot.end)
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
