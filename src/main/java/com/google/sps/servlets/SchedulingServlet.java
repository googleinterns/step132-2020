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
import com.google.sps.utilities.TutorSessionDatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Calendar;
import java.util.Optional;
import java.util.Properties;
import java.text.DateFormatSymbols;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

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

        Entity studentEntity = datastore.getStudentForUserId(studentID);
        Entity tutorEntity = datastore.getTutorForUserId(tutorID);

        sendConfirmationEmailToTutor(studentEntity, tutorEntity, timeslot);
        sendConfirmationEmailToStudent(studentEntity, tutorEntity, timeslot);


        String json = new Gson().toJson(datastore.getScheduledSessionsForTutor(tutorID));
        response.getWriter().println(json);
        return;
    }

    public void sendConfirmationEmailToTutor(Entity studentEntity, Entity tutorEntity, TimeRange timeslot) {
        System.out.println("Create email");

        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        String to = (String) tutorEntity.getProperty("email");
        String subject = "Scheduled Tutoring Session";
        String studentName = (String) studentEntity.getProperty("name");
        Calendar date = timeslot.getDate();
        String month = new DateFormatSymbols().getMonths()[date.get(Calendar.MONTH)];
        int day = date.get(Calendar.DAY_OF_MONTH);
        int year = date.get(Calendar.YEAR);
        String message = studentName + " has scheduled a tutoring session with you on " +
                        month + " " + day + ", " + year + ". Check your My Student page for more information.";

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("contact@icecube-step-2020.appspotmail.com", "Sullivan"));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            msg.setSubject(subject);
            msg.setText(message);
            Transport.send(msg);

            System.out.println("Email sent");

        } catch (AddressException e) {
            System.out.println("Failed to set email address.");
            return;
        } catch (MessagingException e) {
            System.out.println("Failed to send email.");
            return;
        } catch (UnsupportedEncodingException e) {
            System.out.println("Failed to encode email.");
            return;
        } 
    }

    public void sendConfirmationEmailToStudent(Entity studentEntity, Entity tutorEntity, TimeRange timeslot) {
        System.out.println("Create email");

        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        String to = (String) studentEntity.getProperty("email");
        String subject = "Scheduled Tutoring Session";
        String tutorName = (String) tutorEntity.getProperty("name");
        Calendar date = timeslot.getDate();
        String month = new DateFormatSymbols().getMonths()[date.get(Calendar.MONTH)];
        int day = date.get(Calendar.DAY_OF_MONTH);
        int year = date.get(Calendar.YEAR);
        String message = "You have scheduled a tutoring session with " + tutorName + " on " +
                        month + " " + day + ", " + year + ". Check your Manage Tutoring Sessions page for more information.";

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("contact@icecube-step-2020.appspotmail.com", "Sullivan"));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            msg.setSubject(subject);
            msg.setText(message);
            Transport.send(msg);

            System.out.println("Email sent");

        } catch (AddressException e) {
            System.out.println("Failed to set email address.");
            return;
        } catch (MessagingException e) {
            System.out.println("Failed to send email.");
            return;
        } catch (UnsupportedEncodingException e) {
            System.out.println("Failed to encode email.");
            return;
        } 
    }
}
