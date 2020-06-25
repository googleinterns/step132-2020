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

/** Gets the topic the user searched for from the search box and redirects the page to the search-results page with a url that contains a query parameter for the topic. 
    The user may already be on the search-results page. In that case, the page will reload with a different value for the topic query parameter. */
function redirectToResults() {
    return redirectToResultsHelper(document, window);
}

/** Helper function for redirectToResults, used for testing purposes. */
function redirectToResultsHelper(document, window) {
    var topic = document.getElementById("search-box-homepage").value;

    var url = "search-results.html?topic=" + encodeURIComponent(topic);
    window.location.href = url;

    return false;
}

/** Fetches the search results for the topic that the user searched for. */
function getSearchResults() {
    return getSearchResultsHelper(document, window);
}

/** Helper function for getSearchResults, used for testing purposes. */
async function getSearchResultsHelper(document, window) {
    var topic;

    if (window.location.search.split('?').length > 0) {
        var topicParam = window.location.search.split('?')[1];
        topic = decodeURIComponent(topicParam.split('=')[1]);
    }

    if(topic != null) {
        await fetch("/search?topic="+topic).then(response => response.json()).then((results) => {
            var resultContainer = document.getElementById("result-container");
            var searchResultsFor = document.createElement("h4");
            searchResultsFor.innerText = "Search results for " + topic + ":";
            searchResultsFor.id = "search-results-topic";

            resultContainer.appendChild(searchResultsFor);

            results.forEach(function(result) {
                resultContainer.append(createSearchResult(result));
            });
        });
    }
}

function createSearchResult(result) {
   result = JSON.parse(result);

    var container = document.createElement("div");
    var name = document.createElement("h3");
    var email = document.createElement("h6");
    var skills = document.createElement("p");
    var availabilityLink = document.createElement("a");

    name.innerText = result.name;
    email.innerText = result.email;
    skills.innerText = "Skills: " + result.skills.join(" ");
    availabilityLink.innerText = "Availability";

    availabilityLink.href = "/availability.html?tutorID=" + result.email;

    container.classList.add("search-result");
    container.classList.add("list-group-item");

    // list-group-item list-group-item-action flex-column align-items-start

    container.appendChild(name);
    container.appendChild(email);
    container.appendChild(skills);
    container.appendChild(availabilityLink);

    return container;

}