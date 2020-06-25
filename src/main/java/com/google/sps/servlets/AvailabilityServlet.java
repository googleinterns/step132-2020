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
import com.google.gson.Gson;
import java.io.IOException;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.*; // Can be deleted after hardcoded database is removed


@WebServlet("/availability")
public class AvailabilityServlet extends HttpServlet {
    private static final int TIME_0800AM = TimeRange.getTimeInMinutes(8, 00);
    private static final int TIME_0900AM = TimeRange.getTimeInMinutes(9, 00);
    private static final int TIME_1000AM = TimeRange.getTimeInMinutes(10, 00);
    private static final int TIME_1100AM = TimeRange.getTimeInMinutes(11, 00);
    private static final int TIME_1200AM = TimeRange.getTimeInMinutes(12, 00);

    private static final String[] SKILLS1 = new String[]{"math"};

    private static final TimeRange[] AVAILABILITY1 = new TimeRange[]{TimeRange.fromStartToEnd(TIME_0800AM, TIME_0900AM), TimeRange.fromStartToEnd(TIME_1100AM, TIME_1200AM)};

    private List<Tutor> hardcoded = new ArrayList<Tutor>(); 

    public void init(ServletConfig servletconfig) throws ServletException { 
        hardcoded.add(new Tutor("John", "john@gmail.com", SKILLS1, AVAILABILITY1));
        hardcoded.add(new Tutor("Jane", "jane@gmail.com", SKILLS1, AVAILABILITY1));
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("plain/text");
        response.getWriter().println("To be implemented");
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Get the id of the tutor whose availability will be displayed.
        String tutorID = Optional.ofNullable(request.getParameter("tutorID")).orElse(null);

        List<TimeRange> timeslots = new ArrayList<TimeRange>();

        //System.out.println("tutorID: " + tutorID);

        for (Tutor tutor : hardcoded) {
            //System.out.println("tutor email: " + tutor.getEmail());
            if (tutorID.equals(tutor.getEmail())) {
                //System.out.println("match");
                timeslots = Arrays.asList(tutor.getAvailability());
            }
        }

        String json = convertToJsonUsingGson(timeslots);
        response.setContentType("application/json;");
        response.getWriter().println(json);
        return;
    }

    // Converts the time slot array into a JSON string using the Gson library.
    private String convertToJsonUsingGson(List<TimeRange> timeslots) {
        Gson gson = new Gson();
        String json = gson.toJson(timeslots);
        return json;
    }
}
