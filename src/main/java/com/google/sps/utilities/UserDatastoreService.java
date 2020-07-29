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

/** Accesses Datastore to manage a user. */
public final class UserDatastoreService {

    /**
    * Gets the view of the given user
    * @return String
    */
    public String getUserView(String id) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        
        // get all time ranges with user id
        Filter filter = new FilterPredicate("userId", FilterOperator.EQUAL, id);
        Query query = new Query("User").setFilter(filter);

        PreparedQuery pq = datastore.prepare(query);

        //there should only be one result
        Entity entity = pq.asSingleEntity();

        return (String) entity.getProperty("view");
    }

    /**
    * Switch user's view
    * @return void
    */
    public void switchUserView(String id) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        TransactionOptions options = TransactionOptions.Builder.withXG(true);
        Transaction txn = datastore.beginTransaction(options);

        try {        
            // get all time ranges with user id
            Filter filter = new FilterPredicate("userId", FilterOperator.EQUAL, id);
            Query query = new Query("User").setFilter(filter);

            PreparedQuery pq = datastore.prepare(query);

            //there should only be one result
            Entity entity = pq.asSingleEntity();

            String oldView = (String) entity.getProperty("view");

            if (oldView.equals("student")) {
                entity.setProperty("view", "tutor");
            } else {
                entity.setProperty("view", "student");
            }

            datastore.put(txn, entity);

            txn.commit();
        } finally {
          if (txn.isActive()) {
            txn.rollback();
          }
        }

    }
}
