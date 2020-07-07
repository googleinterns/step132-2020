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

            timeslots = getTimeRanges(datastore, txn, (ArrayList) tutorEntity.getProperty("availability"));

            txn.commit();
        } finally {
          if (txn.isActive()) {
            txn.rollback();
          }
        }

        return timeslots;
    }

    /**
    * Gets all the time range entities corresponding to the ids in rangeIds and creates TimeRange objects.
    * @return ArrayList<TimeRange>
    */
    private ArrayList<TimeRange> getTimeRanges(DatastoreService datastore, Transaction txn, List<Long> rangeIds) {
        ArrayList<TimeRange> availability = new ArrayList<TimeRange>();

        //datastore stores empty lists as null values, so if rangeIds is null, there are no available times
        if(rangeIds == null) {
            return availability;
        }

        for(Long id : rangeIds) {
            try {
                availability.add(getTimeRange(datastore, txn, id));
            } catch (EntityNotFoundException e)  {
                //The time range doesn't exist, skip this id
            }
        }

        return availability;
    }

    /**
    * Gets the time range entity corresponding to the id and creates a TimeRange object.
    * @return TimeRange
    */
    private TimeRange getTimeRange(DatastoreService datastore, Transaction txn, long id) throws EntityNotFoundException{

        Key timeRangeKey = KeyFactory.createKey("TimeRange", id);

        Entity entity = datastore.get(txn, timeRangeKey);
        int start = Math.toIntExact((long) entity.getProperty("start"));
        int end = Math.toIntExact((long) entity.getProperty("end"));
        Calendar date = new Gson().fromJson((String) entity.getProperty("date"), Calendar.class);

        return TimeRange.fromStartToEnd(start, end, date);

    }
}
