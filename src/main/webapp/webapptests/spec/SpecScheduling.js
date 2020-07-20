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

describe("Scheduling", function() {
    describe("when tutoring session is scheduled", function() {
        var mockWindow = {location: {href: "scheduling.html?tutorID=123&start=480&end=540&year=2020&month=5&day=18", search: "?tutorID=123&start=480&end=540&year=2020&month=5&day=18"}};

        it("should trigger the fetch function", async function() {
            spyOn(document, "getElementById").and.returnValue("");
            spyOn(window, 'fetch').and.returnValue(Promise.resolve({json: () => Promise.resolve([])}));

            await scheduleTutorSessionHelper(mockWindow);

            expect(window.fetch).toHaveBeenCalled();
        });
    });

    describe("when the URI components are read", function() {
        var mockWindow = {location: {href: "scheduling.html?tutorID=123&start=480&end=540&year=2020&month=5&day=18", search: "?tutorID=123&start=480&end=540&year=2020&month=5&day=18"}};
        var queryString = new Array();
        readComponents(queryString, mockWindow);

        it("should add tutorID, start, end, year, month, and day to the query string", function() {
            expect(queryString["tutorID"]).toEqual("123");
            expect(queryString["start"]).toEqual("480");
            expect(queryString["end"]).toEqual("540");
            expect(queryString["year"]).toEqual("2020");
            expect(queryString["month"]).toEqual("5");
            expect(queryString["day"]).toEqual("18");
        });
    });

    describe("when a user submits the form and they are logged in", function() {
        var mockWindow = {location: {href: "scheduling.html?tutorID=123&start=480&end=540&year=2020&month=5&day=18", search: "?tutorID=123&start=480&end=540&year=2020&month=5&day=18"}};

        it("should redirect the user to confirmation.html ", async function() {
            spyOn(window, 'fetch').and.returnValue(Promise.resolve({json: () => Promise.resolve([])}));
            spyOn(document, "getElementById").and.returnValue("");

            await scheduleTutorSessionHelper(mockWindow);
            expect(mockWindow.location.href).toEqual("confirmation.html");
        });
    });

    describe("when a user submits the form and they are not logged in", function() {
        var mockWindow = {location: {href: "scheduling.html?tutorID=123&start=480&end=540&year=2020&month=5&day=18", search: "?tutorID=123&start=480&end=540&year=2020&month=5&day=18"}};
        var response = {redirected: true, url: "/homepage.html"};
        it("should redirect user to homepage", async function() {
            spyOn(window, 'alert');
            spyOn(window, 'fetch').and.returnValue(Promise.resolve(response));
            spyOn(document, "getElementById").and.returnValue("");
            await scheduleTutorSessionHelper(mockWindow);
            expect(window.alert).toHaveBeenCalledWith('You must be signed in to schedule a tutoring session.');
            expect(mockWindow.location.href).toBe('/homepage.html');
        });
    });

}); 
