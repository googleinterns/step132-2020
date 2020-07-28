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

/** A function that adds event listeners to a DOM objects. */
function addEventListeners() {
    document.getElementById("search-groups-form").addEventListener('submit', event => {
        event.preventDefault();
        getSearchGroupResults();
    });
}

/** Gets the name of the group the user search for and display the search results. */
function redirectToResultsGroups() {
    return redirectToResultsGroupsHelper(document, window);
}

/** Helper function for redirectToResultsGroups, used for testing purposes. */
function redirectToResultsGroupsHelper(document, window) {
    var group = document.getElementById("search-box-results").value;
    
    var url = "groups.html?group=" + encodeURIComponent(group);
    window.location.href = url;

    return false;
}


/** Gets the name the group searched for from the group search box and displays a list of the results from querying the databse
    for the given group name. */
function getSearchGroupResults() {
    return getSearchGroupResultsHelper(document, window);
}

/** Helper function for getSearchGroupResults, used for testing purposes. */
async function getSearchGroupResultsHelper(document, window) {
    var queryString = new Array();
    window.onload = readComponents(queryString, window);
    var group = queryString["group"];

    if(group != null) {
        var groups = getGroups(group);
        
        await groups;
    }
}

/** Fetches the list of groups for the group name the user searched for. */
async function getGroups(group) {
    await fetch("/manage-groups?group=" + group).then(response => response.json()).then((results) => {
        console.log("wait here");

        var groupContainer = document.getElementById("groups");

        var numSearchResults = document.createElement("h4");
        numSearchResults.className = "num-search-results";

        groupContainer.appendChild(numSearchResults);
        
        //if there was an error reported by the servlet, display the error message
        if(results.error) {
            numSearchResults.innerText = results.error;
            return;
        }

        //Only make "groups" plural if there are 0 or more than 1 tutors
        numSearchResults.innerText = "Found " + results.length + (results.length > 1 || results.length === 0 ? " groups name " : " group named ") + name;

        results.forEach(function(result) {
            groupContainer.append(createGroupResult(result));
        });
    });

}

/** Creates a div element containing information about a group result. */
function createGroupResult(result) {
    var container = document.createElement("div");
    var name = document.createElement("h3");
    name.style.textTransform = 'capitalize';
    //var members = document.createElement("h4");
    var topic = document.createElement("h4");
    topic.style.textTransform = 'capitalize';
    var description = document.createElement("h5");
    //var groupLink = document.createElement("a");

    name.innerText = result.name;
    //members.innerText = result.members + (result.members > 1 || result.members === 0 ? " members" : " member");
    topic.innerText = result.topic;
    description.innerText = result.description;
    //groupLink.innerText = "View";

    //groupLink.href = "/group.html?groupId=" + result.groupId;

    container.classList.add("user-result");
    container.classList.add("list-group-item");

    container.appendChild(name);
    //container.appendChild(members);
    container.appendChild(topic);
    container.appendChild(description);
    //container.appendChild(profileLink);

    return container;
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


