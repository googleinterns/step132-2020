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
    params.append('anonymous', document.getElementById('anonymous').value);

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



