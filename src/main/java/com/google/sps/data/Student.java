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
    private ArrayList<String> tutors;
    private ArrayList<TutorSession> scheduledSessions;
    private String userId;

    /**
     * Creates a new student with no scheduled sessions field.
     *
     * @param name The student's name. Must be non-null.
     * @param bio The student's bio.
     * @param pfp The student's prfile picture.
     * @param email The student's email. Must be non-null.
     * @param learning The list of topics the student is learning.
     * @param id The student's id. Must be non-null.
     */
    public Student(String name, String bio, String pfp, String email, ArrayList<String> learning, ArrayList<String> tutors, String id) {
        this.name = name;
        this.bio = bio;
        this.pfp = pfp;
        this.email = email;
        this.learning = learning;
        this.tutors = tutors;
        // Empty array of scheduled sessions (not necessary)
        this.scheduledSessions = new ArrayList<TutorSession>();
        this.userId = id;
    }

    /**
     * Creates a new student.
     *
     * @param name The student's name. Must be non-null.
     * @param bio The student's bio.
     * @param pfp The student's prfile picture.
     * @param email The student's email. Must be non-null.
     * @param learning The list of topics the student is learning.
     * @param tutors The list of tutors the student is has had or will have sessions with.
     * @param scheduledSessions The list of scheduled sessions for the student.
     * @param id The student's id. Must be non-null.
     */
    public Student(String name, String bio, String pfp, String email, ArrayList<String> learning, ArrayList<String> tutors, ArrayList<TutorSession> scheduledSessions, String id) {
        this.name = name;
        this.bio = bio;
        this.pfp = pfp;
        this.email = email;
        this.learning = learning;
        this.tutors = tutors;
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

    public ArrayList<String> getTutors() {
        return this.tutors;
    }

    public ArrayList<TutorSession> getScheduledSessions() {
        return this.scheduledSessions;
    }

    public String getUserId() {
        return this.userId;
    }
}
