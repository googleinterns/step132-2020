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
    document.getElementById("new-post-form").addEventListener('submit', event => {
        event.preventDefault();
        addPost(window);
    });
}

function addPost(window) {
    const params = new URLSearchParams();

    var queryString = new Array();
    window.onload = readComponents(queryString, window);
    const groupId = queryString["groupId"];

    params.append('groupId',groupId);
    params.append('post-content', document.getElementById('post-content').value);
    params.append('anonymous', document.getElementById('anonymous').checked);

    fetch('/manage-posts', {method: 'POST', body: params}).then((response) => {
        //if there was an error posting to the group
        if(response.error) {
            window.location.href = "/group.html?groupId=" + groupId;
            alert("There was an error when posting to this group.");
            return;
        }
        window.location.href = "/group.html?groupId=" + groupId;
    });
}

/** Fetches the posts in the current group. */
function displayGroupPosts() {
    return displayGroupPostsHelper(document, window);
}

/** Helper function for displayGroupPosts, used for testing purposes. */
async function displayGroupPostsHelper(document, window) {
    var queryString = new Array();
    window.onload = readComponents(queryString, window);
    var groupId = queryString["groupId"];

    if(groupId != null) {
        var posts = getPosts(groupId);
        
        await posts;
    }
}

/** Fetches the posts connected with the given group id. */
async function getPosts(groupId) {
    await fetch("/manage-posts?groupId=" + groupId).then(response => response.json()).then((results) => {
        var postsContainer = document.getElementById("posts");

        var numSearchResults = document.createElement("h4");
        numSearchResults.className = "num-search-results";

        postsContainer.appendChild(numSearchResults);
        
        //if there was an error reported by the servlet, display the error message
        if(results.error) {
            numSearchResults.innerText = results.error;
            return;
        }

        //Only make "posts" plural if there are 0 or more than 1 posts
        numSearchResults.innerText = "Found " + results.length + (results.length > 1 || results.length === 0 ? " posts for this group" : " post for this group");

        results.forEach(function(result) {
            postsContainer.append(createPostResult(result));
        });
    });

}

/** Creates a div element containing information about a post result. */
function createPostResult(result) {
    var container = document.createElement("div");
    var name = document.createElement("h3");
    name.style.textTransform = 'capitalize';
    var content = document.createElement("h5");
    var replyForm = document.createElement("form");
    var replyInput = document.createElement("input");
    var replySubmit = document.createElement("button");

    if (result.userID != "anonymous") {
        getUser(result.userID).then((user) => {
            var userInfo;
            // If the user is both a student and a tutor access the appropriate location to find informaton
            if (user.student != null) {
                userInfo = user.student;
            } else {
                userInfo = user;
            }
            name.innerText = userInfo.name;
        });
    } else {
        name.innerText = result.userID;
    }

    replyForm.className = "form-inline my-2 my-lg-0";
    replyInput.className = "form-control mr-sm-2";
    replyInput.id = "reply-input";
    replyInput.type = "search";
    replyInput.placeholder = "Write your reply here";
    replyInput.required = true;
    replySubmit.className = "btn btn-outline-success my-2 my-sm-0";
    replySubmit.type = "submit";
    replySubmit.innerText = "Submit";
    replyForm.addEventListener('submit', event => {
        event.preventDefault();
        addReply(result, document.getElementById('reply-input').value);  
    });

    content.innerText = result.content;

    replyForm.appendChild(replyInput);
    replyForm.appendChild(replySubmit);


   // profileLink.href = "/profile.html?userID=" + result.userId;

    container.classList.add("user-result");
    container.classList.add("list-group-item");

    container.appendChild(name);
    container.appendChild(content);
    container.appendChild(replyForm);

    return container;
}

function addReply(result, content) {
    console.log(result);
    console.log(content);
}




