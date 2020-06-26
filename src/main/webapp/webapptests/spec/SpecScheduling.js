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
        var mockWindow = {location: {href: "scheduling.html?tutorID=test%40gmail.com&start=480&end=540", search: "?tutorID=test%40gmail.com&start=480&end=540"}};

        it("should trigger the fetch function", function() {
            spyOn(document, "getElementById").and.returnValue("");
            spyOn(window, 'fetch').and.callThrough();

            scheduleTutorSession(mockWindow);

            expect(window.fetch).toHaveBeenCalled();
        });
    });

    describe("when the URI components are read", function() {
        var mockWindow = {location: {href: "scheduling.html?tutorID=test%40gmail.com&start=480&end=540", search: "?tutorID=test%40gmail.com&start=480&end=540"}};
        var queryString = new Array();
        readComponents(queryString, mockWindow);

        it("should add tutorID, start, and end to the query string", function() {
            expect(queryString["tutorID"]).toEqual("test@gmail.com");
            expect(queryString["start"]).toEqual("480");
            expect(queryString["end"]).toEqual("540");
        });
    });

}); 
