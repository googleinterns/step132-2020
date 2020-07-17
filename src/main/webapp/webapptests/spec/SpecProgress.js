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

describe("Progress", function() {

    describe("when progress is loaded", function() {
        var mockWindow = {location: {href: "progress.html?studentID=123", search: "?studentID=123"}};

        it("should trigger the fetch function with the correct params", function() {
            spyOn(window, 'fetch').and.callThrough();
            spyOn(window, 'loadProgressHelper').and.returnValue("");
            loadProgress(mockWindow);
            expect(window.fetch).toHaveBeenCalledWith('/login-status');
            expect(window.fetch).toHaveBeenCalled();
        });
    });

    describe("when loadProgressHelper is called it should call the correct functions", function() {
        var mockWindow = {location: {href: "progress.html?studentID=123", search: "?studentID=123"}};
        var mockDocument = {};
        var mockLoginStatus = {};

        it("should trigger the correct functions with the correct params", function() {
            spyOn(window, 'getExperiences').and.callFake(function(){
                console.log("ok");
            });
            spyOn(window, 'getGoals').and.callFake(function(){
                console.log("ok");
            });
            spyOn(window, 'getAchievements').and.callFake(function(){
                console.log("ok");
            });
            spyOn(window, 'getPastSessionsAndTopics').and.callFake(function(){
                console.log("ok");
            });
            loadProgressHelper(mockDocument, mockLoginStatus, mockWindow);
            expect(window.getExperiences).toHaveBeenCalledWith(mockDocument, mockLoginStatus);
            expect(window.getGoals).toHaveBeenCalledWith(mockDocument, mockLoginStatus);
            expect(window.getAchievements).toHaveBeenCalledWith(mockDocument, mockLoginStatus);
            expect(window.getPastSessionsAndTopics).toHaveBeenCalledWith(mockDocument, mockLoginStatus);
        });
    });

    describe("when get experiences is called", function() {

        beforeAll(function() {
            var mockExperiencesForm = document.createElement('p');
            mockExperiencesForm.id = 'experiences-form';

            document.body.appendChild(mockExperiencesForm);
        })

        it("should trigger the fetch function with the correct params and display the form given that the user is a student", function() {
            var mockWindow = {location: {href: "progress.html?studentID=123", search: "?studentID=123"}};
            var mockLoginStatus = {role: "student"};

            spyOn(window, "onload").and.callFake(function() {
                readStudentID(queryString, window);
            });
            spyOn(window, 'fetch').and.returnValue(Promise.resolve({json: () => Promise.resolve()}));
            getExperiences(document, mockLoginStatus, mockWindow);
            expect(window.fetch).toHaveBeenCalledWith('/add-experience?studentID=undefined', {method: 'GET'});
            expect(document.getElementById('experiences-form').style.display).toBe('block');
        });

        it("should trigger the fetch function with the correct params and hide the form given that the user is a tutor", function() {
            var mockWindow = {location: {href: "progress.html?studentID=123", search: "?studentID=123"}};
            var mockLoginStatus = {role: "tutor"};

            spyOn(window, "onload").and.callFake(function() {
                readStudentID(queryString, window);
            });
            spyOn(window, 'fetch').and.returnValue(Promise.resolve({json: () => Promise.resolve()}));
            getExperiences(document, mockLoginStatus, mockWindow);
            expect(window.fetch).toHaveBeenCalledWith('/add-experience?studentID=undefined', {method: 'GET'});
            expect(document.getElementById('experiences-form').style.display).toBe('none');
        });
    });

    describe("when get experiences helper is called", function() {

        beforeEach(function() {
            var mockExperiencesContainer = document.createElement('div');
            mockExperiencesContainer.id = 'experiences';

            document.body.appendChild(mockExperiencesContainer);
        })

        afterEach(function() {
            document.getElementById('experiences').remove();
        })

        it("should display the error if there is one", function() {
            var mockWindow = {location: {href: "progress.html?studentID=123", search: "?studentID=123"}};
            var mockExperiences = {error: "error"};

            getExperiencesHelper(document, mockExperiences, mockWindow);

            expect(document.getElementById('experiences').childNodes[0].tagName).toEqual('P');
            expect(document.getElementById('experiences').childNodes[0].innerHTML).toEqual('error');
        });

        it("should say no experiences exist if none does", function() {
            var mockWindow = {location: {href: "progress.html?studentID=123", search: "?studentID=123"}};
            var mockExperiences = {};

            getExperiencesHelper(document, mockExperiences, mockWindow);

            expect(document.getElementById('experiences').childNodes[0].tagName).toEqual('P');
            expect(document.getElementById('experiences').childNodes[0].innerHTML).toEqual('This user has not set any past experiences');
        });
    });

     describe("when get goals is called", function() {

        beforeAll(function() {
            var mockGoalsForm = document.createElement('p');
            mockGoalsForm.id = 'goals-form';

            document.body.appendChild(mockGoalsForm);
        })

        it("should trigger the fetch function with the correct params and display the form given that the user is a student", function() {
            var mockWindow = {location: {href: "progress.html?studentID=123", search: "?studentID=123"}};
            var mockLoginStatus = {role: "student"};

            spyOn(window, "onload").and.callFake(function() {
                readStudentID(queryString, window);
            });
            spyOn(window, 'fetch').and.returnValue(Promise.resolve({json: () => Promise.resolve()}));
            getGoals(document, mockLoginStatus, mockWindow);
            expect(window.fetch).toHaveBeenCalledWith('/add-goal?studentID=undefined', {method: 'GET'});
            expect(document.getElementById('goals-form').style.display).toBe('block');
        });

        it("should trigger the fetch function with the correct params and hide the form given that the user is a tutor", function() {
            var mockWindow = {location: {href: "progress.html?studentID=123", search: "?studentID=123"}};
            var mockLoginStatus = {role: "tutor"};

            spyOn(window, "onload").and.callFake(function() {
                readStudentID(queryString, window);
            });
            spyOn(window, 'fetch').and.returnValue(Promise.resolve({json: () => Promise.resolve()}));
            getGoals(document, mockLoginStatus, mockWindow);
            expect(window.fetch).toHaveBeenCalledWith('/add-goal?studentID=undefined', {method: 'GET'});
            expect(document.getElementById('goals-form').style.display).toBe('none');
        });
    });

    describe("when get goals helper is called", function() {

        beforeEach(function() {
            var mockGoalsContainer = document.createElement('div');
            mockGoalsContainer.id = 'goals';

            document.body.appendChild(mockGoalsContainer);
        })

        afterEach(function() {
            document.getElementById('goals').remove();
        })

        it("should display the error if there is one", function() {
            var mockWindow = {location: {href: "progress.html?studentID=123", search: "?studentID=123"}};
            var mockGoals = {error: "error"};

            getGoalsHelper(document, mockGoals, mockWindow);

            expect(document.getElementById('goals').childNodes[0].tagName).toEqual('P');
            expect(document.getElementById('goals').childNodes[0].innerHTML).toEqual('error');
        });

        it("should say no goals exist if none does", function() {
            var mockWindow = {location: {href: "progress.html?studentID=123", search: "?studentID=123"}};
            var mockGoals = {};

            getGoalsHelper(document, mockGoals, mockWindow);

            expect(document.getElementById('goals').childNodes[0].tagName).toEqual('P');
            expect(document.getElementById('goals').childNodes[0].innerHTML).toEqual('This user has not set any goals');
        });
    });

    describe("when get achievements is called", function() {

        beforeEach(function() {
            var mockAchievementsContainer = document.createElement('div');
            mockAchievementsContainer.id = 'achievements';

            document.body.appendChild(mockAchievementsContainer);
        })

        afterEach(function() {
            document.getElementById('achievements').remove();
        })

        it("should say no goals exist", function() {
            var mockWindow = {location: {href: "progress.html?studentID=123", search: "?studentID=123"}};
            var mockLoginStatus = {};

            getAchievements(document, mockLoginStatus, mockWindow);

            expect(document.getElementById('achievements').childNodes[0].tagName).toEqual('P');
            expect(document.getElementById('achievements').childNodes[0].innerHTML).toEqual('This user does not have any achievements');
        });
    });

    describe("when get PastSessionsAndTopics is called", function() {
        it("should trigger the fetch function with the correct params", function() {
            var mockWindow = {location: {href: "progress.html?studentID=123", search: "?studentID=123"}};

            spyOn(window, "onload").and.callFake(function() {
                readStudentID(queryString, window);
            });
            spyOn(window, 'fetch').and.returnValue(Promise.resolve({json: () => Promise.resolve()}));
            getPastSessionsAndTopics(document, mockWindow);
            expect(window.fetch).toHaveBeenCalledWith('/history?studentID=undefined', {method: 'GET'});
        });
    });

    describe("when get PastSessionsAndTopics helper is called", function() {

        beforeEach(function() {
            var mockPastSessionsContainer = document.createElement('div');
            mockPastSessionsContainer.id = 'past-sessions';

            var mockPastTopicsContainer = document.createElement('div');
            mockPastTopicsContainer.id = 'past-topics';

            document.body.appendChild(mockPastSessionsContainer);
            document.body.appendChild(mockPastTopicsContainer);
        })

        afterEach(function() {
            document.getElementById('past-sessions').remove();
            document.getElementById('past-topics').remove();
        })

        it("should display the error if there is one", function() {
            var mockWindow = {location: {href: "progress.html?studentID=123", search: "?studentID=123"}};
            var mockTutoringSessions = {error: "error"};

            getPastSessionsAndTopicsHelper(document, mockTutoringSessions, mockWindow);

            expect(document.getElementById('past-sessions').childNodes[0].tagName).toEqual('P');
            expect(document.getElementById('past-sessions').childNodes[0].innerHTML).toEqual('error');
            expect(document.getElementById('past-topics').childNodes[0].tagName).toEqual('P');
            expect(document.getElementById('past-topics').childNodes[0].innerHTML).toEqual('error');
        });

        it("should say no past session and no past topic exist if true", function() {
            var mockWindow = {location: {href: "progress.html?studentID=123", search: "?studentID=123"}};
            var mockTutoringSessions = {};

            getPastSessionsAndTopicsHelper(document, mockTutoringSessions, mockWindow);

            expect(document.getElementById('past-sessions').childNodes[0].tagName).toEqual('P');
            expect(document.getElementById('past-sessions').childNodes[0].innerHTML).toEqual('This user has not had any tutoring sessions yet.');
            expect(document.getElementById('past-topics').childNodes[0].tagName).toEqual('P');
            expect(document.getElementById('past-topics').childNodes[0].innerHTML).toEqual('This user has not had any tutoring sessions yet.');
        });
    });

    describe("when a PastSession box is created", function() {
        beforeAll(function() {
            spyOn(window, "setTutorName").and.callFake(function() {
                console.log("ok");
            });
        })

        var tutoringSession = {tutorID: "123", timeslot: {start: 600, date: {month: 1, dayOfMonth: 1, year: 2020}}};
        var actual = createPastSessionBox(tutoringSession);


        it("should return a div item element", function() {
            expect(actual.tagName).toEqual("DIV");
        });

        it("should have the correct element type for each children of the div", function() {
            expect(actual.childNodes[0].tagName).toEqual("H3");
            expect(actual.childNodes[1].tagName).toEqual("H6");
        });

        it("should have date set appropriately", function() {
            expect(actual.childNodes[1].innerHTML).toEqual("10:00am on February 1, 2020");
        });
    });

    describe("when a Topic box is created", function() {
        var tutoringSession = {subtopics: "test"};
        var actual = createPastTopicBox(tutoringSession);


        it("should return a div item element", function() {
            expect(actual.tagName).toEqual("DIV");
        });

        it("should have the correct element type for each children of the div", function() {
            expect(actual.childNodes[0].tagName).toEqual("H3");
        });

        it("should have date set appropriately", function() {
            expect(actual.childNodes[0].innerHTML).toEqual("test");
        });
    });

    describe("when a Goal box is created and the user is a tutor", function() {
        var goal = {goal: "test"};
        var mockLoginStatus = {role: "tutor"};
        var actual = createGoalBox(goal, mockLoginStatus);


        it("should return a div item element", function() {
            expect(actual.tagName).toEqual("DIV");
        });

        it("should have the correct element type for each children of the div", function() {
            expect(actual.childNodes[0].tagName).toEqual("H3");
        });

        it("should have date set appropriately", function() {
            expect(actual.childNodes[0].innerHTML).toEqual("test");
        });
    });

    describe("when a Goal box is created and the user is a student", function() {
        var goal = {goal: "test"};
        var mockLoginStatus = {role: "student"};
        var actual = createGoalBox(goal, mockLoginStatus);


        it("should return a div item element", function() {
            expect(actual.tagName).toEqual("DIV");
        });

        it("should have the correct element type for each children of the div", function() {
            expect(actual.childNodes[0].tagName).toEqual("H3");
            expect(actual.childNodes[1].tagName).toEqual("BUTTON");
        });

        it("should have date set appropriately", function() {
            expect(actual.childNodes[0].innerHTML).toEqual("test");
        });

        it("should have button with correct text", function() {
            expect(actual.childNodes[1].innerText).toEqual("Delete");
        });
    });

    describe("when an Experience box is created and the user is a tutor", function() {
        var experience = {experience: "test"};
        var mockLoginStatus = {role: "tutor"};
        var actual = createExperienceBox(experience, mockLoginStatus);


        it("should return a div item element", function() {
            expect(actual.tagName).toEqual("DIV");
        });

        it("should have the correct element type for each children of the div", function() {
            expect(actual.childNodes[0].tagName).toEqual("H3");
        });

        it("should have date set appropriately", function() {
            expect(actual.childNodes[0].innerHTML).toEqual("test");
        });
    });
    
    describe("when an Experience box is created and the user is a student", function() {
        var experience = {experience: "test"};
        var mockLoginStatus = {role: "student"};
        var actual = createExperienceBox(experience, mockLoginStatus);


        it("should return a div item element", function() {
            expect(actual.tagName).toEqual("DIV");
        });

        it("should have the correct element type for each children of the div", function() {
            expect(actual.childNodes[0].tagName).toEqual("H3");
            expect(actual.childNodes[1].tagName).toEqual("BUTTON");
        });

        it("should have date set appropriately", function() {
            expect(actual.childNodes[0].innerHTML).toEqual("test");
        });

        it("should have button with correct text", function() {
            expect(actual.childNodes[1].innerText).toEqual("Delete");
        });
    });

    describe("when the student requests to add a goal", function() {
        var mockWindow = {location: {href: "progress.html?studentID=123", search: "?studentID=123"}};

        it("should trigger the fetch function with the correct params", function() {
            var params = new URLSearchParams();
            params.append('goal', "undefined");
            params.append('studentID', 123);

            spyOn(window, "onload").and.callFake(function() {
                readStudentID(queryString, window);
            });
            spyOn(window, 'fetch').and.callThrough();
            spyOn(document, 'getElementById').and.returnValue("test");
            addGoal(mockWindow);
            expect(window.fetch).toHaveBeenCalledWith('/add-goal', {method: 'POST', body: params});
        });
    });

    describe("when the student requests to add an experience", function() {
        var mockWindow = {location: {href: "progress.html?studentID=123", search: "?studentID=123"}};

        it("should trigger the fetch function with the correct params", function() {
            var params = new URLSearchParams();
            params.append('experience', "undefined");
            params.append('studentID', 123);

            spyOn(window, "onload").and.callFake(function() {
                readStudentID(queryString, window);
            });
            spyOn(window, 'fetch').and.callThrough();
            spyOn(document, 'getElementById').and.returnValue("test");
            addExperience(mockWindow);
            expect(window.fetch).toHaveBeenCalledWith('/add-experience', {method: 'POST', body: params});
        });
    });

    describe("when the student requests to delete a goal", function() {
        var mockWindow = {location: {href: "progress.html?studentID=123", search: "?studentID=123"}};
        var mockGoal = {id: 1};
        var mockStudentID = 123;

        it("should trigger the fetch function with the correct params", function() {
            var params = new URLSearchParams();
            params.append('studentID', 123);
            params.append('id', 1);

            spyOn(window, 'fetch').and.callThrough();
            deleteGoal(mockGoal, mockStudentID, mockWindow);
            expect(window.fetch).toHaveBeenCalledWith('/delete-goal', {method: 'POST', body: params});
        });
    });

    describe("when the student requests to delete an experience", function() {
        var mockWindow = {location: {href: "progress.html?studentID=123", search: "?studentID=123"}};
        var mockExperience = {id: 1};
        var mockStudentID = 123;

        it("should trigger the fetch function with the correct params", function() {
            var params = new URLSearchParams();
            params.append('studentID', 123);
            params.append('id', 1);

            spyOn(window, 'fetch').and.callThrough();
            deleteExperience(mockExperience, mockStudentID, mockWindow);
            expect(window.fetch).toHaveBeenCalledWith('/delete-experience', {method: 'POST', body: params});
        });
    });

});