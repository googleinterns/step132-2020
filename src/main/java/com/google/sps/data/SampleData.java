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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

/** Class that stores sample Tutor objects for testing. */
public final class SampleData {
    private static final int TIME_0800AM = TimeRange.getTimeInMinutes(8, 00);
    private static final int TIME_0900AM = TimeRange.getTimeInMinutes(9, 00);
    private static final int TIME_1000AM = TimeRange.getTimeInMinutes(10, 00);
    private static final int TIME_1100AM = TimeRange.getTimeInMinutes(11, 00);
    private static final int TIME_1200AM = TimeRange.getTimeInMinutes(12, 00);
    private static final int TIME_0100PM = TimeRange.getTimeInMinutes(13, 00);
    private static final int TIME_0200PM = TimeRange.getTimeInMinutes(14, 00);
    private static final int TIME_0300PM = TimeRange.getTimeInMinutes(15, 00);
    private static final int TIME_0500PM = TimeRange.getTimeInMinutes(17, 00);
    
    private static final Calendar MAY182020 = new Calendar.Builder()
                                                        .setCalendarType("iso8601")
                                                        .setDate(2020, 4, 18)
                                                        .build();
    private static final Calendar AUGUST102020 = new Calendar.Builder()
                                                        .setCalendarType("iso8601")
                                                        .setDate(2020, 7, 10)
                                                        .build();

    private static final Calendar AUGUST72020 = new Calendar.Builder()
                                                        .setCalendarType("iso8601")
                                                        .setDate(2020, 7, 7)
                                                        .build();
    private static final Calendar AUGUST182020 = new Calendar.Builder()
                                                        .setCalendarType("iso8601")
                                                        .setDate(2020, 7, 18)
                                                        .build();                                                        

    private ArrayList<Tutor> tutors = new ArrayList<Tutor> (Arrays.asList(
        new Tutor("Kashish Arora", "Kashish\'s bio", "images/pfp.jpg", "kashisharora@google.com", new ArrayList<String> (Arrays.asList("Math", "History")),
                new ArrayList<TimeRange> (Arrays.asList(TimeRange.fromStartToEnd(TIME_1200AM, TIME_0100PM, MAY182020),
                            TimeRange.fromStartToEnd(TIME_0300PM,TIME_0500PM, AUGUST102020))),
                new ArrayList<TutorSession> (Arrays.asList())),
        new Tutor("Bernardo Eilert Trevisan", "Bernardo\'s bio", "images/pfp.jpg", "btrevisan@google.com", new ArrayList<String> (Arrays.asList("English", "Physics")),
                new ArrayList<TimeRange> (Arrays.asList(TimeRange.fromStartToEnd(TIME_0800AM, TIME_1000AM, MAY182020),
                             TimeRange.fromStartToEnd(TIME_1100AM,TIME_0100PM, AUGUST102020),
                             TimeRange.fromStartToEnd(TIME_0100PM, TIME_0300PM, AUGUST72020))),
                new ArrayList<TutorSession> (Arrays.asList(new TutorSession("btrevisan@google.com", "btrevisan@google.com", null, null, TimeRange.fromStartToEnd(540, 600, MAY182020))))),
        new Tutor("Sam Falberg", "Sam\'s bio", "images/pfp.jpg", "sfalberg@google.com", new ArrayList<String> (Arrays.asList("Geology", "English")),
                new ArrayList<TimeRange> (Arrays.asList(TimeRange.fromStartToEnd(TIME_1000AM, TIME_1200AM, MAY182020),
                            TimeRange.fromStartToEnd(TIME_0100PM,TIME_0200PM, AUGUST102020))),
                new ArrayList<TutorSession> (Arrays.asList(new TutorSession("sfalberg@google.com", "sfalberg@google.com", null, null, TimeRange.fromStartToEnd(540, 600, AUGUST182020))))),
        new Tutor("Anand Desai", "Anand\'s bio", "images/pfp.jpg", "thegoogler@google.com", new ArrayList<String> (Arrays.asList("Finance", "Chemistry")),
                new ArrayList<TimeRange> (Arrays.asList(TimeRange.fromStartToEnd(TIME_1000AM, TIME_1200AM, MAY182020),
                            TimeRange.fromStartToEnd(TIME_0100PM,TIME_0200PM, AUGUST102020))),
                new ArrayList<TutorSession> (Arrays.asList())),
        new Tutor("Elian Dumitru", "Elian\'s bio", "images/pfp.jpg", "elian@google.com", new ArrayList<String> (Arrays.asList("Geology", "Math")),
                new ArrayList<TimeRange> (Arrays.asList(TimeRange.fromStartToEnd(TIME_1000AM, TIME_1200AM, MAY182020),
                            TimeRange.fromStartToEnd(TIME_0100PM,TIME_0200PM, AUGUST102020))),
                new ArrayList<TutorSession> (Arrays.asList()))
    ));

