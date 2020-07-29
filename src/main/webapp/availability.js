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

/** Creates a calendar with the Charts API and renders it on the page  */
function createCalendar() {
    var queryString = new Array();
    window.onload = readComponents(queryString, window);
    const tutorID = queryString["tutorID"];

    fetch('/availability?tutorID=' + tutorID, {method: 'GET'}).then(response => response.json()).then((timeslots) => {  
        const container = document.getElementById('calendar');
        
        if (timeslots.error) {
            var message = document.createElement("p");
            p.innerText = timeslots.error;
            container.appendChild(message);
            return;
        }

        // Don't create a calendar if there are no available timeslots
        if (timeslots === undefined || timeslots.length == 0) {
            var errorMessage = document.createElement("p");
            errorMessage.innerText = "This user has not set any available timeslots";
            container.appendChild(errorMessage);
            return;
        }

        // There are available timeslots, display header and calendar
        document.getElementById('calendar-header').style.display = 'block';
        
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
                selectTime(tutorID, dataTable, selection);
            }
        });
        chart.draw(dataTable, options);
    });
}

// Redirects the user to the scheduling page and passes down the tutor ID along with the selected time range for the session.
function selectTime(tutorID, dataTable, selection) {
    var start = asMinutes(dataTable.getValue(selection.row, 1));
    var end = asMinutes(dataTable.getValue(selection.row, 2));
    var date = dataTable.getValue(selection.row, 0).split('/');
    var url = "scheduling.html?tutorID=" + encodeURIComponent(tutorID) +
                "&start=" + encodeURIComponent(start) +
                "&end=" + encodeURIComponent(end) +
                "&year=" + encodeURIComponent(date[2]) +
                "&month=" + encodeURIComponent(date[0]-1) +
                "&day=" + encodeURIComponent(date[1]);
    window.location.href = url;
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
