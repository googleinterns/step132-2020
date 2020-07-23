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
import java.util.Objects;
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
    
            datastore.put(txn, sessionEntity);

            long timeslotId = updateTimeRangeToScheduled(session.getTutorID(), session.getTimeslot(), (long) sessionEntity.getKey().getId(), datastore, txn);
            sessionEntity.setProperty("timeslot", timeslotId);

            datastore.put(txn, sessionEntity);

            updateTutorsForStudent(datastore, txn, session.getStudentID(), session.getTutorID());

            txn.commit();

        } finally {
          if (txn.isActive()) {
            txn.rollback();
          }
        }
    }

    /**
    * Deletes a TutorSession for the tutor and student.
    * @return boolean, true if successfully deleted, false otherwise
    */
    public void deleteTutorSession(String studentID, long sessionId) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        TransactionOptions options = TransactionOptions.Builder.withXG(true);
        Transaction txn = datastore.beginTransaction(options);

        Key sessionKey = KeyFactory.createKey("TutorSession", sessionId);

        try {
            Entity sessionEntity = datastore.get(sessionKey); 
            
            //if the person trying to delete the session is not the person who scheduled it
            if(!((String)sessionEntity.getProperty("studentID")).equals(studentID)) {
                return;
            }

            updateTimeRangeToAvailable(sessionId, (String) sessionEntity.getProperty("tutorID"), datastore, txn);

            datastore.delete(txn, sessionKey);

            txn.commit();

        } catch (EntityNotFoundException e) {
            //entity doesn't exist, we can't delete it
        } finally {
          if (txn.isActive()) {
            txn.rollback();
          }
        }
    }

    /**
    * Gets a list of all scheduled sessions for a tutor with the given email.
    * @return List<TutorSession>, empty list if the tutor does not exist
    */
    public List<TutorSession> getScheduledSessionsForTutor(String userId) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        return getScheduledSessions(datastore, "tutor", userId);
    }

    /**
    * Gets a list of all scheduled sessions for a student with the given user id.
    * @return List<TutorSession>, empty list if the student does not exist
    */
    public List<TutorSession> getScheduledSessionsForStudent(String userId) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        return getScheduledSessions(datastore, "student", userId);
    }

    /**
    * Adds the given rating to a tutor session and updates the tutor's overall rating.
    * @return boolean, true if session was rated successfully, false otherwise
    */
    public boolean rateTutorSession(long sessionId, String studentID, int rating) {

        boolean success = true;

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        TransactionOptions options = TransactionOptions.Builder.withXG(true);
        Transaction txn = datastore.beginTransaction(options);

        Key sessionKey = KeyFactory.createKey("TutorSession", sessionId);

        try {

            Entity sessionEntity = datastore.get(txn, sessionKey); 

            //if the user trying to rate this session is not the student, return false
            if(!((String) sessionEntity.getProperty("studentID")).equals(studentID)) {
                return false;
            }

            sessionEntity.setProperty("rated", true);
            sessionEntity.setProperty("rating", rating);

            datastore.put(txn, sessionEntity);

            String tutorID = (String) sessionEntity.getProperty("tutorID");

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
    * Gets a TutorSession with the given session id. Used for testing.
    * @return TutorSession
    */
    public TutorSession getScheduledSession(long sessionId) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key sessionKey = KeyFactory.createKey("TutorSession", sessionId);
        try {
            Entity sessionEntity = datastore.get(sessionKey); 
            return createTutorSession(datastore, sessionEntity);
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    private void updateTutorsForStudent(DatastoreService datastore, Transaction txn, String studentId, String tutorId) {
        //get student with id
        Filter filter = new FilterPredicate("userId", FilterOperator.EQUAL, studentId);
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
    * Updates a tutor's rating by adding the rating to the tutor's current rating. 
    */
    private void updateTutorRating(DatastoreService datastore, Transaction txn, String userId, int rating) {

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
    private ArrayList<TutorSession> getScheduledSessions(DatastoreService datastore, String userType, String userId) {
        ArrayList<TutorSession> sessions = new ArrayList<TutorSession>();

        //get all sessions with given user id
        Filter filter = new FilterPredicate(userType + "ID", FilterOperator.EQUAL, userId);
        Query query = new Query("TutorSession").setFilter(filter);

        PreparedQuery sessionEntities = datastore.prepare(query);

        for(Entity entity : sessionEntities.asIterable()) {
            TutorSession session = createTutorSession(datastore, entity);
            if(session != null) {
                sessions.add(session);
            }
        }

        return sessions;
    }

    /**
    * Gets the tutor entity connected to the given user id.
    * @return Entity, null if user does not exist
    */
    public Entity getTutorForUserId(String userId) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        // get the email for the given user id with the given role
        Filter filter = new FilterPredicate("userId", FilterOperator.EQUAL, userId);
        Query query = new Query("Tutor").setFilter(filter);
        PreparedQuery pq = datastore.prepare(query);

        Entity entity = pq.asSingleEntity();

        return entity;
    }

    /**
    * Gets the student entity connected to the given user id.
    * @return Entity, null if user does not exist
    */
    public Entity getStudentForUserId(String userId) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        // get the email for the given user id with the given role
        Filter filter = new FilterPredicate("userId", FilterOperator.EQUAL, userId);
        Query query = new Query("Student").setFilter(filter);
        PreparedQuery pq = datastore.prepare(query);

        Entity entity = pq.asSingleEntity();

        return entity;
    }

    /**
    * Creates a new TutorSession object from a given TutorSession entity.
    * @return TutorSession
    */
    private TutorSession createTutorSession(DatastoreService datastore, Entity entity) {
        try {
            long id = (long) entity.getKey().getId();
            String studentID = (String) entity.getProperty("studentID");
            String tutorID = (String) entity.getProperty("tutorID");
            String subtopics = (String) entity.getProperty("subtopics");
            String questions = (String) entity.getProperty("questions");
            int rating = Math.toIntExact((long) entity.getProperty("rating"));
            Key timeRangeKey = KeyFactory.createKey("TimeRange", (long) entity.getProperty("timeslot"));
            Entity timeEntity = datastore.get(timeRangeKey); 
            TimeRange timeslot = createTimeRange(timeEntity);

            return new TutorSession(studentID, tutorID, subtopics, questions, timeslot, rating, id);

        } catch (EntityNotFoundException e) {
            return null;
        }
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
    private long updateTimeRangeToScheduled(String userId, TimeRange time, long sessionId, DatastoreService datastore, Transaction txn) {
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
        timeEntity.setProperty("tutorID", String.valueOf(sessionId));

        //update in datastore
        datastore.put(txn, timeEntity);

        return (long) timeEntity.getKey().getId();
    }

    /**
    * Updates the TimeRange entity that the student canceled to reflect that it is now available and not scheduled.
    * @return long, the id of the TimeRange entity
    */
    private long updateTimeRangeToAvailable(long sessionId, String tutorID, DatastoreService datastore, Transaction txn) {

        //filter by session id
        Filter timeFilter = new FilterPredicate("tutorID", FilterOperator.EQUAL, String.valueOf(sessionId));

        Query query = new Query("TimeRange").setFilter(timeFilter);

        PreparedQuery pq = datastore.prepare(query);

        //there should only be one result
        Entity timeEntity = pq.asSingleEntity();

        //change the tutorID property back to the tutor the time range was owned by before
        timeEntity.setProperty("tutorID", tutorID);

        //update in datastore
        datastore.put(txn, timeEntity);

        return (long) timeEntity.getKey().getId();
    }

}
