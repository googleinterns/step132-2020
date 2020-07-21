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

/** Lets users switch between the tutors and books tabs. */
function switchTab(elem) {
    var currentActiveTab = document.getElementsByClassName("active")[0];
    var currentActiveContainer = document.getElementsByClassName("active-container")[0];

    currentActiveTab.classList.remove("active");
    currentActiveContainer.classList.remove("active-container");

    document.getElementById(elem.innerText.toLowerCase()).classList.add("active-container");

    elem.parentNode.classList.add("active");
}

/** Gets the topic the user searched for from the search box and redirects the page to the search-results page with a url that contains a query parameter for the topic. 
    The user may already be on the search-results page. In that case, the page will reload with a different value for the topic query parameter. */
function redirectToResults() {
    return redirectToResultsHelper(document, window);
}

/** Helper function for redirectToResults, used for testing purposes. */
function redirectToResultsHelper(document, window) {
    var topic = document.getElementsByClassName("search-box")[0].value;
    
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
        var tutors = getTutors(topic);
        var books = getBooks(topic);
        
        await tutors;
        await books;
    }
}

/** Fetches the list of books for the topic the user searched for. */
async function getBooks(topic) {
    //TODO: create load more button so we can get next 40 results
    await fetch("https://www.googleapis.com/books/v1/volumes?q=" + topic + "&maxResults=40&key=AIzaSyBR6aZ9eFTRQ3Cv4eDBMeB0LSH-jF-Rbsw").then(response => response.json()).then((results) => {
        console.log(results);
        var books = document.getElementById("books");

        var numSearchResults = document.createElement("h4");
        numSearchResults.className = "num-search-results";

        books.appendChild(numSearchResults);

        //if there was an error reported by the servlet, display the error message
        if(results.error) {
            numSearchResults.innerText = results.error.message;
            return;
        }

        //Only make "books" plural if there are 0 or more than 1 books
        numSearchResults.innerText = "Found " + results.items.length + (results.items.length > 1 || results.items.length === 0 ? " books for " : " book for ") + topic;

        //create container to put books
        var booksContainer = document.createElement("div");
        booksContainer.id = "books-container";

        results.items.forEach(function(result) {
            booksContainer.append(createBookResult(result.volumeInfo));
        });

        books.appendChild(booksContainer);
    });
}

/** Fetches the list of tutors for the topic the user searched for. */
async function getTutors(topic) {
    await fetch("/search?topic="+topic).then(response => response.json()).then((results) => {
        var tutorContainer = document.getElementById("tutors");

        var numSearchResults = document.createElement("h4");
        numSearchResults.className = "num-search-results";

        tutorContainer.appendChild(numSearchResults);
        
        //if there was an error reported by the servlet, display the error message
        if(results.error) {
            numSearchResults.innerText = results.error;
            return;
        }

        //Only make "tutors" plural if there are 0 or more than 1 tutors
        numSearchResults.innerText = "Found " + results.length + (results.length > 1 || results.length === 0 ? " tutors for " : " tutor for ") + topic;

        results.forEach(function(result) {
            tutorContainer.append(createTutorResult(result));
        });
    });

}

/** Creates a anchor element containing information about a book result. */
function createBookResult(result) {
    var container = document.createElement("a");
    var thumbnail = document.createElement("img");
    var title = document.createElement("p");
    var author = document.createElement("p");

    container.href = result.infoLink;
    container.setAttribute("target", "_blank");

    if(!result.imageLinks) {
        thumbnail.src = "/images/book-cover.png"
    } else {
        thumbnail.src = result.imageLinks.smallThumbnail;
    }

    title.innerText = result.title;

    if(result.authors) {
        author.innerText = "by " + result.authors.join(", ");
    }

    container.classList.add("book-result");
    container.classList.add("col-md-4");

    container.appendChild(thumbnail);
    container.appendChild(title);
    container.appendChild(author);

    return container;
}

/** Creates a div element containing information about a tutor result. */
function createTutorResult(result) {
    var container = document.createElement("div");
    var name = document.createElement("h3");
    var email = document.createElement("h6");
    var skills = document.createElement("p");
    var availabilityLink = document.createElement("a");

    name.innerText = result.name;
    email.innerText = result.email;
    skills.innerText = "Skills: " + result.skills.join(", ");
    availabilityLink.innerText = "Availability";

    availabilityLink.href = "/availability.html?tutorID=" + result.userId;

    container.classList.add("tutor-result");
    container.classList.add("list-group-item");

    skills.style.textTransform = "capitalize";

    container.appendChild(name);
    container.appendChild(email);
    container.appendChild(skills);
    container.appendChild(availabilityLink);

    return container;
} 
