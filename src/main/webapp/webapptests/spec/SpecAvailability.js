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

describe("Availability", function() {

    describe("when the user requests to see a tutor's availability", function() {
        var mockWindow = {location: {href: "availability.html?tutorID=test%40gmail.com", search: "?tutorID=test%40gmail.com"}};

        it("should trigger the fetch function", function() {
            spyOn(window, "onload").and.callFake(function() {
                readTutorID(queryString, window);
            });
            spyOn(window, 'fetch').and.callThrough();
            getAvailability(mockWindow);
            expect(window.fetch).toHaveBeenCalledWith('/availability?tutorID=undefined', {method: 'GET'});
            expect(window.fetch).toHaveBeenCalled();
        });
    });

    describe("when the tutor ID is read", function() {
        var mockWindow = {location: {href: "availability.html?tutorID=test%40gmail.com", search: "?tutorID=test%40gmail.com"}};
        var queryString = new Array();
        readTutorID(queryString, mockWindow);

        it("should set tutorID inside queryString as the tutorID", function() {
            expect(queryString["tutorID"]).toEqual("test@gmail.com");
        });
    });

    describe("when a time slot box is created", function() {
        var timeslot = {start: 480, date: {month: 4, dayOfMonth: 18, year: 2020}};
        var tutorID = "test@gmail.com";
        var actual = createTimeSlotBox(timeslot, tutorID);

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

        it("should have a button element as the child of the second div element inside each list item element", function() {
            expect(actual.childNodes[1].childNodes[0].tagName).toEqual("BUTTON");
        });

        it("should have the inner HTML of the h3 tag equal to the time slot for that tutoring session", function() {
            expect(actual.childNodes[0].childNodes[0].innerHTML).toEqual("8:00am on May 18, 2020");
        })

        it("should have the inner text of the button equal to Select", function() {
            expect(actual.childNodes[1].childNodes[0].innerText).toEqual("Select");
        });
    });

    describe("when a user selects an available time slot", function() {
        var mockWindow = {location: {href: "availability.html"}};
        var timeslot = {duration: 60, end: 540, start: 480, date: {year: 2020, month: 5, dayOfMonth: 18}};

        it("should redirect the user to scheduling.html and pass tutorID as an URI component", function() {
            selectTimeSlot("test@gmail.com", mockWindow, timeslot);
            expect(mockWindow.location.href).toEqual("scheduling.html?tutorID=test%40gmail.com&start=480&end=540&year=2020&month=5&day=18");
        });
    });

});
