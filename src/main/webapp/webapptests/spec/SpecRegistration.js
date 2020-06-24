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
    beforeEach(function() {  
        var mockGenInfoDiv = document.createElement('div');
        mockGenInfoDiv.id = "general-info";
        mockGenInfoDiv.style.display = 'none';

        var mockTutorInfoDiv = document.createElement('div');
        mockTutorInfoDiv.id = "tutor-info";
        mockTutorInfoDiv.style.display = 'none';

        var mockStudentRadioBtn = document.createElement('input');
        mockStudentRadioBtn.setAttribute('type', 'radio');
        mockStudentRadioBtn.setAttribute('checked', true);
        mockStudentRadioBtn.id = 'student';
        mockStudentRadioBtn.name = 'role';

        var mockTutorRadioBtn = document.createElement('input');
        mockTutorRadioBtn.setAttribute('type', 'radio');
        mockTutorRadioBtn.setAttribute('checked', false);
        mockTutorRadioBtn.id = 'tutor';
        mockTutorRadioBtn.name = 'role';

        document.body.appendChild(mockStudentRadioBtn);
        document.body.appendChild(mockTutorRadioBtn);
        document.body.appendChild(mockGenInfoDiv);
        mockGenInfoDiv.appendChild(mockTutorInfoDiv);
    })

    it("should always ask for general info", function() {
        displayRegistrationInfoHelper(document);
        expect(window.getComputedStyle(document.getElementById('general-info')).display).toBe('block');
        expect(window.getComputedStyle(document.getElementById('tutor-info')).display).toBe('none');
    });

    it("should ask for general and tutor info if user is tutor", function() {
        document.getElementById('tutor').checked = true;
        displayRegistrationInfoHelper(document);
        expect(window.getComputedStyle(document.getElementById('tutor-info')).display).toBe('block');
        expect(window.getComputedStyle(document.getElementById('general-info')).display).toBe('block');
    });

    it("should ask only for general info if user clicks tutor then switches to student", function() {
        document.getElementById('tutor').checked = true;
        document.getElementById('student').checked = true;
        displayRegistrationInfoHelper(document);
        expect(window.getComputedStyle(document.getElementById('tutor-info')).display).toBe('none');
        expect(window.getComputedStyle(document.getElementById('general-info')).display).toBe('block');
    });
});

describe("Fetching login status", function() {


    it("displays only sign in link if user not logged in", function() {
        
        expect(window.getComputedStyle(document.getElementById('login-form')).display).toBe('block');
        expect(window.getComputedStyle(document.getElementById('logout-form')).display).toBe('none');
        expect(window.getComputedStyle(document.getElementById('registration-form')).display).toBe('none');
    });

    it("displays only sign out link if user is logged in and registered", function() {
        
        expect(window.getComputedStyle(document.getElementById('login-form')).display).toBe('none');
        expect(window.getComputedStyle(document.getElementById('logout-form')).display).toBe('block');
        expect(window.getComputedStyle(document.getElementById('registration-form')).display).toBe('none');
    });

    it("displays only sign out link and registration form if logged in and needs to register", function() {
        
        expect(window.getComputedStyle(document.getElementById('login-form')).display).toBe('none');
        expect(window.getComputedStyle(document.getElementById('logout-form')).display).toBe('block');
        expect(window.getComputedStyle(document.getElementById('registration-form')).display).toBe('block');
    });

    it("sets correct link when user logged out", function() {
        
        expect(document.getElementById('logout-url').href.toBe('/_ah/login?continue=%2Fregistration.html'));
    });

    it("sets correct link when user logged in", function() {
       
        expect(document.getElementById('login-url').href.toBe('/_ah/logout?continue=%2Fhomepage.html'));
    });
});
