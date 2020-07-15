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
import com.google.sps.data.Tutor;
import com.google.sps.data.Student;
import com.google.sps.data.TimeRange;
import com.google.sps.data.TutorSession;
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
import java.lang.String;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import com.google.gson.Gson;

/** Accesses datastore to get students that have had a tutoring session with the given tutor. */ 
public final class GetStudentsSerice {

    /**
    * Gets all students that the given userId was or will be a tutor for.
    * @return ArrayList<Student>, empty list if no students
    */
    public ArrayList<Student> getStudentsForTutor(String userId) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query studentQuery = new Query("Student");
        PreparedQuery studentResults = datastore.prepare(studentQuery);

        ArrayList<Student> students = new ArrayList<Student>();

        for (Entity studentEntity : studentResults.asIterable()) {

            if ((ArrayList) studentEntity.getProperty("tutors").contains(userId)) {
                String name = (String) studentEntity.getProperty("name");
                String userId = (String) studentEntity.getProperty("userId");
                String bio = (String) studentEntity.getProperty("bio");
                String pfp = (String) studentEntity.getProperty("pfp");
                String email = (String) studentEntity.getProperty("email");
                ArrayList learning = (ArrayList) studentEntity.getProperty("learning");

                Student student = new Student(name, userId, bio, pfp, email, learning);

                students.add(student);
            }
        }

        return students;
    }
}