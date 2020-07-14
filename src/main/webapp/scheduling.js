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

function scheduleTutorSession() {
    scheduleTutorSessionHelper(window);
}

function scheduleTutorSessionHelper(window) {
    getUserId().then(function(studentID) {
        var queryString = new Array();
        window.onload = readComponents(queryString, window);
        const tutorID = queryString["tutorID"];
        const start = queryString["start"];
        const end = queryString["end"];
        const year = queryString["year"];
        const month = queryString["month"];
        const day = queryString["day"];

        var subtopics = document.getElementById("topics").value;
        var questions = document.getElementById("questions").value;

        const params = new URLSearchParams();
        params.append('tutorID', tutorID);
        params.append('start', start);
        params.append('end', end);
        params.append('year', year);
        params.append('month', month);
        params.append('day', day);
        params.append('studentID', studentID);
        params.append('subtopics', subtopics);
        params.append('questions', questions);

        // Redirect user to confirmation
        fetch('/scheduling', {method: 'POST', body: params}).then(response => response.json()).then((tutors) => {
            if(tutors.error) {
                var message = document.createElement("p");
                message.innerText = tutors.error;
                document.body.appendChild(message);
                return;
            }

            redirectToConfirmation(studentID, window);
        });

    });

}

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

// Redirects the user to the confirmation page and passes down the student ID.
function redirectToConfirmation(studentID, window) {
    var url = "confirmation.html?studentID=" + encodeURIComponent(studentID);
    window.location.href = url;
}
