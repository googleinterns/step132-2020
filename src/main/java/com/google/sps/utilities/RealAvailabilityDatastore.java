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
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;
import java.util.Calendar;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;

/** Accesses Datastore to get a tutor's available times. */
public final class RealAvailabilityDatastore implements AvailabilityDatastoreService {

    /**
    * Gets the availability of a tutor with the given email.
    * @return List<TimeRange>
    */
    @Override
    public List<TimeRange> getAvailabilityForTutor(String email) {

        List<TimeRange> timeslots = new ArrayList<TimeRange>();

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        TransactionOptions options = TransactionOptions.Builder.withXG(true);
        Transaction txn = datastore.beginTransaction();

        Filter filter = new FilterPredicate("email", FilterOperator.EQUAL, email.toLowerCase());
        Query tutorQuery = new Query("Tutor").setFilter(filter);

        try {
            PreparedQuery pq = datastore.prepare(tutorQuery);

            //there should only be one result
            Entity tutorEntity = pq.asSingleEntity();

            timeslots = getTimeRanges(datastore, txn, tutorEntity.getKey());

            txn.commit();

        } catch(NullPointerException e) {
            //the entity does not exist, skip to finally statement and return empty list
        } finally {
          if (txn.isActive()) {
            txn.rollback();
          }
        }

        return timeslots;
    }

    /**
    * Gets all the time range entities corresponding that have the given tutorId.
    * @return ArrayList<TimeRange>
    */
    private ArrayList<TimeRange> getTimeRanges(DatastoreService datastore, Transaction txn, Key tutorKey) {
        ArrayList<TimeRange> availability = new ArrayList<TimeRange>();
        
        //Use ancestor query to get all time ranges that belong to the tutor
        Query query = new Query("TimeRange", tutorKey);
        PreparedQuery timeRanges = datastore.prepare(txn, query);

        for(Entity time : timeRanges.asIterable()) {
            availability.add(createTimeRange(time));
        }

        return availability;
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