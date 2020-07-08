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
public final class RealSchedulingDatastore implements SchedulingDatastoreService {

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

            sessionEntity.setProperty("tutorEmail", tutorEmail);
            sessionEntity.setProperty("studentEmail", studentEmail);
            sessionEntity.setProperty("subtopics", session.getSubtopics());
            sessionEntity.setProperty("questions", session.getQuestions());
            sessionEntity.setProperty("isRated", session.isRated());
            sessionEntity.setProperty("rating", session.getRating());
            sessionEntity.setProperty("timeslot", updateTimeRange(tutorEmail, session.getTimeslot(), datastore, txn));

            datastore.put(txn, sessionEntity);

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
    public List<TutorSession> getScheduledSessionForTutor(String email) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        return getScheduledSessions(datastore, "tutor", email);
    }

    /**
    * Gets a list of all scheduled sessions for a student with the given email.
    * @return List<TutorSession>
    */
    @Override
    public List<TutorSession> getScheduledSessionForStudent(String email) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        return getScheduledSessions(datastore, "student", email);
    }

    /**
    * Gets all the tutor session entities for a userType (tutor or student) with the corresponding email. 
    * @return ArrayList<TutorSession>
    */
    private ArrayList<TutorSession> getScheduledSessions(DatastoreService datastore, String userType, String email) {
        ArrayList<TutorSession> sessions = new ArrayList<TutorSession>();

        //get all sessions with given email
        Filter filter = new FilterPredicate(userType + "Email", FilterOperator.EQUAL, email);
        Query query = new Query("TutorSession").setFilter(filter);

        PreparedQuery sessionEntities = datastore.prepare(query);

        for(Entity entity : sessionEntities.asIterable()) {
            try {

                String studentEmail = (String) entity.getProperty("studentEmail");
                String tutorEmail = (String) entity.getProperty("tutorEmail");
                String subtopics = (String) entity.getProperty("subtopics");
                String questions = (String) entity.getProperty("questions");

                Key timeRangeKey = KeyFactory.createKey("TimeRange", (long) entity.getProperty("timeslot"));
                Entity timeEntity = datastore.get(timeRangeKey); 
                TimeRange timeslot = createTimeRange(timeEntity);

                sessions.add(new TutorSession(studentEmail, tutorEmail, subtopics, questions, timeslot));

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

}
