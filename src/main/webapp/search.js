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

function handleTutorSort(elem) {
    return getSearchResultsHelper(window, elem.options[elem.selectedIndex].value);
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
    return getSearchResultsHelper(window);
}

/** Helper function for getSearchResults, used for testing purposes. */
async function getSearchResultsHelper(window, sort) {
    var topic;

    if (window.location.search.split('?').length > 0) {
        var topicParam = window.location.search.split('?')[1];
        topic = decodeURIComponent(topicParam.split('=')[1]);
    }

    if(topic != null) {
        var tutors = getTutors(topic, sort);
        var tutorBooks = getTutorBooks(topic);
        var googleBooks = getBooks(topic);
        
        await tutors;
        await tutorBooks;
        await googleBooks;
    }
}

/** Fetches book lists created by tutors for the specified topic and displays them on the page. */
async function getTutorBooks(topic) {
    await fetch("/lists?topic=" + topic).then(response => response.json()).then((results) => {
        //if there was an error, display the error message
        if(results.error) {
            document.getElementById("tutor-books-label").innerText = results.error;
            return;
        }

        if(Object.keys(results).length === 0) {
            document.getElementById("tutor-books").style.display = "none";
            return;
        }

        var listContainer = document.getElementById("lists-container");

        results.forEach((result) => {
            listContainer.appendChild(createTutorListElement(result));
        });

    });
}

/** Creates a tutor list result element and sets the modal content to the list when the user clicks on "view list" button. */
function createTutorListElement(result) {
    var container = document.createElement("li");
    var listName = document.createElement("h4");
    var tutorName = document.createElement("a"); 
    var listButton = document.createElement("button");

    listName.innerText = result.name;
    setTutorNameInLink(tutorName, result.tutorID);
    listButton.innerText = "View List";

    //link to tutor profile
    tutorName.href = "/profile.html?userID=" + result.tutorID;

    listName.className = "list-name-result";
    tutorName.className = "tutor-name-result";
    listButton.classList.add("list-modal-button");
    listButton.classList.add("btn");
    listButton.classList.add("btn-sm");
    container.classList.add("list-group-item");
    container.classList.add("tutor-list-result");

    tutorName.setAttribute("target", "_blank");
    listButton.setAttribute("data-toggle", "modal");
    listButton.setAttribute("data-target", "#view-list-modal");

    //set the modal content when user clicks on view list button
    listButton.addEventListener("click", function() {
        var modalName = document.getElementById("list-modal-name");
        modalName.innerText = result.name;

        var modalBody = document.getElementById("list-modal-body");
        modalBody.innerHTML = "";

        result.books.forEach((book) => {
            var element = document.createElement("li");
            element.classList.add("list-group-item");
            element.innerText = book;
            modalBody.appendChild(element);
        });
    });

    container.appendChild(listName);
    container.appendChild(tutorName);
    container.appendChild(listButton);

    return container;
}

//keeps track of the number of books loaded so far from the Google Books API
var numResultsLoaded = 0;
/** Fetches the list of books for the topic the user searched for and displays them on the page. */
async function getBooks(topic) {
    await fetch("/books?topic="+topic)
        .then(response => response.json()).then((results) => {
            var numSearchResults = document.getElementById("num-book-results");
            //if there was an error reported by the api, display the error message
            if(results.error) {
                numSearchResults.innerText = results.error.message;
                return;
            }

            //Only make "books" plural if there are 0 or more than 1 books
            numSearchResults.innerText = "Found " + results.items.length + (results.items.length > 1 || results.items.length === 0 ? " books for " : " book for ") + topic;

            //create container to put books
            var booksContainer = document.getElementById("books-container");

            numResultsLoaded = results.items.length;

            results.items.forEach(function(result) {
                booksContainer.append(createBookResult(result.volumeInfo));
            });

            //if we got the max results, there might be more
            if(results.items.length == 40) {
                var loadMore = document.createElement("button");
                loadMore.id = "load-more";
                loadMore.classList.add("btn");
                loadMore.classList.add("btn-default");
                loadMore.addEventListener("click", function() {
                    loadMoreBooks(topic);
                });
                loadMore.innerText = "Load More";
                document.getElementById("google-books").appendChild(loadMore);
            }

    });
}

