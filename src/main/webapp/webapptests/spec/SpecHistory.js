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

describe("History", function() {
    describe("when the history of tutoring sessions is loaded", function() {
        var mockWindow = {location: {href: "history.html?studentEmail=test%40gmail.com", search: "?studentEmail=test%40gmail.com"}};

        it("should trigger the fetch function", function() {
            spyOn(window, "onload").and.callFake(function() {
                readStudentEmail(queryString, window);
            });
            spyOn(window, 'fetch').and.callThrough();
            getTutoringSessionHistory(mockWindow);
            expect(window.fetch).toHaveBeenCalled();
        });
    });

    describe("when the student email is read", function() {
        var mockWindow = {location: {href: "history.html?studentEmail=test%40gmail.com", search: "?studentEmail=test%40gmail.com"}};
        var queryString = new Array();
        readStudentEmail(queryString, mockWindow);

        it("should set studentEmail inside queryString as the studentEmail passed down", function() {
            expect(queryString["studentEmail"]).toEqual("test@gmail.com");
        });
    });

    describe("when a tutoring session box is created", function() {
        var tutoringSession = {tutorEmail: "tester@gmail.com", timeslot: {start: 480}};
        var actual = createTutoringSessionBox(tutoringSession);

        it("should return a list item element", function() {
            expect(actual.tagName).toEqual("LI");
        });

        it("should have a div element as the child of each list item element", function() {
            expect(actual.childNodes[0].tagName).toEqual("DIV");
        });

        it("should have an h3 element as the child of the first div element inside each list item element", function() {
            expect(actual.childNodes[0].childNodes[0].tagName).toEqual("H3");
        });

        it("should have an h3 element as the second child of the first div element inside each list item element", function() {
            expect(actual.childNodes[0].childNodes[1].tagName).toEqual("H3");
        });

        it("should have a div element as the third child of each div element", function() {
            expect(actual.childNodes[0].childNodes[2].tagName).toEqual("DIV");
        });

        it("should have the inner HTML of the h3 tag equal to the start of the time slot for that tutoring session", function() {
            expect(actual.childNodes[0].childNodes[0].innerHTML).toEqual("480");
        });

        it("should have the inner HTML of the second h3 tag equal to the email of the tutor", function() {
            expect(actual.childNodes[0].childNodes[1].innerHTML).toEqual("tester@gmail.com");
        });

        it("should have 5 stars inside the second div element", function() {
            expect(actual.childNodes[0].childNodes[2].childNodes.length).toEqual(5);
        });

        it("should have all stars inside the second div element of tag SPAN", function() {
            expect(actual.childNodes[0].childNodes[2].childNodes[0].tagName).toEqual("SPAN");
            expect(actual.childNodes[0].childNodes[2].childNodes[1].tagName).toEqual("SPAN");
            expect(actual.childNodes[0].childNodes[2].childNodes[2].tagName).toEqual("SPAN");
            expect(actual.childNodes[0].childNodes[2].childNodes[3].tagName).toEqual("SPAN");
            expect(actual.childNodes[0].childNodes[2].childNodes[4].tagName).toEqual("SPAN");
        });

        it("should have all stars inside the second div element of class glyphicon glyphicon-star", function() {
            expect(actual.childNodes[0].childNodes[2].childNodes[0].className).toContain("glyphicon glyphicon-star");
            expect(actual.childNodes[0].childNodes[2].childNodes[1].className).toContain("glyphicon glyphicon-star");
            expect(actual.childNodes[0].childNodes[2].childNodes[2].className).toContain("glyphicon glyphicon-star");
            expect(actual.childNodes[0].childNodes[2].childNodes[3].className).toContain("glyphicon glyphicon-star");
            expect(actual.childNodes[0].childNodes[2].childNodes[4].className).toContain("glyphicon glyphicon-star");
        });
    });

    describe("when loadStars is called and the tutoring session has been rated", function() {
        var fakeTutoringSession = {tutorEmail: "tester@gmail.com", rated: true, rating: 3, timeslot: {start: 480}};
        var fakeStarsElement = document.createElement('div');
        var actual = loadStars(fakeStarsElement, fakeTutoringSession);

        it("should have the same number of full stars as the rating for that tutoring session", function() {
            expect(actual.childNodes[0].className).toEqual("glyphicon glyphicon-star");
            expect(actual.childNodes[1].className).toEqual("glyphicon glyphicon-star");
            expect(actual.childNodes[2].className).toEqual("glyphicon glyphicon-star");
        });

        it("should have the remaining stars empty if the rating is less than 5", function() {
            expect(actual.childNodes[3].className).toEqual("glyphicon glyphicon-star-empty");
            expect(actual.childNodes[4].className).toEqual("glyphicon glyphicon-star-empty");
        });
        
    });

    describe("when loadStars is called and the tutoring session has not been rated", function() {
        var fakeTutoringSession = {tutorEmail: "tester@gmail.com", rated: false, rating: 3, timeslot: {start: 480}};
        var fakeStarsElement = document.createElement('div');
        var actual = loadStars(fakeStarsElement, fakeTutoringSession);

        it("should have all stars empty", function() {
            expect(actual.childNodes[0].className).toEqual("glyphicon glyphicon-star-empty");
            expect(actual.childNodes[1].className).toEqual("glyphicon glyphicon-star-empty");
            expect(actual.childNodes[2].className).toEqual("glyphicon glyphicon-star-empty");
            expect(actual.childNodes[3].className).toEqual("glyphicon glyphicon-star-empty");
            expect(actual.childNodes[4].className).toEqual("glyphicon glyphicon-star-empty");
        });
        
    });

    describe("when rateTutor is called", function() {
        var fakeTutoringSession = {tutorEmail: "tester@gmail.com", studentEmail: "test@gmail.com"};
        var fakeRating = 5;

        it("should trigger the fetch function", function() {
            spyOn(window, 'fetch').and.callThrough();
            rateTutor(fakeTutoringSession, fakeRating);
            expect(window.fetch).toHaveBeenCalled();
        });
    });

});
