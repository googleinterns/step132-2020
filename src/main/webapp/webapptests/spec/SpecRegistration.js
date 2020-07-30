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
describe("Registration", function() {
    describe("When displaying registration form", function() {
        beforeAll(function() {  
            var mockGenInfoDiv = document.createElement('div');
            mockGenInfoDiv.id = "general-info";
            mockGenInfoDiv.style.display = 'none';

            var mockStudentTopics = document.createElement('p');
            mockStudentTopics.id = 'student-topics';
            mockStudentTopics.style.display = 'none';

            var mockTutorTopics = document.createElement('p');
            mockTutorTopics.id = 'tutor-topics';
            mockTutorTopics.style.display = 'none';

            var mockStudentRadioBtn = document.createElement('input');
            mockStudentRadioBtn.setAttribute('type', 'radio');
            mockStudentRadioBtn.id = 'student';
            mockStudentRadioBtn.name = 'role';

            var mockTutorRadioBtn = document.createElement('input');
            mockTutorRadioBtn.setAttribute('type', 'radio');
            mockTutorRadioBtn.id = 'tutor';
            mockTutorRadioBtn.name = 'role';

            var mockBothRadioBtn = document.createElement('input');
            mockBothRadioBtn.setAttribute('type', 'radio');
            mockBothRadioBtn.id = 'both';
            mockBothRadioBtn.name = 'role';

            document.body.appendChild(mockStudentRadioBtn);
            document.body.appendChild(mockTutorRadioBtn);
            document.body.appendChild(mockBothRadioBtn);
            document.body.appendChild(mockGenInfoDiv);
            mockGenInfoDiv.appendChild(mockStudentTopics);
            mockGenInfoDiv.appendChild(mockTutorTopics);
        })

        it("should ask for general and student info if user is student", function() {
            document.getElementById('student').checked = true;
            displayRegistrationInfoHelper(document);

            expect(document.getElementById('general-info').style.display).toBe('block');
            expect(document.getElementById('student-topics').style.display).toBe('block');
            expect(document.getElementById('tutor-topics').style.display).toBe('none');
        });

        it("should ask for general and tutor info if user is tutor", function() {
            document.getElementById('tutor').checked = true;
            displayRegistrationInfoHelper(document);

            expect(document.getElementById('general-info').style.display).toBe('block');
            expect(document.getElementById('tutor-topics').style.display).toBe('block');
            expect(document.getElementById('student-topics').style.display).toBe('none');
        });

        it("should ask for general and tutor and student info if user is both", function() {
            document.getElementById('both').checked = true;
            displayRegistrationInfoHelper(document);

            expect(document.getElementById('general-info').style.display).toBe('block');
            expect(document.getElementById('tutor-topics').style.display).toBe('block');
            expect(document.getElementById('student-topics').style.display).toBe('block');
        });

        it("should ask for general and student info if user clicks tutor then switches to student", function() {
            document.getElementById('tutor').checked = true;
            document.getElementById('student').checked = true;
            displayRegistrationInfoHelper(document);

            expect(document.getElementById('general-info').style.display).toBe('block');
            expect(document.getElementById('student-topics').style.display).toBe('block');
            expect(document.getElementById('tutor-topics').style.display).toBe('none');
        });

        it("should ask for general and tutor info if user clicks student then switches to tutor", function() {
            document.getElementById('student').checked = true;
            document.getElementById('tutor').checked = true;
            displayRegistrationInfoHelper(document);

            expect(document.getElementById('general-info').style.display).toBe('block');
            expect(document.getElementById('tutor-topics').style.display).toBe('block');
            expect(document.getElementById('student-topics').style.display).toBe('none');
        });
    });

    describe("Fetching login status", function() {
        
        describe("for the registration page", function() {    
            var mockLoginStatus;

            beforeAll(function() {
                var mockLoginForm = document.createElement('p');
                mockLoginForm.id = 'return';

                var mockRegistrationDiv = document.createElement('div');
                mockRegistrationDiv.id = 'registration-form';

                document.body.appendChild(mockLoginForm);
                document.body.appendChild(mockRegistrationDiv);
            })

            it("displays only sign in link if user not logged in", function() {
                mockLoginStatus = {isLoggedIn:false, needsToRegister:false, url:''};
                fetchLoginStatusHelper(document, mockLoginStatus);

                expect(document.getElementById('return').style.display).toBe('block');
                expect(document.getElementById('registration-form').style.display).toBe('none');
            });

            it("displays only registration form if logged in and needs to register", function() {
                mockLoginStatus = {isLoggedIn:true, needsToRegister:true, url:''};
                fetchLoginStatusHelper(document, mockLoginStatus);

                expect(document.getElementById('return').style.display).toBe('none');
                expect(document.getElementById('registration-form').style.display).toBe('block');
            });

        });

        describe("for any page excluding registration", function() {
            var mockLoginStatus;

            beforeAll(function() {
                var mockLoginForm = document.createElement('p');
                mockLoginForm.id = 'login';

                var mockLoginUrl = document.createElement('a');
                mockLoginUrl.id = 'login-url';

                var mockLogoutForm = document.createElement('p');
                mockLogoutForm.id = 'logout';

                var mockLogoutUrl = document.createElement('a');
                mockLogoutUrl.id = 'logout-url';

                var mockProfileLink = document.createElement('button');
                mockProfileLink.id = 'profile';

                var mockGroupsLink = document.createElement('button');
                mockGroupsLink.id = 'groups';

                var mockAvailabilitySettingsLink = document.createElement('button');
                mockAvailabilitySettingsLink.id = 'availability-settings';

                var mockMyStudentsLink = document.createElement('button');
                mockMyStudentsLink.id = 'my-students';

                var mockMyListsLink = document.createElement('button');
                mockMyListsLink.id = 'my-lists';
              
                var mockTutorSessionSettingsLink = document.createElement('button');
                mockTutorSessionSettingsLink.id = 'tutor-session-settings';

                var mockHistoryLink = document.createElement('button');
                mockHistoryLink.id = 'history';

                var mockDropdownLink = document.createElement('button');
                mockDropdownLink.id = 'account-dropdown';

                var mockStudentLabel = document.createElement('li');
                mockStudentLabel.id = 'label-student';
                mockStudentLabel.style.display = "none";

                var mockTutorLabel = document.createElement('li');
                mockTutorLabel.id = 'label-tutor';
                mockTutorLabel.style.display = "none";

                var mockRoleSwitch = document.createElement('li');
                mockRoleSwitch.id = 'role-view-switch';
                mockRoleSwitch.style.display = "none";

                var mockViewCheck = document.createElement('input');
                mockViewCheck.id = 'view-checkbox';

                document.body.appendChild(mockLoginForm);
                document.body.appendChild(mockLoginUrl);
                document.body.appendChild(mockLogoutForm);
                document.body.appendChild(mockLogoutUrl);
                document.body.appendChild(mockProfileLink);
                document.body.appendChild(mockGroupsLink);
                document.body.appendChild(mockAvailabilitySettingsLink);
                document.body.appendChild(mockMyStudentsLink);
                document.body.appendChild(mockMyListsLink);
                document.body.appendChild(mockTutorSessionSettingsLink);
                document.body.appendChild(mockHistoryLink);
                document.body.appendChild(mockDropdownLink);
                document.body.appendChild(mockStudentLabel);
                document.body.appendChild(mockTutorLabel);
                document.body.appendChild(mockRoleSwitch);
                document.body.appendChild(mockViewCheck);
            })

            it("displays login link when user logged out", function() {
                mockLoginStatus = {isLoggedIn:false, needsToRegister:false, url:''};
                displayLoginLogoutLinkHelper(document, mockLoginStatus);

                expect(document.getElementById('login').style.display).toBe('block');
                expect(document.getElementById('logout').style.display).toBe('none');
                expect(document.getElementById('account-dropdown').style.display).toBe('none');
            });

            it("displays logout link when user logged in and the user is a student", function() {
                mockLoginStatus = {isLoggedIn:true, needsToRegister:false, url:'', role: "student"};
                displayLoginLogoutLinkHelper(document, mockLoginStatus);

                expect(document.getElementById('login').style.display).toBe('none');
                expect(document.getElementById('logout').style.display).toBe('block');
                expect(document.getElementById('profile').style.display).toBe('block');
                expect(document.getElementById('groups').style.display).toBe('block');
                expect(document.getElementById('availability-settings').style.display).toBe('none');
                expect(document.getElementById('my-students').style.display).toBe('none');
                expect(document.getElementById('my-lists').style.display).toBe('none');
                expect(document.getElementById('tutor-session-settings').style.display).toBe('block');
                expect(document.getElementById('history').style.display).toBe('block');
                expect(document.getElementById('account-dropdown').style.display).toBe('block');
            });

            it("displays logout link when user logged in and the user is a tutor", function() {
                mockLoginStatus = {isLoggedIn:true, needsToRegister:false, url:'', role: "tutor"};
                displayLoginLogoutLinkHelper(document, mockLoginStatus);

                expect(document.getElementById('login').style.display).toBe('none');
                expect(document.getElementById('logout').style.display).toBe('block');
                expect(document.getElementById('profile').style.display).toBe('block');
                expect(document.getElementById('groups').style.display).toBe('block');
                expect(document.getElementById('availability-settings').style.display).toBe('block');
                expect(document.getElementById('my-students').style.display).toBe('block');
                expect(document.getElementById('my-lists').style.display).toBe('block');
                expect(document.getElementById('tutor-session-settings').style.display).toBe('none');
                expect(document.getElementById('history').style.display).toBe('none');
                expect(document.getElementById('account-dropdown').style.display).toBe('block')
            });

            it("displays logout link when user logged in and the user is both student and tutor but is in student view", function() {
                mockLoginStatus = {isLoggedIn:true, needsToRegister:false, url:'', role: "both", view: "student"};
                displayLoginLogoutLinkHelper(document, mockLoginStatus);

                expect(document.getElementById('login').style.display).toBe('none');
                expect(document.getElementById('logout').style.display).toBe('block');
                expect(document.getElementById('profile').style.display).toBe('block');
                expect(document.getElementById('groups').style.display).toBe('block');
                expect(document.getElementById('availability-settings').style.display).toBe('none');
                expect(document.getElementById('my-students').style.display).toBe('none');
                expect(document.getElementById('tutor-session-settings').style.display).toBe('block');
                expect(document.getElementById('history').style.display).toBe('block');
                expect(document.getElementById('account-dropdown').style.display).toBe('block');
                expect(document.getElementById('role-view-switch').style.display).toBe('block');
                expect(document.getElementById('label-student').style.display).toBe('block');
                expect(document.getElementById('label-tutor').style.display).toBe('block');
                expect(document.getElementById('view-checkbox').checked).toBe(false);
            });

            it("displays logout link when user logged in and the user is both student and tutor but is in tutor view", function() {
                mockLoginStatus = {isLoggedIn:true, needsToRegister:false, url:'', role: "both", view: "tutor"};
                displayLoginLogoutLinkHelper(document, mockLoginStatus);

                expect(document.getElementById('login').style.display).toBe('none');
                expect(document.getElementById('logout').style.display).toBe('block');
                expect(document.getElementById('profile').style.display).toBe('block');
                expect(document.getElementById('groups').style.display).toBe('block');
                expect(document.getElementById('availability-settings').style.display).toBe('block');
                expect(document.getElementById('my-students').style.display).toBe('block');
                expect(document.getElementById('tutor-session-settings').style.display).toBe('none');
                expect(document.getElementById('history').style.display).toBe('none');
                expect(document.getElementById('account-dropdown').style.display).toBe('block');
                expect(document.getElementById('role-view-switch').style.display).toBe('block');
                expect(document.getElementById('label-student').style.display).toBe('block');
                expect(document.getElementById('label-tutor').style.display).toBe('block');
                expect(document.getElementById('view-checkbox').checked).toBe(true);
            });

            it("sets logout link correctly", function() {
                mockLoginStatus = {isLoggedIn:true, needsToRegister:false, url:'#', userId:'abc123', role: "tutor"};
                displayLoginLogoutLinkHelper(document, mockLoginStatus);

                expect(document.getElementById('logout-url').getAttribute("href")).toBe('#');
            });

            if("makes a request to log the user out and redirect to homepage", function() {
                var mockWindow = {location: {href: "test"}};
                spyOn(window, 'fetch').and.returnValue(Promise.resolve({redirected:true, url:"/homepage.html"}));
                logout(mockWindow);
                expect(mockWindow.location.href).toBe('/homepage.html');
            }); 

            it("sets login link correctly", function() {
                mockLoginStatus = {isLoggedIn:false, needsToRegister:false, url:'/_ah/login?continue=%2Fregistration.html', userId:null, role: "tutor"};
                displayLoginLogoutLinkHelper(document, mockLoginStatus);

                expect(document.getElementById('login-url').href).toBe('http://localhost:8080/_ah/login?continue=%2Fregistration.html');
            });

            it("adds event listener that redirects the user to their profile", function() {
                mockLoginStatus = {isLoggedIn:false, needsToRegister:false, url:'/_ah/login?continue=%2Fregistration.html', userId:'123', role: "tutor"};
                var mockWindow = {location: {href: "homepage.html"}};
                setProfileQueryString(mockWindow, mockLoginStatus);

                expect(mockWindow.location.href).toEqual("profile.html?userID=123");
            });

            it("adds event listener that redirects the user to their profile if their role is both", function() {
                mockLoginStatus = {isLoggedIn:false, needsToRegister:false, url:'/_ah/login?continue=%2Fregistration.html', userId:'123', role: "both"};
                var mockWindow = {location: {href: "homepage.html"}};
                setProfileQueryString(mockWindow, mockLoginStatus);

                expect(mockWindow.location.href).toEqual("profile.html?userID=123");
            });

            it("adds event listener that redirects the user to the groups page", function() {
                mockLoginStatus = {isLoggedIn:false, needsToRegister:false, url:'/_ah/login?continue=%2Fregistration.html', userId:'123', role: "tutor"};
                var mockWindow = {location: {href: "homepage.html"}};
                redirectToGroups(mockWindow, mockLoginStatus);

                expect(mockWindow.location.href).toEqual("groups.html");
            });


            it("adds event listener that redirects the user to their availability settings", function() {
                mockLoginStatus = {isLoggedIn:false, needsToRegister:false, url:'/_ah/login?continue=%2Fregistration.html', userId:'123', role: "tutor"};
                var mockWindow = {location: {href: "homepage.html"}};
                redirectToManageAvailability(mockWindow, mockLoginStatus);

                expect(mockWindow.location.href).toEqual("manage-availability.html");
            });

            it("adds event listener that redirects the user to their students", function() {
                mockLoginStatus = {isLoggedIn:false, needsToRegister:false, url:'/_ah/login?continue=%2Fregistration.html', userId:'123'};
                var mockWindow = {location: {href: "homepage.html"}};
                redirectToMyStudents(mockWindow, mockLoginStatus);

                expect(mockWindow.location.href).toEqual("my-students.html");
            });

            it("adds event listener that redirects the user to their lists", function() {
                mockLoginStatus = {isLoggedIn:false, needsToRegister:false, url:'/_ah/login?continue=%2Fregistration.html', userId:'123'};
                var mockWindow = {location: {href: "homepage.html"}};
                redirectToMyLists(mockWindow, mockLoginStatus);

                expect(mockWindow.location.href).toEqual("my-lists.html");
            })

            it("adds event listener that redirects the user to their tutor session settings", function() {
                mockLoginStatus = {isLoggedIn:false, needsToRegister:false, url:'/_ah/login?continue=%2Fregistration.html', userId:'123', role: "student"};
                var mockWindow = {location: {href: "homepage.html"}};
                redirectToManageSessions(mockWindow, mockLoginStatus);

                expect(mockWindow.location.href).toEqual("manage-sessions.html");
            });

            it("adds event listener that redirects the user to their history", function() {
                mockLoginStatus = {isLoggedIn:false, needsToRegister:false, url:'/_ah/login?continue=%2Fregistration.html', userId:'123', role: "student"};
                var mockWindow = {location: {href: "homepage.html"}};
                redirectToHistory(mockWindow, mockLoginStatus);

                expect(mockWindow.location.href).toEqual("history.html");
            });
        });
    });
});
