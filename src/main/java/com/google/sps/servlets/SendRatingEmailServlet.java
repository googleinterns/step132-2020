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
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Calendar;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
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

@WebServlet("/rating-email")
public class SendRatingEmailServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("triggered!!!!!!!!!!!");
        String email = request.getReader().lines().collect(Collectors.joining());

        System.out.println(email);

        boolean testRatingEmail = sendRatingEmailToStudent(email);

        response.setContentType("application/json;");
        response.getWriter().println("{testRatingEmail: " + testRatingEmail + "}");

        return;
    }

    /**
  * Sends an email prompting the student to rate the tutor
  */
  public boolean sendRatingEmailToStudent(String email) {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        String subject = "Rate Your Tutoring Session";
        String message = "Hi there,\n" +
                        "We hope you had a great tutoring session! Don't forget to rate your tutor by " +
                        "accessing your tutoring session history.\n" + 
                        "The Sullivan Team";

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("contact@icecube-step-2020.appspotmail.com", "Sullivan"));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
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
