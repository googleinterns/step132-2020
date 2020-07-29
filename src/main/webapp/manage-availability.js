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

/** Tells the server to add a timeslot for a tutor. */
function addTimeSlot() {
    return addTimeSlotHelper(window);
}

//Helper function for addTimeSlot, used for testing.
function addTimeSlotHelper(window) {
    const params = new URLSearchParams();

    params.append('startTime', document.getElementById('startTime').value);
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

// Sets the minimum date and time that the user can enter into the timeslot form
function setMinDate() {
    var today = new Date();
    var year = today.getFullYear();
    var month = today.getMonth() + 1; 
    var day = today.getDate(); 
    var hour = today.getHours(); 
    var minute = today.getMinutes(); 
    setMinDateHelper(document, year, month, day, hour, minute);
}

// Helper function for setMinDate, used for testing
function setMinDateHelper(document, year, month, day, hour, minute) {
    // Add a 0 before the month, day, hour, and minute if they're less than 10 so the min value is properly set
    if (month < 10) {
        month = "0" + month;
    }
    if (day < 10) {
        day = "0" + day;
    }
    if (hour < 10) {
        hour = "0" + hour;
    }
    if (minute < 10) {
        minute = "0" + minute;
    }

    document.getElementById('date').min = year + '-' + month + '-' + day;
    document.getElementById('startTime').min = hour + ":" + minute;
}

/** Creates a calendar with the Charts API and renders it on the page  */
function createCalendar() {
    fetch('/manage-availability', {method: 'GET'}).then((response) => {
        //if the student is not the current user or not signed in
        if(response.redirected) {
            window.location.href = response.url;
            alert("You must be signed in to manage availability.");
            return [];
        }
        return response.json();
    }).then((timeslots) => {
        const container = document.getElementById('calendar');
        
        if(timeslots.error) {
            var message = document.createElement("p");
            p.innerText = timeslots.error;
            document.getElementById('calendar').appendChild(message);
            return;
        }

        // Don't create a calendar if there are no available timeslots
        if (timeslots === undefined || timeslots.length == 0) {
            var errorMessage = document.createElement("p");
            errorMessage.innerText = "This user has not set any available timeslots.";
            container.appendChild(errorMessage);
            return;
        }

        createCalendarHelper(timeslots, container);
    });
}

// Helper function for createCalendar to improve readability 
function createCalendarHelper(timeslots, container) {
    const chart = new google.visualization.Timeline(container);

    const dataTable = new google.visualization.DataTable();
    dataTable.addColumn({type: 'string', id: 'Date'});
    dataTable.addColumn({type: 'date', id: 'Start'});
    dataTable.addColumn({type: 'date', id: 'End'});

    // Sort timeslots in chronological order
    sortTimeslots(timeslots);
    
    for (var slot of timeslots) {
        // Add 1 to the month so it displays correctly (January's default value is 0, February's is 1, etc.)
        var date = (slot.date.month+1) + '/' + slot.date.dayOfMonth + '/' + slot.date.year;
        dataTable.addRow([
            date, asDate(slot.start), asDate(slot.end)
        ]);
    }

    // Have timeline span 24 hours regardless of what's scheduled
    var min = new Date();
    min.setHours(0);
    var max = new Date();
    max.setHours(24);

    const options = {
        width: 1000,
        height: 300,
        hAxis: {
            minValue: min,
            maxValue: max
        }
    };

    google.visualization.events.addListener(chart, 'select', function() {
        var selection = chart.getSelection()[0];
        if (selection) {
            deleteSession(dataTable, selection);
        }
    });

    chart.draw(dataTable, options);
}

// Sorts all available time slots in chronological order
function sortTimeslots(timeslots) {
    timeslots.sort(function(a, b) {
        return (a.date.year + a.date.month/12 + a.date.dayOfMonth/365) - (b.date.year + b.date.month/12 + b.date.dayOfMonth/365);
    });
}

/** Function to tell the server to delete an available time slot for tutor.  */
function deleteSession(dataTable, selection) {
    // Add confirmation popup in case user accidentally clicked on time slot
    var deleteSession = confirm("Delete available time slot?");

    if (deleteSession) {
        var start = asMinutes(dataTable.getValue(selection.row, 1));
        var end = asMinutes(dataTable.getValue(selection.row, 2));
        var date = dataTable.getValue(selection.row, 0).split('/');
        const params = new URLSearchParams();
        params.append('year', date[2]);
        params.append('month', date[0]-1);
        params.append('day', date[1]);
        params.append('start', start);
        params.append('end', end);

        fetch('/delete-availability', {method: 'POST', body: params}).then((response) => {
            //if the tutor is not the current user or not signed in
            if(response.redirected) {
                window.location.href = response.url;
                alert("You must be signed in to edit availability.");
                return;
            }
        }).then(location.reload());
    }
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

// Converts a JavaScript Date object into "minutes since midnight"
function asMinutes(date) {
    return date.getHours()*60 + date.getMinutes();
}

google.charts.load('current', {'packages': ['timeline']});
google.charts.setOnLoadCallback(createCalendar);
