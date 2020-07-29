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

describe("Availability", function() {

    describe("when the user requests to see a tutor's availability", function() {
        var mockWindow = {location: {href: "availability.html?tutorID=test%40gmail.com", search: "?tutorID=test%40gmail.com"}};

        it("should trigger the fetch function", function() {
            spyOn(window, "onload").and.callFake(function() {
                readComponents(queryString, window);
            });
            spyOn(window, 'fetch').and.callThrough();
            fetchAvailabilityInfo(mockWindow);
            expect(window.fetch).toHaveBeenCalledWith('/availability?tutorID=undefined', {method: 'GET'});
            expect(window.fetch).toHaveBeenCalled();
        });
    });

    describe("when the tutor ID is read", function() {
        var mockWindow = {location: {href: "availability.html?tutorID=test%40gmail.com", search: "?tutorID=test%40gmail.com"}};
        var queryString = new Array();
        readComponents(queryString, mockWindow);

        it("should set tutorID inside queryString as the tutorID", function() {
            expect(queryString["tutorID"]).toEqual("test@gmail.com");
        });
    });

});
