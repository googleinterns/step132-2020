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
import com.google.sps.data.Goal;
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

/** Accesses Datastore to manage goals. */ 
public final class GoalDatastoreService {

    /**
    * Retrieves a list of goals for the student whose id was passsed to the function.
    */
    public List<Goal> getGoalsByStudent(String studentID) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        List<Goal> goals = new ArrayList<Goal>();
        
        //get all goals with student id
        Filter filter = new FilterPredicate("studentID", FilterOperator.EQUAL, studentID);
        Query query = new Query("Goal").setFilter(filter);

        PreparedQuery results = datastore.prepare(query);

        for(Entity result : results.asIterable()) {
            goals.add(createGoal(result));
        }

        return goals;
    }

    /**
    * Adds a new goal.
    */
    public void addGoal(Goal goal) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        TransactionOptions options = TransactionOptions.Builder.withXG(true);
        Transaction txn = datastore.beginTransaction(options);

        try {
            Entity goalEntity = new Entity("Goal");

            goalEntity.setProperty("studentID", goal.getStudentID());
            goalEntity.setProperty("goal", goal.getGoal());

            datastore.put(txn, goalEntity);

            txn.commit();
        } finally {
          if (txn.isActive()) {
            txn.rollback();
          }
        }
    }

    /**
    * Deletes a goal.
    */
    public void deleteGoal(long id) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        TransactionOptions options = TransactionOptions.Builder.withXG(true);
        Transaction txn = datastore.beginTransaction(options);

        Key goalKey = KeyFactory.createKey("Goal", id);

        try {
            datastore.delete(txn, goalKey);

            txn.commit();
        } finally {
          if (txn.isActive()) {
            txn.rollback();
          }
        }
    }

    /**
    * Creates a Goal object from a given Goal entity.
    */
    private Goal createGoal(Entity entity) {
        String studentID = (String) entity.getProperty("studentID");
        String goal = (String) entity.getProperty("goal");
        long id = (long) entity.getKey().getId();

        return new Goal(studentID, goal, id);
    }

}
