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

import com.google.sps.data.SampleData;
import com.google.sps.data.TimeRange;
import com.google.sps.data.Tutor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;
import java.util.Calendar;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;

/** Accesses Datastore to manage a tutor's available times. */
public final class AvailabilityDatastoreService {

    /**
    * Gets the availability of a tutor with the given user id.
    * @return List<TimeRange>
    */
    public List<TimeRange> getAvailabilityForTutor(String id) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        ArrayList<TimeRange> availability = new ArrayList<TimeRange>();
        
        //get all time ranges with user id
        Filter filter = new FilterPredicate("tutorID", FilterOperator.EQUAL, id);
        Query query = new Query("TimeRange").setFilter(filter);

        PreparedQuery timeRanges = datastore.prepare(query);

        for(Entity time : timeRanges.asIterable()) {
            availability.add(createTimeRange(time));
        }

        return availability;
    }

    /**
    * Adds a new time range to a tutor's availability.
    * @return boolean, true if time was added, false if there was a problem adding it
    */
    public boolean addAvailability(String userId, TimeRange time) {
        boolean added = true;

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        TransactionOptions options = TransactionOptions.Builder.withXG(true);
        Transaction txn = datastore.beginTransaction(options);

        try {
            
            Entity timeEntity = new Entity("TimeRange");

            timeEntity.setProperty("tutorID", userId);
            timeEntity.setProperty("start", time.getStart());
            timeEntity.setProperty("end", time.getEnd());
            timeEntity.setProperty("date", new Gson().toJson(time.getDate()));

            datastore.put(txn, timeEntity);

            txn.commit();

        } catch(NullPointerException e) {
            //the tutor entity does not exist, return false
            added = false;
        } finally {
          if (txn.isActive()) {
            txn.rollback();
          }
        }

        return added;
    }

    /**
    * Deletes a time range from a tutor's availability.
    * @return boolean, true if time was deleted, false if there was a problem deleting it
    */
    public boolean deleteAvailability(String userId, TimeRange time) {
        boolean deleted = true;

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        TransactionOptions options = TransactionOptions.Builder.withXG(true);
        Transaction txn = datastore.beginTransaction(options);

        int start = time.getStart();
        int end = time.getEnd();
        // If timeslot bleeds into next day (i.e. 11:30pm - 12:30am), start time is shown as 1410 and end time 30
        // However in the datastore, start time is 1410 and end time is 1470, so the filter below doesn't work
        // If the end time is less than the start time, update the end time so it can be found in datastore
        if (end < start) {
            end = start + 60;
        }

        //filter by tutor's id and time range properties
        CompositeFilter timeFilter = CompositeFilterOperator.and(FilterOperator.EQUAL.of("tutorID", userId),
                                                                 FilterOperator.EQUAL.of("start", start), 
                                                                 FilterOperator.EQUAL.of("end", end), 
                                                                 FilterOperator.EQUAL.of("date", new Gson().toJson(time.getDate())));

        Query query = new Query("TimeRange").setFilter(timeFilter);

        try {
            PreparedQuery pq = datastore.prepare(query);

            //there should only be one result
            Entity timeEntity = pq.asSingleEntity();

            //delete from datastore
            datastore.delete(txn, timeEntity.getKey());

            txn.commit();

        } catch(NullPointerException e) {
            //the entity does not exist, return false
            deleted = false;
        } finally {
          if (txn.isActive()) {
            txn.rollback();
          }
        }

        return deleted;
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
}
