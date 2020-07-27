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
function loadProgress(document, loginStatus, user) {
    addEventListeners();

    document.getElementById('progress-tracker').style.display = 'block';

    getExperiences(document, loginStatus, user);
    getGoals(document, loginStatus, user);
    getAchievements(document, loginStatus);
    getPastSessionsAndTopics(document, loginStatus, user);
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

function getExperiences(document, loginStatus, user) {
    var queryString = new Array();
    window.onload = readComponents(queryString, window);
    const studentID = queryString["userID"];

    const params = new URLSearchParams();
    params.append('studentID', studentID);
    fetch('/experience?studentID=' + studentID, {method: 'GET'}).then(response => response.json()).then((experiences) => {
        getExperiencesHelper(document, experiences, loginStatus, user);
    });

    // If the user is a student, allow them to add experiences
    if (loginStatus.userId == user.userId) {
        document.getElementById('experiences-form').style.display = "block";
    } else {
        document.getElementById('experiences-form').style.display = "none";
    }
}

function getExperiencesHelper(document, experiences, loginStatus, user) {
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
            document.getElementById('experiences').appendChild(createExperienceBox(experience, loginStatus, user));
        })
    } else {
        var experienceContainer = document.getElementById('experiences');
        var errorMessage = document.createElement("p");
        errorMessage.innerText = "This user has not set any past experiences";
        experienceContainer.appendChild(errorMessage);
        return;
    }
}

function getGoals(document, loginStatus, user) {
    var queryString = new Array();
    window.onload = readComponents(queryString, window);
    const studentID = queryString["userID"];

    const params = new URLSearchParams();
    params.append('studentID', studentID);
    fetch('/goal?studentID=' + studentID, {method: 'GET'}).then(response => response.json()).then((goals) => {
        getGoalsHelper(document, goals, loginStatus, user);
    });

    // If the user is a student, allow them to add goals
    if (loginStatus.userId == user.userId) {
        document.getElementById('goals-form').style.display = "block";
    } else {
        document.getElementById('goals-form').style.display = "none";
    }
}

function getGoalsHelper(document, goals, loginStatus, user) {
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
            document.getElementById('goals').appendChild(createGoalBox(goal, loginStatus, user));
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

function getPastSessionsAndTopics(document, loginStatus, user) {
    // Do not display past tutoring sessions and past learned topics if the user viewing the profile is not one of the student's tutors or 
    // the student themselves
    if (user.tutors.includes(loginStatus.userId) || user.userId == loginStatus.userId) {
        const studentID = user.userId;

        const params = new URLSearchParams();
        params.append('studentID', studentID);
        fetch('/history?studentIDTutorView=' + studentID, {method: 'GET'}).then(response => response.json()).then((tutoringSessions) => {
            getPastSessionsAndTopicsHelper(document, tutoringSessions);
        });
    } else {
        document.getElementById("sessionsAndAchievements").style.display = "none";
        return;
    }
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
    date.innerText = hour + ":" + minute + amOrPm + " on " + months[tutoringSession.timeslot.date.month] +
                     " " + tutoringSession.timeslot.date.dayOfMonth + ", " + tutoringSession.timeslot.date.year;

    sessionContainer.classList.add("result");
    sessionContainer.classList.add("list-group-item");
    sessionContainer.appendChild(tutorName);
    sessionContainer.appendChild(date);
    return sessionContainer;
}

/** Creates a div element containing information about a past topic. */
function createPastTopicBox(tutoringSession) {
    const topicContainer = document.createElement("div");
    const topic = document.createElement("h3");

    topic.innerText = tutoringSession.subtopics;
    topic.style.textTransform = "capitalize";

    topicContainer.classList.add("result");
    topicContainer.classList.add("list-group-item");
    topicContainer.appendChild(topic);
    return topicContainer;
}

/** Creates a div element containing information about a goal. */
function createGoalBox(goal, loginStatus, user) {
    const goalContainer = document.createElement("div");
    const description = document.createElement("h3");

    description.innerText = goal.goal;
    description.style.textTransform = "capitalize";
    description.style.display = 'inline';
    description.style.padding = '0px 15px 0px 0px';

    goalContainer.classList.add("result");
    goalContainer.classList.add("list-group-item");
    goalContainer.appendChild(description);

    // Only create the delete button if the user has the student role
    if (loginStatus.userId == user.userId) {
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
function createExperienceBox(experience, loginStatus, user) {
    const experienceContainer = document.createElement("div");
    const description = document.createElement("h3");

    description.innerText = experience.experience;
    description.style.textTransform = "capitalize";
    description.style.display = 'inline';
    description.style.padding = '0px 15px 0px 0px';

    experienceContainer.classList.add("result");
    experienceContainer.classList.add("list-group-item");
    experienceContainer.appendChild(description);

    // Only create the delete button if the user has the student role
    if (loginStatus.userId == user.userId) {
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
    window.onload = readComponents(queryString, window);
    const studentID = queryString["userID"];

    params.append('goal', document.getElementById('newGoal').value);

    fetch('/add-goal', {method: 'POST', body: params}).then((response) => {
        //if the student is not the current user or not signed in
        if(response.redirected) {
            window.location.href = response.url;
            alert("You must be signed in to add a goal.");
            return;
        }
        window.location.href = "/profile.html?userID=" + studentID;
    });
}

function deleteGoal(goal, window) {
    const params = new URLSearchParams();
    params.append('id', goal.id);

    fetch('/delete-goal', {method: 'POST', body: params}).then((response) => {
        //if the student is not the current user or not signed in
        if(response.redirected) {
            window.location.href = response.url;
            alert("You must be signed in to delete a goal.");
            return;
        }
    });
}

function addExperience(window) {
    const params = new URLSearchParams();

    var queryString = new Array();
    window.onload = readComponents(queryString, window);
    const studentID = queryString["userID"];

    params.append('experience', document.getElementById('newExperience').value);

    fetch('/add-experience', {method: 'POST', body: params}).then((response) => {
        //if the student is not the current user or not signed in
        if(response.redirected) {
            window.location.href = response.url;
            alert("You must be signed in to add an experience.");
            return;
        }
        window.location.href = "/profile.html?userID=" + studentID;
    });
}

function deleteExperience(experience, window) {
    const params = new URLSearchParams();

    params.append('id', experience.id);

    fetch('/delete-experience', {method: 'POST', body: params}).then((response) => {
        //if the student is not the current user or not signed in
        if(response.redirected) {
            window.location.href = response.url;
            alert("You must be signed in to delete an experience.");
            return;
        }
    });
}

