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
        testSearchBox.id = "topic-search-box";
        testSearchBox.setAttribute("value", "math") ;

        var mockWindow = {location: {href: "homepage.html"}};

        it("should change window.location.href to the correct search results url", function() {
            // document.getElementById = jasmine.createSpy("search result").and.returnValue(testSearchBox);
            spyOn(document, "getElementById").withArgs("topic-search-box").and.returnValue(testSearchBox);
            redirectToResultsHelper(document, mockWindow);
            expect(mockWindow.location.href).toEqual("search-results.html?topic=math");
        });

    });

    describe("when search results page is loaded", function() {
        var resultContainer = document.createElement("div");
        resultContainer.id = "result-container";

        var mockWindow = {location: {href: "search-results.html?topic=math", search: "?topic=math"}};
        var mockDocument = {getElementById: (id) => resultContainer};

        it("should set resultContainer's text to results returned by fetch call", async function() {
            spyOn(window, "fetch").and.callFake(function(servletUrl) {
                return Promise.resolve({text: () => Promise.resolve("hello world.")});
            });
            await getSearchResultsHelper(mockDocument, mockWindow);
            
            expect(window.fetch).toHaveBeenCalledWith("/search?topic=math");
            expect(resultContainer.innerHTML).toContain("hello world.");
        });

    });


});
