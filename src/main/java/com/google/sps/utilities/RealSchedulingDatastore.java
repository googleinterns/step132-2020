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
        Transaction txn = datastore.beginTransaction();

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
