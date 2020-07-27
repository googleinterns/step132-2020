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

    getUser(userID).then((user) => {
        console.log(user);
        document.getElementById('profile-container').style.display = 'block';

        // Check if profile belongs to user currently logged in; if not, don't allow them to edit the profile
        // Also fetches the role of the user
        fetch('/login-status').then(response => response.json()).then((loginStatus) => {
            if (loginStatus.userId == userID) {
                document.getElementById('edit-profile-btn').style.display = 'block';
                document.getElementById('edit-profile-btn').addEventListener('click', () => {
                    editProfile(user, loginStatus.role, document); 
                });
            }

            document.getElementById('profile-container').appendChild(createProfileDiv(user, loginStatus));
        });
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
        if(user.error) {
            var message = document.createElement("p");
            p.innerText = user.error;
            document.body.appendChild(message);
            return;
        }
        return user;
    });
}
 
// Create elements and fill their inner HTML with user info
function createProfileDiv(user, loginStatus) {
    const profileDiv = document.createElement('div');
    const profileName = document.createElement('h3');
    const profileBio = document.createElement('p');
    const profilePfp = document.createElement('img');
    profilePfp.width = 100;
    profilePfp.height = 100;
    const profileEmail = document.createElement('p');
    const profileTopics = document.createElement('p');

    // If the user is both a tutor and a studen
    if (user.student != null) {
        profileName.innerText = user.student.name;
        profileBio.innerText = "About me: " + user.student.bio;
        profilePfp.src = user.student.pfp;
        profileEmail.innerText = user.student.email;
    } else {
        profileName.innerText = user.name;
        profileBio.innerText = "About me: " + user.bio;
        profilePfp.src = user.pfp;
        profileEmail.innerText = user.email;
    }

    // Check if profile belongs to user currently logged in; if not, don't allow them to edit the profile
    profileTopics.innerText = fetchStatusHelper(user, loginStatus, window, document);
    profileTopics.style.textTransform = 'capitalize';

    profileDiv.appendChild(profilePfp);
    profileDiv.appendChild(profileName);
    profileDiv.appendChild(profileBio);
    profileDiv.appendChild(profileEmail);
    profileDiv.appendChild(profileTopics);

    return profileDiv;
}

// Helper function for testing
function fetchStatusHelper(user, loginStatus, window, document) {
    var role = loginStatus.role;
    var text;

    if (user.student != null) {
        var learning = user.student.learning.toString();
        // Remove blank entry that marks start of other topics
        var removeBlankStudent = learning.replace(', ', '');
        var listWithSpacesStudent = removeBlankStudent.replace(/,/g, ', ');

        var skills = user.tutor.skills.toString();
        // Remove blank entry that marks start of other topics
        var removeBlankTutor = skills.replace(', ', '');
        var listWithSpacesTutor = removeBlankTutor.replace(/,/g, ', ')

        text = "I am learning: " + listWithSpacesStudent + "\n" +
            "I am tutoring in: " + listWithSpacesTutor;

        loadProgress(document, loginStatus, user);
    } else if (user.learning != null) { // Display topics as comma-separated list with spaces
        var learning = user.learning.toString();
        // Remove blank entry that marks start of other topics
        var removeBlank = learning.replace(', ', '');
        var listWithSpaces = removeBlank.replace(/,/g, ', ');
        text = "I am learning: " + listWithSpaces;

        loadProgress(document, loginStatus, user);
    } else if (user.skills != null){ 
        var skills = user.skills.toString();
        // Remove blank entry that marks start of other topics
        var removeBlank = skills.replace(', ', '');
        var listWithSpaces = removeBlank.replace(/,/g, ', ');
        text = "I am tutoring in: " + listWithSpaces;
    }

    if (loginStatus.userId == getIdParameter(window)) {
        document.getElementById('edit-profile-btn').style.display = 'block';
        document.getElementById('edit-profile-btn').addEventListener('click', () => {
            editProfile(user, role, document); 
        });
    }

    return text;
}

