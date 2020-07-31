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

describe("User Profile", function() {
    
    describe("When profile page loads", function() {
        var mockWindow = {location: {href: "http://localhost:8080/profile.html?userID=123", search:"?userID=123"}};
        var actualUserID = getIdParameter(mockWindow);
        var mockUser = {name: "Sam Falberg", bio: "The best bio", pfp: "images/pfp.jpg", 
            email: "sfalberg@google.com", skills: ["Math", "Physics"]};
        var mockLoginStatusTutor = {role:"tutor", userId: "123"};
        var mockLoginStatusStudent = {role:"student", userId: "abc"};
        var mockLoginStatusBoth = {role:"both", userId: "abc"};
 
        beforeAll(function() {
            var mockEditButton = document.createElement('btn');
            mockEditButton.style.display = 'none';
            mockEditButton.id = "edit-profile-btn";
 
            var mockAvailability = document.createElement('div');
            mockAvailability.style.display = 'none';
            mockAvailability.id = "tutor-availability";

            var mockTutorLists = document.createElement("div");
            mockTutorLists.style.display = 'none';
            mockTutorLists.id = "list-container-profile";
 
            var mockAvailabilityButton = document.createElement('btn');
            mockAvailabilityButton.id = "tutor-availability-btn";
 
            document.body.appendChild(mockEditButton);
            document.body.appendChild(mockAvailability);
            document.body.appendChild(mockTutorLists);
            document.body.appendChild(mockAvailabilityButton);
            
            spyOn(window, 'addEventListeners');
            spyOn(window, 'getExperiences');
            spyOn(window, 'getGoals');
            spyOn(window, 'getPastSessionsAndTopics');
            spyOn(window, 'getListsProfile');
        })
 
        it("should correctly get the userID parameter from the URL", function() {
            expect(actualUserID).toEqual("123");
        });
 
        it("should create div for inside the container", function() {
            spyOn(window, 'fetchStatusHelper');
            var mockProfilePicture = document.createElement('img');
            var actualDiv = createProfileDiv(mockUser, mockProfilePicture, mockLoginStatusTutor);
            expect(actualDiv.tagName).toEqual("DIV");
        });
 
        it("should create and set img element for pfp", function() {
            spyOn(window, 'fetchStatusHelper');
            var mockProfilePicture = document.createElement('img');
            var actualDiv = createProfileDiv(mockUser, mockProfilePicture, mockLoginStatusTutor);
            expect(actualDiv.childNodes[0].tagName).toEqual("IMG");
        });
 
        it("should create and set h3 element for name", function() {
            spyOn(window, 'fetchStatusHelper');
            var mockProfilePicture = document.createElement('img');
            var actualDiv = createProfileDiv(mockUser, mockProfilePicture, mockLoginStatusTutor);
            expect(actualDiv.childNodes[1].tagName).toEqual("H3");
            expect(actualDiv.childNodes[1].innerHTML).toEqual("Sam Falberg");
        });
 
        it("should create and set p element for bio", function() {
            spyOn(window, 'fetchStatusHelper');
            var mockProfilePicture = document.createElement('img');
            var actualDiv = createProfileDiv(mockUser, mockProfilePicture, mockLoginStatusTutor);
            expect(actualDiv.childNodes[2].tagName).toEqual("P");
            expect(actualDiv.childNodes[2].innerHTML).toEqual("About me: The best bio");
        });
 
        it("should create and set p element for email", function() {
            spyOn(window, 'fetchStatusHelper');
            var mockProfilePicture = document.createElement('img');
            var actualDiv = createProfileDiv(mockUser, mockProfilePicture, mockLoginStatusTutor);
            expect(actualDiv.childNodes[3].tagName).toEqual("P");
            expect(actualDiv.childNodes[3].innerHTML).toEqual("sfalberg@google.com");
        });
 
        it("should create p", function() {
            spyOn(window, 'fetchStatusHelper');
            var mockProfilePicture = document.createElement('img');
            var actualDiv = createProfileDiv(mockUser, mockProfilePicture, mockLoginStatusTutor);
            expect(actualDiv.childNodes[4].tagName).toEqual("P");
        });
 
        it("should set p to the correct topics", function() {
            var actualText = fetchStatusHelper(mockUser, mockLoginStatusTutor, mockWindow, document);
            expect(actualText).toEqual("I am tutoring in: Math, Physics");
        });
 
        it("should hide the tutor availability button if current user is a tutor", function() {
            fetchStatusHelper(mockUser, mockLoginStatusTutor, mockWindow, document);
            expect(document.getElementById('tutor-availability').style.display).toBe('none');
        });
 
        it("should set make the tutor availability button visible if current user is a student", function() {
            fetchStatusHelper(mockUser, mockLoginStatusStudent, mockWindow, document);
            expect(document.getElementById('tutor-availability').style.display).toBe('block');
        });

        it("should set make the tutor availability button visible if current user is a both", function() {
            fetchStatusHelper(mockUser, mockLoginStatusBoth, mockWindow, document);
            expect(document.getElementById('tutor-availability').style.display).toBe('block');
        });

        it("adds event listener that redirects the user to the tutor's availability", function() {
            var mockWindow = {location: {href: "profile.html"}};
            var mockUser = {userId: "123"};
            redirectToAvailability(mockUser, mockWindow);

            expect(mockWindow.location.href).toEqual("availability.html?tutorID=123");
        });
    });

    describe("When the user edits their profile", function() {
        var mockUser = {name: "Sam Falberg", bio: "The best bio", pfp: "images/pfp.jpg", 
            email: "sfalberg@google.com", skills: ["math", "english", " ", "Other"]};
        
        beforeAll(function() {
            var mockProfileContainer = document.createElement('div');
            mockProfileContainer.id = 'profile-container';
            mockProfileContainer.style.display = 'block';

            var mockButtonsDiv = document.createElement('div');
            mockButtonsDiv.id = 'top-right-buttons';
            mockButtonsDiv.style.display = 'block';

            var mockEditForm = document.createElement('form');
            mockEditForm.id = 'edit-profile-form';
            mockEditForm.style.display = 'none';

            var mockBio = document.createElement('textarea');
            mockBio.id = 'bio';

            var mockStudentTopics = document.createElement('p');
            mockStudentTopics.id = 'student-topics';
            mockStudentTopics.style.display = 'none';

            var mockTutorTopics = document.createElement('p');
            mockTutorTopics.id = 'tutor-topics';
            mockTutorTopics.style.display = 'none';

            var mockMathCheckbox = document.createElement('checkbox');
            mockMathCheckbox.id = 'math-tutor';

            mockMathCheckbox.checked = true;

            var mockEnglishCheckbox = document.createElement('checkbox');
            mockEnglishCheckbox.id = 'english-tutor';

            mockEnglishCheckbox.checked = true;

            var mockPhysicsCheckbox = document.createElement('checkbox');
            mockPhysicsCheckbox.id = 'physics-tutor';
            
            var mockOtherTextbox = document.createElement('text');
            mockOtherTextbox.id = 'other-subject-tutor';

            document.body.appendChild(mockProfileContainer);
            document.body.appendChild(mockButtonsDiv);
            document.body.appendChild(mockEditForm);
            document.body.appendChild(mockBio);
            document.body.appendChild(mockStudentTopics);
            document.body.appendChild(mockTutorTopics);
            document.body.appendChild(mockMathCheckbox);
            document.body.appendChild(mockEnglishCheckbox);
            document.body.appendChild(mockPhysicsCheckbox);
            document.body.appendChild(mockOtherTextbox);

            editProfile(mockUser, 'tutor', document);
        })
        
        it("should hide profile info and show the edit form", function() {
            expect(document.getElementById('profile-container').style.display).toBe('none');
            expect(document.getElementById('edit-profile-form').style.display).toBe('block');
        });

        it("should have the default text for the bio be what the user had previously", function() {
            expect(document.getElementById('bio').value).toBe('The best bio');
        });

        it("should check off the topics the user had previously selected", function() {
            expect(document.getElementById('math-tutor').checked).toBe(true);
            expect(document.getElementById('english-tutor').checked).toBe(true);
            expect(document.getElementById('physics-tutor').checked).toBe(undefined);
        });
    });
});