function loadMoreBooks(topic) {
    fetch("/books?topic=" + topic + "&num-loaded=" + numResultsLoaded)
        .then(response => response.json()).then((results) => {
            var numSearchResults = document.getElementById("num-book-results");

            //if there was an error reported by the api, display the error message
            if(results.error) {
                numSearchResults.innerText = results.error.message;
                return;
            }

            numResultsLoaded += results.items.length;

            //Only make "books" plural if there are 0 or more than 1 books
            numSearchResults.innerText = "Found " + numResultsLoaded + " books for " + topic;

            //create container to put books
            var booksContainer = document.getElementById("books-container");

            results.items.forEach(function(result) {
                booksContainer.append(createBookResult(result.volumeInfo));
            });

            if(results.items.length < 40) {
                document.getElementById("load-more").display = "none";
            }

    });
}

/** Fetches the list of tutors for the topic the user searched for. */
async function getTutors(topic, sort) {
    if(sort === undefined) {
        sort = "alpha";
    }
    await fetch("/search?topic=" + topic + "&sort-type=" + sort).then(response => response.json()).then((results) => {

        var numSearchResults = document.getElementById("num-tutor-results");
        
        //if there was an error reported by the servlet, display the error message
        if(results.error) {
            numSearchResults.innerText = results.error;
            return;
        }

        //Only make "tutors" plural if there are 0 or more than 1 tutors
        numSearchResults.innerText = "Found " + results.length + (results.length > 1 || results.length === 0 ? " tutors for " : " tutor for ") + topic;

        if(results.length == 0) {
            document.getElementById("tutor-sort").style.display = "none";
        }

        var tutorContainer = document.getElementById("tutors-container");

        tutorContainer.innerHTML = "";

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
    var rating = document.createElement("div");
    var email = document.createElement("h6");
    var skills = document.createElement("p");
    var availabilityLink = document.createElement("a");

    name.innerHTML = "<a style='color:black' href='/profile.html?userID=" + result.userId + "'>" + result.name + "</a>";
    email.innerText = result.email;
    // Remove blank entry before adding to inner text
    var index = result.skills.indexOf(' ');
    result.skills.splice(index, 1);
    skills.innerText = "Skills: " + result.skills.join(", ");
    availabilityLink.innerText = "Availability";
    loadStars(rating, Math.round(parseInt(result.ratingSum) / parseInt(result.ratingCount)));

    availabilityLink.href = "/availability.html?tutorID=" + result.userId;

    name.classList.add("tutor-name");
    rating.classList.add("tutor-rating");
    container.classList.add("tutor-result");
    container.classList.add("list-group-item");

    skills.style.textTransform = "capitalize";

    container.appendChild(name);
    container.appendChild(rating);
    container.appendChild(email);
    container.appendChild(skills);
    container.appendChild(availabilityLink);

    return container;
}

/** Loads filled and unfilled stars for tutor's rating. */
function loadStars(starsElement, rating) {
    //tutor does not have a rating yet, don't display anything
    if (rating == 0) {
        return;
    }
    
    var stars = [];
    for (var i = 0; i < rating; i++) {
        stars[i] = document.createElement('span');
        stars[i].className = 'glyphicon glyphicon-star';
        starsElement.appendChild(stars[i]);
    }

    for (var i = rating; i < 5; i++) {
        stars[i] = document.createElement('span');
        stars[i].className = 'glyphicon glyphicon-star-empty';
        starsElement.appendChild(stars[i]);
    }

    return starsElement;
} 

//Helper function for testing purposes
//Sets tutor's name in tutor anchor element
function setTutorNameInLink(tutorElement, tutorID) {
    var tutor;
    return getUser(tutorID).then(user => tutor = user).then(() => {
        tutorElement.innerText = tutor.name;
    });
}
