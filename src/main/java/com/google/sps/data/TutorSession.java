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

    private String studentEmail;
    private String tutorEmail;
    private String subtopics;
    private String questions;
    private TimeRange timeslot;

    /**
     * Creates a new tutoring session.
     *
     * @param studentEmail The student's email. Must be non-null.
     * @param tutorEmail The the tutor's email. Must be non-null.
     * @param subtopics The subtopics the student wishes to cover during the tutoring session. Can be null.
     * @param questions The questions the student would like answers to. Can be null.
     * @param timeslot The time range during which the tutoring session will take place. Must be non-null.
     */
    public TutorSession(String studentEmail, String tutorEmail, String subtopics, String questions, TimeRange timeslot) {
        this.studentEmail = studentEmail;
        this.tutorEmail = tutorEmail;
        this.subtopics = subtopics;
        this.questions = questions;
        this.timeslot = timeslot;
    }

    public String getStudentEmail() {
        return this.studentEmail;
    }

    public String getTutorEmail() {
        return this.tutorEmail;
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

}
