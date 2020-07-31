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

describe("My Students", function() {

    describe("when the tutor requests to see their student", function() {
        var mockWindow = {location: {href: "my-students.html"}};

        it("should trigger the fetch function", function() {
            spyOn(window, 'fetch').and.returnValue(Promise.resolve({json: () => Promise.resolve([])}));
            getMyStudents();
            expect(window.fetch).toHaveBeenCalledWith('/my-students', {method: 'GET'});
            expect(window.fetch).toHaveBeenCalled();
        });
    });

    describe("when user tries to access someone else's students page", function() {
        var mockWindow = {location: {href: "my-students.html"}};
        var response = {redirected: true, url: "/homepage.html"};
        it("should redirect user to homepage", async function() {
            spyOn(window, 'alert');
            spyOn(window, 'fetch').and.returnValue(Promise.resolve(response));
            var studentsContainer = document.createElement("div");
            spyOn(document, 'getElementById').and.returnValue(studentsContainer);
            await getMyStudentsHelper(mockWindow);
            expect(window.alert).toHaveBeenCalledWith('You must be signed in to view your students.');
            expect(mockWindow.location.href).toBe('/homepage.html');
        });
    });

    describe("when a student box is created", function() {
        var student = {name: "Tester", email: "tester@example.com", learning: ["math", "english", " "], userId: "123"};
        var actual = createStudentBox(student);

        it("should return a div item element", function() {
            expect(actual.tagName).toEqual("DIV");
        });

        it("should have the correct element type for each children of the div", function() {
            expect(actual.childNodes[0].tagName).toEqual("H3");
            expect(actual.childNodes[1].tagName).toEqual("H6");
            expect(actual.childNodes[2].tagName).toEqual("P");
            expect(actual.childNodes[3].tagName).toEqual("A");
        });

        it("should name set to Tester", function() {
            expect(actual.childNodes[0].innerHTML).toEqual("Tester");
        });

        it("should email set to tester@example.com", function() {
            expect(actual.childNodes[1].innerHTML).toEqual("tester@example.com");
        });

        it("should set learning to Math, English", function() {
            expect(actual.childNodes[2].innerHTML).toEqual("Learning: math, english");
        });

        it("should track progress link's inner html set to Track Progress", function() {
            expect(actual.childNodes[3].innerHTML).toEqual("Track Progress");
        });

        it("should track progress link's to /progress.html?studentID=123", function() {
            expect(actual.childNodes[3].href).toContain("/profile.html?userID=123");
        });
    });

});
