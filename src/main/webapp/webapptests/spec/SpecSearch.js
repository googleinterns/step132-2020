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
        var tutors = [{"name": "Tutor 1", "email": "tutor1@gmail.com", "skills": ["Math", "History"]}, 
                            {"name": "Tutor 2", "email": "tutor2@gmail.com", "skills": ["Math", "History"]}];

        var books = [{"title": "Book 1", "author": "Author 1", "subject": "Math", "thumbnail": ""},
                    {"title": "Book 2", "author": "Author 2", "subject": "Math", "thumbnail": ""}]

        var tutorContainer = document.createElement("div");
        tutorContainer.id = "tutors";

        var bookContainer = document.createElement("div");
        bookContainer.id = "books";

        var mockWindow = {location: {href: "search-results.html?topic=math", search: "?topic=math"}};

        it("should create result elements inside tutorContainer", async function() {
            spyOn(window, "fetch").and.returnValues(Promise.resolve({json: () => Promise.resolve(tutors)}), Promise.resolve({json: () => Promise.resolve(books)}));

            spyOn(document, "getElementById").and.returnValues(tutorContainer, bookContainer);

            await getSearchResultsHelper(document, mockWindow);

            expect(window.fetch).toHaveBeenCalledTimes(2);
            expect(window.fetch.calls.allArgs()[0][0]).toEqual("/search?topic=math");
            expect(window.fetch.calls.allArgs()[1][0]).toEqual("/books?topic=math");

            //one for the number of results label + 2 for the number of results in testResults
            expect(tutorContainer.childNodes.length).toEqual(3);
            expect(tutorContainer.childNodes[0].innerText).toContain("Found 2 tutors for math");

            //one for the number of results label + div container that contains the book results
            expect(bookContainer.childNodes.length).toEqual(2);
            expect(bookContainer.childNodes[0].innerText).toContain("Found 2 books for math");
            //the div child container should contain the 2 books 
            expect(bookContainer.childNodes[1].childNodes.length).toEqual(2);
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

        describe("when Tutors page is active and Books tab is clicked", function() {
            tutorsTab.classList.add("active");
            tutorContainer.classList.add("active-container");

            booksTab.classList.remove("active");
            bookContainer.classList.remove("active-container");

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

            tutorsTab.classList.remove("active");
            tutorContainer.classList.remove("active-container");

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

            booksTab.classList.remove("active");
            bookContainer.classList.remove("active-container");

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

            tutorsTab.classList.remove("active");
            tutorContainer.classList.remove("active-container");

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
        var result = {"name": "Tutor 1", "email": "tutor1@gmail.com", "skills": ["Math", "History"]};
        var element = createTutorResult(result);

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

    describe("when a book result is created", function() {
        var result = {"title": "Book 1", "author": "Author 1", "subject": "Math", "thumbnail": ""};
        var element = createBookResult(result);

        it("should create div for result element", function() {
            expect(element.tagName).toEqual("DIV");
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
            expect(element.childNodes[2].innerText).toContain("Author 1");
        });

    });
    
});
