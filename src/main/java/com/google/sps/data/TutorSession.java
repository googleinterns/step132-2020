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

/**
 * Keeps track of information relating to a tutoring session, which includes the email
 * of the participants, subtopics, specific questions, and the time slot during which the
 * session will take place.
 * TutorSession connects to the overarching database by serving as the data type of the 
 * scheduledSessions property of each Tutor object class.
 */
public final class TutorSession {

    private String studentID;
    private String tutorID;
    private String subtopics;
    private String questions;
    private TimeRange timeslot;
    private boolean rated;
    private int rating;
    private long id;

    /**
     * Creates a new tutoring session. (This constructor is used for testing.)
     *
     * @param studentID The student's ID. Must be non-null.
     * @param tutorID The the tutor's ID. Must be non-null.
     * @param subtopics The subtopics the student wishes to cover during the tutoring session. Can be null.
     * @param questions The questions the student would like answers to. Can be null.
     * @param timeslot The time range during which the tutoring session will take place. Must be non-null.
     */
    public TutorSession(String studentID, String tutorID, String subtopics, String questions, TimeRange timeslot) {
        this.studentID = studentID;
        this.tutorID = tutorID;
        this.subtopics = subtopics;
        this.questions = questions;
        this.timeslot = timeslot;
        // Rated is initially set to false by default
        this.rated = false;
        // Rating is initially set to 0 by default
        this.rating = 0;
    }

    /**
     * Creates a new tutoring session with an id.
     *
     * @param studentID The student's ID. Must be non-null.
     * @param tutorID The the tutor's ID. Must be non-null.
     * @param subtopics The subtopics the student wishes to cover during the tutoring session. Can be null.
     * @param questions The questions the student would like answers to. Can be null.
     * @param timeslot The time range during which the tutoring session will take place. Must be non-null.
     * @param id A unique id for the tutoring session
     */
    public TutorSession(String studentID, String tutorID, String subtopics, String questions, TimeRange timeslot, long id) {
        this.studentID = studentID;
        this.tutorID = tutorID;
        this.subtopics = subtopics;
        this.questions = questions;
        this.timeslot = timeslot;
        this.id = id;
        // Rated is initially set to false by default
        this.rated = false;
        // Rating is initially set to 0 by default
        this.rating = 0;
    }

    /**
     * Creates a new tutoring session with rating and id.
     *
     * @param studentID The student's ID. Must be non-null.
     * @param tutorID The the tutor's ID. Must be non-null.
     * @param subtopics The subtopics the student wishes to cover during the tutoring session. Can be null.
     * @param questions The questions the student would like answers to. Can be null.
     * @param timeslot The time range during which the tutoring session will take place. Must be non-null.
     * @param rating The rating the student of the tutoring session selected
     * @param id A unique id for the tutoring session
     */
    public TutorSession(String studentID, String tutorID, String subtopics, String questions, TimeRange timeslot, int rating, long id) {
        this.studentID = studentID;
        this.tutorID = tutorID;
        this.subtopics = subtopics;
        this.questions = questions;
        this.timeslot = timeslot;
        this.id = id;
        this.rated = rating == 0 ? false : true;
        this.rating = rating;
    }

    public String getStudentID() {
        return this.studentID;
    }

    public String getTutorID() {
        return this.tutorID;
    }

    public String getSubtopics() {
        return this.subtopics;
    }

    public String getQuestions() {
        return this.questions;
    }

    public TimeRange getTimeslot() {
        return this.timeslot;
    }

    public long getId(){
        return this.id;
    }

    public boolean isRated() {
        return this.rated;
    }

    public int getRating() {
        return this.rating;
    }

    public void rateSession(int newRating) {
        this.rated = true;
        this.rating = newRating;
        return;
    }

}
