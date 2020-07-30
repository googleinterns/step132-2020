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

// Referenced to https://www.aspsnippets.com/Articles/Redirect-to-another-Page-on-Button-Click-using-JavaScript.aspx#:~:text=Redirecting%
// 20on%20Button%20Click%20using%20JavaScript&text=Inside%20the%20Send%20JavaScript%20function,is%20redirected%20to%20the%20URL on June 23rd.
// This function reads the id of the tutor that the student has selected and the start and end of the time range selected, which are
// all passed as URI components, and adds them to the queryString array.
function readComponents(queryString, window) {
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

//Helper function for testing purposes
//Sets the tutor element's email field to the tutor email
function setTutorName(tutorElement, tutorID) {
    var tutor;
    return getUser(tutorID).then(user => tutor = user).then(() => {
        tutorElement.innerHTML = "Tutoring Session with " + tutor.name;
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

function getUserId() {
    return fetch('/login-status').then(response => response.json()).then((loginStatus) => {
        return loginStatus.userId;
    });
}

/** When a user selects "other" topic, it displays the text input so that the user can type their topic. If the user 
*   selects a different topic, it hides the text input.
*/
function checkOtherSelected(value) {
    var otherTextBox = document.getElementById('other-topic');
    if(value === 'other') {
        otherTextBox.style.display = 'block';
        otherTextBox.setAttribute("required", "");
        otherTextBox.setAttribute("aria-required", true);
    } else  {
        otherTextBox.style.display = 'none';
        otherTextBox.removeAttribute("required");
        otherTextBox.removeAttribute("aria-required");
    }
}


