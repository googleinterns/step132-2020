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

/** Gets all of the tutor's lists from the server, used for the manage-lists page. */
function getListsManage() {
    return getListsHelper(window, '/manage-lists');
}

/** Gets all of the tutor's lists from the server to display on the user's profile */
function getListsProfile() {
    var queryString = new Array();
    readComponents(queryString, window);
    document.getElementById("list-container-profile").style.display = "block";
    return getListsHelper(window, '/lists?tutorID=' + queryString["userID"]);
}

/** Helper function for getLists, used for testing. */
async function getListsHelper(window, url) {
    await fetch(url, {method: 'GET'}).then((response) => {
        //if the tutor is not the current user or not signed in, used for the my-lists page
        if(response.redirected) {
            window.location.href = response.url;
            alert("You must be signed in to manage lists.");
            return null;
        }
        return response.json();
        
    }).then((lists) => {
        //page was redirected
        if(lists === null) {
            return;
        }

        if(lists.error) {
            var message = document.createElement("p");
            message.innerText = timeslots.error;
            document.getElementById('list-names').appendChild(message);
            return;
        }

        if(Object.keys(lists).length === 0) {
            var message = document.createElement("p");
            message.innerText = "This user has no lists.";
            document.getElementById('list-names').appendChild(message);
            return;
        }

        var listNames = document.getElementById('list-names');
        var listInfo = document.getElementById('list-info-container')

        lists.forEach((list) => {
            listNames.appendChild(createListNameLink(list));
            listInfo.appendChild(createListElement(list));
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

/** Creates a list element containing the list of books, the name of the list and the topic of the list. */
function createListElement(list) {
    var container = document.createElement("div");
    var listName = document.createElement("h3");
    var topic = document.createElement("p");
    var books = document.createElement("ul");

    listName.innerText = list.name;
    topic.innerText = capitalizeFirstLetter(list.topic.toLowerCase());

    list.books.forEach((book) => {
        var elem = document.createElement("li");
        elem.className = "book";
        elem.innerText = book;
        books.appendChild(elem);
    });

    container.id = list.name;

    listName.className = "list-name";
    topic.className = "list-topic";
    books.className = "book-list";
    container.classList.add("col-md-8");
    container.classList.add("list-info");

    container.appendChild(listName);
    container.appendChild(topic);
    container.appendChild(books);

    container.style.display = "none";

    return container;
}

/** Adds an anchor element to the table created on the left side of the page containing links to the corresponding list element container. */
function createListNameLink(list) {
    var listNameLink = document.createElement("a");

    listNameLink.href = "#";

    listNameLink.innerText = list.name;

    listNameLink.classList.add("list-group-item");
    listNameLink.classList.add("list-group-item-action");
    listNameLink.classList.add("list-name-link");

    listNameLink.addEventListener("click", function() {
        switchList(listNameLink);
    });

    return listNameLink;
}

/** Function to switch between lists and display the active list.*/
function switchList(elem) {
    //get the name of the project that was clicked
    var listName = elem.innerText;
    //get the current list that we see
    var currentActiveList = document.getElementsByClassName("active-list")[0];
    var currentActiveListLink = document.getElementsByClassName("active")[0];

    if(currentActiveList !== undefined && currentActiveListLink !== undefined) {
        //remove the active/active-list classes from the list that we currently see
        //the active/active-list classes determine whether or not we can see the element
        currentActiveList.classList.remove("active-list");
        currentActiveListLink.classList.remove("active");
    }
    //get the list we clicked on
    var nextActiveList = document.getElementById(listName);
    //add the active/active-list classes to the project we clicked so we can see the description/image
    elem.classList.add("active");
    nextActiveList.classList.add("active-list");
}

//Reference: https://stackoverflow.com/questions/1026069/how-do-i-make-the-first-letter-of-a-string-uppercase-in-javascript
function capitalizeFirstLetter(string) {
  return string.charAt(0).toUpperCase() + string.slice(1);
}
