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

describe("When displaying registration form", function() {
    beforeAll(function() {  
        var mockGenInfoDiv = document.createElement('div');
        mockGenInfoDiv.id = "general-info";
        mockGenInfoDiv.style.display = 'none';

        var mockTutorInfoDiv = document.createElement('div');
        mockTutorInfoDiv.id = "tutor-info";
        mockTutorInfoDiv.style.display = 'none';

        var mockStudentRadioBtn = document.createElement('input');
        mockStudentRadioBtn.setAttribute('type', 'radio');
        mockStudentRadioBtn.id = 'student';
        mockStudentRadioBtn.name = 'role';

        var mockTutorRadioBtn = document.createElement('input');
        mockTutorRadioBtn.setAttribute('type', 'radio');
        mockTutorRadioBtn.id = 'tutor';
        mockTutorRadioBtn.name = 'role';

        document.body.appendChild(mockStudentRadioBtn);
        document.body.appendChild(mockTutorRadioBtn);
        document.body.appendChild(mockGenInfoDiv);
        mockGenInfoDiv.appendChild(mockTutorInfoDiv);
    })

    it("should ask only for general info if user is student", function() {
        document.getElementById('student').checked = true;
        displayRegistrationInfoHelper(document);

        expect(document.getElementById('general-info').style.display).toBe('block');
        expect(document.getElementById('tutor-info').style.display).toBe('none');
    });

    it("should ask for general and tutor info if user is tutor", function() {
        document.getElementById('tutor').checked = true;
        displayRegistrationInfoHelper(document);

        expect(document.getElementById('general-info').style.display).toBe('block');
        expect(document.getElementById('tutor-info').style.display).toBe('block');
    });

    it("should ask only for general info if user clicks tutor then switches to student", function() {
        document.getElementById('tutor').checked = true;
        document.getElementById('student').checked = true;
        displayRegistrationInfoHelper(document);

        expect(document.getElementById('general-info').style.display).toBe('block');
        expect(document.getElementById('tutor-info').style.display).toBe('none');
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

        // it("sets login link correctly", function() {
        //     mockLoginStatus = {isLoggedIn:false, needsToRegister:false, url:'/_ah/login?continue=%2Fregistration.html'};
        //     fetchLoginStatusHelper(document, mockLoginStatus);

        //     expect(document.getElementById('login-url').href).toBe('http://localhost:8080/_ah/login?continue=%2Fregistration.html');
        // });
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

            document.body.appendChild(mockLoginForm);
            document.body.appendChild(mockLoginUrl);
            document.body.appendChild(mockLogoutForm);
            document.body.appendChild(mockLogoutUrl);
        })

        it("displays login link when user logged out", function() {
            mockLoginStatus = {isLoggedIn:false, needsToRegister:false, url:''};
            displayLoginLogoutLinkHelper(document, mockLoginStatus);

            expect(document.getElementById('login').style.display).toBe('block');
            expect(document.getElementById('logout').style.display).toBe('none');
        });

        it("displays logout link when user logged in", function() {
            mockLoginStatus = {isLoggedIn:true, needsToRegister:false, url:''};
            displayLoginLogoutLinkHelper(document, mockLoginStatus);

            expect(document.getElementById('login').style.display).toBe('none');
            expect(document.getElementById('logout').style.display).toBe('block');
        });

        it("sets logout link correctly", function() {
            mockLoginStatus = {isLoggedIn:true, needsToRegister:false, url:'/_ah/logout?continue=%2Fhomepage.html'};
            displayLoginLogoutLinkHelper(document, mockLoginStatus);

            expect(document.getElementById('logout-url').href).toBe('http://localhost:8080/_ah/logout?continue=%2Fhomepage.html');
        });

        it("sets login link correctly", function() {
            mockLoginStatus = {isLoggedIn:false, needsToRegister:false, url:'/_ah/login?continue=%2Fregistration.html'};
            displayLoginLogoutLinkHelper(document, mockLoginStatus);

            expect(document.getElementById('login-url').href).toBe('http://localhost:8080/_ah/login?continue=%2Fregistration.html');
        });
    });
});
