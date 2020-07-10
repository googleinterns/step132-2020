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

    fetch('/history?studentEmail=' + studentEmail, {method: 'GET'}).then(response => response.json()).then((tutoringSessions) => {
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
    var months = [ "January", "February", "March", "April", "May", "June", 
           "July", "August", "September", "October", "November", "December" ];

    const tutoringSessionElement = document.createElement('li');
    tutoringSessionElement.className = 'list-group-item';

    const tutorElement = document.createElement('h3');
    tutorElement.style.textAlign = 'left';
    tutorElement.style.display = 'inline';
    tutorElement.innerHTML = "Tutoring Session with " + tutoringSession.tutorEmail;

    const tutorLineElement = document.createElement('div');
    tutorLineElement.className = 'd-flex w-100 justify-content-between';
    tutorLineElement.appendChild(tutorElement);

    const dateElement = document.createElement('h3');
    dateElement.style.textAlign = 'left';
    dateElement.style.display = 'inline';
    var hour = parseInt(tutoringSession.timeslot.start) / 60;
    var amOrPm = "am";
    if (hour > 12) {
        hour = hour - 12;
        amOrPm = "pm"
    }
    var minute = parseInt(tutoringSession.timeslot.start) % 60;
    if (minute == 0) {
        minute = "00";
    }
    dateElement.innerHTML = hour + ":" + minute + amOrPm + " on " + months[tutoringSession.timeslot.date.month] +
                             " " + tutoringSession.timeslot.date.dayOfMonth + ", " + tutoringSession.timeslot.date.year;

    const dateLineElement = document.createElement('div');
    dateLineElement.className = 'd-flex w-100 justify-content-between';
    dateLineElement.appendChild(dateElement); 

    const starsElement = document.createElement('div');
    const starsLineElement = document.createElement('div');
    starsLineElement.className = 'd-flex w-100 justify-content-between';
    starsLineElement.appendChild(loadStars(starsElement, tutoringSession));

    tutoringSessionElement.appendChild(tutorLineElement);
    tutoringSessionElement.appendChild(dateLineElement);
    tutoringSessionElement.appendChild(starsLineElement);
    return tutoringSessionElement;
}

function loadStars(starsElement, tutoringSession) {
    var rating = 0;
    if (tutoringSession.rated) {
        rating = tutoringSession.rating;
    }

    var stars = [];
    for (var i = 0; i < rating; i++) {
        stars[i] = document.createElement('span');
        stars[i].className = 'glyphicon glyphicon-star';
        const rating = i + 1;
        stars[i].addEventListener('click', () => {
            rateTutor(tutoringSession, rating);
            location.reload();
        });
        starsElement.appendChild(stars[i])
    }

    for (var i = rating; i < 5; i++) {
        stars[i] = document.createElement('span');
        stars[i].className = 'glyphicon glyphicon-star-empty';
        const rating = i + 1;
        stars[i].addEventListener('click', () => {
            rateTutor(tutoringSession, rating);
            location.reload();
        });
        starsElement.appendChild(stars[i])
    }

    return starsElement;
}

function rateTutor(tutoringSession, rating) {
    const params = new URLSearchParams();
    params.append('studentEmail', tutoringSession.studentEmail);
    params.append('tutorEmail', tutoringSession.tutorEmail);
    params.append('rating', rating);
    fetch('/rating', {method: 'POST', body: params});
}
