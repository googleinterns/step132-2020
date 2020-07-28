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

function getAvailability() {
    var queryString = new Array();
    window.onload = readComponents(queryString, window);
    const tutorID = queryString["tutorID"];

    fetch('/availability?tutorID=' + tutorID, {method: 'GET'}).then(response => response.json()).then((timeslots) => {
        if(timeslots.error) {
            var message = document.createElement("p");
            p.innerText = timeslots.error;
            document.getElementById('timeslots').appendChild(message);
            return;
        }

        if (Object.keys(timeslots).length != 0) {
            timeslots.forEach((timeslot) => {
                document.getElementById('timeslots').appendChild(createTimeSlotBox(timeslot, tutorID));
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

function createTimeSlotBox(timeslot, tutorID) {
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
    // Display minutes regularly (e.g. 1:05pm rather than 1:5pm)
    if (minute < 10) {
        minute = "0" + minute;
    }
    dateElement.innerText = hour + ":" + minute + amOrPm + " on " + months[timeslot.date.month] + " " + timeslot.date.dayOfMonth + ", " + timeslot.date.year;

    const dateLineElement = document.createElement('div');
    dateLineElement.className = 'd-flex w-100 justify-content-between';
    dateLineElement.style.padding = '10px';
    dateLineElement.appendChild(dateElement);

    const selectButtonElement = document.createElement('button');
    selectButtonElement.innerText = 'Select';
    selectButtonElement.style.textAlign = 'right';
    selectButtonElement.style.display = 'inline';
    selectButtonElement.className = 'btn btn-default btn-lg';
    selectButtonElement.addEventListener('click', () => {
        selectTimeSlot(tutorID, window, timeslot);
    });

    const buttonLineElement = document.createElement('div');
    buttonLineElement.className = 'd-flex w-100 justify-content-between';
    buttonLineElement.style.padding = '10px';
    buttonLineElement.appendChild(selectButtonElement);

    timeslotElement.appendChild(dateLineElement);
    timeslotElement.appendChild(buttonLineElement);
    return timeslotElement;
}

// Redirects the user to the scheduling page and passes down the tutor ID along with the selected time range for the session.
function selectTimeSlot(tutorID, window, timeslot) {
    var url = "scheduling.html?tutorID=" + encodeURIComponent(tutorID) +
                "&start=" + encodeURIComponent(timeslot.start) +
                "&end=" + encodeURIComponent(timeslot.end) +
                "&year=" + encodeURIComponent(timeslot.date.year) +
                "&month=" + encodeURIComponent(timeslot.date.month) +
                "&day=" + encodeURIComponent(timeslot.date.dayOfMonth);
    window.location.href = url;
}

/** Creates a calendar with the Charts API and renders it on the page  */
function createCalendar() {
    var queryString = new Array();
    window.onload = readComponents(queryString, window);
    const tutorID = queryString["tutorID"];

    fetch('/availability?tutorID=' + tutorID, {method: 'GET'}).then(response => response.json()).then((timeslots) => {  
        console.log(timeslots);
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

        // Sort timeslots in chronological order
        timeslots.sort(function(a, b) {
            return (a.date.year + a.date.month/12 + a.date.dayOfMonth/365) - (b.date.year + b.date.month/12 + b.date.dayOfMonth/365);
        });
        
        for (var slot of timeslots) {
            console.log(slot.date);
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
