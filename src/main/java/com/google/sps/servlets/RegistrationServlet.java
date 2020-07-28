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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.sps.data.TimeRange;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

/** Servlet that handles user's registration info */
@WebServlet("/registration")
public class RegistrationServlet extends HttpServlet {
  
  private UserService userService = UserServiceFactory.getUserService();
  private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String role = Optional.ofNullable(request.getParameter("role")).orElse(null);
    String firstName = Optional.ofNullable(request.getParameter("first-name"))
            .orElseThrow(() -> new IllegalArgumentException("Must fill out first name"));
    String lastName = Optional.ofNullable(request.getParameter("last-name"))
            .orElseThrow(() -> new IllegalArgumentException("Must fill out last name"));
    String fullName = firstName + " " + lastName;
    String bio = Optional.ofNullable(request.getParameter("bio")).orElse("");
    // For now, we will automatically set everyone's profile picture to a default avatar
    String pfp = "images/pfp.jpg";
    
    String email = userService.getCurrentUser().getEmail();
    String userId = userService.getCurrentUser().getUserId();

    // Make entity for user with all registration info
    Entity userEntity = new Entity("User");
    createUserEntityAndPutInDatastore(datastore, userEntity, role, userId, fullName.toLowerCase(), firstName.toLowerCase(), lastName.toLowerCase());

    // The "learn" parameter in the getTopics function refers to the topics that the user is learning and the 
    // "tutor" parameter refers to the topics the user is tutoring in. The distinction between these two sets of
    // topics exist to that a user can have different topics to tutor in from the topics they are learning.
    if (role.toLowerCase().equals("both")) {
        List<String> learningTopics = getTopics(request, "learn");
        List<String> tutoringTopics = getTopics(request, "tutor");

        Entity tutorEntity = new Entity("Tutor");
        createTutorEntityAndPutInDatastore(datastore, tutorEntity, fullName, bio, pfp, email, tutoringTopics, userId);

        Entity studentEntity = new Entity("Student");
        createStudentEntityAndPutInDatastore(datastore, studentEntity, fullName, bio, pfp, email, learningTopics, userId);
    } else if (role.toLowerCase().equals("tutor")) {
        List<String> tutoringTopics = getTopics(request, "tutor");

        Entity tutorEntity = new Entity("Tutor");
        createTutorEntityAndPutInDatastore(datastore, tutorEntity, fullName, bio, pfp, email, tutoringTopics, userId);
    } else if (role.toLowerCase().equals("student")) {
        List<String> learningTopics = getTopics(request, "learn");

        Entity studentEntity = new Entity("Student");
        createStudentEntityAndPutInDatastore(datastore, studentEntity, fullName, bio, pfp, email, learningTopics, userId);
    }

    boolean testRegistrationEmail = sendRegistrationEmail(fullName, email);
    
    response.setContentType("application/json;");
    response.getWriter().println("testRegistrationEmail: " + testRegistrationEmail);
    // TODO: Redirect back to page user was at before registration rather than always redirect to homepage, Issue #41
    response.sendRedirect("/homepage.html");
  }

  /**
  * Creates a student entity and puts it in datastore, used for testing
  */
  public void createStudentEntityAndPutInDatastore(DatastoreService ds, Entity entity, String name, String bio, String pfp, String email, List<String> topics, String userId) {
    entity.setProperty("name", name);
    entity.setProperty("bio", bio);
    entity.setProperty("pfp", pfp);
    entity.setProperty("email", email);
    entity.setProperty("learning", topics);
    List<String> tutors = new ArrayList<String>();
    entity.setProperty("tutors", tutors);
    entity.setProperty("userId", userId);
    ds.put(entity);
  }

  /**
  * Creates a tutor entity and puts it in datastore, used for testing
  */
  public void createTutorEntityAndPutInDatastore(DatastoreService ds, Entity entity, String name, String bio, String pfp, String email, List<String> topics, String userId) {
    entity.setProperty("name", name);
    entity.setProperty("bio", bio);
    entity.setProperty("pfp", pfp);
    entity.setProperty("email", email);
    entity.setProperty("topics", topics);
    entity.setProperty("ratingSum", 0);
    entity.setProperty("ratingCount", 0);
    entity.setProperty("rating", 0);
    entity.setProperty("userId", userId);
    ds.put(entity);
  }

 /**
  * Creates a user entity and puts it in datastore, used for testing
  */
  public void createUserEntityAndPutInDatastore(DatastoreService ds, Entity entity, String role, String userId, String fullName, String firstName, String lastName) {
    entity.setProperty("role", role);
    entity.setProperty("userId", userId);
    // If the user has both roles, set the first view to student by default
    if (role.equals("both")) {
        entity.setProperty("view", "student");
    }
    entity.setProperty("fullName", fullName);
    entity.setProperty("firstName", firstName);
    entity.setProperty("lastName", lastName);
    ds.put(entity);
  }

  /**
  * Sends a welcome email to the user after they register
  */
  public boolean sendRegistrationEmail(String name, String email) {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        String to = email;
        String subject = "Welcome to Sullivan";
        String userName = name;
        String message = "Dear " + userName + ",\n" +
                        "Welcome to Sullivan! You have successfully registered with us. " +
                        "We are very happy to have you here!\n" + 
                        "The Sullivan Team";

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

    public List<String> getTopics(HttpServletRequest request, String type) {
        // Make list of selected topics, remove unchecked topics
        List<Optional<String>> topics = new ArrayList<Optional<String>>();
        topics.add(Optional.ofNullable(request.getParameter("math-" + type)));
        topics.add(Optional.ofNullable(request.getParameter("physics-" + type)));
        topics.add(Optional.ofNullable(request.getParameter("chemistry-" + type)));
        topics.add(Optional.ofNullable(request.getParameter("biology-" + type)));
        topics.add(Optional.ofNullable(request.getParameter("computer-science-" + type)));
        topics.add(Optional.ofNullable(request.getParameter("social-studies-" + type)));
        topics.add(Optional.ofNullable(request.getParameter("english-" + type)));
        topics.add(Optional.ofNullable(request.getParameter("spanish-" + type)));
        topics.add(Optional.ofNullable(request.getParameter("french-" + type)));
        topics.add(Optional.ofNullable(request.getParameter("chinese-" + type)));
        List<String> topicsToStr = topics
                                    .stream()
                                    .filter(t -> t.isPresent())
                                    .map(t -> t.get().toLowerCase())
                                    .collect(Collectors.toList());

        // Add blank entry to topics list to know where default topics end and custom topics begin
        topicsToStr.add(" ");

        String otherTopics = Optional.ofNullable(request.getParameter("other-" + type)).orElse("");
        if (!otherTopics.equals("")) {
            // Split the list, removing commas and whitespace, and add to the rest of the topics
            List<String> otherTopicsToList = Arrays.asList(otherTopics.split("\\s*,\\s*"));
            for (String otherTopic : otherTopicsToList) {
                topicsToStr.add(otherTopic);
            }
        }

        return topicsToStr;
    }
}
