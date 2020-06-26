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
        var mockWindow = {location: {href: "confirmation.html?tutorID=test%40gmail.com", search: "?tutorID=test%40gmail.com"}};

        it("should trigger the fetch function", function() {
            spyOn(window, "onload").and.callFake(function() {
                readTutorID(queryString, window);
            });
            spyOn(window, 'fetch').and.callThrough();
            getScheduledSessions(mockWindow);
            expect(window.fetch).toHaveBeenCalled();
        });
    });

    describe("when the tutor ID is read", function() {
        var mockWindow = {location: {href: "confirmation.html?tutorID=test%40gmail.com", search: "?tutorID=test%40gmail.com"}};
        var queryString = new Array();
        readTutorID(queryString, mockWindow);

        it("should set tutorID inside queryString as the tutorID", function() {
            expect(queryString["tutorID"]).toEqual("test@gmail.com");
        });
    });

    describe("when a scheduled session box is created", function() {
        var scheduledSession = {timeslot: {start: 480}};
        var tutorID = "test@gmail.com";
        var actual = createScheduledSessionBox(scheduledSession, tutorID);

        it("should return a list item element", function() {
            expect(actual.tagName).toEqual("LI");
        });

        it("should have a div element as the child of each list item element", function() {
            expect(actual.childNodes[0].tagName).toEqual("DIV");
        });

        it("should have an h3 element as the child of each div element inside each list item element", function() {
            expect(actual.childNodes[0].childNodes[0].tagName).toEqual("H3");
        });

        it("should have the inner HTML of the h3 tag equal to the start of the time slot for that tutoring session", function() {
            expect(actual.childNodes[0].childNodes[0].innerHTML).toEqual("480");
        });
    });

});