    private ArrayList<Student> students = new ArrayList<Student> (Arrays.asList(
        new Student("Kashish Arora", "Kashish\'s bio", "images/pfp.jpg", "kashisharora@google.com", new ArrayList<String> (Arrays.asList("English", "Physics")),
                new ArrayList<TutorSession> (Arrays.asList())),
        new Student("Bernardo Eilert Trevisan", "Bernardo\'s bio", "images/pfp.jpg", "btrevisan@google.com", new ArrayList<String> (Arrays.asList("Math", "History")),
                new ArrayList<TutorSession> (Arrays.asList(new TutorSession("btrevisan@google.com", "btrevisan@google.com", null, null, TimeRange.fromStartToEnd(540, 600, MAY182020))))),
        new Student("Sam Falberg", "Sam\'s bio", "images/pfp.jpg", "sfalberg@google.com", new ArrayList<String> (Arrays.asList("Finance", "Chemistry")),
                 new ArrayList<TutorSession> (Arrays.asList(new TutorSession("sfalberg@google.com", "sfalberg@google.com", null, null, TimeRange.fromStartToEnd(540, 600, AUGUST182020))))),
        new Student("Anand Desai", "Anand\'s bio", "images/pfp.jpg", "thegoogler@google.com", new ArrayList<String> (Arrays.asList("Geology", "English")),
                new ArrayList<TutorSession> (Arrays.asList())),
        new Student("Elian Dumitru", "Elian\'s bio", "images/pfp.jpg", "elian@google.com", new ArrayList<String> (Arrays.asList("Finance", "Chemistry")),
                new ArrayList<TutorSession> (Arrays.asList()))
    ));

    public ArrayList<Tutor> getSampleTutors() {
        return tutors;
    }

    public ArrayList<Student> getSampleStudents() {
        return students;
    }

    public void addTutor(Tutor tutor) {
        tutors.add(tutor);
    }

    public void addStudent(Student student) {
        students.add(student);
    }

    /** Finds and deletes a tutor with given email. */
    public void deleteTutorByEmail(String email) {
        for(Tutor tutor : tutors) {
            if(tutor.getEmail().toLowerCase().equals(email.toLowerCase())) {
                tutors.remove(tutor);
                break;
            }
        }
    }

    /** Finds and deletes a student with given email. */
    public void deleteStudentByEmail(String email) {
        for(Student student : students) {
            if(student.getEmail().toLowerCase().equals(email.toLowerCase())) {
                students.remove(student);
                break;
            }
        }
    }

   /** 
    *  Finds and returns a tutor that has the given email. If no such tutor is found, returns null.
    *  @return Tutor
    */
    public Tutor getTutorByEmail(String email) {
        System.out.println("got to the sample data function");
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

    /** Deletes the given timeslot from the given tutor's availability array. */
    public void deleteAvailabilityByTimeRange(String email, TimeRange delete) {
        Tutor tutor = getTutorByEmail(email);

        for(TimeRange timeslot : tutor.getAvailability()) {

            if(timeslot.getStart() == delete.getStart() && timeslot.getEnd() == delete.getEnd()) {
                int comparison = timeslot.getDate().compareTo(delete.getDate());
                if (comparison == 0) {
                    tutor.deleteAvailabilityByTimeRange(timeslot);
                    break;
                }
            }
        }
    }

    /** Adds the given timeslot to the given tutor's availability list. */
    public void addAvailabilityByTutorEmail(String email, TimeRange timeslot) {
        Tutor tutor = getTutorByEmail(email);
        tutor.addAvailabilityByTimeRange(timeslot);
    }

    /** Adds the given TutorSession to the scheduled sessions array of the given tutor. */
    public void addToTutorScheduledSessionsByEmail(String email, TutorSession tutoringSession) {
        Tutor tutor = getTutorByEmail(email);

        tutors.remove(tutor);
        tutor.addToScheduledSessions(tutoringSession);
        tutors.add(tutor);
    }

    /** Adds the given TutorSession to the scheduled sessions array of the given student. */
    public void addToStudentScheduledSessionsByEmail(String email, TutorSession tutoringSession) {
        Student student = getStudentByEmail(email);

        students.remove(student);
        student.addToScheduledSessions(tutoringSession);
        students.add(student);
    }

     public void rateTutorByEmail(String tutorEmail, String studentEmail, int newRating) {
        Tutor tutor = getTutorByEmail(tutorEmail);
        Student student = getStudentByEmail(studentEmail);

        tutors.remove(tutor);
        tutor.rateTutor(newRating);
        tutors.add(tutor);

        students.remove(student);
        student.markTutoringSessionAsRatedByTutorEmail(tutorEmail, newRating);
        students.add(student);
     }

}
