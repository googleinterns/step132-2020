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
import java.util.List;
import java.util.LinkedList;

/**
 * Keeps track of a student's information, which includes their name, email, topics they
 * are learning, and tutoring sessions they have scheduled.
 * Student connects to the overarching database as a list of Students inside the database. 
 */
public final class Student {
    
    private String name;
    private String bio;
    private String pfp;
    private String email;
    private ArrayList<String> learning;
    private ArrayList<TutorSession> scheduledSessions;
    private String userId;

    public Student(String name, String bio, String pfp, String email, ArrayList<String> learning, ArrayList<TutorSession> scheduledSessions, String id) {
        this.name = name;
        this.bio = bio;
        this.pfp = pfp;
        this.email = email;
        this.learning = learning;
        this.scheduledSessions = scheduledSessions;
        this.userId = id;
    }

    public String getName() {
        return this.name;
    }

    public String getBio() {
        return this.bio;
    }

    public String getPfp() {
        return this.pfp;
    }

    public String getEmail() {
        return this.email;
    }

    public ArrayList<String> getLearning() {
        return this.learning;
    }

    public ArrayList<TutorSession> getScheduledSessions() {
        return this.scheduledSessions;
    }

    public String getUserId() {
        return this.userId;
    }

    /** Adds the given Tutor Session to the scheduledSessions array. */
    public void addToScheduledSessions(TutorSession tutoringSession) {
        this.scheduledSessions.add(tutoringSession);
    }


    // /** Deletes the given Tutor Session from the scheduledSessions array. */
    // public void deleteFromScheduledSessions(TutorSession tutoringSession) {
    //     TutorSession tutorSession = getSessionById(tutoringSession.getId());
    //     this.scheduledSessions.remove(tutorSession);
    // }

    // public TutorSession getSessionById(long id) {
    //     for (TutorSession tutorSession : this.scheduledSessions) {
    //         if (tutorSession.getId() == id) {
    //             return tutorSession;
    //         }
    //     }

    //     return null;
    // }

    // /** Marks the tutoring session that has the given tutor's email as rated.*/
    // public void markTutoringSessionAsRatedByTutorEmail(String tutorEmail, int newRating) {
    //     for(TutorSession tutorSession : this.scheduledSessions) {
    //         if(tutorEmail.toLowerCase().equals(tutorSession.getTutorEmail().toLowerCase())) {
    //             tutorSession.rateSession(newRating);
    //         }
    //     }
    // }

}

