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

/** Fetches info used to create the calendar  */
function fetchSessionInfo() {
    fetchSessionInfoHelper(window);
}

// Helper function for fetchSessionInfo, used for testing
function fetchSessionInfoHelper(window) {
    fetch('/confirmation', {method: 'GET'}).then((response) => {
        //if the student is not the current user or not signed in
        if(response.redirected) {
            window.location.href = response.url;
            alert("You must be signed in to manage sessions.");
            return [];
        }
        return response.json();
    }).then(async (scheduledSessions) => {
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
        createCalendar(scheduledSessions, container);
    });
}

/** Creates a calendar with the Charts API and renders it on the page  */
async function createCalendar(scheduledSessions, container) {
    const chart = new google.visualization.Timeline(container);

    const dataTable = new google.visualization.DataTable();
    dataTable.addColumn({type: 'string', id: 'Date'});
    dataTable.addColumn({type: 'string', id: 'Description'});
    dataTable.addColumn({type: 'date', id: 'Start'});
    dataTable.addColumn({type: 'date', id: 'End'});

    // Sort sessions in chronological order
    sortSessions(scheduledSessions);
    
    var sessionIDs = new Object();

    for (var session of scheduledSessions) {
        // Add 1 to the month so it displays correctly (January's default value is 0, February's is 1, etc.)
        var date = (session.timeslot.date.month+1) + '/' + session.timeslot.date.dayOfMonth + '/' + session.timeslot.date.year;

        // Wait for this promise to resolve so tutor is defined when making calendar row
        var tutorInfo = await getUser(session.tutorID);
        var description;
        var tutor;

        // If the tutor is also a student, get the information that relates to the tutor
        if (tutorInfo.student != null) {
            tutor = tutorInfo.tutor;
        } else {
            tutor = tutorInfo;
        }
        
        if (session.subtopics == '') {
            description = "Tutoring session with " + tutor.name;
        } else {
            description = session.subtopics + " with " + tutor.name;
        }

        // Add to associative array with calendar entry info as key and session ID as value so we can use them in the chart's event listener
        sessionIDs[date+description+asDate(session.timeslot.start)] = session.id;
        
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

    google.visualization.events.addListener(chart, 'select', function() {
        var selection = chart.getSelection()[0];
        if (selection) {
            cancelSession(sessionIDs, dataTable, selection);
        }
    });

    chart.draw(dataTable, options);
}

// Sorts all scheduled sessions in chronological order
function sortSessions(scheduledSessions) {
    scheduledSessions.sort(function(a, b) {
        return (a.timeslot.date.year + a.timeslot.date.month/12 + a.timeslot.date.dayOfMonth/365) - 
            (b.timeslot.date.year + b.timeslot.date.month/12 + b.timeslot.date.dayOfMonth/365);
    });
}

// Cancels the tutor session associated with the specified session ID
function cancelSession(sessionIDs, dataTable, selection) {
    // Add confirmation popup in case user accidentally clicked on session
    var deleteSession = confirm("Cancel upcoming tutor session?");

    if (deleteSession) {
        const params = new URLSearchParams();
        // Use associative array to get session ID
        params.append('id', sessionIDs[dataTable.getValue(selection.row, 0)+dataTable.getValue(selection.row, 1)+dataTable.getValue(selection.row, 2)]);

        fetch('/delete-tutor-session', {method: 'POST', body: params}).then((response) => {
            //if the student is not the current user or not signed in
            if(response.redirected) {
                window.location.href = response.url;
                alert("You must be signed in to cancel a tutoring session.");
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
   
google.charts.load('current', {'packages': ['timeline']});
google.charts.setOnLoadCallback(fetchSessionInfo);
