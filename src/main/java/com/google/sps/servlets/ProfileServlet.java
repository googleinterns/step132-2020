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
import com.google.sps.utilities.RealTutorSessionDatastore;
import com.google.sps.utilities.MockTutorSessionDatastore;
import com.google.sps.utilities.AvailabilityDatastoreService;
import com.google.sps.utilities.MockAvailabilityDatastore;
import com.google.sps.utilities.RealAvailabilityDatastore;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
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

    /**
    * Because we created a constructor with a parameter (the testing one), the default empty constructor does not work anymore so we have to explicitly create it. 
    * We need the default one for deployment because the servlet is created without parameters.
    */
    public ProfileServlet(){}

    public ProfileServlet(boolean test) {
        if(test) {
            sessionDatastore = new MockTutorSessionDatastore();
            availabilityDatastore = new MockAvailabilityDatastore();
        }
    }

    public void init() {
        sessionDatastore = new RealTutorSessionDatastore();
        availabilityDatastore = new RealAvailabilityDatastore();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        
        String userId = Optional.ofNullable(request.getParameter("user-id")).orElse(null);
        
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
            ArrayList<TutorSession> scheduledSessions = (ArrayList) sessionDatastore.getScheduledSessionsForStudent(email);

            Student student = new Student(name, bio, pfp, email, learning, scheduledSessions);

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
            ArrayList<TimeRange> availability = (ArrayList) availabilityDatastore.getAvailabilityForTutor(email);
            ArrayList<TutorSession> scheduledSessions = (ArrayList) sessionDatastore.getScheduledSessionsForTutor(email);

            Tutor tutor = new Tutor(name, bio, pfp, email, skills, availability, scheduledSessions);

            String json = new Gson().toJson(tutor);
            response.getWriter().println(json);
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    }
}
