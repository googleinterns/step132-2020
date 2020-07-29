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

package com.google.sps.servlets;

import com.google.sps.data.Tutor;
import com.google.sps.data.TimeRange;
import com.google.sps.data.TutorSession;
import com.google.sps.data.SampleData;
import com.google.sps.utilities.AvailabilityDatastoreService;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Calendar;
import java.util.Optional;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that gets and adds to a tutor's availability. */
@WebServlet("/manage-availability")
public class ManageAvailabilityServlet extends HttpServlet {
    private AvailabilityDatastoreService datastore;

    public void init() {
        datastore = new AvailabilityDatastoreService();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Get the id of the tutor whose availability will be displayed
        //if id is null, use default id -1 (no tutor has id -1, so no availability will be displayed)
        String tutorID = Optional.ofNullable((String)request.getSession(false).getAttribute("userId")).orElse("-1");

        if(tutorID.equals("-1")) {
            response.setContentType("application/json");
            response.getWriter().println("{\"error\": \"There was an error getting tutor's availability.\"}");
        }

        List<TimeRange> timeslots = datastore.getAvailabilityForTutor(tutorID);

        String json = new Gson().toJson(timeslots);
        response.setContentType("application/json;");
        response.getWriter().println(json);
        return; 
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        // Set default value to -1 
        String tutorID = Optional.ofNullable((String)request.getSession(false).getAttribute("userId")).orElse("-1");
        String startTime = request.getParameter("startTime");
        // Split hour and minute to separate strings
        String[] startHourAndMinute = startTime.split(":");
        
        String date = request.getParameter("date");
        // Split year, month, and day to separate strings
        String[] yearMonthDay = date.split("-");

        int start = Integer.parseInt(startHourAndMinute[0]) * 60 + Integer.parseInt(startHourAndMinute[1]);
        // Make all tutor sessions last 1 hour
        int end = Integer.parseInt(startHourAndMinute[0]) * 60 + Integer.parseInt(startHourAndMinute[1]) + 60;

        if(tutorID.equals("-1")) {
            response.getWriter().println("{\"error\": \"There was an error adding availability.\"}");
            return;
        }

        Calendar calendarDate = new Calendar.Builder()
                                .setCalendarType("iso8601")
                                .setDate(Integer.parseInt(yearMonthDay[0]), Integer.parseInt(yearMonthDay[1])-1, Integer.parseInt(yearMonthDay[2]))
                                .build();

        TimeRange timeslot = TimeRange.fromStartToEnd(start, end, calendarDate);

        // Add available timeslot
        datastore.addAvailability(tutorID, timeslot);

        String json = new Gson().toJson(datastore.getAvailabilityForTutor(tutorID));
        response.getWriter().println(json);
        return;
    }
}
