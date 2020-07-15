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
    const userID = getIdParameter(window);

    fetch('/profile?user-id='+userID).then(response => response.json()).then((user) => {
        console.log(user);
        document.getElementById('profile-container').appendChild(createProfileDiv(user));
    });
}

// Get the userID parameter from the URL, created as helper function for testing
function getIdParameter(window) {
    var url = new URL(window.location.href);
    return url.searchParams.get('userID');
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
    var isStudent;
    // Students and tutors have different properties, check to see what kind of profile we're displaying
    if (user.learning != undefined) {   // User is a student
        profileTopics.innerHTML = "I am learning: " + user.learning;
        isStudent = true;
    }
    else {   // User is a tutor
        profileTopics.innerHTML = "I am tutoring in: " + user.skills;
        isStudent = false;
    }

    // Check if profile belongs to user currently logged in
    // If not, don't allow them to edit the profile
    fetch('/login-status').then(response => response.json()).then((loginStatus) => {
        if (loginStatus.userId == getIdParameter(window)) {
            document.getElementById('edit-profile-btn').style.display = 'block';
            document.getElementById('edit-profile-btn').addEventListener('click', () => {
                editProfile(user, isStudent, document); 
            });
        }
    });

    profileDiv.appendChild(profilePfp);
    profileDiv.appendChild(profileName);
    profileDiv.appendChild(profileBio);
    profileDiv.appendChild(profileEmail);
    profileDiv.appendChild(profileTopics);

    return profileDiv;
}

// Displays a form that will allow the user to edit the info on their profile
function editProfile(user, isStudent, document) {
    document.getElementById('profile-container').style.display = 'none';
    document.getElementById('top-right-buttons').style.display = 'none';
    document.getElementById('edit-profile-form').style.display = 'block';

    // Set default value of bio to what user previously had
    // This will prevent this data from being lost if the user decides not change this field
    document.getElementById('bio').value = user.bio;

    if (isStudent) {
        document.getElementById('student-topics').style.display = 'block';
        // Automatically check boxes of topics user has already selected
        if (user.learning.indexOf('math') > -1) {
            document.getElementById('math').checked = true;
        }
        if (user.learning.indexOf('english') > -1) {
            document.getElementById('english').checked = true;
        }
        if (user.learning.indexOf('other') > -1) {
            document.getElementById('other-subject').checked = true;
        }
    } else {
        document.getElementById('tutor-topics').style.display = 'block';
        // Atuomatically check boxes of topics user has already selected
        if (user.skills.indexOf('math') > -1) {
            document.getElementById('math').checked = true;
        }
        if (user.skills.indexOf('english') > -1) {
            document.getElementById('english').checked = true;
        }
        if (user.skills.indexOf('other') > -1) {
            document.getElementById('other-subject').checked = true;
        }
    }
}
