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

package com.google.sps.data;

import java.util.Timer; 
import java.util.TimerTask;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 * Task to send out an email requesting the user to rate their tutoring session. 
 * RatingEmail is setup when a tutoring session is scheduled. 
 */
public final class RatingEmailTask extends TimerTask {
    private MimeMessage emailContent;

    public RatingEmailTask(TutorSession tutoringSession) {
        System.out.println("Create email");

        this.emailContent = createEmail(tutoringSession.getStudentEmail());
    }

    public void run() { 
        System.out.println("Send email");

        if (emailContent != null) {
            try {
                Transport.send(emailContent);
            } catch (MessagingException e) {
                System.out.println("Unable send message");
                return;
            }
        }
    }

    /**
    * Create a MimeMessage using the student's email provided.
    *
    * @param to email address of the receiver
    * @return the MimeMessage to be used to send email
    */
    public MimeMessage createEmail(String to) {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        try {
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("anything@icecube-step-2020.appspotmail.com", "Sullivan"));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            msg.setSubject("Test");
            msg.setText("Test history.html?userID=" +  to);
            Transport.send(msg);
            return msg;
        } catch (AddressException e) {
            System.out.println("Email address appears to be invalid");
            return null;
        } catch (MessagingException e) {
            System.out.println("Unable create message");
            return null;
        } catch (UnsupportedEncodingException e) {
            System.out.println("Unable to encode message");
            return null;
        }
    }
}
