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
async function createCalendar() {
    await fetch('/confirmation', {method: 'GET'}).then((response) => {
        //if the student is not the current user or not signed in
        if(response.redirected) {
            window.location.href = response.url;
            alert("You must be signed in to manage sessions.");
            return [];
        }
        return response.json();
    }).then((scheduledSessions) => {
        const container = document.getElementById('calendar');
        
        if(scheduledSessions.error) {
            var message = document.createElement("p");
            p.innerText = scheduledSessions.error;
            document.getElementById('calendar').appendChild(message);
            return;
        }

        // Don't create a calendar if there are no scheduled sessions
        if (scheduledSessions === undefined || scheduledSessions.length == 0) {
            var errorMessage = document.createElement("p");
            errorMessage.innerText = "This user does not have any scheduled tutoring sessions.";
            container.appendChild(errorMessage);
            return;
        }

        createCalendarHelper(scheduledSessions, container);
    });
}

// Helper function for createCalendar to improve readability 
async function createCalendarHelper(scheduledSessions, container) {
    const chart = new google.visualization.Timeline(container);

    const dataTable = new google.visualization.DataTable();
    dataTable.addColumn({type: 'string', id: 'Date'});
    dataTable.addColumn({type: 'string', id: 'Description'});
    dataTable.addColumn({type: 'date', id: 'Start'});
    dataTable.addColumn({type: 'date', id: 'End'});

    // Sort sessions in chronological order
    sortSessions(scheduledSessions);

    for (var session of scheduledSessions) {
        // Add 1 to the month so it displays correctly (January's default value is 0, February's is 1, etc.)
        var date = (session.timeslot.date.month+1) + '/' + session.timeslot.date.dayOfMonth + '/' + session.timeslot.date.year;

        // Wait for this promise to resolve so tutor is defined when making calendar row
        var tutor = await getUser(session.tutorID);
        var description;
        if (session.subtopics == '') {
            description = "Tutoring session with " + tutor.name;
        } else {
            description = session.subtopics + " with " + tutor.name;
        }
        
        dataTable.addRow([
            date, description, asDate(session.timeslot.start), asDate(session.timeslot.end)
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
}

// Sorts all scheduled sessions in chronological order
function sortSessions(scheduledSessions) {
    scheduledSessions.sort(function(a, b) {
        return (a.timeslot.date.year + a.timeslot.date.month/12 + a.timeslot.date.dayOfMonth/365) - 
            (b.timeslot.date.year + b.timeslot.date.month/12 + b.timeslot.date.dayOfMonth/365);
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
