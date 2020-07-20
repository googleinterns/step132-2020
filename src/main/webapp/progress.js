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

/**
 * Function for progess.html, checks what kind of user the viewer is, and loads progress information
 */
function loadProgress() {
    fetch('/login-status').then(response => response.json()).then((loginStatus) => {
        console.log(loginStatus);    
        loadProgressHelper(document, loginStatus);
    });
}

function loadProgressHelper(document, loginStatus) {
    getExperiences(document, loginStatus);
    getGoals(document, loginStatus);
    getAchievements(document, loginStatus);
    getPastSessionsAndTopics(document, loginStatus);
}

/** A function that adds event listeners to a DOM objects. */
function addEventListeners() {
    document.getElementById("experiences-form").addEventListener('submit', event => {
        event.preventDefault();
        addExperience(window);
    });

    document.getElementById("goals-form").addEventListener('submit', event => {
        event.preventDefault();
        addGoal(window);
    });
}

function getExperiences(document, loginStatus) {
    var queryString = new Array();
    window.onload = readStudentID(queryString, window);
    const studentID = queryString["studentID"];

    const params = new URLSearchParams();
    params.append('studentID', studentID);
    fetch('/experience?studentID=' + studentID, {method: 'GET'}).then(response => response.json()).then((experiences) => {
        getExperiencesHelper(document, experiences, loginStatus);
    });

    // If the user is a student, allow them to add experiences
    if (loginStatus.role == "student") {
        document.getElementById('experiences-form').style.display = "block";
    } else {
        document.getElementById('experiences-form').style.display = "none";
    }
}

function getExperiencesHelper(document, experiences, loginStatus) {
    //if there was an error
    if(experiences.error) {
        var experienceContainer = document.getElementById('experiences');
        var errorMessage = document.createElement("p");
        errorMessage.innerText = experiences.error;
        experienceContainer.appendChild(errorMessage);
        return;
    }
    if (Object.keys(experiences).length != 0) {
        experiences.forEach((experience) => {
            document.getElementById('experiences').appendChild(createExperienceBox(experience, loginStatus));
        })
    } else {
        var experienceContainer = document.getElementById('experiences');
        var errorMessage = document.createElement("p");
        errorMessage.innerText = "This user has not set any past experiences";
        experienceContainer.appendChild(errorMessage);
        return;
    }
}

function getGoals(document, loginStatus) {
    var queryString = new Array();
    window.onload = readStudentID(queryString, window);
    const studentID = queryString["studentID"];

    const params = new URLSearchParams();
    params.append('studentID', studentID);
    fetch('/goal?studentID=' + studentID, {method: 'GET'}).then(response => response.json()).then((goals) => {
        getGoalsHelper(document, goals, loginStatus);
    });

    // If the user is a student, allow them to add goals
    if (loginStatus.role == "student") {
        document.getElementById('goals-form').style.display = "block";
    } else {
        document.getElementById('goals-form').style.display = "none";
    }
}

function getGoalsHelper(document, goals, loginStatus) {
    //if there was an error
    if(goals.error) {
        var goalContainer = document.getElementById('goals');
        var errorMessage = document.createElement("p");
        errorMessage.innerText = goals.error;
        goalContainer.appendChild(errorMessage);
        return;
    }
    if (Object.keys(goals).length != 0) {
        goals.forEach((goal) => {
            document.getElementById('goals').appendChild(createGoalBox(goal, loginStatus));
        })
    } else {
        var goalContainer = document.getElementById('goals');
        var errorMessage = document.createElement("p");
        errorMessage.innerText = "This user has not set any goals";
        goalContainer.appendChild(errorMessage);
        return;
    }
}

function getAchievements(document, loginStatus) {
    var achievementsContainer = document.getElementById('achievements');
    var errorMessage = document.createElement("p");
    errorMessage.innerText = "This user does not have any achievements";
    achievementsContainer.appendChild(errorMessage);
    return;
}

function getPastSessionsAndTopics(document, loginStatus) {
    var queryString = new Array();
    window.onload = readStudentID(queryString, window);
    const studentID = queryString["studentID"];

    const params = new URLSearchParams();
    params.append('studentID', studentID);
    fetch('/history?studentID=' + studentID, {method: 'GET'}).then(response => response.json()).then((tutoringSessions) => {
        getPastSessionsAndTopicsHelper(document, tutoringSessions);
    });
}

