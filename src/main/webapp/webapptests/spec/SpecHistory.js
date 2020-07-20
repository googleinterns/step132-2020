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
        it("should trigger the fetch function", function() {
            spyOn(window, 'fetch').and.returnValue(Promise.resolve({json: () => Promise.resolve([])}));
            getTutoringSessionHistory();
            expect(window.fetch).toHaveBeenCalledWith('/history', {method: 'GET'});
            expect(window.fetch).toHaveBeenCalled();
        });
    });

    describe("when user tries to access someone else's confirmation page", function() {
        var mockWindow = {location: {href: "history.html"}};
        var response = {redirected: true, url: "/homepage.html"};
        it("should redirect user to homepage", async function() {
            spyOn(window, 'alert');
            spyOn(window, 'fetch').and.returnValue(Promise.resolve(response));
            await getTutoringSessionHistoryHelper(mockWindow);
            expect(window.alert).toHaveBeenCalledWith('You must be signed in to view history.');
            expect(mockWindow.location.href).toBe('/homepage.html');
        });
    });

    describe("when a tutoring session box is created", function() {
        var tutoringSession = {tutorID: "123", timeslot: {start: 480, date: {month: 4, dayOfMonth: 18, year: 2020}}};
        var actual = createTutoringSessionBox(tutoringSession);

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

        it("should have an h3 element as the child of the second div element inside each list item element", function() {
            expect(actual.childNodes[1].childNodes[0].tagName).toEqual("H3");
        });

        it("should have a div element as the child of each third div element", function() {
            expect(actual.childNodes[2].childNodes[0].tagName).toEqual("DIV");
        });

        it("should have the inner HTML of the h3 tag equal to to the email of the tutor", function() {
            var tutor = {email: "tester@gmail.com"};
            spyOn(window, "fetch").and.returnValues(Promise.resolve({json: () => Promise.resolve(user)}), Promise.resolve({json: () => Promise.resolve(tutor)}));

            const tutorElement = document.createElement('h3');
            setTutorEmail(tutorElement, "123").then(() => {
                expect(tutorElement.innerHTML).toEqual("Tutoring Session with tester@gmail.com");
            });
        });

        it("should have the inner HTML of the h3 tag equal to the time slot for that tutoring session", function() {
            expect(actual.childNodes[1].childNodes[0].innerHTML).toEqual("8:00am on May 18, 2020");
        });

        it("should have 5 stars inside the third div element", function() {
            expect(actual.childNodes[2].childNodes[0].childNodes.length).toEqual(5);
        });

        it("should have all stars inside the third div element of tag SPAN", function() {
            expect(actual.childNodes[2].childNodes[0].childNodes[0].tagName).toEqual("SPAN");
            expect(actual.childNodes[2].childNodes[0].childNodes[1].tagName).toEqual("SPAN");
            expect(actual.childNodes[2].childNodes[0].childNodes[2].tagName).toEqual("SPAN");
            expect(actual.childNodes[2].childNodes[0].childNodes[3].tagName).toEqual("SPAN");
            expect(actual.childNodes[2].childNodes[0].childNodes[4].tagName).toEqual("SPAN");
        });

        it("should have all stars inside the third div element of class glyphicon glyphicon-star", function() {
            expect(actual.childNodes[2].childNodes[0].childNodes[0].className).toContain("glyphicon glyphicon-star");
            expect(actual.childNodes[2].childNodes[0].childNodes[1].className).toContain("glyphicon glyphicon-star");
            expect(actual.childNodes[2].childNodes[0].childNodes[2].className).toContain("glyphicon glyphicon-star");
            expect(actual.childNodes[2].childNodes[0].childNodes[3].className).toContain("glyphicon glyphicon-star");
            expect(actual.childNodes[2].childNodes[0].childNodes[4].className).toContain("glyphicon glyphicon-star");
        });
    });

    describe("when loadStars is called and the tutoring session has been rated", function() {
        var fakeTutoringSession = {tutorID: "123", rated: true, rating: 3, timeslot: {start: 480}};
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
        var fakeTutoringSession = {tutorID: "123", rated: false, rating: 3, timeslot: {start: 480}};
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
        var fakeTutoringSession = {id: "1"};
        var fakeRating = 5;

        it("should trigger the fetch function", function() {
            spyOn(window, 'fetch').and.returnValue(Promise.resolve({json: () => Promise.resolve([])}));
            rateTutor(fakeTutoringSession, fakeRating);
            expect(window.fetch).toHaveBeenCalled();
        });
    });

});
