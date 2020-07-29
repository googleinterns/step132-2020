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
    return getSearchResultsHelper(window, elem.value);
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
            document.getElementById("lists-container").innerText = results.error;
            return;
        }

        if(Object.keys(results).length === 0) {
            document.getElementById("lists-container").innerText = "No lists found for " + topic + ".";
            //open google books panel
            document.getElementById("collapsible-2").classList.add("in");
            document.getElementById("collapsible-2").setAttribute("aria-expanded", true);
            document.getElementById("google-books-panel").setAttribute("aria-expanded", true);
            return;
        }

        //open lists panel
        document.getElementById("collapsible-1").classList.add("in");
        document.getElementById("collapsible-1").setAttribute("aria-expanded", true);
        document.getElementById("tutor-books-panel").setAttribute("aria-expanded", true);

        var listContainer = document.getElementById("lists-container");

        results.forEach((result) => {
            listContainer.appendChild(createTutorListElement(result));
        });

    });
}

/** Creates a tutor list result element and sets the modal content to the list when the user clicks on "view list" button. */
function createTutorListElement(result) {
    var container = document.createElement("div");
    var listName = document.createElement("h4");
    var tutorName = document.createElement("a"); 
    var books = document.createElement("div");

    listName.innerText = result.name;
    setTutorNameInLink(tutorName, result.tutorID);

    //link to tutor profile
    tutorName.href = "/profile.html?userID=" + result.tutorID;


    result.books.forEach((book) => {
        var titleAndAuthor = book.toLowerCase().split(" by ");
        var title = titleAndAuthor[0].trim();
        //if user left out author, use empty string
        var author = titleAndAuthor[1] === undefined ? "" : titleAndAuthor[1].trim();

        fetch("https://www.googleapis.com/books/v1/volumes?q=intitle:" + title + "+inauthor:" + author + "&maxResults=1&key=AIzaSyB1IWrd3mYWJsTWOqK7IYDrw9q_MOk1K9Y")
            .then(response => response.json()).then((results) => {
                if(results.totalItems === 0) {
                    books.appendChild(createNoGoogleBookResult(book, "list-book"));
                    return;
                }

                books.appendChild(createBookResult(results.items[0].volumeInfo, "list-book"));
            });
    });

    listName.className = "list-name-result";
    tutorName.className = "tutor-name-result";
    books.classList.add("book-list-container");
    books.classList.add("list-inline");
    container.classList.add("tutor-list-result");

    tutorName.setAttribute("target", "_blank");


    container.appendChild(listName);
    container.appendChild(tutorName);
    container.appendChild(books);

    return container;
}

//keeps track of the number of books loaded so far from the Google Books API
var numResultsLoaded = 0;
/** Fetches the list of books for the topic the user searched for and displays them on the page. */
async function getBooks(topic) {
    await fetch("https://www.googleapis.com/books/v1/volumes?q=" + topic + "&maxResults=40&key=AIzaSyB1IWrd3mYWJsTWOqK7IYDrw9q_MOk1K9Y")
        .then(response => response.json()).then((results) => {
            var numSearchResults = document.getElementById("num-book-results");
            //if there was an error reported by the api, display the error message
            if(results.error) {
                numSearchResults.innerText = results.error.message;
                return;
            }

            //Only make "books" plural if there are 0 or more than 1 books
            numSearchResults.innerText = "Found " + results.totalItems + (results.totalItems > 1 || results.totalItems === 0 ? " books for " : " book for ") + topic;

            //create container to put books
            var booksContainer = document.getElementById("books-container");

            numResultsLoaded = results.items.length;

            results.items.forEach(function(result) {
                booksContainer.append(createBookResult(result.volumeInfo, "book-result col-md-4"));
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

/** Gets the next 40 books from the Google Books API for the specified topic. */
function loadMoreBooks(topic) {
    fetch("https://www.googleapis.com/books/v1/volumes?q=" + topic + "&maxResults=40&startIndex=" + numResultsLoaded + "&key=AIzaSyB1IWrd3mYWJsTWOqK7IYDrw9q_MOk1K9Y")
        .then(response => response.json()).then((results) => {

            //if there was an error reported by the api, display the error message
            if(results.error) {
                numSearchResults.innerText = results.error.message;
                return;
            }

            numResultsLoaded += results.items.length;

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
            document.getElementById("tutor-filter").style.display = "none";
        }

        var tutorContainer = document.getElementById("tutors-container");

        tutorContainer.innerHTML = "";

        results.forEach(function(result) {
            tutorContainer.append(createTutorResult(result));
        });
    });

}

/** Creates a book result with an empty book cover and the name of the book. */
function createNoGoogleBookResult(result, className) {
    var container = document.createElement("div");
    var thumbnail = document.createElement("img");
    var book = document.createElement("p");

    thumbnail.src = "/images/book-cover.png";

    book.innerText = result;

    container.classList.add(className);
    container.classList.add("col-md-4");

    container.appendChild(thumbnail);
    container.appendChild(book);

    return container;
}

/** Creates a anchor element containing information about a book result. */
function createBookResult(result, className) {
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

    container.className = className;

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
