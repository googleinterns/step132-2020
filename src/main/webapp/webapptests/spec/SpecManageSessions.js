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

describe("Manage Sessions", function() {

    describe("when the student requests to see a their scheduled sessions", function() {
        var mockWindow = {location: {href: "manage-sessions.html"}};

        it("should trigger the fetch function", async function() {
            spyOn(window, 'fetch').and.returnValue(Promise.resolve({json: () => Promise.resolve([])}));
            var sessionsContainer = document.createElement("div");
            spyOn(document, 'getElementById').and.returnValue(sessionsContainer);
            await fetchSessionInfoHelper(mockWindow);
            expect(window.fetch).toHaveBeenCalled();
        });
    });

    describe("when user tries to access someone else's sessions page", function() {
        var mockWindow = {location: {href: "manage-sessions.html"}};
        var response = {redirected: true, url: "/homepage.html"};
        it("should redirect user to homepage", async function() {
            spyOn(window, 'alert');
            spyOn(window, 'fetch').and.returnValue(Promise.resolve(response));
            var sessionsContainer = document.createElement("div");
            spyOn(document, 'getElementById').and.returnValue(sessionsContainer);
            await fetchSessionInfoHelper(mockWindow);
            expect(window.alert).toHaveBeenCalledWith('You must be signed in to manage sessions.');
            expect(mockWindow.location.href).toBe('/homepage.html');
        });
    });

});