function getPastSessionsAndTopicsHelper(document, tutoringSessions) {
    //if there was an error
    if(tutoringSessions.error) {
        var pastSessions = document.getElementById('past-sessions');
        var pastTopics = document.getElementById('past-topics');
        var errorMessage1 = document.createElement("p");
        var errorMessage2 = document.createElement("p");
        errorMessage1.innerText = tutoringSessions.error;
        errorMessage2.innerText = tutoringSessions.error;
        pastSessions.appendChild(errorMessage1);
        pastTopics.appendChild(errorMessage2);
        return;
    }

    if (Object.keys(tutoringSessions).length != 0) {
        tutoringSessions.forEach((tutoringSession) => {
            document.getElementById('past-sessions').appendChild(createPastSessionBox(tutoringSession));
            document.getElementById('past-topics').appendChild(createPastTopicBox(tutoringSession));
        })
    } else {
        var pastSessions = document.getElementById('past-sessions');
        var pastTopics = document.getElementById('past-topics');
        var errorMessage1 = document.createElement("p");
        var errorMessage2 = document.createElement("p");
        errorMessage1.innerText = "This user has not had any tutoring sessions yet.";
        errorMessage2.innerText = "This user has not had any tutoring sessions yet.";
        pastSessions.appendChild(errorMessage1);
        pastTopics.appendChild(errorMessage2);
        return;
    }
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
            message.innerText = user.error;
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

/** Creates a div element containing information about a goal. */
function createGoalBox(goal, loginStatus) {
    const goalContainer = document.createElement("div");
    const description = document.createElement("h3");

    description.innerHTML = goal.goal;
    description.style.textTransform = "capitalize";
    description.style.display = 'inline';
    description.style.padding = '0px 15px 0px 0px';

    goalContainer.classList.add("result");
    goalContainer.classList.add("list-group-item");
    goalContainer.appendChild(description);

    // Only create the delete button if the user has the student role
    if (loginStatus.role == "student") {
        const deleteGoalButton = document.createElement('button');
        deleteGoalButton.innerText = 'Delete';
        deleteGoalButton.className = 'btn btn-default btn-lg';
        deleteGoalButton.addEventListener('click', () => {
            deleteGoal(goal, loginStatus.userId, window);

            goalContainer.remove();
        });
        goalContainer.appendChild(deleteGoalButton);
    }

    return goalContainer;
}

/** Creates a div element containing information about an experience. */
function createExperienceBox(experience, loginStatus) {
    const experienceContainer = document.createElement("div");
    const description = document.createElement("h3");

    description.innerHTML = experience.experience;
    description.style.textTransform = "capitalize";
    description.style.display = 'inline';
    description.style.padding = '0px 15px 0px 0px';

    experienceContainer.classList.add("result");
    experienceContainer.classList.add("list-group-item");
    experienceContainer.appendChild(description);

    // Only create the delete button if the user has the student role
    if (loginStatus.role == "student") {
        const deleteExperienceButton = document.createElement('button');
        deleteExperienceButton.innerText = 'Delete';
        deleteExperienceButton.className = 'btn btn-default btn-lg';
        deleteExperienceButton.addEventListener('click', () => {
            deleteExperience(experience, loginStatus.userId, window);

            experienceContainer.remove();
        });
        experienceContainer.appendChild(deleteExperienceButton);
    }

    return experienceContainer;
}

function addGoal(window) {
    const params = new URLSearchParams();

    var queryString = new Array();
    window.onload = readStudentID(queryString, window);
    const studentID = queryString["studentID"];

    params.append('goal', document.getElementById('newGoal').value);

    fetch('/add-goal', {method: 'POST', body: params}).then((response) => {
        //if the student id is not the id of the current user
        if(response.redirected) {
            window.location.href = response.url;
            alert("You must be signed in to add a goal.");
            return;
        }
        window.location.href = "/progress.html?studentID=" + studentID;
    });
}

function deleteGoal(goal, window) {
    const params = new URLSearchParams();
    params.append('id', goal.id);

    fetch('/delete-goal', {method: 'POST', body: params}).then((response) => {
        //if the student id is not the id of the current user
        if(response.redirected) {
            window.location.href = response.url;
            alert("You must be signed in to delete a goal.");
            return;
        }
    });;
}

function addExperience(window) {
    const params = new URLSearchParams();

    var queryString = new Array();
    window.onload = readStudentID(queryString, window);
    const studentID = queryString["studentID"];

    params.append('experience', document.getElementById('newExperience').value);

    fetch('/add-experience', {method: 'POST', body: params}).then((response) => {
        //if the student id is not the id of the current user
        if(response.redirected) {
            window.location.href = response.url;
            alert("You must be signed in to add an experience.");
            return;
        }
        window.location.href = "/progress.html?studentID=" + studentID;
    });
}

function deleteExperience(experience, window) {
    const params = new URLSearchParams();

    params.append('id', experience.id);

    fetch('/delete-experience', {method: 'POST', body: params}).then((response) => {
        //if the student id is not the id of the current user
        if(response.redirected) {
            window.location.href = response.url;
            alert("You must be signed in to delete an experience.");
            return;
        }
    });;
}

