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
import java.lang.String;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import com.google.gson.Gson;

/** Accesses Datastore to implement the SearchDatastoreService methods. */
public final class RealSearchDatastore implements SearchDatastoreService {

    /**
    * Gets a list of tutors from datastore that have the specified topic as a skill.
    * @return List<Tutor>
    */
    @Override
    public List<Tutor> getTutorsForTopic(String topic) {

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Filter topicFilter = new FilterPredicate("topics", FilterOperator.EQUAL, topic.toLowerCase());
        Query tutorQuery = new Query("Tutor").setFilter(topicFilter);

        PreparedQuery tutorResults = datastore.prepare(tutorQuery);

        ArrayList<Tutor> tutors = new ArrayList<Tutor>();

        for (Entity tutorEntity : tutorResults.asIterable()) {

            String name = (String) tutorEntity.getProperty("name");
            String email = (String) tutorEntity.getProperty("email");
            ArrayList skills = (ArrayList) tutorEntity.getProperty("topics");
            ArrayList<TimeRange> availability = getTimeRanges(datastore, (ArrayList) tutorEntity.getProperty("availability"));
            ArrayList<TutorSession> scheduledSessions = getScheduledSessions(datastore, (ArrayList) tutorEntity.getProperty("scheduledSessions"));

            Tutor tutor = new Tutor(name, email, skills, availability, scheduledSessions);

            tutors.add(tutor);
            
        }

        return tutors;
        
    }

    /**
    * Gets all the tutor session entities corresponding to the ids in sessionIds and creates TutorSession objects.
    * @return ArrayList<TutorSession>
    */
    private ArrayList<TutorSession> getScheduledSessions(DatastoreService datastore, List<Long> sessionIds) {
        ArrayList<TutorSession> sessions = new ArrayList<TutorSession>();

        //datastore stores empty lists as null values, so if sessionIds is null, there are no scheduled sessions
        if(sessionIds == null) {
            return sessions;
        }

        for(Long id : sessionIds) {
            Key tutorSessionKey = KeyFactory.createKey("TutorSession", id);

            try {
                Entity entity = datastore.get(tutorSessionKey);
                String studentEmail = (String) entity.getProperty("studentEmail");
                String tutorEmail = (String) entity.getProperty("tutorEmail");
                String subtopics = (String) entity.getProperty("subtopics");
                String questions = (String) entity.getProperty("questions");
                TimeRange timeslot = getTimeRange(datastore, (long) entity.getProperty("timeslot"));

                sessions.add(new TutorSession(studentEmail, tutorEmail, subtopics, questions, timeslot));
            } catch (EntityNotFoundException e)  {
                //The tutoring session doesn't exist, skip this id
            }
        }

        return sessions;
    }

    /**
    * Gets all the time range entities corresponding to the ids in rangeIds and creates TimeRange objects.
    * @return ArrayList<TimeRange>
    */
    private ArrayList<TimeRange> getTimeRanges(DatastoreService datastore, List<Long> rangeIds) {
        ArrayList<TimeRange> availability = new ArrayList<TimeRange>();

        //datastore stores empty lists as null values, so if rangeIds is null, there are no available times
        if(rangeIds == null) {
            return availability;
        }

        for(Long id : rangeIds) {
            try {
                availability.add(getTimeRange(datastore, id));
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
    private TimeRange getTimeRange(DatastoreService datastore, long id) throws EntityNotFoundException{

        Key timeRangeKey = KeyFactory.createKey("TimeRange", id);

        Entity entity = datastore.get(timeRangeKey);
        int start = (int) entity.getProperty("start");
        int end = (int) entity.getProperty("end");
        Calendar date = new Gson().fromJson((String) entity.getProperty("date"), Calendar.class);

        return TimeRange.fromStartToEnd(start, end, date);

    }
}
