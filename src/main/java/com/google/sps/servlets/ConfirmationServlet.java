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

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("plain/text");
        response.getWriter().println("To be implemented");
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Get the id of the student whose availability will be displayed.
        String studentEmail = request.getParameter("studentEmail");

        List<TutorSession> scheduledSessions = new ArrayList<TutorSession>();

        for (Student student : SampleData.getSampleStudents()) {
            if (studentEmail.toLowerCase().equals(student.getEmail().toLowerCase())) {
                scheduledSessions = student.getScheduledSessions();
                break;
            }
        }

        List<TutorSession> upcomingSessions = new ArrayList<TutorSession>();

        filterUpcomingSessions(scheduledSessions, upcomingSessions);

        String json = new Gson().toJson(upcomingSessions);
        response.setContentType("application/json;");
        response.getWriter().println(json);
        return;
    }

    private void filterUpcomingSessions(List<TutorSession> allSessions, List<TutorSession> upcomingSessions) {
        Date currentDate = new Date();

        for (TutorSession session : allSessions) {
            Calendar sessionCalendar = session.getTimeslot().getDate();
            int start = session.getTimeslot().getStart();
            int hour = start / 60;
            int minute = start % 60;

            sessionCalendar.set(Calendar.HOUR_OF_DAY, hour);
            sessionCalendar.set(Calendar.MINUTE, minute);

            Date sessionDate = sessionCalendar.getTime();
            int comparison = currentDate.compareTo(sessionDate);
            if (comparison == 0 || comparison == -1) {
                    upcomingSessions.add(session);
            }
        }

        return;
    }

}
