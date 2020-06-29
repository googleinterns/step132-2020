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

function getTutoringSessionHistory() {
    var queryString = new Array();
    window.onload = readStudentEmail(queryString, window);
    const studentEmail = queryString["studentEmail"];

    const params = new URLSearchParams();
    params.append('studentEmail', studentEmail);
    fetch('/history', {method: 'POST', body: params}).then(response => response.json()).then((tutoringSessions) => {
        tutoringSessions.forEach((tutoringSession) => {
            document.getElementById('tutoringSessionHistory').appendChild(createTutoringSessionBox(tutoringSession));
        })
    });
}

// Referenced to https://www.aspsnippets.com/Articles/Redirect-to-another-Page-on-Button-Click-using-JavaScript.aspx#:~:text=Redirecting%
// 20on%20Button%20Click%20using%20JavaScript&text=Inside%20the%20Send%20JavaScript%20function,is%20redirected%20to%20the%20URL on June 23rd.
// This function reads the id of the tutor that the student has selected, which is passed as an URI component, and add it to the queryString array..
function readStudentEmail(queryString, window) {
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

function createTutoringSessionBox(tutoringSession) {
    const tutoringSessionElement = document.createElement('li');
    tutoringSessionElement.className = 'list-group-item';

    const dateElement = document.createElement('h3');
    dateElement.style.textAlign = 'left';
    dateElement.style.display = 'inline';
    dateElement.innerHTML = tutoringSession.timeslot.start;

    const tutorNameElement = document.createElement('h3');
    tutorNameElement.style.textAlign = 'left';
    tutorNameElement.style.display = 'inline';
    tutorNameElement.innerHTML = tutoringSession.tutorEmail;


    const mainLineElement = document.createElement('div');
    mainLineElement.className = 'd-flex w-100 justify-content-between';
    mainLineElement.appendChild(dateElement);
    mainLineElement.appendChild(tutorNameElement);

    tutoringSessionElement.appendChild(mainLineElement);
    return tutoringSessionElement;
}
