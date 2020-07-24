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

/** Gets the name the user searched for from the user search box and redirects the page to the search-user-results
    page with a url that contains a query parameter for the desired user's name. The user may already be on the
    search-user-results page. In that case, the page will reload with a different value for the topic query parameter. */
function redirectToResultsUsers() {
    return redirectToResultsUsersHelper(document, window);
}

/** Helper function for redirectToResultsUsers, used for testing purposes. */
function redirectToResultsUsersHelper(document, window) {
    var name = document.getElementById("searched-name").value;
    
    var url = "search-user-results.html?name=" + encodeURIComponent(name);
    window.location.href = url;

    return false;
}


/** Fetches the search results for the name that the user searched for. */
function getSearchUserResults() {
    return getSearchUserResultsHelper(document, window);
}

/** Helper function for getSearchUserResults, used for testing purposes. */
async function getSearchUserResultsHelper(document, window) {
    var queryString = new Array();
    window.onload = readComponents(queryString, window);
    var name = queryString["name"];

    if(name != null) {
        var users = getUsers(name);
        
        await users;
    }
}

/** Fetches the list of users for the name the user searched for. */
async function getUsers(name) {
    await fetch("/search-user?name=" + name).then(response => response.json()).then((results) => {
        var userContainer = document.getElementById("users");

        var numSearchResults = document.createElement("h4");
        numSearchResults.className = "num-search-results";

        userContainer.appendChild(numSearchResults);
        
        //if there was an error reported by the servlet, display the error message
        if(results.error) {
            numSearchResults.innerText = results.error;
            return;
        }

        //Only make "users" plural if there are 0 or more than 1 tutors
        numSearchResults.innerText = "Found " + results.length + (results.length > 1 || results.length === 0 ? " users named " : " user named ") + name;

        results.forEach(function(result) {
            userContainer.append(createUserResult(result));
        });
    });

}

/** Creates a div element containing information about a user result. */
function createUserResult(result) {
    var container = document.createElement("div");
    var name = document.createElement("h3");
    name.style.textTransform = 'capitalize';
    var profileLink = document.createElement("a");

    name.innerText = result.name;
    profileLink.innerText = "Profile";

    profileLink.href = "/profile.html?userID=" + result.userId;

    container.classList.add("user-result");
    container.classList.add("list-group-item");

    container.appendChild(name);
    container.appendChild(profileLink);

    return container;
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


