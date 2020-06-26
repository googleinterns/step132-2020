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
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.*; // Can be deleted after hardcoded database is removed

@WebServlet("/scheduling")
public class SchedulingServlet extends HttpServlet {
    SampleData hardcoded;

    public void init(ServletConfig servletconfig) throws ServletException { 
        hardcoded = new SampleData();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("plain/text");
        response.getWriter().println("To be implemented");
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String tutorID = request.getParameter("tutorID");
        String start = request.getParameter("start");
        String end = request.getParameter("end");
        String studentEmail = request.getParameter("studentEmail");
        String subtopics = request.getParameter("subtopics");
        String questions = request.getParameter("questions");

        TimeRange timeslot = TimeRange.fromStartToEnd(Integer.parseInt(start), Integer.parseInt(end));

        TutorSession tutoringSession = new TutorSession(studentEmail, tutorID, subtopics, questions, timeslot);
        
        // Update scheduledSessions
        hardcoded.addToScheduledSessionsByEmail(tutorID, tutoringSession);

        // Remove available timeslot
        hardcoded.deleteAvailabilityByTimeRange(tutorID, timeslot);

        String json = convertToJsonUsingGson(hardcoded.getSampleTutors());
        response.setContentType("application/json;");
        response.getWriter().println(json);
        return;
    }

    // Converts the hardcoded into a JSON string using the Gson library.
    private String convertToJsonUsingGson(List<Tutor> tutors) {
        Gson gson = new Gson();
        String json = gson.toJson(tutors);
        return json;
    }
}
