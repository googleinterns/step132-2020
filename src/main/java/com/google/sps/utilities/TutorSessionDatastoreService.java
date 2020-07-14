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
public final class TutorSessionDatastoreService {

    /**
    * Adds a new TutorSession for the tutor and student.
    */
    public void addTutorSession(TutorSession session) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        TransactionOptions options = TransactionOptions.Builder.withXG(true);
        Transaction txn = datastore.beginTransaction(options);

        try {

            Entity sessionEntity = new Entity("TutorSession");

            sessionEntity.setProperty("tutorID", session.getTutorID());
            sessionEntity.setProperty("studentID", session.getStudentID());
            sessionEntity.setProperty("subtopics", session.getSubtopics());
            sessionEntity.setProperty("questions", session.getQuestions());
            sessionEntity.setProperty("rated", session.isRated());
            sessionEntity.setProperty("rating", session.getRating());
            sessionEntity.setProperty("timeslot", updateTimeRange(session.getTutorID(), session.getTimeslot(), (long) sessionEntity.getKey().getId(), datastore, txn));

            datastore.put(txn, sessionEntity);

            txn.commit();

        } finally {
          if (txn.isActive()) {
            txn.rollback();
          }
        }
    }

    /**
    * Gets a list of all scheduled sessions for a tutor with the given user id.
    * @return List<TutorSession>
    */
    public List<TutorSession> getScheduledSessionsForTutor(long userId) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        return getScheduledSessions(datastore, "tutor", userId);
    }

    /**
    * Gets a list of all scheduled sessions for a student with the given user id.
    * @return List<TutorSession>, empty list if the tutor does not exist
    */
    public List<TutorSession> getScheduledSessionsForStudent(long userId) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        return getScheduledSessions(datastore, "student", userId);
    }

    /**
    * Adds the given rating to a tutor session and updates the tutor's overall rating.
    * @return boolean, true if session was rated successfully, false otherwise
    */
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

            long tutorID = (long) sessionEntity.getProperty("tutorID");

            updateTutorRating(datastore, txn, tutorID, rating);

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

    /**
    * Updates a tutor's rating by adding the rating to the tutor's current rating. 
    */
    private void updateTutorRating(DatastoreService datastore, Transaction txn, long userId, int rating) {

        //get tutor with user id
        Filter filter = new FilterPredicate("userId", FilterOperator.EQUAL, userId);
        Query query = new Query("Tutor").setFilter(filter);

        PreparedQuery pq = datastore.prepare(query);

        Entity tutorEntity = pq.asSingleEntity();

        int ratingSum = Math.toIntExact((long) tutorEntity.getProperty("ratingSum"));
        int ratingCount = Math.toIntExact((long) tutorEntity.getProperty("ratingCount"));

        tutorEntity.setProperty("ratingSum", ratingSum + rating);
        tutorEntity.setProperty("ratingCount", ratingCount + 1);

        datastore.put(txn, tutorEntity);

    }

    /**
    * Gets all the tutor session entities for a userType (tutor or student) with the corresponding user id. 
    * @return ArrayList<TutorSession>, empty list if the student does not exist
    */
    private ArrayList<TutorSession> getScheduledSessions(DatastoreService datastore, String userType, long userId) {
        ArrayList<TutorSession> sessions = new ArrayList<TutorSession>();

        //get all sessions with given user id
        Filter filter = new FilterPredicate(userType + "ID", FilterOperator.EQUAL, userId);
        Query query = new Query("TutorSession").setFilter(filter);

        PreparedQuery sessionEntities = datastore.prepare(query);

        for(Entity entity : sessionEntities.asIterable()) {
            try {
                long id = (long) entity.getKey().getId();
                long studentID = (long) entity.getProperty("studentID");
                long tutorID = (long) entity.getProperty("tutorID");
                String subtopics = (String) entity.getProperty("subtopics");
                String questions = (String) entity.getProperty("questions");
                int rating = Math.toIntExact((long) entity.getProperty("rating"));

                Key timeRangeKey = KeyFactory.createKey("TimeRange", (long) entity.getProperty("timeslot"));
                Entity timeEntity = datastore.get(timeRangeKey); 
                TimeRange timeslot = createTimeRange(timeEntity);

                sessions.add(new TutorSession(studentID, tutorID, subtopics, questions, timeslot, rating, id));

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
    private long updateTimeRange(long userId, TimeRange time, long sessionId, DatastoreService datastore, Transaction txn) {
        //filter by tutor's email and time range properties
        CompositeFilter timeFilter = CompositeFilterOperator.and(FilterOperator.EQUAL.of("tutorID", userId),
                                                                 FilterOperator.EQUAL.of("start", time.getStart()), 
                                                                 FilterOperator.EQUAL.of("end", time.getEnd()), 
                                                                 FilterOperator.EQUAL.of("date", new Gson().toJson(time.getDate())));

        Query query = new Query("TimeRange").setFilter(timeFilter);

        PreparedQuery pq = datastore.prepare(query);

        //there should only be one result
        Entity timeEntity = pq.asSingleEntity();

        //change the tutorID property to the sessionId
        //instead of deleting the TimeRange entity, we can just set the tutorID property to the sessionId to indicate that it is a scheduled session 
        timeEntity.setProperty("tutorID", sessionId);

        //update in datastore
        datastore.put(txn, timeEntity);

        return timeEntity.getKey().getId();
    }

}
