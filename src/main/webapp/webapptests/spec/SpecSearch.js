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
                    {"volumeInfo": {"infoLink": "", "title": "Book 2", "authors": ["Author 2"], "subject": "Math", "imageLinks": {"smallThumbnail": ""}}}]}

        var tutorsLabel = document.createElement("p");
        tutorsLabel.id = "num-tutor-results";

        var tutorContainer = document.createElement("div");
        tutorContainer.id = "tutors-container";

        var listContainer = document.createElement("div");
        listContainer.id = "lists-container";

        var booksLabel = document.createElement("p");
        booksLabel.id = "num-book-results";

        var bookContainer = document.createElement("div");
        bookContainer.id = "books-container";

        var mockWindow = {location: {href: "search-results.html?topic=math", search: "?topic=math"}};

        it("should create result elements inside tutorContainer, listContainer, and bookContainer", async function() {
            spyOn(window, "fetch").and.returnValues(Promise.resolve({json: () => Promise.resolve(tutors)}), Promise.resolve({json: () => Promise.resolve(lists)}), Promise.resolve({json: () => Promise.resolve(books)}));

            spyOn(document, "getElementById").and.returnValues(tutorsLabel, tutorContainer, listContainer, booksLabel, bookContainer);

            spyOn(window, "createTutorListElement").and.returnValue(document.createElement("div"));

            await getSearchResultsHelper(mockWindow);

            expect(window.fetch).toHaveBeenCalledTimes(3);
            expect(window.fetch.calls.allArgs()[0][0]).toEqual("/search?topic=math&sort-type=alpha");
            expect(window.fetch.calls.allArgs()[1][0]).toEqual("/lists?topic=math");
            expect(window.fetch.calls.allArgs()[2][0]).toContain("https://www.googleapis.com/books/v1/volumes");

            expect(tutorContainer.childNodes.length).toEqual(2);
            expect(tutorsLabel.innerText).toContain("Found 2 tutors for math");

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

    describe("when a tutor result is created", function() {
        var result = {"name": "Tutor 1", "email": "tutor1@gmail.com", "skills": ["Math", " ", "History"], "userId": "123", "ratingCount": 1, "ratingSum": 5};
        var element = createTutorResult(result);

        it("should create div for result element", function() {
            expect(element.tagName).toEqual("DIV");
        });

        it("should create h3 element inside result element for name", function() {
            expect(element.childNodes[0].tagName).toEqual("H3");
            expect(element.childNodes[0].innerText).toContain("Tutor 1");
        });

        it("should create anchor element inside name element for link to user's profile", function() {
            expect(element.childNodes[0].childNodes[0].tagName).toEqual("A");
            expect(element.childNodes[0].childNodes[0].href).toContain("/profile.html?userID=123");
        });

        it("should create div element inside result element for rating", function() {
            expect(element.childNodes[1].tagName).toEqual("DIV");
        });

        it("should create a span element for each star inside of rating element", function() {
            expect(element.childNodes[1].childNodes[0].tagName).toEqual("SPAN");
            expect(element.childNodes[1].childNodes[1].tagName).toEqual("SPAN");
            expect(element.childNodes[1].childNodes[2].tagName).toEqual("SPAN");
            expect(element.childNodes[1].childNodes[3].tagName).toEqual("SPAN");
            expect(element.childNodes[1].childNodes[4].tagName).toEqual("SPAN");
        });

        it("should have all stars inside the rating element of class glyphicon glyphicon-star", function() {
            expect(element.childNodes[1].childNodes[0].className).toContain("glyphicon glyphicon-star");
            expect(element.childNodes[1].childNodes[1].className).toContain("glyphicon glyphicon-star");
            expect(element.childNodes[1].childNodes[2].className).toContain("glyphicon glyphicon-star");
            expect(element.childNodes[1].childNodes[3].className).toContain("glyphicon glyphicon-star");
            expect(element.childNodes[1].childNodes[4].className).toContain("glyphicon glyphicon-star");
        });

        it("should create h6 element inside result element for email", function() {
            expect(element.childNodes[2].tagName).toEqual("H6");
            expect(element.childNodes[2].innerText).toContain("tutor1@gmail.com");
        });

        it("should create p element inside result element for skills", function() {
            expect(element.childNodes[3].tagName).toEqual("P");
            expect(element.childNodes[3].innerText).toContain("Skills: Math, History");
        });

        it("should create anchor element inside result element for availability link", function() {
            expect(element.childNodes[4].tagName).toEqual("A");
            expect(element.childNodes[4].innerText).toContain("Availability");
            expect(element.childNodes[4].href).toContain("/availability.html?tutorID=123");
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
        var modalName = document.createElement("div");
        modalName.id = "list-modal-name";
        var modalBody = document.createElement("div");
        modalBody.id = "list-modal-body";
        var element;

        beforeAll(function() { 
            spyOn(document, "getElementById").and.returnValues(modalName, modalBody);
            element = createTutorListElement(result);
        });

        it("should create li for result element", function() {
            expect(element.tagName).toEqual("LI");
        });

        it("should create h4 element inside result element for list name", function() {
            expect(element.childNodes[0].tagName).toEqual("H4");
            expect(element.childNodes[0].innerText).toContain("list 1");
        });

        it("should create anchor element inside result element and set href to tutor profile", function() {
            expect(element.childNodes[1].tagName).toEqual("A");
            expect(element.childNodes[1].getAttribute("href")).toEqual("/profile.html?userID=123");
        });

        it("should set anchor element to tutor name", async function() {
            var tutor = {name: "Test"};
            spyOn(window, "fetch").and.returnValues(Promise.resolve({json: () => Promise.resolve(tutor)}));

            const tutorElement = element.childNodes[1];
            await setTutorNameInLink(tutorElement, "123").then(() => {
                expect(tutorElement.innerText).toEqual("Test");
            });
        });

        it("should create button element inside result element to view list", function() {
            expect(element.childNodes[2].tagName).toEqual("BUTTON");
            expect(element.childNodes[2].innerText).toContain("View List");
        });

        it("should set modal content when view list button is clicked", function() {
            element.childNodes[2].click();
            expect(modalName.innerText).toContain("list 1");
            expect(modalBody.childNodes.length).toBe(2);
            expect(modalBody.childNodes[0].innerText).toBe("book 1");
            expect(modalBody.childNodes[1].innerText).toBe("book 2");
        });

    });

    describe("when sort type is changed to rating", function() {
        var radioButton = document.createElement("input");
        radioButton.value = "rating";
        it("should call getSearchResultsHelper with the correct parameters", function() {
            spyOn(window, "getSearchResultsHelper");
            handleTutorSort(radioButton);
            expect(window.getSearchResultsHelper).toHaveBeenCalledTimes(1);
            expect(window.getSearchResultsHelper.calls.allArgs()[0][1]).toEqual("rating");

        });
    });
    
});
