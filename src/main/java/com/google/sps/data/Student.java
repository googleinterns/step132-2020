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
    private String email;
    private List<String> learning;
    private List<TutorSession> scheduledSessions;

    public Student(String name, String email, List<String> learning, List<TutorSession> scheduledSessions) {
        this.name = name;
        this.email = email;
        this.learning = learning;
        this.scheduledSessions = scheduledSessions;
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public List<String> getLearning() {
        return this.learning;
    }

    public List<TutorSession> getScheduledSessions() {
        return this.scheduledSessions;
    }

    /** Adds the given Tutor Session to the scheduledSessions array. */
    public void addToScheduledSessions(TutorSession tutoringSession) {
        this.scheduledSessions.add(tutoringSession);
    }

}
