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
import com.google.sps.utilities.RealTutorSessionDatastore;
import com.google.sps.utilities.MockTutorSessionDatastore;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Calendar;
import java.util.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/confirmation")
public class ConfirmationServlet extends HttpServlet {

    private TutorSessionDatastoreService datastore;

    /**
    * Because we created a constructor with a parameter (the testing one), the default empty constructor does not work anymore so we have to explicitly create it. 
    * We need the default one for deployment because the servlet is created without parameters.
    */
    public ConfirmationServlet(){}

    public ConfirmationServlet(boolean test) {
        if(test) {
            datastore = new MockTutorSessionDatastore();
        }
    }

    public void init() {
        datastore = new RealTutorSessionDatastore();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Get the id of the student whose availability will be displayed.
        String studentEmail = request.getParameter("studentEmail");

        List<TutorSession> scheduledSessions = datastore.getScheduledSessionsForStudent(studentEmail);

        List<TutorSession> upcomingSessions = filterUpcomingSessions(scheduledSessions);

        String json = new Gson().toJson(upcomingSessions);
        response.setContentType("application/json;");
        response.getWriter().println(json);
        return;
    }

    /**
    * Filters out tutoring sessions that are in the future.
    * @return List<TutorSession>
    */
    private List<TutorSession> filterUpcomingSessions(List<TutorSession> allSessions) {
        List<TutorSession> upcomingSessions = new ArrayList<TutorSession>();

        Calendar currentCalendar = Calendar.getInstance();

        for (TutorSession session : allSessions) {
            Calendar sessionCalendar = session.getTimeslot().getDate();
            int comparison = currentCalendar.compareTo(sessionCalendar);
            if (comparison == 0 || comparison == -1) {
                    upcomingSessions.add(session);
            }
        }

        return upcomingSessions;
    }

}
