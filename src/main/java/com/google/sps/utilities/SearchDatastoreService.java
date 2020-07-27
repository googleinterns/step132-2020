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
import com.google.appengine.api.datastore.Query.SortDirection;
import java.lang.String;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import com.google.gson.Gson;

/** Accesses datastore to get tutors for a specific topic. */ 
public final class SearchDatastoreService {

    /**
    * Gets a list of tutors from datastore that have the specified topic as a skill. If a tutor is searching, 
    * it should not return that tutor in results.
    * @return List<Tutor>
    */
    public List<Tutor> getTutorsForTopic(String topic, String studentID, String sortType) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Filter topicFilter = new FilterPredicate("topics", FilterOperator.EQUAL, topic.toLowerCase());
        Query tutorQuery = new Query("Tutor").setFilter(topicFilter);

        switch(sortType.toLowerCase()) {
            case "rating":
                tutorQuery.addSort("rating", SortDirection.DESCENDING);
                break;
            default:
                tutorQuery.addSort("name", SortDirection.ASCENDING);
        }

        ArrayList<Tutor> tutors = new ArrayList<Tutor>();

        PreparedQuery tutorResults = datastore.prepare(tutorQuery);

        for (Entity tutorEntity : tutorResults.asIterable()) {
            
            String userId = (String) tutorEntity.getProperty("userId");
            //don't return the tutor that is currently searching
            if(userId.equals(studentID)) {
                continue;
            }

            String name = (String) tutorEntity.getProperty("name");
            String bio = (String) tutorEntity.getProperty("bio");
            String pfp = (String) tutorEntity.getProperty("pfp");
            String email = (String) tutorEntity.getProperty("email");
            ArrayList skills = (ArrayList) tutorEntity.getProperty("topics");
            ArrayList<TimeRange> availability = getTimeRanges(datastore, userId);
            ArrayList<TutorSession> scheduledSessions = getScheduledSessions(datastore, userId);
            int ratingCount = Math.toIntExact((long) tutorEntity.getProperty("ratingCount"));
            int ratingSum = Math.toIntExact((long) tutorEntity.getProperty("ratingSum"));

            Tutor tutor = new Tutor(name, bio, pfp, email, skills, availability, scheduledSessions, userId);

            tutors.add(tutor);
            
        }

        return tutors;
        
    }

    /**
    * Gets all the tutor session entities with the corresponding user id. 
    * @return ArrayList<TutorSession>
    */
    private ArrayList<TutorSession> getScheduledSessions(DatastoreService datastore, String userId) {
        ArrayList<TutorSession> sessions = new ArrayList<TutorSession>();

        //get all sessions with user id
        Filter filter = new FilterPredicate("tutorID", FilterOperator.EQUAL, userId);
        Query query = new Query("TutorSession").setFilter(filter);

        PreparedQuery sessionEntities = datastore.prepare(query);

        for(Entity entity : sessionEntities.asIterable()) {
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

                sessions.add(new TutorSession(studentID, tutorID, subtopics, questions, timeslot, rating, id));

            } catch (EntityNotFoundException e) {
                //timeslot was not found, skip this tutoring session
            }
           
        }

        return sessions;
    }

    /**
    * Gets all the time range entities that have the given user id.
    * @return ArrayList<TimeRange>
    */
    private ArrayList<TimeRange> getTimeRanges(DatastoreService datastore, String userId) {
        ArrayList<TimeRange> availability = new ArrayList<TimeRange>();
        
        //get all time ranges with tutor id
        Filter filter = new FilterPredicate("tutorID", FilterOperator.EQUAL, userId);
        Query query = new Query("TimeRange").setFilter(filter);

        PreparedQuery timeRanges = datastore.prepare(query);

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
