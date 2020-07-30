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
import com.google.sps.data.Student;
import com.google.sps.utilities.TutorSessionDatastoreService;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Date;
import java.util.Calendar;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that keeps track of a user's past sessions. */
@WebServlet("/history")
public class HistoryServlet extends HttpServlet {
  
    private TutorSessionDatastoreService datastore;

    public void init() {
        datastore = new TutorSessionDatastoreService();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Get the id of the student whose tutoring history will be displayed.
        // Make the default id -1 if the parameter is null, so an empty list will be returned
        String studentID = Optional.ofNullable((String)request.getSession(false).getAttribute("userId")).orElse("-1");
        String studentIDTutorView = Optional.ofNullable(request.getParameter("studentIDTutorView")).orElse("-1");

        if (!studentIDTutorView.equals("-1")) {
            studentID = studentIDTutorView;
        }

        if(studentID.equals("-1")) {
            response.setContentType("application/json");
            response.getWriter().println("{\"error\": \"There was an error getting history.\"}");
            return;
        }

        List<TutorSession> scheduledSessions = datastore.getScheduledSessionsForStudent(studentID);

        List<TutorSession> previousSessions = filterPastSessions(scheduledSessions);

        String json = new Gson().toJson(previousSessions);
        response.setContentType("application/json;");
        response.getWriter().println(json);
        return;
    }

    private List<TutorSession> filterPastSessions(List<TutorSession> allSessions) {
        List<TutorSession> previousSessions = new ArrayList<TutorSession>();

        // Calendar object on timeslots don't have hour or minute, so create this calendar with only year, month, and day
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH);
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        Calendar currentCalendar = new Calendar.Builder()
                                                    .setCalendarType("iso8601")
                                                    .setDate(year, month, day)
                                                    .build();

        for (TutorSession session : allSessions) {
            Calendar sessionCalendar = session.getTimeslot().getDate();
            int comparison = currentCalendar.compareTo(sessionCalendar);
            if (comparison == 0 || comparison == 1) {
                    previousSessions.add(session);
            }
        }


        return previousSessions;
    }
}
