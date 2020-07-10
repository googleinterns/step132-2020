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

describe("Confirmation", function() {
    describe("when the list of upcoming sessions is loaded", function() {
        var mockWindow = {location: {href: "confirmation.html?studentEmail=test%40gmail.com", search: "?studentEmail=test%40gmail.com"}};

        it("should trigger the fetch function", function() {
            spyOn(window, "onload").and.callFake(function() {
                readTutorID(queryString, window);
            });
            spyOn(window, 'fetch').and.callThrough();
            getScheduledSessions(mockWindow);
            expect(window.fetch).toHaveBeenCalledWith('/confirmation?studentEmail=undefined', {method: 'GET'});
            expect(window.fetch).toHaveBeenCalled();
        });
    });

    describe("when the tutor ID is read", function() {
        var mockWindow = {location: {href: "confirmation.html?studentEmail=test%40gmail.com", search: "?studentEmail=test%40gmail.com"}};
        var queryString = new Array();
        readTutorID(queryString, mockWindow);

        it("should set tutorID inside queryString as the tutorID", function() {
            expect(queryString["studentEmail"]).toEqual("test@gmail.com");
        });
    });

    describe("when a scheduled session box is created", function() {
        var scheduledSession = {tutorEmail: "tester@gmail.com", timeslot: {start: 480, date: {month: 4, dayOfMonth: 18, year: 2020}}};
        var studentEmail = "test@gmail.com";
        var actual = createScheduledSessionBox(scheduledSession, studentEmail);

        it("should return a list item element", function() {
            expect(actual.tagName).toEqual("LI");
        });

        it("should have div elements as the children of each list item element", function() {
            expect(actual.childNodes[0].tagName).toEqual("DIV");
            expect(actual.childNodes[1].tagName).toEqual("DIV");
        });

        it("should have an h3 element as the child of the first div element inside each list item element", function() {
            expect(actual.childNodes[0].childNodes[0].tagName).toEqual("H3");
        });

        it("should have an h3 element as the child of the second div element inside each list item element", function() {
            expect(actual.childNodes[1].childNodes[0].tagName).toEqual("H3");
        });

        it("should have the inner HTML of the h3 tag equal to to the email of the tutor", function() {
            expect(actual.childNodes[0].childNodes[0].innerHTML).toEqual("Tutoring Session with tester@gmail.com");
        });

        it("should have the inner HTML of the h3 tag equal to the time slot for that tutoring session", function() {
            expect(actual.childNodes[1].childNodes[0].innerHTML).toEqual("8:00am on May 18, 2020");
        });
    });

});

