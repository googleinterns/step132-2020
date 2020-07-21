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
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.sps.data.Student;
import com.google.sps.data.TimeRange;
import com.google.sps.data.Tutor;
import com.google.sps.data.TutorSession;
import com.google.sps.utilities.TutorSessionDatastoreService;
import com.google.sps.utilities.AvailabilityDatastoreService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that handles user's registration info */
@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private UserService userService = UserServiceFactory.getUserService();
    private TutorSessionDatastoreService sessionDatastore;
    private AvailabilityDatastoreService availabilityDatastore;

    public void init() {
        sessionDatastore = new TutorSessionDatastoreService();
        availabilityDatastore = new AvailabilityDatastoreService();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        //if the user id is null, the default value will be -1 because no tutor or student will have id = -1
        String userId = Optional.ofNullable(request.getParameter("userId")).orElse("-1");

        if(userId.equals("-1")) {
            response.setContentType("application/json");
            response.getWriter().println("{\"error\": \"There was an error getting profile.\"}");
            return;
        }
        
        // Find out whether the user is a student or a tutor
        Query query = new Query("User").setFilter(new Query.FilterPredicate("userId", Query.FilterOperator.EQUAL, userId));
        PreparedQuery results = datastore.prepare(query);
        Entity entity = results.asSingleEntity();
        if (entity == null) {
            return;
        }
        String role = (String) entity.getProperty("role"); 

        // User is a student, get their info
        if (role.toLowerCase().equals("student")) {
            query = new Query("Student").setFilter(new Query.FilterPredicate("userId", Query.FilterOperator.EQUAL, userId));
            results = datastore.prepare(query);
            entity = results.asSingleEntity();

            String name = (String) entity.getProperty("name");
            String bio = (String) entity.getProperty("bio");
            String pfp = (String) entity.getProperty("pfp");
            String email = (String) entity.getProperty("email");
            ArrayList<String> learning = (ArrayList) entity.getProperty("learning");
            ArrayList<String> tutors = (ArrayList) entity.getProperty("tutors");
            if (tutors == null) {
                tutors = new ArrayList<String> (Arrays.asList());
            }
            ArrayList<TutorSession> scheduledSessions = (ArrayList) sessionDatastore.getScheduledSessionsForStudent(userId);

            Student student = new Student(name, bio, pfp, email, learning, tutors, scheduledSessions, userId);

            String json = new Gson().toJson(student);
            response.getWriter().println(json);
        } else {   // User is a tutor, get their info
            query = new Query("Tutor").setFilter(new Query.FilterPredicate("userId", Query.FilterOperator.EQUAL, userId));
            results = datastore.prepare(query);
            entity = results.asSingleEntity();

            String name = (String) entity.getProperty("name");
            String bio = (String) entity.getProperty("bio");
            String pfp = (String) entity.getProperty("pfp");
            String email = (String) entity.getProperty("email");
            ArrayList<String> skills = (ArrayList) entity.getProperty("topics");
            ArrayList<TimeRange> availability = (ArrayList) availabilityDatastore.getAvailabilityForTutor(userId);
            ArrayList<TutorSession> scheduledSessions = (ArrayList) sessionDatastore.getScheduledSessionsForTutor(userId);
            int ratingCount = Math.toIntExact((long) entity.getProperty("ratingCount"));
            int ratingSum = Math.toIntExact((long) entity.getProperty("ratingSum"));

            Tutor tutor = new Tutor(name, bio, pfp, email, skills, availability, scheduledSessions, ratingCount, ratingSum, userId);

            String json = new Gson().toJson(tutor);
            response.getWriter().println(json);
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {  
        String userId = userService.getCurrentUser().getUserId();
        
        // Don't update profile if cancel button is clicked
        if (request.getParameter("submit").equals("Cancel")) {
            response.sendRedirect("/profile.html?userID=" + userId);
            return;
        }
        
        //Make userId a query filter
        Query query = new Query("User").setFilter(new Query.FilterPredicate("userId", Query.FilterOperator.EQUAL, userId));
        PreparedQuery results = datastore.prepare(query);
        Entity userEntity = results.asSingleEntity();

        String role = (String) userEntity.getProperty("role"); 
        String bio = Optional.ofNullable(request.getParameter("bio")).orElse("");
        // Make list of selected topics, remove unchecked topics
        List<Optional<String>> topics = new ArrayList<Optional<String>>();
        topics.add(Optional.ofNullable(request.getParameter("math")));
        topics.add(Optional.ofNullable(request.getParameter("physics")));
        topics.add(Optional.ofNullable(request.getParameter("chemistry")));
        topics.add(Optional.ofNullable(request.getParameter("biology")));
        topics.add(Optional.ofNullable(request.getParameter("computer-science")));
        topics.add(Optional.ofNullable(request.getParameter("social-studies")));
        topics.add(Optional.ofNullable(request.getParameter("english")));
        topics.add(Optional.ofNullable(request.getParameter("spanish")));
        topics.add(Optional.ofNullable(request.getParameter("french")));
        topics.add(Optional.ofNullable(request.getParameter("chinese")));
        List<String> topicsToStr = topics
                                    .stream()
                                    .filter(t -> t.isPresent())
                                    .map(t -> t.get())
                                    .collect(Collectors.toList());

        // Add blank entry to topics list to know where default topics end and custom topics begin
        topicsToStr.add(" ");

        String otherTopics = Optional.ofNullable(request.getParameter("other")).orElse("");
        if (!otherTopics.equals("")) {
            // Split the list, removing commas and whitespace, and add to the rest of the topics
            List<String> otherTopicsToList = Arrays.asList(otherTopics.split("\\s*,\\s*"));
            for (String otherTopic : otherTopicsToList) {
                topicsToStr.add(otherTopic);
            }
        }

        // User is a student, update their info
        if (role.toLowerCase().equals("student")) {
            Entity studentEntity = new Entity("Student");
            updateStudentEntityAndPutInDatastore(datastore, studentEntity, userId, bio, topicsToStr);
        } else {   // User is a tutor, update their info
            Entity tutorEntity = new Entity("Tutor");
            updateTutorEntityAndPutInDatastore(datastore, tutorEntity, userId, bio, topicsToStr);
        }
        response.sendRedirect("/profile.html?userID=" + userId);
    }

    /**
    * Updates a student entity and puts it in datastore, used for testing
    */
    public void updateStudentEntityAndPutInDatastore(DatastoreService ds, Entity entity, String userId, String bio, List<String> topics) {
        Query query = new Query("Student").setFilter(new Query.FilterPredicate("userId", Query.FilterOperator.EQUAL, userId));
        PreparedQuery results = ds.prepare(query);
        entity = results.asSingleEntity();
        if (entity == null) {
            return;
        }
        entity.setProperty("bio", bio);
        entity.setProperty("learning", topics);
        ds.put(entity);
    }

    /**
    * Updates a tutor entity and puts it in datastore, used for testing
    */
    public void updateTutorEntityAndPutInDatastore(DatastoreService ds, Entity entity, String userId, String bio, List<String> topics) {
        Query query = new Query("Tutor").setFilter(new Query.FilterPredicate("userId", Query.FilterOperator.EQUAL, userId));
        PreparedQuery results = ds.prepare(query);   
        entity = results.asSingleEntity();
        if (entity == null) {
            return;
        }
        entity.setProperty("bio", bio);
        entity.setProperty("topics", topics);
        ds.put(entity);
    }
}
