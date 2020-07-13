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
import com.google.sps.utilities.RealAvailabilityDatastore;
import com.google.sps.utilities.MockAvailabilityDatastore;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Calendar;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/addAvailability")
public class AddAvailabilityServlet extends HttpServlet {
    private AvailabilityDatastoreService datastore;

    /**
    * Because we created a constructor with a parameter (the testing one), the default empty constructor does not work anymore so we have to explicitly create it. 
    * We need the default one for deployment because the servlet is created without parameters.
    */
    public AddAvailabilityServlet(){}

    public AddAvailabilityServlet(boolean test) {
        if(test) {
            datastore = new MockAvailabilityDatastore();
        }
    }

    public void init() {
        datastore = new RealAvailabilityDatastore();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("plain/text");
        response.getWriter().println("To be implemented");
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String tutorID = request.getParameter("tutorEmail");
        String startHour = request.getParameter("startHour");
        String startMinute = request.getParameter("startMinute");
        String endHour = request.getParameter("endHour");
        String endMinute = request.getParameter("endMinute");
        String day = request.getParameter("day");
        String month = request.getParameter("month");
        String year = request.getParameter("year");

        int start = Integer.parseInt(startHour) * 60 + Integer.parseInt(startMinute);
        int end = Integer.parseInt(endHour) * 60 + Integer.parseInt(endMinute);

        Calendar date = new Calendar.Builder()
                                .setCalendarType("iso8601")
                                .setDate(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day))
                                .build();

        TimeRange timeslot = TimeRange.fromStartToEnd(start, end, date);

        // Add available timeslot
        datastore.addAvailability(tutorID, timeslot);

        String json = new Gson().toJson(datastore.getAvailabilityForTutor(tutorID));
        response.setContentType("application/json;");
        response.getWriter().println(json);
        response.sendRedirect("/manage-availability.html?userID=" + tutorID.replace("@", "%40"));
        return;
    }
}
