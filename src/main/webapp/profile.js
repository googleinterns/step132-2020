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

// Fetch info about the spceific user logged in 
function displayProfile() {
    var userID = getIdParameter(window);

    getUser(userID).then((user) => {
        console.log(user);
        document.getElementById('profile-container').appendChild(createProfileDiv(user));
    });
}

// Get the userID parameter from the URL, created as helper function for testing
function getIdParameter(window) {
    var url = new URL(window.location.href);
    return url.searchParams.get('userID');
}

/** Gets information about the given user from the server. */
function getUser(userID) {
    return fetch('/profile?userId='+userID).then(response => response.json()).then((user) => {
        return user;
    });
}
 
// Create elements and fill their inner HTML with user info
function createProfileDiv(user) {
    const profileDiv = document.createElement('div');
    
    const profileName = document.createElement('h3');
    profileName.innerHTML = user.name;

    const profileBio = document.createElement('p');
    profileBio.innerHTML = "About me: " + user.bio;

    const profilePfp = document.createElement('img');
    profilePfp.src = user.pfp;
    profilePfp.width = 100;
    profilePfp.height = 100;

    const profileEmail = document.createElement('p');
    profileEmail.innerHTML = user.email;

    const profileTopics = document.createElement('p');
    // Students and tutors have different properties, check to see what kind of profile we're displaying
    if (user.learning != undefined) {   // User is a student
        profileTopics.innerHTML = "I am learning: " + user.learning;
    }
    else {   // User is a tutor
        profileTopics.innerHTML = "I am tutoring in: " + user.skills;
    }

    profileDiv.appendChild(profilePfp);
    profileDiv.appendChild(profileName);
    profileDiv.appendChild(profileBio);
    profileDiv.appendChild(profileEmail);
    profileDiv.appendChild(profileTopics);

    return profileDiv;
}

