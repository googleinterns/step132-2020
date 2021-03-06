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
 * Function for registration.html, checks if user needs to register, if not display registration page
 */
function fetchLoginStatus() {
    fetch('/login-status').then(response => response.json()).then((loginStatus) => {
        fetchLoginStatusHelper(document, loginStatus);
    });
}

/**
 * Used for testing purposes with Jasmine
 */
function fetchLoginStatusHelper(document, loginStatus) {
    returnForm = document.getElementById('return');
    registrationForm = document.getElementById('registration-form');

    // If not registered, display registration form
    if (loginStatus.needsToRegister) {
        returnForm.style.display = 'none';
        registrationForm.style.display = 'block';
    } else {  // Logged out, set and display homepage URL
        registrationForm.style.display = 'none';
        returnForm.style.display = 'block';
    }
}

/**
 * Function for every page BUT registration.html, fetches login status and displays either login/logout link
 */
function displayLoginLogoutLink() {
    fetch('/login-status').then(response => response.json()).then((loginStatus) => {
        displayLoginLogoutLinkHelper(document, window, loginStatus);
    });
}

/**
 * Used for testing purposes with Jasmine
 */
function displayLoginLogoutLinkHelper(document, window, loginStatus) {
    // User is logged in, display logout and profile link on page 
    if (loginStatus.isLoggedIn) {
        //user needs to register and they are not on the registration page already
        if(loginStatus.needsToRegister && !window.location.href.includes("/registration.html")) {
            window.location.href = "/registration.html";
            return;
        }

        document.getElementById('login').style.display = "none";
        document.getElementById('logout').style.display = "block";
        document.getElementById('logout-url').href = "#";
        document.getElementById('logout-url').addEventListener('click', () => {
             logout(window);
        });
        document.getElementById('account-dropdown').style.display = "block";
        document.getElementById('profile').style.display = "block";
        document.getElementById('groups').style.display = "block";
        document.getElementById('profile').addEventListener('click', () => {
            setProfileQueryString(window, loginStatus);
        });
        document.getElementById('groups').addEventListener('click', () => {
            redirectToGroups(window, loginStatus);
        });

        if (loginStatus.role == "both") {
            document.getElementById('label-student').style.display = "block";
            document.getElementById('role-view-switch').style.display = "block";
            document.getElementById('label-tutor').style.display = "block";

            if (loginStatus.view == "student") {
                document.getElementById('view-checkbox').checked = false;
                displayStudentView(document, loginStatus);
            } else if (loginStatus.view == "tutor") {
                document.getElementById('view-checkbox').checked = true;
                displayTutorView(document, loginStatus);
            }
        }

        // If the user is only tutor, display availability settings
        if (loginStatus.role == "tutor") {
            displayTutorView(document, loginStatus);
        // Display tutor session settings and history if the user is a student
        } else if (loginStatus.role == "student") {
            displayStudentView(document, loginStatus);
        }
    }
    else {   // Display login link
        document.getElementById('logout').style.display = "none";
        document.getElementById('login').style.display = "block";
        document.getElementById('login-url').href = loginStatus.url;
        document.getElementById('account-dropdown').style.display = "none";
    }
}

function displayStudentView(document, loginStatus) {
    document.getElementById('availability-settings').style.display = "none";
    document.getElementById('my-students').style.display = "none";
    document.getElementById('my-lists').style.display = "none";
    document.getElementById('tutor-session-settings').style.display = "block";
    document.getElementById('history').style.display = "block";
    document.getElementById('tutor-session-settings').addEventListener('click', () => {
        redirectToManageSessions(window, loginStatus);
    });
    document.getElementById('history').addEventListener('click', () => {
        redirectToHistory(window, loginStatus);
    });
}

function displayTutorView(document, loginStatus) {
    document.getElementById('availability-settings').style.display = "block";
    document.getElementById('my-students').style.display = "block";
    document.getElementById('my-lists').style.display = "block";
    document.getElementById('tutor-session-settings').style.display = "none";
    document.getElementById('history').style.display = "none";
    document.getElementById('availability-settings').addEventListener('click', () => {
        redirectToManageAvailability(window, loginStatus);
    });
    document.getElementById('my-students').addEventListener('click', () => {
        redirectToMyStudents(window, loginStatus);
    });
    document.getElementById('my-lists').addEventListener('click', () => {
        redirectToMyLists(window, loginStatus);
    });
}

/** Tells the server to log the user out and redirect to homepage. */
function logout(window) {
    fetch('/logout', {method: 'GET'}).then((response) => {
        if(response.redirected) {
            window.location.href = response.url;
        } else {
            window.location.href = "/_gcp_iap/clear_login_cookie";
        }
    });
}

/**
 * Sets the URL's query string for the user's profile to their user ID
 */
function setProfileQueryString(window, loginStatus) {
    var url = "profile.html?userID=" + encodeURIComponent(loginStatus.userId);
    window.location.href = url;
}

/**
 * Redirects the user to the groups page.
 */
function redirectToGroups(window, loginStatus) {
    var url = "groups.html";
    window.location.href = url;
}

/**
 * Sets the URL's query string for the user's profile to their user ID and redirect them to manage-availability.html
 */
function redirectToManageAvailability(window, loginStatus) {
    var url = "manage-availability.html";
    window.location.href = url;
}

/**
 * Sets the URL's query string for the user's profile to their user ID and redirect them to my-students.html
 */
function redirectToMyStudents(window, loginStatus) {
    var url = "my-students.html";
    window.location.href = url;
}

/**
 * Sets the URL's query string for the user's profile to their user ID and redirect them to my-lists.html
 */
function redirectToMyLists(window, loginStatus) {
    var url = "my-lists.html";
    window.location.href = url;
}

/**
 * Sets the URL's query string for the user's profile to their user ID and redirect them to manage-sessions.html
 */
function redirectToManageSessions(window, loginStatus) {
    var url = "manage-sessions.html";
    window.location.href = url;
}

/**
 * Sets the URL's query string for the user's profile to their user ID and redirect them to history.html
 */
function redirectToHistory(window, loginStatus) {
    var url = "history.html";
    window.location.href = url;
}

/**
 * Displays registration information on the page
 */
function displayRegistrationInfo() {
    displayRegistrationInfoHelper(document);
}

/**
 * Used for testing purposes with Jasmine
 */
function displayRegistrationInfoHelper(document) {
    var generalInfo = document.getElementById('general-info');
    var studentTopics = document.getElementById('student-topics');
    var tutorTopics = document.getElementById('tutor-topics');
    generalInfo.style.display = 'block';

    // Display tutor info, hide student info
    if (document.getElementById('tutor').checked) {
        studentTopics.style.display = 'none';
        tutorTopics.style.display = 'block';
    } else if (document.getElementById('student').checked) {   // Display student info, hide tutor info
        tutorTopics.style.display = 'none';
        studentTopics.style.display = 'block';
    } else if (document.getElementById('both').checked) {
        tutorTopics.style.display = 'block';
        studentTopics.style.display = 'block';
    }
}

// Sets the action type of the registration form to the upload URL
function fetchBlobstoreUrl() {
    fetch('/blob-upload-url')
      .then((response) => {
        return response.text();
      })
      .then((imageUploadUrl) => {
        document.getElementById('registration-form').action = imageUploadUrl;
      });
}
