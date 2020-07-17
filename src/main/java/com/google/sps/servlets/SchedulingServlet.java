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
import com.google.sps.data.RatingEmailTask;
import com.google.sps.utilities.TutorSessionDatastoreService;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Calendar;
import java.util.Timer; 
import java.util.TimerTask;
import java.lang.Math;
import java.util.Optional;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/scheduling")
public class SchedulingServlet extends HttpServlet {

    private TutorSessionDatastoreService datastore;

    public void init() {
        datastore = new TutorSessionDatastoreService();
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;");

        //make the default value -1 if ids were null
        String tutorID = Optional.ofNullable(request.getParameter("tutorID")).orElse("-1");
        String studentID = Optional.ofNullable((String)request.getSession(false).getAttribute("userId")).orElse("-1");
        String start = request.getParameter("start");
        String end = request.getParameter("end");
        String year = request.getParameter("year");
        String month = request.getParameter("month");
        String day = request.getParameter("day");
        String subtopics = request.getParameter("subtopics");
        String questions = request.getParameter("questions");

        //if the tutor or student id was null
        if(tutorID.equals("-1") || studentID.equals("-1")) {
            response.getWriter().println("{\"error\": \"There was an error scheduling your session.\"}");
        }

        Calendar date = new Calendar.Builder()
                                .setCalendarType("iso8601")
                                .setDate(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day))
                                .build();

        TimeRange timeslot = TimeRange.fromStartToEnd(Integer.parseInt(start), Integer.parseInt(end), date);
        
        TutorSession tutoringSession = new TutorSession(studentID, tutorID, subtopics, questions, timeslot);

        datastore.addTutorSession(tutoringSession);

        scheduleRatingEmail(tutoringSession);

        String json = new Gson().toJson(datastore.getScheduledSessionsForTutor(tutorID));
        response.getWriter().println(json);
        return;
    }

    // Schedules an email to prompt the user to rate their tutoring session
    // to be sent out after the tutoring session is over.
    public void scheduleRatingEmail(TutorSession tutoringSession) {
        Timer timer = new Timer();
        TimerTask sendEmail = new RatingEmailTask(tutoringSession);

        int year = tutoringSession.getTimeslot().getDate().get(Calendar.YEAR);
        int month = tutoringSession.getTimeslot().getDate().get(Calendar.MONTH);
        int day = tutoringSession.getTimeslot().getDate().get(Calendar.DAY_OF_MONTH);
        int hour = (int) Math.floor(tutoringSession.getTimeslot().getEnd() / 60);
        int min = tutoringSession.getTimeslot().getEnd() % 60;

        Calendar scheduledDate = Calendar.getInstance();
        scheduledDate.set(Calendar.YEAR, year);
        scheduledDate.set(Calendar.MONTH, month);
        scheduledDate.set(Calendar.DAY_OF_MONTH, day);
        scheduledDate.set(Calendar.HOUR_OF_DAY, hour);
        scheduledDate.set(Calendar.MINUTE, min);

        // The run method in RatingEmailTask will be triggered after the end of the tutoring session
        timer.schedule(sendEmail, scheduledDate.getTime());
    }
}
