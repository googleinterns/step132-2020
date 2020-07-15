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

function getPastClasses() {
    const classesContainer = document.createElement("div");
    const message = document.createElement("h3");
    message.innerHTML = "This user has not added any past classes";

    classesContainer.classList.add("result");
    classesContainer.classList.add("list-group-item");
    classesContainer.appendChild(message);

    document.getElementById('past-classes').appendChild(classesContainer);
}

function getGoals() {
    const goalsContainer = document.createElement("div");
    const message = document.createElement("h3");
    message.innerHTML = "This user has not set any goals";

    goalsContainer.classList.add("result");
    goalsContainer.classList.add("list-group-item");
    goalsContainer.appendChild(message);

    document.getElementById('goals').appendChild(goalsContainer);
}

function getAchievements() {
    const achievementsContainer = document.createElement("div");
    const message = document.createElement("h3");
    message.innerHTML = "No achievements for this user";

    achievementsContainer.classList.add("result");
    achievementsContainer.classList.add("list-group-item");
    achievementsContainer.appendChild(message);

    document.getElementById('achievements').appendChild(achievementsContainer);
}

function getPastSessionsAndTopics() {
    var queryString = new Array();
    window.onload = readStudentID(queryString, window);
    const studentID = queryString["studentID"];

    const params = new URLSearchParams();
    params.append('studentID', studentID);
    fetch('/history?studentID=' + studentID, {method: 'GET'}).then(response => response.json()).then((tutoringSessions) => {
        //if there was an error
        if(tutoringSessions.error) {
            var pastSessions = document.getElementById('past-sessions');
            var pastTopics = document.getElementById('past-topics');
            var errorMessage = document.createElement("p");
            p.innerText = tutoringSessions.error;
            pastSessions.appendChild(errorMessage);
            pastTopics.appendChild(errorMessage);
            return;
        }

        tutoringSessions.forEach((tutoringSession) => {
            document.getElementById('past-sessions').appendChild(createPastSessionBox(tutoringSession));
            document.getElementById('past-topics').appendChild(createPastTopicBox(tutoringSession));
        })
    });
}

// Referenced to https://www.aspsnippets.com/Articles/Redirect-to-another-Page-on-Button-Click-using-JavaScript.aspx#:~:text=Redirecting%
// 20on%20Button%20Click%20using%20JavaScript&text=Inside%20the%20Send%20JavaScript%20function,is%20redirected%20to%20the%20URL on June 23rd.
// This function reads the id of the tutor that the student has selected, which is passed as an URI component, and add it to the queryString array..
function readStudentID(queryString, window) {
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

/** Creates a div element containing information about a past tutoring session. */
function createPastSessionBox(tutoringSession) {
    var months = [ "January", "February", "March", "April", "May", "June", 
           "July", "August", "September", "October", "November", "December" ];

    const sessionContainer = document.createElement("div");
    const tutorName = document.createElement("h3");
    const date = document.createElement("h6");

    setTutorName(tutorName, tutoringSession.tutorID);

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
    date.innerHTML = hour + ":" + minute + amOrPm + " on " + months[tutoringSession.timeslot.date.month] +
                     " " + tutoringSession.timeslot.date.dayOfMonth + ", " + tutoringSession.timeslot.date.year;

    sessionContainer.classList.add("result");
    sessionContainer.classList.add("list-group-item");
    sessionContainer.appendChild(tutorName);
    sessionContainer.appendChild(date);
    return sessionContainer;
}

//Sets tutorName to the name of the tutor who has the given id
function setTutorName(tutorName, tutorID) {
    var tutor;
    return getUser(tutorID).then(user => tutor = user).then(() => {
        tutorName.innerHTML = "Tutoring Session with " + tutor.name;
    });
}

/** Gets information about the given user from the server. */
function getUser(userID) {
    return fetch('/profile?userId='+userID).then(response => response.json()).then((user) => {
        if(user.error) {
            var message = document.createElement("p");
            p.innerText = user.error;
            document.body.appendChild(message);
            return;
        }
        return user;
    });
}

/** Creates a div element containing information about a past topic. */
function createPastTopicBox(tutoringSession) {
    const topicContainer = document.createElement("div");
    const topic = document.createElement("h3");

    topic.innerHTML = tutoringSession.subtopics;
    topic.style.textTransform = "capitalize";

    topicContainer.classList.add("result");
    topicContainer.classList.add("list-group-item");
    topicContainer.appendChild(topic);
    return topicContainer;
}




