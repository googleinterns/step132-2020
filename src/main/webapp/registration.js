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
        console.log(loginStatus);    
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
        console.log(loginStatus);   
        displayLoginLogoutLinkHelper(document, loginStatus);
    });
}

/**
 * Used for testing purposes with Jasmine
 */
function displayLoginLogoutLinkHelper(document, loginStatus) {
    // User is logged in, display logout link on page 
    if (loginStatus.isLoggedIn) {
        document.getElementById('login').style.display = "none";
        document.getElementById('logout').style.display = "block";
        document.getElementById('logout-url').href = loginStatus.url;
    }
    else {   // Display login link
        document.getElementById('logout').style.display = "none";
        document.getElementById('login').style.display = "block";
        document.getElementById('login-url').href = loginStatus.url;
    }
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
    var tutorInfo = document.getElementById('tutor-info');
    generalInfo.style.display = 'block';

    // Display extra information to fill if user is a tutor
    if (document.getElementById('tutor').checked) {
        tutorInfo.style.display = 'block';
    } else {   // User may have clicked tutor then switched back to student, in that case hide tutor information
        tutorInfo.style.display = 'none';
    }
}
