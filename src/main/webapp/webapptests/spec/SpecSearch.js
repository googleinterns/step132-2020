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

describe("Search", function() {

    describe("when homepage needs to get redirected to search results", function() {
        var testSearchBox = document.createElement("input");
        testSearchBox.setAttribute("type", "text");
        testSearchBox.className = "search-box";
        testSearchBox.setAttribute("value", "math") ;

        var mockWindow = {location: {href: "homepage.html"}};

        it("should change window.location.href to the correct search results url", function() {
            spyOn(document, "getElementsByClassName").withArgs("search-box").and.returnValue([testSearchBox]);
            redirectToResultsHelper(document, mockWindow);
            expect(mockWindow.location.href).toEqual("search-results.html?topic=math");
        });

    });

    describe("when search results page is loaded", function() {
        var testResults = ['{"name": "Tutor 1", "email": "tutor1@gmail.com", "skills": ["Math", "History"]}', 
                            '{"name": "Tutor 2", "email": "tutor2@gmail.com", "skills": ["Math", "History"]}'];

        var resultContainer = document.createElement("div");
        resultContainer.id = "result-container";

        var mockWindow = {location: {href: "search-results.html?topic=math", search: "?topic=math"}};

        it("should create result elements inside resultContainer", async function() {
            spyOn(window, "fetch").withArgs("/search?topic=math").and.returnValue(Promise.resolve({json: () => Promise.resolve(testResults)}));

            spyOn(document, "getElementById").withArgs("result-container").and.returnValue(resultContainer);

            await getSearchResultsHelper(document, mockWindow);
            
            expect(window.fetch).toHaveBeenCalledWith("/search?topic=math");
            //one for the number of results label + 2 for the number of results in testResults
            expect(resultContainer.childNodes.length).toEqual(3);
            expect(resultContainer.childNodes[0].innerText).toContain("Found 2 tutors for math");
        });


    });

    describe("when a search result is created", function() {
        var result = '{"name": "Tutor 1", "email": "tutor1@gmail.com", "skills": ["Math", "History"]}';
        var element = createSearchResult(result);

        it("should create div for result element", function() {
            expect(element.tagName).toEqual("DIV");
        });

        it("should create h3 element inside result element for name", function() {
            expect(element.childNodes[0].tagName).toEqual("H3");
            expect(element.childNodes[0].innerText).toContain("Tutor 1");
        });

        it("should create h6 element inside result element for email", function() {
            expect(element.childNodes[1].tagName).toEqual("H6");
            expect(element.childNodes[1].innerText).toContain("tutor1@gmail.com");
        });

        it("should create p element inside result element for skills", function() {
            expect(element.childNodes[2].tagName).toEqual("P");
            expect(element.childNodes[2].innerText).toContain("Skills: Math, History");
        });

        it("should create anchor element inside result element for availability link", function() {
            expect(element.childNodes[3].tagName).toEqual("A");
            expect(element.childNodes[3].innerText).toContain("Availability");
            expect(element.childNodes[3].href).toContain("/availability.html?tutorID=tutor1@gmail.com");
        });

    });
    
});
