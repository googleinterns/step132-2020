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

describe("Manage Availability", function() {

    describe("when the tutor requests to see their availability", function() {
        var mockWindow = {location: {href: "manage-availability.html?tutorID=123", search: "?123"}};

        it("should trigger the fetch function", async function() {
            spyOn(window, 'fetch').and.returnValue(Promise.resolve({json: () => Promise.resolve([])}));
            fetchTimeslotInfoHelper(mockWindow);
            expect(window.fetch).toHaveBeenCalled();
        });
    });

    describe("when user tries to access someone else's manage availability page", function() {
        var mockWindow = {location: {href: "manage-availability.html"}};
        var response = {redirected: true, url: "/homepage.html"};
        it("should redirect user to homepage", async function() {
            spyOn(window, 'alert');
            spyOn(window, 'fetch').and.returnValue(Promise.resolve(response));
            await fetchTimeslotInfoHelper(mockWindow);
            expect(window.alert).toHaveBeenCalledWith('You must be signed in to manage availability.');
            expect(mockWindow.location.href).toBe('/homepage.html');
        });
    });

    describe("when the calendar is created", function() {

    });

    describe("when a user adds a time slot", function() {
        mockWindow = {location: {href: "manage-availability.html"}}
        var startHour = document.createElement("select");
        var startMinute = document.createElement("select");
        var endHour = document.createElement("select");
        var endMinute = document.createElement("select");
        var day = document.createElement("select");
        var month = document.createElement("select");
        var year = document.createElement("select");
        startHour.value = 1;
        startMinute.value = 30;
        endHour.value = 2;
        endMinute.value = 30;
        day.value = 1;
        month.value = 1;
        year.value = 2020;

        var params = new URLSearchParams();
        params.append('startHour', 1);
        params.append('startMinute', 30);
        params.append('endHour', 2);
        params.append('endMinute', 30);
        params.append('day', 1);
        params.append('month', 1);
        params.append('year', 2020);

        it("should trigger the fetch function", function() {
            var mockWindow = {location: {href: "manage-availability.html?tutorID=123", search: "?123"}};
            spyOn(window, 'alert');
            spyOn(document, "getElementById").and.returnValues(startHour, startMinute, endHour, endMinute, day, month, year);
            spyOn(window, 'fetch').and.returnValue(Promise.resolve({json: () => Promise.resolve([])}));
            addTimeSlotHelper(mockWindow);
            expect(window.fetch).toHaveBeenCalledWith('/manage-availability', {method: 'POST', body: params})
            expect(window.fetch).toHaveBeenCalled();
            expect(mockWindow.location.href).toBe("manage-availability.html?tutorID=123");
        });
    });

    describe("when checking inputted time", function() {
        var mockWindow = {location: {href: "manage-availability.html"}};
        
        it("should prohibit times from the past when the date is set to today", function() {
            spyOn(window, 'alert');
            var actual = checkMinTime(2020, 7, 30, 15, 10, '2020-07-30', '13:30', mockWindow);
            expect(window.alert).toHaveBeenCalledWith("You cannot add an available timeslot in the past!");
            expect(actual).toBe(true);
        });

        it("should do nothing when the date is in the future", function() {
            spyOn(window, 'alert');
            var actual = checkMinTime(2020, 7, 30, 15, 10, '2020-07-31', '13:30', mockWindow);
            expect(actual).toBe(false);
        });

        it("should do nothing when the time is in the future and the date is set to today", function() {
            spyOn(window, 'alert');
            var actual = checkMinTime(2020, 7, 30, 15, 10, '2020-07-30', '16:30', mockWindow);
            expect(actual).toBe(false);
        });
    });

    describe("when setting a min date", function() {  
        var year = 2020;
        var month = 5;
        var day = 4;

        beforeAll(function() {
            var mockDateInput = document.createElement('input');
            mockDateInput.id = 'date';
            mockDateInput.setAttribute('type', 'date');

            document.body.appendChild(mockDateInput);
        })
    
        it("should correctly format the month, day, hour, and minute", function() {
            setMinDateHelper(document, year, month, day);

            expect(document.getElementById('date').min).toBe('2020-05-04');
        });
    });

});
