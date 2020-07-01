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
    window.onload = readTutorID(queryString, window);
    const tutorID = queryString["tutorID"];

    const params = new URLSearchParams();
    params.append('tutorID', tutorID);
    fetch('/availability', {method: 'POST', body: params}).then(response => response.json()).then((timeslots) => {
        timeslots.forEach((timeslot) => {
            document.getElementById('timeslots').appendChild(createTimeSlotBox(timeslot, tutorID));
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

function createTimeSlotBox(timeslot, tutorID) {
    const timeslotElement = document.createElement('li');
    timeslotElement.className = 'list-group-item';

    const dateElement = document.createElement('h3');
    dateElement.style.textAlign = 'left';
    dateElement.style.display = 'inline';
    dateElement.innerHTML = timeslot.start;

    const selectButtonElement = document.createElement('button');
    selectButtonElement.innerText = 'Select';
    selectButtonElement.style.textAlign = 'right';
    selectButtonElement.style.display = 'inline';
    selectButtonElement.className = 'btn btn-default btn-lg';
    selectButtonElement.addEventListener('click', () => {
        selectTimeSlot(tutorID, window, timeslot);
    });

    const mainLineElement = document.createElement('div');
    mainLineElement.className = 'd-flex w-100 justify-content-between';
    mainLineElement.appendChild(dateElement);
    mainLineElement.appendChild(selectButtonElement);

    timeslotElement.appendChild(mainLineElement);
    return timeslotElement;
}

// Redirects the user to the scheduling page and passes down the tutor ID along with the selected time range for the session.
function selectTimeSlot(tutorID, window, timeslot) {
    var url = "scheduling.html?tutorID=" + encodeURIComponent(tutorID) +
                "&start=" + encodeURIComponent(timeslot.start) +
                "&end=" + encodeURIComponent(timeslot.end) +
                "&year=" + encodeURIComponent(timeslot.year) +
                "&month=" + encodeURIComponent(timeslot.month) +
                "&day=" + encodeURIComponent(timeslot.day);
    window.location.href = url;
}
