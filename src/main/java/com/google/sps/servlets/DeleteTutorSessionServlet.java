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
import java.util.Optional;
import java.util.Calendar;
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

@WebServlet("/delete-tutor-session")
public class DeleteTutorSessionServlet extends HttpServlet {
    private TutorSessionDatastoreService datastore;

    public void init() {
        datastore = new TutorSessionDatastoreService();
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String studentID = Optional.ofNullable((String)request.getSession(false).getAttribute("userId")).orElse("-1");
        long id = Long.parseLong(request.getParameter("id"));

        TutorSession tutorSession = datastore.getScheduledSession(id);

        Entity studentEntity = datastore.getStudentForUserId(tutorSession.getStudentID());
        String studentName = (String) studentEntity.getProperty("name");
        String studentEmail = (String) studentEntity.getProperty("email");
        Entity tutorEntity = datastore.getTutorForUserId(tutorSession.getTutorID());
        String tutorName = (String) tutorEntity.getProperty("name");
        String tutorEmail = (String) tutorEntity.getProperty("email");
        TimeRange timeslot = tutorSession.getTimeslot();
        Calendar date = timeslot.getDate();
        String month = new DateFormatSymbols().getMonths()[date.get(Calendar.MONTH)];
        int day = date.get(Calendar.DAY_OF_MONTH);
        int year = date.get(Calendar.YEAR);

        String messageStudent = "You have cancelled your tutoring session with " + tutorName + " on " +
                        month + " " + day + ", " + year + ". Check your Manage Tutoring Sessions page for more information.";

        String messageTutor = studentName + " has cancelled their tutoring session with you on " +
                        month + " " + day + ", " + year + ". Check your My Student page for more information.";               

        boolean testTutorEmail = sendCancellationEmail(messageTutor, tutorEmail);
        boolean testStudentEmail = sendCancellationEmail(messageStudent, studentEmail);

        // Remove tutor session
        datastore.deleteTutorSession(studentID, id);

        String json = new Gson().toJson(datastore.getScheduledSession(id));
        response.setContentType("application/json;");
        response.getWriter().println(json + "testTutorEmail: " + testTutorEmail + " testStudentEmail: " + testStudentEmail);
        return;
    }

    private boolean sendCancellationEmail(String message, String to) {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        String subject = "Cancelled Tutoring Session";

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("contact@icecube-step-2020.appspotmail.com", "Sullivan"));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            msg.setSubject(subject);
            msg.setText(message);
            Transport.send(msg);
            return true;
        } catch (AddressException e) {
            System.out.println("Failed to set email address.");
            return false;
        } catch (MessagingException e) {
            System.out.println("Failed to send email.");
            return false;
        } catch (UnsupportedEncodingException e) {
            System.out.println("Failed to encode email.");
            return false;
        }
    }
}
