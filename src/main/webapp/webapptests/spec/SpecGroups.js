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

describe("Groups", function() {

    describe("when a page needs to get redirected to the search results for a given group", function() {
        var testSearchBox = document.createElement("input");
        testSearchBox.setAttribute("type", "search");
        testSearchBox.setAttribute("value", "Test Tester") ;

        var mockWindow = {location: {href: "groups.html"}};

        it("should change window.location.href to the correct groups url", function() {
            spyOn(document, "getElementById").withArgs("search-box-results").and.returnValue("Test");
            redirectToResultsGroupsHelper(document, mockWindow);
            expect(mockWindow.location.href).toEqual("groups.html?group=undefined");
        });
    });

    describe("when groups results page is loaded", function() {
        var groups = [{"name": "Test", "topic": "test", "description": "test", "id": "1"}, 
                        {"name": "Test", "topic": "test", "description": "test", "id": "3"}];

        var groupContainer = document.createElement("div");
        groupContainer.id = "groups";

        var mockWindow = {location: {href: "groups.html?group=Test", search: "?group=Test"}};

        it("should create result elements inside groupContainer", async function() {
            spyOn(window, "fetch").and.returnValues(Promise.resolve({json: () => Promise.resolve(groups)}));

            spyOn(document, "getElementById").and.returnValues(groupContainer);

            await getSearchGroupResultsHelper(document, mockWindow);

            expect(window.fetch).toHaveBeenCalledTimes(1);
            expect(window.fetch.calls.allArgs()[0][0]).toEqual("/manage-groups?group=Test");

            //one for the number of results label + 2 for the number of results in testResults
            expect(groupContainer.childNodes.length).toEqual(3);
            expect(groupContainer.childNodes[0].innerText).toContain("Found 2 groups named Test");
        });

    });

    describe("when a group result is created", function() {
        var result = {"name": "Test", "topic": "test", "description": "test", "id": "1"};
        var element = createGroupResult(result);

        it("should create div for result element", function() {
            expect(element.tagName).toEqual("DIV");
        });

        it("should create h3 element inside result element for name", function() {
            expect(element.childNodes[0].tagName).toEqual("H3");
            expect(element.childNodes[0].innerText).toContain("Test");
        });

        it("should create h4 element inside result element for topic", function() {
            expect(element.childNodes[1].tagName).toEqual("H4");
            expect(element.childNodes[1].innerText).toContain("test");
        });

        it("should create h5 element inside result element for description", function() {
            expect(element.childNodes[2].tagName).toEqual("H5");
            expect(element.childNodes[2].innerText).toContain("test");
        });

        it("should create anchor element inside result element for group link", function() {
            expect(element.childNodes[3].tagName).toEqual("A");
            expect(element.childNodes[3].innerText).toContain("View");
            expect(element.childNodes[3].href).toContain("/group.html?groupId=1");
        });

    });

    describe("when user selects other in the topic selection form", function() {
        var input = document.createElement("input");
        input.id = "other-topic";

        it("should display the input and make the input required", function() {
            spyOn(document, "getElementById").and.returnValue(input);

            checkOtherSelected("other");

            expect(input.style.display).toBe("block");
            expect(input.hasAttribute("required")).toBe(true);
            expect(input.hasAttribute("aria-required")).toBe(true);
            expect(input.getAttribute("aria-required")).toBe("true");
        });
    });

    describe("when user had selected other topic but then selected something else", function() {
        var input = document.createElement("input");
        input.id = "other-topic";
        input.style.display = 'block';
        input.setAttribute("required", "");
        input.setAttribute("aria-required", true);

        it("should not display the other topic input", function() {
            spyOn(document, "getElementById").and.returnValue(input);

            checkOtherSelected("Math");

            expect(input.style.display).toBe("none");
            expect(input.hasAttribute("required")).toBe(false);
            expect(input.hasAttribute("aria-required")).toBe(false);
        });
    });
    
});
