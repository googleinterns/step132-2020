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

package com.google.sps.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.Filter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import com.google.gson.Gson;


/** Class that stores sample Tutor objects for testing. */
public final class SampleData {
    private final int TIME_0800AM = TimeRange.getTimeInMinutes(8, 00);
    private final int TIME_0900AM = TimeRange.getTimeInMinutes(9, 00);
    private final int TIME_1000AM = TimeRange.getTimeInMinutes(10, 00);
    private final int TIME_1100AM = TimeRange.getTimeInMinutes(11, 00);
    private final int TIME_1200AM = TimeRange.getTimeInMinutes(12, 00);
    private final int TIME_0100PM = TimeRange.getTimeInMinutes(13, 00);
    private final int TIME_0200PM = TimeRange.getTimeInMinutes(14, 00);
    private final int TIME_0300PM = TimeRange.getTimeInMinutes(15, 00);
    private final int TIME_0500PM = TimeRange.getTimeInMinutes(17, 00);
    
    private final Calendar MAY182020 = new Calendar.Builder()
                                                        .setCalendarType("iso8601")
                                                        .setDate(2020, 4, 18)
                                                        .build();
    private final Calendar AUGUST102020 = new Calendar.Builder()
                                                        .setCalendarType("iso8601")
                                                        .setDate(2020, 7, 10)
                                                        .build();

    private final Calendar AUGUST72020 = new Calendar.Builder()
                                                        .setCalendarType("iso8601")
                                                        .setDate(2020, 7, 7)
                                                        .build();
    private final Calendar AUGUST182020 = new Calendar.Builder()
                                                        .setCalendarType("iso8601")
                                                        .setDate(2020, 7, 18)
                                                        .build();
    //9 and 14 are the ids local datastore gives to these entities
    private final TutorSession bernardoSession = new TutorSession("1", "1", null, null, TimeRange.fromStartToEnd(540, 600, MAY182020), 9); 
    private final TutorSession samSession = new TutorSession("2", "2", null, null, TimeRange.fromStartToEnd(540, 600, AUGUST182020), 14);

    private ArrayList<Tutor> tutors = new ArrayList<Tutor> (Arrays.asList(
        new Tutor("Kashish Arora", "Kashish\'s bio", "images/pfp.jpg", "kashisharora@google.com", new ArrayList<String> (Arrays.asList("math", "history")),
                new ArrayList<TimeRange> (Arrays.asList(TimeRange.fromStartToEnd(TIME_1200AM, TIME_0100PM, MAY182020),
                            TimeRange.fromStartToEnd(TIME_0300PM,TIME_0500PM, AUGUST102020))),
                new ArrayList<TutorSession> (Arrays.asList()), "0"),
        new Tutor("Bernardo Eilert Trevisan", "Bernardo\'s bio", "images/pfp.jpg", "btrevisan@google.com", new ArrayList<String> (Arrays.asList("english", "physics")),
                new ArrayList<TimeRange> (Arrays.asList(TimeRange.fromStartToEnd(TIME_0800AM, TIME_1000AM, MAY182020),
                             TimeRange.fromStartToEnd(TIME_1100AM,TIME_0100PM, AUGUST102020),
                             TimeRange.fromStartToEnd(TIME_0100PM, TIME_0300PM, AUGUST72020))),
                new ArrayList<TutorSession> (Arrays.asList(bernardoSession)), "1"),
        new Tutor("Sam Falberg", "Sam\'s bio", "images/pfp.jpg", "sfalberg@google.com", new ArrayList<String> (Arrays.asList("geology", "english")),
                new ArrayList<TimeRange> (Arrays.asList(TimeRange.fromStartToEnd(TIME_1000AM, TIME_1200AM, MAY182020),
                            TimeRange.fromStartToEnd(TIME_0100PM,TIME_0200PM, AUGUST102020))),
                new ArrayList<TutorSession> (Arrays.asList(samSession)), "2"),
        new Tutor("Anand Desai", "Anand\'s bio", "images/pfp.jpg", "thegoogler@google.com", new ArrayList<String> (Arrays.asList("finance", "chemistry")),
                new ArrayList<TimeRange> (Arrays.asList(TimeRange.fromStartToEnd(TIME_1000AM, TIME_1200AM, MAY182020),
                            TimeRange.fromStartToEnd(TIME_0100PM,TIME_0200PM, AUGUST102020))),
                new ArrayList<TutorSession> (Arrays.asList()), "3"),
        new Tutor("Elian Dumitru", "Elian\'s bio", "images/pfp.jpg", "elian@google.com", new ArrayList<String> (Arrays.asList("geology", "math")),
                new ArrayList<TimeRange> (Arrays.asList(TimeRange.fromStartToEnd(TIME_1000AM, TIME_1200AM, MAY182020),
                            TimeRange.fromStartToEnd(TIME_0100PM,TIME_0200PM, AUGUST102020))),
                new ArrayList<TutorSession> (Arrays.asList()), "4")
    ));

