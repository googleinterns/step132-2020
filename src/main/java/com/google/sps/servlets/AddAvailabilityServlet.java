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

@WebServlet("/add-availability")
public class AddAvailabilityServlet extends HttpServlet {
    private AvailabilityDatastoreService datastore;

    public void init() {
        datastore = new AvailabilityDatastoreService();
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        //Set default value to -1 
        String tutorID = Optional.ofNullable((String)request.getSession(false).getAttribute("userId")).orElse("-1");
        String startHour = request.getParameter("startHour");
        String startMinute = request.getParameter("startMinute");
        String endHour = request.getParameter("endHour");
        String endMinute = request.getParameter("endMinute");
        String day = request.getParameter("day");
        String month = request.getParameter("month");
        String year = request.getParameter("year");

        int start = Integer.parseInt(startHour) * 60 + Integer.parseInt(startMinute);
        int end = Integer.parseInt(endHour) * 60 + Integer.parseInt(endMinute);

        if(tutorID.equals("-1")) {
            response.getWriter().println("{\"error\": \"There was an error adding availability.\"}");
            return;
        }

        Calendar date = new Calendar.Builder()
                                .setCalendarType("iso8601")
                                .setDate(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day))
                                .build();

        TimeRange timeslot = TimeRange.fromStartToEnd(start, end, date);

        // Add available timeslot
        datastore.addAvailability(tutorID, timeslot);

        String json = new Gson().toJson(datastore.getAvailabilityForTutor(tutorID));
        response.getWriter().println(json);
        return;
    }
}
