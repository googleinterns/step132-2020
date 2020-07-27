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

describe("Search Users", function() {

    describe("when a page needs to get redirected to search user results", function() {
        var testSearchBox = document.createElement("input");
        testSearchBox.setAttribute("type", "search");
        testSearchBox.setAttribute("value", "Test Tester") ;

        var mockWindow = {location: {href: "homepage.html"}};

        it("should change window.location.href to the correct search user results url", function() {
            spyOn(document, "getElementById").withArgs("searched-name").and.returnValue("Test Tester");
            redirectToResultsUsersHelper(document, mockWindow);
            expect(mockWindow.location.href).toEqual("search-user-results.html?name=Test Tester");
        });
    });

    describe("when search user results page is loaded", function() {
        var tutors = [{"name": "Test Tester", "userId":"1"}, 
                        {"name": "Tester Test", "userId":"2"}];

        var userContainer = document.createElement("div");
        userContainer.id = "users";

        var mockWindow = {location: {href: "search-user-results.html?name=Test", search: "?name=Test"}};

        it("should create result elements inside tutorContainer", async function() {
            spyOn(window, "fetch").and.returnValues(Promise.resolve({json: () => Promise.resolve(tutors)}));

            spyOn(document, "getElementById").and.returnValues(userContainer);

            await getSearchUserResultsHelper(document, mockWindow);

            expect(window.fetch).toHaveBeenCalledTimes(1);
            expect(window.fetch.calls.allArgs()[0][0]).toEqual("/search-user?name=Test");

            //one for the number of results label + 2 for the number of results in testResults
            expect(userContainer.childNodes.length).toEqual(3);
            expect(userContainer.childNodes[0].innerText).toContain("Found 2 users named Test");
        });

    });

    describe("when a user result is created", function() {
        var result = {"name": "Test Tester", "userId":"1"};
        var element = createUserResult(result);

        it("should create div for result element", function() {
            expect(element.tagName).toEqual("DIV");
        });

        it("should create h3 element inside result element for name", function() {
            expect(element.childNodes[0].tagName).toEqual("H3");
            expect(element.childNodes[0].innerText).toContain("Test");
        });

        it("should create anchor element inside result element for profile link", function() {
            expect(element.childNodes[1].tagName).toEqual("A");
            expect(element.childNodes[1].innerText).toContain("Profile");
            expect(element.childNodes[1].href).toContain("/profile.html?userID=1");
        });

    });
    
});
