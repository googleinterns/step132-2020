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

package com.google.sps.utilities;

import com.google.sps.data.TutorSession;
import com.google.sps.data.TimeRange;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import java.lang.String;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import com.google.gson.Gson;

/** Accesses Datastore to manage tutoring sessions. */ 
public final class RealTutorSessionDatastore implements TutorSessionDatastoreService {

    /**
    * Adds a new TutorSession for the tutor and student.
    */
    @Override
    public void addTutorSession(String tutorEmail, String studentEmail, TutorSession session) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        TransactionOptions options = TransactionOptions.Builder.withXG(true);
        Transaction txn = datastore.beginTransaction(options);

        try {

            Entity sessionEntity = new Entity("TutorSession");

            sessionEntity.setProperty("tutorEmail", tutorEmail.toLowerCase());
            sessionEntity.setProperty("studentEmail", studentEmail.toLowerCase());
            sessionEntity.setProperty("subtopics", session.getSubtopics());
            sessionEntity.setProperty("questions", session.getQuestions());
            sessionEntity.setProperty("rated", session.isRated());
            sessionEntity.setProperty("rating", session.getRating());
            sessionEntity.setProperty("timeslot", updateTimeRange(tutorEmail, session.getTimeslot(), datastore, txn));

            datastore.put(txn, sessionEntity);

            updateTutorsForStudent(datastore, txn, studentEmail.toLowerCase(), tutorEmail.toLowerCase());

            txn.commit();

        } finally {
          if (txn.isActive()) {
            txn.rollback();
          }
        }
    }

    /**
    * Deletes a TutorSession for the tutor and student.
    */
    @Override
    public void deleteTutorSession(TutorSession session) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        TransactionOptions options = TransactionOptions.Builder.withXG(true);
        Transaction txn = datastore.beginTransaction(options);

        long sessionId = session.getId();
        Key sessionKey = KeyFactory.createKey("TutorSession", sessionId);

        try {            
            updateTimeRangeToAvailable(session.getTutorEmail().toLowerCase(), session.getTimeslot(), datastore, txn);

            datastore.delete(txn, sessionKey);

            txn.commit();
        } finally {
          if (txn.isActive()) {
            txn.rollback();
          }
        }
    }

    /**
    * Gets a list of all scheduled sessions for a tutor with the given email.
    * @return List<TutorSession>
    */
    @Override
    public List<TutorSession> getScheduledSessionsForTutor(String email) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        return getScheduledSessions(datastore, "tutor", email);
    }

    /**
    * Gets a list of all scheduled sessions for a student with the given email.
    * @return List<TutorSession>, empty list if the tutor does not exist
    */
    @Override
    public List<TutorSession> getScheduledSessionsForStudent(String email) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        return getScheduledSessions(datastore, "student", email);
    }

    /**
    * Adds the given rating to a tutor session and updates the tutor's overall rating.
    * @return boolean, true if session was rated successfully, false otherwise
    */
    @Override
    public boolean rateTutorSession(long sessionId, int rating) {

        boolean success = true;

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        TransactionOptions options = TransactionOptions.Builder.withXG(true);
        Transaction txn = datastore.beginTransaction(options);

        Key sessionKey = KeyFactory.createKey("TutorSession", sessionId);

        try {

            Entity sessionEntity = datastore.get(txn, sessionKey); 

            sessionEntity.setProperty("rated", true);
            sessionEntity.setProperty("rating", rating);

            datastore.put(txn, sessionEntity);

            String tutorEmail = (String) sessionEntity.getProperty("tutorEmail");

            updateTutorRating(datastore, txn, tutorEmail, rating);

            txn.commit();
            
        } catch (EntityNotFoundException e) {
            success = false;
        } finally {
          if (txn.isActive()) {
            txn.rollback();
          }
        }

        return success;

    }

    private void updateTutorRating(DatastoreService datastore, Transaction txn, String email, int rating) {

        //get tutor with email
        Filter filter = new FilterPredicate("email", FilterOperator.EQUAL, email.toLowerCase());
        Query query = new Query("Tutor").setFilter(filter);

        PreparedQuery pq = datastore.prepare(query);

        Entity tutorEntity = pq.asSingleEntity();

        int ratingSum = Math.toIntExact((long) tutorEntity.getProperty("ratingSum"));
        int ratingCount = Math.toIntExact((long) tutorEntity.getProperty("ratingCount"));

        tutorEntity.setProperty("ratingSum", ratingSum + rating);
        tutorEntity.setProperty("ratingCount", ratingCount + 1);

        datastore.put(txn, tutorEntity);

    }

    private void updateTutorsForStudent(DatastoreService datastore, Transaction txn, String studentId, String tutorId) {
        //get student with id
        Filter filter = new FilterPredicate("email", FilterOperator.EQUAL, studentId);
        Query query = new Query("Student").setFilter(filter);

        PreparedQuery pq = datastore.prepare(query);

        Entity studentEntity = pq.asSingleEntity();

        List<String> tutors = (List<String>) studentEntity.getProperty("tutors");
        if (tutors == null) {
            tutors = new ArrayList<String>();
        }

        tutors.add(tutorId);

        studentEntity.setProperty("tutors", tutors);

        datastore.put(txn, studentEntity);
    }

    /**
    * Gets all the tutor session entities for a userType (tutor or student) with the corresponding email. 
    * @return ArrayList<TutorSession>, empty list if the student does not exist
    */
    private ArrayList<TutorSession> getScheduledSessions(DatastoreService datastore, String userType, String email) {
        ArrayList<TutorSession> sessions = new ArrayList<TutorSession>();

        //get all sessions with given email
        Filter filter = new FilterPredicate(userType + "Email", FilterOperator.EQUAL, email.toLowerCase());
        Query query = new Query("TutorSession").setFilter(filter);

        PreparedQuery sessionEntities = datastore.prepare(query);

        for(Entity entity : sessionEntities.asIterable()) {
            try {

                long id = (long) entity.getKey().getId();
                String studentEmail = (String) entity.getProperty("studentEmail");
                String tutorEmail = (String) entity.getProperty("tutorEmail");
                String subtopics = (String) entity.getProperty("subtopics");
                String questions = (String) entity.getProperty("questions");
                int rating = Math.toIntExact((long) entity.getProperty("rating"));

                Key timeRangeKey = KeyFactory.createKey("TimeRange", (long) entity.getProperty("timeslot"));
                Entity timeEntity = datastore.get(timeRangeKey); 
                TimeRange timeslot = createTimeRange(timeEntity);

                sessions.add(new TutorSession(studentEmail, tutorEmail, subtopics, questions, timeslot, rating, id));

            } catch (EntityNotFoundException e) {
                //timeslot was not found, skip this tutoring session
            }
           
        }

        return sessions;
    }

     /**
    * Creates a TimeRange object from a given TimeRange entity.
    * @return TimeRange
    */
    private TimeRange createTimeRange(Entity entity) {
        int start = Math.toIntExact((long) entity.getProperty("start"));
        int end = Math.toIntExact((long) entity.getProperty("end"));
        Calendar date = new Gson().fromJson((String) entity.getProperty("date"), Calendar.class);

        return TimeRange.fromStartToEnd(start, end, date);
    }

    /**
    * Updates the TimeRange entity that the student selected to reflect that it is now scheduled and not available.
    * @return long, the id of the TimeRange entity
    */
    private long updateTimeRange(String email, TimeRange time, DatastoreService datastore, Transaction txn) {
        //filter by tutor's email and time range properties
        CompositeFilter timeFilter = CompositeFilterOperator.and(FilterOperator.EQUAL.of("email", email),
                                                                 FilterOperator.EQUAL.of("start", time.getStart()), 
                                                                 FilterOperator.EQUAL.of("end", time.getEnd()), 
                                                                 FilterOperator.EQUAL.of("date", new Gson().toJson(time.getDate())));

        Query query = new Query("TimeRange").setFilter(timeFilter);

        PreparedQuery pq = datastore.prepare(query);

        //there should only be one result
        Entity timeEntity = pq.asSingleEntity();

        //change the email property from the tutor's email to "scheduled"
        //instead of deleting the TimeRange entity, we can just set the email property to "scheduled" to indicate that it is a scheduled session 
        timeEntity.setProperty("email", "scheduled");

        //update in datastore
        datastore.put(txn, timeEntity);

        return timeEntity.getKey().getId();
    }

    /**
    * Updates the TimeRange entity that the student canceled to reflect that it is now available and not scheduled.
    * @return long, the id of the TimeRange entity
    */
    private long updateTimeRangeToAvailable(String email, TimeRange time, DatastoreService datastore, Transaction txn) {
        //filter by tutor's email and time range properties
        CompositeFilter timeFilter = CompositeFilterOperator.and(FilterOperator.EQUAL.of("email", "scheduled"),
                                                                 FilterOperator.EQUAL.of("start", time.getStart()), 
                                                                 FilterOperator.EQUAL.of("end", time.getEnd()), 
                                                                 FilterOperator.EQUAL.of("date", new Gson().toJson(time.getDate())));

        Query query = new Query("TimeRange").setFilter(timeFilter);

        PreparedQuery pq = datastore.prepare(query);

        //there should only be one result
        Entity timeEntity = pq.asSingleEntity();

        //change the email property from "scheduled" to the tutor's email
        timeEntity.setProperty("email", email);

        //update in datastore
        datastore.put(txn, timeEntity);

        return timeEntity.getKey().getId();
    }

}
