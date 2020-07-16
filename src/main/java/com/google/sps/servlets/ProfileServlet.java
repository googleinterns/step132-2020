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
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.gson.Gson;
import com.google.sps.data.Student;
import com.google.sps.data.TimeRange;
import com.google.sps.data.Tutor;
import com.google.sps.data.TutorSession;
import com.google.sps.utilities.TutorSessionDatastoreService;
import com.google.sps.utilities.AvailabilityDatastoreService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Arrays;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that handles user's registration info */
@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
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

    }
}
