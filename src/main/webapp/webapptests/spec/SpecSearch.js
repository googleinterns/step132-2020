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
        var tutors = [{"name": "Tutor 1", "email": "tutor1@gmail.com", "skills": ["Math", "History"], "userId":"1", "ratingCount": 1, "ratingSum": 5}, 
                            {"name": "Tutor 2", "email": "tutor2@gmail.com", "skills": ["Math", "History"], "userId":"2", "ratingCount": 2, "ratingSum": 8}];

        var lists = [{name: "list 1", books: ["book 1", "book 2"], topic: "math"}, {name: "list 2", books: ["book 1", "book 2"], topic: "math"}];

        var books = {"totalItems": 2, "items": [{"volumeInfo": {"infoLink": "", "title": "Book 1", "authors": ["Author 1"], "subject": "Math", "imageLinks": {"smallThumbnail": ""}}},
                    {"volumeInfo": {"infoLink": "", "title": "Book 2", "authors": ["Author 2"], "subject": "Math", "imageLinks": {"smallThumbnail": ""}}}]};

        var tutorsLabel = document.createElement("p");
        tutorsLabel.id = "num-tutor-results";

        var tutorContainer = document.createElement("div");
        tutorContainer.id = "tutors-container";

        var listContainer = document.createElement("div");
        listContainer.id = "lists-container";

        var listsLabel = document.createElement("p");
        listsLabel.id = "num-lists-results"

        var booksLabel = document.createElement("p");
        booksLabel.id = "num-book-results";

        var bookContainer = document.createElement("div");
        bookContainer.id = "books-container";

        var panel = document.createElement("div");

        var mockWindow = {location: {href: "search-results.html?topic=math", search: "?topic=math"}};

        it("should create result elements inside tutorContainer, listContainer, and bookContainer", async function() {
            spyOn(window, "fetch").and.returnValues(Promise.resolve({json: () => Promise.resolve(tutors)}), 
                                                    Promise.resolve({json: () => Promise.resolve(lists)}), 
                                                    Promise.resolve({json: () => Promise.resolve(books)}));

            spyOn(document, "getElementById").and.returnValues(tutorsLabel, tutorContainer, listsLabel, panel, panel, panel, listContainer, booksLabel, bookContainer);

            spyOn(window, "createTutorListElement").and.returnValues(document.createElement("div"), document.createElement("div"));

            await getSearchResultsHelper(mockWindow);

            expect(window.fetch).toHaveBeenCalledTimes(3);
            expect(window.fetch.calls.allArgs()[0][0]).toEqual("/search?topic=math&sort-type=alpha");
            expect(window.fetch.calls.allArgs()[1][0]).toEqual("/lists?topic=math");
            expect(window.fetch.calls.allArgs()[2][0]).toEqual("/books?topic=math");

            expect(tutorContainer.childNodes.length).toEqual(2);
            expect(tutorsLabel.innerText).toContain("Found 2 tutors for math");

            expect(listContainer.childNodes.length).toEqual(2);
            expect(listsLabel.innerText).toContain("Found 2 book playlists for math");

            expect(bookContainer.childNodes.length).toEqual(2);
            expect(booksLabel.innerText).toContain("Found 2 books for math");
        });

    });

    describe("when tabs are clicked", function() {
        var tutorsTab = document.createElement("li");
        var tutorsLink = document.createElement("a");
        tutorsLink.innerText = "Tutors";
        tutorsTab.appendChild(tutorsLink);

        var booksTab = document.createElement("li");
        var booksLink = document.createElement("a");
        booksLink.innerText = "Books";
        booksTab.appendChild(booksLink);

        var tutorContainer = document.createElement("div");
        tutorContainer.id = "tutors";

        var bookContainer = document.createElement("div");
        bookContainer.id = "books";

        beforeEach(function() {
            tutorsTab.classList.remove("active");
            tutorContainer.classList.remove("active-container");
            booksTab.classList.remove("active");
            bookContainer.classList.remove("active-container");

        });

        describe("when Tutors page is active and Books tab is clicked", function() {
            tutorsTab.classList.add("active");
            tutorContainer.classList.add("active-container");

            it("should make the Books tab active and remove active class from Tutors", function() {
                spyOn(document, "getElementsByClassName").and.returnValues([tutorsTab], [tutorContainer]);
                spyOn(document, "getElementById").and.returnValue(bookContainer);

                switchTab(booksLink);
                expect(booksTab.classList).toContain("active");
                expect(tutorsTab.classList).not.toContain("active");
                expect(bookContainer.classList).toContain("active-container");
                expect(tutorContainer.classList).not.toContain("active-container");
            });

        });

        describe("when Books page is active and Tutors tab is clicked", function() {
            booksTab.classList.add("active");
            bookContainer.classList.add("active-container");

            it("should make the Tutors tab active and remove active class from Books", function() {
                spyOn(document, "getElementsByClassName").and.returnValues([booksTab], [bookContainer]);
                spyOn(document, "getElementById").and.returnValue(tutorContainer);

                switchTab(tutorsLink);
                expect(tutorsTab.classList).toContain("active");
                expect(booksTab.classList).not.toContain("active");
                expect(tutorContainer.classList).toContain("active-container");
                expect(bookContainer.classList).not.toContain("active-container");
            });

        });

        describe("when Tutors page is active and Tutors tab is clicked", function() {
            tutorsTab.classList.add("active");
            tutorContainer.classList.add("active-container");

            it("should stay on Tutors page", function() {
                spyOn(document, "getElementsByClassName").and.returnValues([tutorsTab], [tutorContainer]);
                spyOn(document, "getElementById").and.returnValue(tutorContainer);

                switchTab(tutorsLink);
                expect(tutorsTab.classList).toContain("active");
                expect(booksTab.classList).not.toContain("active");
                expect(tutorContainer.classList).toContain("active-container");
                expect(bookContainer.classList).not.toContain("active-container");
            });

        });

        describe("when Books page is active and Books tab is clicked", function() {
            booksTab.classList.add("active");
            bookContainer.classList.add("active-container");

            it("should stay on Books page", function() {
                spyOn(document, "getElementsByClassName").and.returnValues([booksTab], [bookContainer]);
                spyOn(document, "getElementById").and.returnValue(bookContainer);

                switchTab(booksLink);
                expect(booksTab.classList).toContain("active");
                expect(tutorsTab.classList).not.toContain("active");
                expect(bookContainer.classList).toContain("active-container");
                expect(tutorContainer.classList).not.toContain("active-container");
            });

        });

    });

    describe("when book lists are fetched and there are 0 results", function() {
        var tutorBooksPanel = document.createElement("div");
        var collapseOne = document.createElement("div");

        var googleBooksPanel = document.createElement("div");
        var collapseTwo = document.createElement("div");

        var listsLabel = document.createElement("p");

        it("should open google books panel", async function() {
            spyOn(window, "fetch").and.returnValue(Promise.resolve({json: () => Promise.resolve([])})); 
            spyOn(document, "getElementById").and.returnValues(listsLabel, collapseTwo, collapseTwo, googleBooksPanel); 
            await getTutorBooks("math");
            expect(listsLabel.innerText).toBe("No lists found for math.");
            expect(googleBooksPanel.getAttribute("aria-expanded")).toBe("true");
            expect(collapseTwo.getAttribute("aria-expanded")).toBe("true");
            expect(collapseTwo.classList).toContain("in");
            expect(tutorBooksPanel.hasAttribute("aria-expanded")).toBe(false);
            expect(collapseOne.hasAttribute("aria-expanded")).toBe(false);
            expect(collapseOne.classList).not.toContain("in");
        });

    });

    describe("when book lists are fetched and there are 0 results", function() {
        var tutorBooksPanel = document.createElement("div");
        var collapseOne = document.createElement("div");

        var googleBooksPanel = document.createElement("div");
        var collapseTwo = document.createElement("div");

        var listsLabel = document.createElement("p");
        var listContainer = document.createElement("div");

        it("should open tutor books panel", async function() {
            spyOn(window, "fetch").and.returnValue(Promise.resolve({json: () => Promise.resolve(["1", "2"])})); 
            spyOn(document, "getElementById").and.returnValues(listsLabel, collapseOne, collapseOne, tutorBooksPanel, listContainer); 
            spyOn(window, "createTutorListElement").and.returnValues(document.createElement("div"), document.createElement("div"));
            await getTutorBooks("math");
            expect(tutorBooksPanel.getAttribute("aria-expanded")).toBe("true");
            expect(collapseOne.getAttribute("aria-expanded")).toBe("true");
            expect(collapseOne.classList).toContain("in");
            expect(googleBooksPanel.hasAttribute("aria-expanded")).toBe(false);
            expect(collapseTwo.hasAttribute("aria-expanded")).toBe(false);
            expect(collapseTwo.classList).not.toContain("in");
        });

    });

    describe("when a tutor result is created", function() {
        var result = {"name": "Tutor 1", "email": "tutor1@gmail.com", "skills": ["Math", " ", "History"], "userId": "123", "ratingCount": 1, "ratingSum": 5};
        var element = createTutorResult(result);

        it("should create div for result element", function() {
            expect(element.tagName).toEqual("DIV");
        });

        it("should create div for header row that has name, profile link, and rating inside", function() {
            expect(element.childNodes[0].tagName).toEqual("DIV");
            expect(element.childNodes[0].childNodes.length).toEqual(3);
        });

        it("should create h3 element inside header row for name", function() {
            expect(element.childNodes[0].childNodes[0].tagName).toEqual("H3");
            expect(element.childNodes[0].childNodes[0].innerText).toContain("Tutor 1");
        });

        it("should create anchor element with profile icon inside header for link to user's profile", function() {
            expect(element.childNodes[0].childNodes[1].tagName).toEqual("A");
            expect(element.childNodes[0].childNodes[1].getAttribute("href")).toEqual("/profile.html?userID=123");
        });

        it("should create div element inside header row for rating", function() {
            expect(element.childNodes[0].childNodes[2].tagName).toEqual("DIV");
        });

        it("should create a span element for each star inside of rating element", function() {
            expect(element.childNodes[0].childNodes[2].childNodes[0].tagName).toEqual("SPAN");
            expect(element.childNodes[0].childNodes[2].childNodes[1].tagName).toEqual("SPAN");
            expect(element.childNodes[0].childNodes[2].childNodes[2].tagName).toEqual("SPAN");
            expect(element.childNodes[0].childNodes[2].childNodes[3].tagName).toEqual("SPAN");
            expect(element.childNodes[0].childNodes[2].childNodes[4].tagName).toEqual("SPAN");
        });

        it("should have all stars inside the rating element of class glyphicon glyphicon-star", function() {
            expect(element.childNodes[0].childNodes[2].childNodes[0].className).toEqual("glyphicon glyphicon-star");
            expect(element.childNodes[0].childNodes[2].childNodes[1].className).toEqual("glyphicon glyphicon-star");
            expect(element.childNodes[0].childNodes[2].childNodes[2].className).toEqual("glyphicon glyphicon-star");
            expect(element.childNodes[0].childNodes[2].childNodes[3].className).toEqual("glyphicon glyphicon-star");
            expect(element.childNodes[0].childNodes[2].childNodes[4].className).toEqual("glyphicon glyphicon-star");
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
            expect(element.childNodes[3].href).toContain("/availability.html?tutorID=123");
        });

    });

    describe("when a book result is created", function() {
        var result = {"infoLink": "", "title": "Book 1", "authors": ["Author 1"], "subject": "Math", "imageLinks": {"smallThumbnail": ""}};
        var element = createBookResult(result);

        it("should create anchor element for result element", function() {
            expect(element.tagName).toEqual("A");
        });

        it("should create img element inside result element for thumbnail", function() {
            expect(element.childNodes[0].tagName).toEqual("IMG");
            expect(element.childNodes[0].src).toContain("");
        });

        it("should create p element inside result element for title", function() {
            expect(element.childNodes[1].tagName).toEqual("P");
            expect(element.childNodes[1].innerText).toContain("Book 1");
        });

        it("should create p element inside result element for author", function() {
            expect(element.childNodes[2].tagName).toEqual("P");
            expect(element.childNodes[2].innerText).toContain("by Author 1");
        });

    });

    describe("when a list result is created", function() {
        var result = {name: "list 1", books: ["book 1", "book 2"], topic: "math", tutorID: "123"};
        var googleBookResults = {"totalItems": 1, "items": [{"volumeInfo": {"infoLink": "", "title": "Book 1", "authors": ["Author 1"], "subject": "Math", "imageLinks": {"smallThumbnail": ""}}}]};
        var element;

        beforeAll(function() {
            spyOn(window, 'fetch').and.returnValue(Promise.resolve({json: () => Promise.resolve(googleBookResults)}));
            element = createTutorListElement(result);
        });

        it("should create div for result element", function() {
            expect(element.tagName).toEqual("DIV");
        });

        it("should create h4 element inside result element for list name", function() {
            expect(element.childNodes[0].tagName).toEqual("H4");
            expect(element.childNodes[0].innerText).toContain("list 1");
        });

        it("should create anchor element inside result element and set href to tutor profile", function() {
            expect(element.childNodes[1].tagName).toEqual("A");
            expect(element.childNodes[1].getAttribute("href")).toEqual("/profile.html?userID=123");
        });

        it("should create div element inside result element to display books in list", function() {
            expect(element.childNodes[2].tagName).toEqual("DIV");
            expect(element.childNodes[2].childNodes.length).toEqual(2);
        });

    });

    describe("when setTutorNameInLink is called", function() {
        var tutor = {name: "Test"};

        it("should set anchor element to tutor name", async function() {
            spyOn(window, 'fetch').and.returnValue(Promise.resolve({json: () => Promise.resolve(tutor)}));
            const tutorElement = document.createElement("a");
            await setTutorNameInLink(tutorElement, "123").then(() => {
                expect(tutorElement.innerText).toEqual("Test");
            });
        });
    });

    describe("when sort type is changed", function() {
        var select = document.createElement("select");
        var rating = document.createElement("option");
        rating.value = "rating";

        rating.selected = 'selected';

        var availability = document.createElement("option");
        availability.value = "availability";

        select.appendChild(rating);
        select.appendChild(availability);
        
        it("should call getSearchResultsHelper with the correct parameters", function() {
            spyOn(window, "getSearchResultsHelper");
            handleTutorSort(select);
            expect(window.getSearchResultsHelper).toHaveBeenCalledTimes(1);
            expect(window.getSearchResultsHelper.calls.allArgs()[0][1]).toEqual("rating");

        });
    });

    describe("when the load more button is clicked", function() {
        numResultsLoaded = 2;

        var bookContainer = document.createElement("div");
        bookContainer.id = "books-container";

        var loadMoreButton = document.createElement("button");
        loadMoreButton.style.display = "block";
        
        it("should call fetch with the correct parameter and hide the load more button", async function() {
            spyOn(window, "createBookResult").and.returnValues(document.createElement("div"));
            spyOn(document, "getElementById").and.returnValues(bookContainer, loadMoreButton);
            spyOn(window, "fetch").and.returnValue(Promise.resolve({json: () => Promise.resolve({totalItems: 3, items: [{volumeInfo: ""}]})}));
            await loadMoreBooks("math");
            expect(window.fetch.calls.allArgs()[0][0]).toEqual("/books?topic=math&startIndex=2");
            expect(loadMoreButton.style.display).toBe("none");

        });
    });
    
});