    private ArrayList<Student> students = new ArrayList<Student> (Arrays.asList(
        new Student("Kashish Arora", "Kashish\'s bio", "images/pfp.jpg", "kashisharora@google.com", new ArrayList<String> (Arrays.asList("English", "Physics")),
                new ArrayList<String> (Arrays.asList()), new ArrayList<TutorSession> (Arrays.asList()), "0"),
        new Student("Bernardo Eilert Trevisan", "Bernardo\'s bio", "images/pfp.jpg", "btrevisan@google.com", new ArrayList<String> (Arrays.asList("Math", "History")),
                new ArrayList<String> (Arrays.asList()), new ArrayList<TutorSession> (Arrays.asList(bernardoSession)), "1"),
        new Student("Sam Falberg", "Sam\'s bio", "images/pfp.jpg", "sfalberg@google.com", new ArrayList<String> (Arrays.asList("Finance", "Chemistry")),
                new ArrayList<String> (Arrays.asList()), new ArrayList<TutorSession> (Arrays.asList(samSession)), "2"),
        new Student("Anand Desai", "Anand\'s bio", "images/pfp.jpg", "thegoogler@google.com", new ArrayList<String> (Arrays.asList("Geology", "English")),
                new ArrayList<String> (Arrays.asList("0")), new ArrayList<TutorSession> (Arrays.asList()), "3"),
        new Student("Elian Dumitru", "Elian\'s bio", "images/pfp.jpg", "elian@google.com", new ArrayList<String> (Arrays.asList("Finance", "Chemistry")),
                new ArrayList<String> (Arrays.asList("0")), new ArrayList<TutorSession> (Arrays.asList()), "4")
    ));

    private ArrayList<User> users = new ArrayList<User> (Arrays.asList(
        new User("Test Tester", "0"),
        new User("Tester Test", "1")
    ));

   /** 
    *  Finds and returns a tutor that has the given email. If no such tutor is found, returns null.
    *  @return Tutor
    */
    public Tutor getTutorByEmail(String email) {
        for(Tutor tutor : tutors) {
            if(tutor.getEmail().toLowerCase().equals(email.toLowerCase())) {
                return tutor;
            }
        }

        return null;
    }

    /** 
    *  Finds and returns a student that has the given email. If no such student is found, returns null.
    *  @return Student
    */
    public Student getStudentByEmail(String email) {
        for(Student student : students) {
            if(student.getEmail().toLowerCase().equals(email.toLowerCase())) {
                return student;
            }
        }

        return null;
    }

    /**
    * Adds all the sample Tutor objects to datastore.
    */
    public void addTutorsToDatastore() {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        for(Tutor tutor : tutors) {
            Entity entity = new Entity("Tutor");
            entity.setProperty("name", tutor.getName());
            entity.setProperty("bio", tutor.getBio());
            entity.setProperty("pfp", tutor.getPfp());
            entity.setProperty("email", tutor.getEmail());
            entity.setProperty("topics", tutor.getSkills());
            entity.setProperty("ratingSum", 0);
            entity.setProperty("ratingCount", 0);
            entity.setProperty("rating", 0);
            entity.setProperty("userId", tutor.getUserId());
            datastore.put(entity);

            addTutorAvailabilityToDatastore(datastore, tutor.getAvailability(), tutor.getUserId());
            addTutorSessionsToDatastore(datastore, tutor.getScheduledSessions());
        }
    }

    /**
    * Adds all the sample Student objects to datastore.
    */
    public void addStudentsToDatastore() {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        for(Student student : students) {
            Entity entity = new Entity("Student");
            entity.setProperty("name", student.getName());
            entity.setProperty("bio", student.getBio());
            entity.setProperty("pfp", student.getPfp());
            entity.setProperty("email", student.getEmail());
            entity.setProperty("learning", student.getLearning());
            entity.setProperty("tutors", student.getTutors());
            entity.setProperty("userId", student.getUserId());
            datastore.put(entity);
        }
    }

    /**
    * Rates a sample tutor with the given rating.
    */
    public void rateTutor(String userId, int rating) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        //get tutor with user id
        Filter filter = new FilterPredicate("userId", FilterOperator.EQUAL, userId);
        Query query = new Query("Tutor").setFilter(filter);

        PreparedQuery pq = datastore.prepare(query);

        Entity tutorEntity = pq.asSingleEntity();

        int ratingSum = Math.toIntExact((long) tutorEntity.getProperty("ratingSum")) + rating;
        int ratingCount = Math.toIntExact((long) tutorEntity.getProperty("ratingCount")) + 1;

        tutorEntity.setProperty("ratingSum", ratingSum);
        tutorEntity.setProperty("ratingCount", ratingCount);
        tutorEntity.setProperty("rating", Math.round(ratingSum/ratingCount));

        datastore.put(tutorEntity);
    }

    private void addTutorAvailabilityToDatastore(DatastoreService datastore, ArrayList<TimeRange> times, String userId) {

        for(TimeRange time : times) {
            addTimeRangeToDatastore(datastore, time, userId);
        }

    }

    private void addTutorSessionsToDatastore(DatastoreService datastore, ArrayList<TutorSession> sessions) {

        for(TutorSession session : sessions) {

            Entity sessionEntity = new Entity("TutorSession");

            sessionEntity.setProperty("tutorID", session.getTutorID());
            sessionEntity.setProperty("studentID", session.getStudentID());
            sessionEntity.setProperty("subtopics", session.getSubtopics());
            sessionEntity.setProperty("questions", session.getQuestions());
            sessionEntity.setProperty("rated", session.isRated());
            sessionEntity.setProperty("rating", session.getRating());
            sessionEntity.setProperty("timeslot", addTimeRangeToDatastore(datastore, session.getTimeslot(), String.valueOf(session.getId())));

            datastore.put(sessionEntity);

        }
    }

    private long addTimeRangeToDatastore(DatastoreService datastore, TimeRange time, String id) {
        Entity timeEntity = new Entity("TimeRange");

        timeEntity.setProperty("tutorID", id);
        timeEntity.setProperty("start", time.getStart());
        timeEntity.setProperty("end", time.getEnd());
        timeEntity.setProperty("date", new Gson().toJson(time.getDate()));

        datastore.put(timeEntity);

        return (long) timeEntity.getKey().getId();
    }

}
