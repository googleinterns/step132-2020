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

/** Gets a list of the student's tutoring session history and displays them on the page. */
function getTutoringSessionHistory() {
    return getTutoringSessionHistoryHelper(window);
}

//Helper function for getTutoringSessionHistory, used for testing.
function getTutoringSessionHistoryHelper(window) {
    fetch('/history', {method: 'GET'}).then((response) => {
        //if the student id is not the id of the current user
        if(response.redirected) {
            window.location.href = response.url;
            alert("You must be signed in to view history.");
            return [];
        }
        return response.json();
    }).then((tutoringSessions) => {
        //if there was an error
        if(tutoringSessions.error) {
            var container = document.getElementById('tutoringSessionHistory');
            var errorMessage = document.createElement("p");
            p.innerText = tutoringSessions.error;
            container.appendChild(errorMessage);
            return;
        }

        if (Object.keys(tutoringSessions).length != 0) {
            tutoringSessions.forEach((tutoringSession) => {
                document.getElementById('tutoringSessionHistory').appendChild(createTutoringSessionBox(tutoringSession));
            });
        } else {
            var historyContainer = document.getElementById('tutoringSessionHistory');
            var errorMessage = document.createElement("p");
            errorMessage.innerText = "This user does not have any tutoring session history.";
            historyContainer.appendChild(errorMessage);
            return;
        }
    });
}

function createTutoringSessionBox(tutoringSession) {
    
    var months = [ "January", "February", "March", "April", "May", "June", 
           "July", "August", "September", "October", "November", "December" ];

    const tutoringSessionElement = document.createElement('li');
    tutoringSessionElement.className = 'list-group-item';

    const tutorElement = document.createElement('h3');
    tutorElement.style.textAlign = 'left';
    tutorElement.style.display = 'inline';

    setTutorEmail(tutorElement, tutoringSession.tutorID);

    const tutorLineElement = document.createElement('div');
    tutorLineElement.className = 'd-flex w-100 justify-content-between';
    tutorLineElement.appendChild(tutorElement);

    const dateElement = document.createElement('h3');
    dateElement.style.textAlign = 'left';
    dateElement.style.display = 'inline';
    var hour = Math.floor(parseInt(tutoringSession.timeslot.start) / 60);
    var amOrPm = "am";
    if (hour > 12) {
        hour = hour - 12;
        amOrPm = "pm"
    }
    var minute = parseInt(tutoringSession.timeslot.start) % 60;
    if (minute == 0) {
        minute = "00";
    }
    dateElement.innerText = hour + ":" + minute + amOrPm + " on " + months[tutoringSession.timeslot.date.month] +
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
            if(!tutoringSession.rated) {
                rateTutor(tutoringSession, rating);
                location.reload();
            }
        });
        starsElement.appendChild(stars[i])
    }

    for (var i = rating; i < 5; i++) {
        stars[i] = document.createElement('span');
        stars[i].className = 'glyphicon glyphicon-star-empty';
        const rating = i + 1;
        stars[i].addEventListener('click', () => {
            if(!tutoringSession.rated) {
                rateTutor(tutoringSession, rating);
                location.reload();
            }
        });
        starsElement.appendChild(stars[i])
    }

    return starsElement;
}

function rateTutor(tutoringSession, rating) {
    const params = new URLSearchParams();
    params.append('sessionId', tutoringSession.id);
    params.append('rating', rating);
    fetch('/rating', {method: 'POST', body: params});
}
