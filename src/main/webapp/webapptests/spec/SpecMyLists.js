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

describe("My Lists", function() {

    describe("when a tutor requests to see their lists", function() {
        var lists = [{name: "list 1", books: ["book 1", "book 2"], topic: "math"}, {name: "list 2", books: ["book 1", "book 2"], topic: "science"}];
        var listLinkContainer = document.createElement("div");
        listLinkContainer.id = "list-names";

        var listContainer = document.createElement("div");
        listContainer.id = "list-info-container";

        it("should trigger the fetch function and create list links and info elements", async function() {
            spyOn(window, 'fetch').and.returnValue(Promise.resolve({json: () => Promise.resolve(lists)}));
            spyOn(document, "getElementById").and.returnValues(listLinkContainer, listContainer);
            await getListsHelper(window, '/manage-lists');
            expect(window.fetch).toHaveBeenCalledWith('/manage-lists', {method: 'GET'});
            expect(window.fetch).toHaveBeenCalled();

            expect(listLinkContainer.childNodes.length).toEqual(2);
            expect(listContainer.childNodes.length).toEqual(2);
        });
    });

    describe("when user tries to access someone else's lists page", function() {
        var mockWindow = {location: {href: "my-lists.html"}};
        var response = {redirected: true, url: "/homepage.html"};
        it("should redirect user to homepage", async function() {
            spyOn(window, 'alert');
            spyOn(window, 'fetch').and.returnValue(Promise.resolve(response));
            await getListsHelper(mockWindow, '/manage-lists');
            expect(window.alert).toHaveBeenCalledWith('You must be signed in to manage lists.');
            expect(mockWindow.location.href).toBe('/homepage.html');
        });
    });

    describe("when a list link is created", function() {
        var list = {name: "list 1", books: ["book 1", "book 2"], topic: "math"};
        var actual = createListNameLink(list);

        it("should return a div item element", function() {
            expect(actual.tagName).toEqual("A");
        });

        it("should set the text to the name of the list", function() {
            expect(actual.innerText).toEqual("list 1");
        });

    });

    describe("when a list info box is created", function() {
        var list = {name: "list 1", books: ["book 1 by author 1", "book 2 by author 2"], topic: "math"};
        var googleBookResults = {"totalItems": 1, "items": [{"volumeInfo": {"infoLink": "", "title": "Book 1", "authors": ["Author 1"], "subject": "Math", "imageLinks": {"smallThumbnail": ""}}}]};
        var element;

        beforeAll(function() {
            spyOn(window, 'fetch').and.returnValue(Promise.resolve({json: () => Promise.resolve(googleBookResults)}));
            element = createListElement(list);
        });
        
        it("should call fetch with the correct parameter", function() {
            expect(window.fetch).toHaveBeenCalledWith("/books?title=book%201&author=author%201");
        });

        it("should return a div item element with display none", function() {
            expect(element.tagName).toEqual("DIV");
            expect(element.style.display).toEqual("none");
        });

        it("should create h3 element inside result element for list name", function() {
            expect(element.childNodes[0].tagName).toEqual("H3");
            expect(element.childNodes[0].innerText).toContain("list 1");
        });

        it("should create p element inside result element for topic", function() {
            expect(element.childNodes[1].tagName).toEqual("P");
            expect(element.childNodes[1].innerText).toContain("Math");
        });

        it("should create ul element inside result element for books", function() {
            expect(element.childNodes[2].tagName).toEqual("UL");
        });

    });

    describe("when a book is not found through Google Books and is displayed", function() {
        var element = createNoGoogleBookResult("book 1 by author 1");

        it("should return a div item element with display none", function() {
            expect(element.tagName).toEqual("DIV");
        });

        it("should create img element inside result element for thumbnail", function() {
            expect(element.childNodes[0].tagName).toEqual("IMG");
            expect(element.childNodes[0].src).toContain("/images/book-cover.png");
        });

        it("should create p element inside result element for title and author", function() {
            expect(element.childNodes[1].tagName).toEqual("P");
            expect(element.childNodes[1].innerText).toContain("book 1 by author 1");
        });

    });

    describe("when user clicks on a list link", function() {
        var lists = [{name: "list 1", books: ["book 1", "book 2"], topic: "math"}, {name: "list 2", books: ["book 1", "book 2"], topic: "science"}];
        
        list1Link = createListNameLink(lists[0]);
        list2Link = createListNameLink(lists[1]);

        list1Info = createListElement(lists[0]);
        list2Info = createListElement(lists[1]);

        beforeEach(function() {
            list1Link.classList.remove("active");
            list1Info.classList.remove("active-list");
            list2Link.classList.remove("active");
            list2Info.classList.remove("active-list");

        });

        describe("when pages loads and list 1 link is clicked", function() {
            it("should make the list 1 tab active", function() {
                spyOn(document, "getElementsByClassName").and.returnValues([], []);
                spyOn(document, "getElementById").and.returnValue(list1Info);

                switchList(list1Link);
                expect(list1Link.classList).toContain("active");
                expect(list2Link.classList).not.toContain("active");
                expect(list1Info.classList).toContain("active-list");
                expect(list2Info.classList).not.toContain("active-list");
            });

        });

        describe("when list 1 is active and list 2 is clicked", function() {
            list1Link.classList.add("active");
            list1Info.classList.add("active-list");

            it("should make the list 2 tab active", function() {
                spyOn(document, "getElementsByClassName").and.returnValues([list1Info], [list1Link]);
                spyOn(document, "getElementById").and.returnValue(list2Info);

                switchList(list2Link);
                expect(list2Link.classList).toContain("active");
                expect(list1Link.classList).not.toContain("active");
                expect(list2Info.classList).toContain("active-list");
                expect(list1Info.classList).not.toContain("active-list");
            });

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
