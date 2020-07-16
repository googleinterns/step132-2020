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
import com.google.sps.data.Experience;
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

/** Accesses Datastore to manage experiences. */ 
public final class ExperienceDatastoreService {

    /**
    * Retrieves a list of experiences for the student whose id was passsed to the function.
    */
    public List<Experience> getExperiencesByStudent(String studentID) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        List<Experience> experiences = new ArrayList<Experience>();
        
        //get all experiences with student id
        Filter filter = new FilterPredicate("studentID", FilterOperator.EQUAL, studentID);
        Query query = new Query("Experience").setFilter(filter);

        PreparedQuery results = datastore.prepare(query);

        for(Entity result : results.asIterable()) {
            experiences.add(createExperience(result));
        }

        return experiences;
    }

    /**
    * Adds a new experience.
    */
    public void addExperience(Experience experience) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        TransactionOptions options = TransactionOptions.Builder.withXG(true);
        Transaction txn = datastore.beginTransaction(options);

        try {
            Entity experienceEntity = new Entity("Experience");

            experienceEntity.setProperty("studentID", experience.getStudentID());
            experienceEntity.setProperty("experience", experience.getExperience());

            datastore.put(txn, experienceEntity);

            txn.commit();
        } finally {
          if (txn.isActive()) {
            txn.rollback();
          }
        }
    }

    /**
    * Creates a Experience object from a given Experience entity.
    */
    private Experience createExperience(Entity entity) {
        String studentID = (String) entity.getProperty("studentID");
        String experience = (String) entity.getProperty("experience");

        return new Experience(studentID, experience);
    }

}
