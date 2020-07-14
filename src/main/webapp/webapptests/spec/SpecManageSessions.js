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

describe("Manage Sessions", function() {

    describe("when the student requests to see a their scheduled sessions", function() {
        var mockWindow = {location: {href: "manage-sessions.html?studentEmail=test%40gmail.com", search: "?studentEmail=test%40gmail.com"}};

        it("should trigger the fetch function", function() {
            spyOn(window, "onload").and.callFake(function() {
                readTutorID(queryString, window);
            });
            spyOn(window, 'fetch').and.callThrough();
            getTutorSessionsManage(mockWindow);
            expect(window.fetch).toHaveBeenCalledWith('/confirmation?studentEmail=undefined', {method: 'GET'});
            expect(window.fetch).toHaveBeenCalled();
        });
    });

    describe("when the student email is read", function() {
        var mockWindow = {location: {href: "manage-sessions.html?studentEmail=test%40gmail.com", search: "?studentEmail=test%40gmail.com"}};
        var queryString = new Array();
        readTutorID(queryString, mockWindow);

        it("should set studentEmail inside queryString as the studentEmail", function() {
            expect(queryString["studentEmail"]).toEqual("test@gmail.com");
        });
    });

    describe("when a tutor session box is created", function() {
        var scheduledSession = {tutorEmail: "test@gmail.com", timeslot: {start: 600, date: {month: 4, dayOfMonth: 18, year: 2020}}};
        var userID = "test@gmail.com";
        var actual = createScheduledSessionBoxManage(scheduledSession, userID);

        it("should return a list item element", function() {
            expect(actual.tagName).toEqual("LI");
        });

        it("should have div elements as the children of each list item element", function() {
            expect(actual.childNodes[0].tagName).toEqual("DIV");
            expect(actual.childNodes[1].tagName).toEqual("DIV");
            expect(actual.childNodes[2].tagName).toEqual("DIV");
        });

        it("should have an h3 element as the child of the first div element inside each list item element", function() {
            expect(actual.childNodes[0].childNodes[0].tagName).toEqual("H3");
        });

        it("should have the inner HTML of the h3 tag equal to the name of the tutor for the tutoring session", function() {
            expect(actual.childNodes[0].childNodes[0].innerHTML).toEqual("Tutoring Session with test@gmail.com");
        })

        it("should have an h3 element as the child of the second div element inside each list item element", function() {
            expect(actual.childNodes[1].childNodes[0].tagName).toEqual("H3");
        });

        it("should have the inner HTML of the h3 tag equal to the time slot for that tutoring session", function() {
            expect(actual.childNodes[1].childNodes[0].innerHTML).toEqual("10:00am on May 18, 2020");
        });

        it("should have a button element as the child of the second div element inside each list item element", function() {
            expect(actual.childNodes[2].childNodes[0].tagName).toEqual("BUTTON");
        });

        it("should have the inner text of the button equal to Select", function() {
            expect(actual.childNodes[2].childNodes[0].innerText).toEqual("Cancel");
        });
    });

    describe("when a user cancels a tutoring session", function() {
        var mockWindow = {location: {href: "manage-sessions.html?studentEmail=test%40gmail.com", search: "?studentEmail=test%40gmail.com"}};
        var scheduledSession = {tutorEmail: "test@gmail.com", studentEmail: "test2@gmail.com", 
                                subtopics: null, questions: null, rating: 5, id: 1,
                                timeslot: {start: 600, end: 660,  date: {month: 4, dayOfMonth: 18, year: 2020}}};
        var params = new URLSearchParams();
        params.append('tutorEmail', "test@gmail.com");
        params.append('studentEmail', "test2@gmail.com");
        params.append('year', 2020);
        params.append('month', 4);
        params.append('day', 18);
        params.append('start', 600);
        params.append('end', 660);
        params.append('subtopics', null);
        params.append('questions', null);
        params.append('rating', 5);
        params.append('id', 1);

        it("should trigger the fetch function", function() {
            spyOn(window, 'fetch').and.callThrough();
            cancelTutorSession("test@gmail.com", mockWindow, scheduledSession);
            expect(window.fetch).toHaveBeenCalledWith('/delete-tutor-session', {method: 'POST', body: params})
            expect(window.fetch).toHaveBeenCalled();
        });
    });

});