// Displays a form that will allow the user to edit the info on their profile
function editProfile(user, role, document) {
    document.getElementById('profile-container').style.display = 'none';
    document.getElementById('edit-profile-form').style.display = 'block';

    if (role == "both") {
        // Set default value of bio to what user previously had
        // This will prevent this data from being lost if the user decides not change this field
        document.getElementById('bio').value = user.student.bio;

        editProfileTutor(user, true, document);
        editProfileStudent(user, true, document);
    } else if (role == 'student') {
        document.getElementById('bio').value = user.bio;

        editProfileStudent(user, false, document);
    } else if (role == tutor) {
        document.getElementById('bio').value = user.bio;

        editProfileTutor(user, false, document);
    }
}

function editProfileTutor(user, both, document) {
    if (both == true) {
        document.getElementById('tutor-topics').style.display = 'block';
        // Automatically check boxes of topics user has already selected
        if (user.tutor.skills.indexOf('math') > -1) {
            document.getElementById('math-tutor').checked = true;
        }
        if (user.tutor.skills.indexOf('physics') > -1) {
            document.getElementById('physics-tutor').checked = true;
        }
        if (user.tutor.skills.indexOf('chemistry') > -1) {
            document.getElementById('chemistry-tutor').checked = true;
        }
        if (user.tutor.skills.indexOf('biology') > -1) {
            document.getElementById('biology-tutor').checked = true;
        }
        if (user.tutor.skills.indexOf('computer science') > -1) {
            document.getElementById('computer-science-tutor').checked = true;
        }
        if (user.tutor.skills.indexOf('social studies') > -1) {
            document.getElementById('social-studies-tutor').checked = true;
        }
        if (user.tutor.skills.indexOf('english') > -1) {
            document.getElementById('english-tutor').checked = true;
        }
        if (user.tutor.skills.indexOf('spanish') > -1) {
            document.getElementById('spanish-tutor').checked = true;
        }
        if (user.tutor.skills.indexOf('french') > -1) {
            document.getElementById('french-tutor').checked = true;
        }
        if (user.tutor.skills.indexOf('chinese') > -1) {
            document.getElementById('chinese-tutor').checked = true;
        }
        var otherTopicsIndex = user.tutor.skills.indexOf(' ');
        var otherTopics = [];
        // Check if any other entries exist
        if (otherTopicsIndex > -1 && user.tutor.skills.length > otherTopicsIndex + 1) {
            otherTopics = user.tutor.skills.slice(otherTopicsIndex + 1);
        }
        document.getElementById('other-subject-tutor').value = otherTopics.join(', ');
    } else {
        document.getElementById('tutor-topics').style.display = 'block';
        // Automatically check boxes of topics user has already selected
        if (user.skills.indexOf('math') > -1) {
            document.getElementById('math-tutor').checked = true;
        }
        if (user.skills.indexOf('physics') > -1) {
            document.getElementById('physics-tutor').checked = true;
        }
        if (user.skills.indexOf('chemistry') > -1) {
            document.getElementById('chemistry-tutor').checked = true;
        }
        if (user.skills.indexOf('biology') > -1) {
            document.getElementById('biology-tutor').checked = true;
        }
        if (user.skills.indexOf('computer science') > -1) {
            document.getElementById('computer-science-tutor').checked = true;
        }
        if (user.skills.indexOf('social studies') > -1) {
            document.getElementById('social-studies-tutor').checked = true;
        }
        if (user.skills.indexOf('english') > -1) {
            document.getElementById('english-tutor').checked = true;
        }
        if (user.skills.indexOf('spanish') > -1) {
            document.getElementById('spanish-tutor').checked = true;
        }
        if (user.skills.indexOf('french') > -1) {
            document.getElementById('french-tutor').checked = true;
        }
        if (user.skills.indexOf('chinese') > -1) {
            document.getElementById('chinese-tutor').checked = true;
        }
        var otherTopicsIndex = user.skills.indexOf(' ');
        var otherTopics;
        // Check if any other entries exist
        if (otherTopicsIndex > -1 && user.skills.length > otherTopicsIndex + 1) {
            otherTopics = user.skills.slice(otherTopicsIndex + 1);
        }
        document.getElementById('other-subject-tutor').value = otherTopics.join(', ');
    }
}

