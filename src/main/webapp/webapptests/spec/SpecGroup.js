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

describe("Single Group", function() {

    describe("when the user requests to add a post", function() {
        var mockWindow = {location: {href: "group.html?groupId=123", search: "?groupId=123"}};

        it("should trigger the fetch function with the correct params", function() {
            var expectedParams = new URLSearchParams();
            expectedParams.append('groupId', "undefined");
            expectedParams.append('post-content', "test");
            expectedParams.append('anonymous', "test");
            spyOn(window, 'alert');
            spyOn(window, 'fetch').and.returnValue(Promise.resolve({json: () => Promise.resolve([])}));
            spyOn(document, "getElementById").and.returnValue("test");
            addPost(mockWindow);
            expect(window.fetch).toHaveBeenCalledWith('/manage-posts', {method: 'POST', body: expectedParams});
        });
    });

    describe("when the group header is loaded", function() {
        var mockWindow = {location: {href: "group.html?groupId=123", search: "?groupId=123"}};

        it("should trigger the fetch function with the correct params and display the correct header", async function() {
            var result = {"name": "test", "topic": "test", "description": "test"}
            var headerContainer = document.createElement("div");
            spyOn(document, 'getElementById').and.returnValue(headerContainer);
            spyOn(window, 'fetch').and.returnValue(Promise.resolve({json: () => Promise.resolve(result)}));
            await loadGroupHeaderHelper(document, mockWindow);
            expect(window.fetch).toHaveBeenCalledWith('/group-data?groupId=123');
            expect(headerContainer.childNodes[0].tagName).toEqual("H1");
            expect(headerContainer.childNodes[0].innerText).toContain("test");
            expect(headerContainer.childNodes[1].tagName).toEqual("H3");
            expect(headerContainer.childNodes[1].innerText).toContain("test");
            expect(headerContainer.childNodes[2].tagName).toEqual("H4");
            expect(headerContainer.childNodes[2].innerText).toContain("test");
        });
    });

    describe("when posts are displayed", function() {
        var posts = [{"userID": "anonymous", "groupID": "321", "content": "test", "id": "1"}, 
                        {"userID": "anonymous", "groupID": "321", "content": "test", "id": "3"}];

        var postContainer = document.createElement("div");
        postContainer.id = "posts";

        var mockWindow = {location: {href: "group.html?groupId=123", search: "?groupId=123"}};

        it("should create result elements inside postContainer", async function() {
            spyOn(window, "fetch").and.returnValues(Promise.resolve({json: () => Promise.resolve(posts)}));

            spyOn(document, "getElementById").and.returnValues(postContainer);

            await displayGroupPostsHelper(document, mockWindow);

            expect(window.fetch).toHaveBeenCalledTimes(1);
            expect(window.fetch.calls.allArgs()[0][0]).toEqual("/manage-posts?groupId=123");

            //one for the number of results label + 2 for the number of results in testResults
            expect(postContainer.childNodes.length).toEqual(3);
            expect(postContainer.childNodes[0].innerText).toContain("Found 2 posts for this group");
        });

    });

    describe("when a post result is created", function() {
        var result = {"userID": "anonymous", "content": "test"};
        var element = createPostResult(result, document);

        it("should create div for result element", function() {
            expect(element.tagName).toEqual("DIV");
        });

        it("should create h4 element inside result element for content", function() {
            expect(element.childNodes[0].tagName).toEqual("H4");
            expect(element.childNodes[0].innerText).toContain("test");
        });

        it("should create h5 element inside result element for name", function() {
            expect(element.childNodes[1].tagName).toEqual("H5");
            expect(element.childNodes[1].innerText).toContain("By anonymous");
        });

        it("should create form element inside result element for adding replies", function() {
            expect(element.childNodes[2].tagName).toEqual("FORM");
        });

        it("should add an input and a button to the form element", function() {
            expect(element.childNodes[2].childNodes[0].tagName).toEqual("INPUT");
            expect(element.childNodes[2].childNodes[1].tagName).toEqual("BUTTON");
            expect(element.childNodes[2].childNodes[1].innerText).toContain("Submit");
        });

        it("should create break element inside result element", function() {
            expect(element.childNodes[3].tagName).toEqual("BR");
        });

        it("should create button element inside result element for viewing a thread", function() {
            expect(element.childNodes[4].tagName).toEqual("BUTTON");
            expect(element.childNodes[4].innerText).toContain("View Thread");
        });

    });

    describe("when the user requests to add a reply", function() {
        var mockReply = {"id": "123", "groupID": "321"};
        var mockContent = "test";
        var mockWindow = {};

        it("should trigger the fetch function with the correct params", function() {
            var expectedParams = new URLSearchParams();
            expectedParams.append('postId', "undefined");
            expectedParams.append('content', "test");
            spyOn(window, 'alert');
            spyOn(window, 'fetch').and.returnValue(Promise.resolve({json: () => Promise.resolve([])}));
            addReply(mockReply, mockContent, mockWindow);
            expect(window.fetch).toHaveBeenCalledWith('/manage-replies', {method: 'POST', body: expectedParams});
        });
    });

    describe("when the user request to see the thread", function() {
        var replies = [{"userID": "anonymous", "content": "test", "test": "true"}, 
                        {"userID": "anonymous", "content": "test", "test": "true"}];

        var mockPost = {"id": "123"};
        var mockContainer = document.createElement("div");

        it("should create result elements inside container", async function() {
            spyOn(window, "fetch").and.returnValues(Promise.resolve({json: () => Promise.resolve(replies)}));

            await displayThread(mockPost, mockContainer, document);

            expect(window.fetch).toHaveBeenCalledTimes(1);
            expect(window.fetch.calls.allArgs()[0][0]).toEqual("/manage-replies?postId=123");

            //one for the number of results label + 2 for the number of results in testResults
            expect(mockContainer.childNodes.length).toEqual(3);
            expect(mockContainer.childNodes[0].innerText).toContain("Found 2 replies for this post");
        });

    });

    describe("when a reply result is created", function() {
        var result = {"test": "true", "content": "test"};
        var element = createReplyResult(result);

        it("should create div for result element", function() {
            expect(element.tagName).toEqual("DIV");
        });

        it("should create h4 element inside result element for content", function() {
            expect(element.childNodes[0].tagName).toEqual("H4");
            expect(element.childNodes[0].innerText).toContain("test");
        });

        it("should create h5 element inside result element for name", function() {
            expect(element.childNodes[1].tagName).toEqual("H5");
            expect(element.childNodes[1].innerText).toContain("By Test");
        });
    });

});
