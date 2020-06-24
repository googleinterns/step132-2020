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
        var timeslot = {duration: 60, end: 540, start: 480};
        var tutorID = "test@gmail.com";
        var actual = createTimeSlotBox(timeslot, tutorID);

        it("should return a list item element", function() {
            expect(actual.tagName).toEqual("LI");
        });

        it("should have a div element as the child of each list item element", function() {
            expect(actual.childNodes[0].tagName).toEqual("DIV");
        });

        it("should have an h3 element as the child of each div element inside each list item element", function() {
            expect(actual.childNodes[0].childNodes[0].tagName).toEqual("H3");
        });

        it("should have the inner HTML of the h3 tag equal to the start of the time slot", function() {
            expect(actual.childNodes[0].childNodes[0].innerHTML).toEqual("480");
        });

        it("should have a button element as the child of each div element inside each list item element", function() {
            expect(actual.childNodes[0].childNodes[1].tagName).toEqual("BUTTON");
        });

        it("should have the inner text of the button equal to Select", function() {
            expect(actual.childNodes[0].childNodes[1].innerText).toEqual("Select");
        });
    });

    describe("when a user selects an available time slot", function() {
        var mockWindow = {location: {href: "availability.html"}};

        it("should redirect the user to scheduling.html and pass tutorID as an URI component", function() {
            selectTimeSlot("test@gmail.com", mockWindow);
            expect(mockWindow.location.href).toEqual("scheduling.html?tutorID=test%40gmail.com");
        });
    });

});