function editProfileStudent(user, both, document) {
    if (both == true) {
        document.getElementById('student-topics').style.display = 'block';
        // Automatically check boxes of topics user has already selected
        if (user.student.learning.indexOf('math') > -1) {
            document.getElementById('math-learn').checked = true;
        }
        if (user.student.learning.indexOf('physics') > -1) {
            document.getElementById('physics-learn').checked = true;
        }
        if (user.student.learning.indexOf('chemistry') > -1) {
            document.getElementById('chemistry-learn').checked = true;
        }
        if (user.student.learning.indexOf('biology') > -1) {
            document.getElementById('biology-learn').checked = true;
        }
        if (user.student.learning.indexOf('computer science') > -1) {
            document.getElementById('computer-science-learn').checked = true;
        }
        if (user.student.learning.indexOf('social studies') > -1) {
            document.getElementById('social-studies-learn').checked = true;
        }
        if (user.student.learning.indexOf('english') > -1) {
            document.getElementById('english-learn').checked = true;
        }
        if (user.student.learning.indexOf('spanish') > -1) {
            document.getElementById('spanish-learn').checked = true;
        }
        if (user.student.learning.indexOf('french') > -1) {
            document.getElementById('french-learn').checked = true;
        }
        if (user.student.learning.indexOf('chinese') > -1) {
            document.getElementById('chinese-learn').checked = true;
        }
        var otherTopicsIndex = user.student.learning.indexOf(' ');
        var otherTopics = [];
        // Check if any other entries exist
        if (otherTopicsIndex > -1 && user.student.learning.length > otherTopicsIndex + 1) {
            otherTopics = user.student.learning.slice(otherTopicsIndex + 1);
        }
        document.getElementById('other-subject-learn').value = otherTopics.join(', ');
    } else {
        document.getElementById('student-topics').style.display = 'block';
        // Automatically check boxes of topics user has already selected
        if (user.learning.indexOf('math') > -1) {
            document.getElementById('math-learn').checked = true;
        }
        if (user.learning.indexOf('physics') > -1) {
            document.getElementById('physics-learn').checked = true;
        }
        if (user.learning.indexOf('chemistry') > -1) {
            document.getElementById('chemistry-learn').checked = true;
        }
        if (user.learning.indexOf('biology') > -1) {
            document.getElementById('biology-learn').checked = true;
        }
        if (user.learning.indexOf('computer science') > -1) {
            document.getElementById('computer-science-learn').checked = true;
        }
        if (user.learning.indexOf('social studies') > -1) {
            document.getElementById('social-studies-learn').checked = true;
        }
        if (user.learning.indexOf('english') > -1) {
            document.getElementById('english-learn').checked = true;
        }
        if (user.learning.indexOf('spanish') > -1) {
            document.getElementById('spanish-learn').checked = true;
        }
        if (user.learning.indexOf('french') > -1) {
            document.getElementById('french-learn').checked = true;
        }
        if (user.learning.indexOf('chinese') > -1) {
            document.getElementById('chinese-learn').checked = true;
        }
        var otherTopicsIndex = user.learning.indexOf(' ');
        var otherTopics;
        // Check if any other entries exist
        if (otherTopicsIndex > -1 && user.learning.length > otherTopicsIndex + 1) {
            otherTopics = user.learning.slice(otherTopicsIndex + 1);
        }
        document.getElementById('other-subject-learn').value = otherTopics.join(', ');
    }
}
