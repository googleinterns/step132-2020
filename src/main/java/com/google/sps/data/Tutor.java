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

/** Stores information about a tutor including name, email, skills, availability, and scheduled tutoring sessions. */
public final class Tutor {
    
    private String name;
    private String bio;
    private String pfp;
    private String email;
    private ArrayList<String>  skills;
    private ArrayList<TimeRange> availability;
    private ArrayList<TutorSession> scheduledSessions;
    private int ratingSum;
    private int ratingCount;
    private String userId;

    public Tutor(String name, String bio, String pfp, String email, ArrayList<String>  skills, ArrayList<TimeRange> availability, ArrayList<TutorSession> scheduledSessions, String id) {
        this.name = name;
        this.bio = bio;
        this.pfp = pfp;
        this.email = email;
        this.skills = skills;
        this.availability = availability;
        this.scheduledSessions = scheduledSessions;
        // Rating count and sum are both initially set to 0 by default
        this.ratingCount = 0;
        this.ratingSum = 0;
        this.userId = id;
    }

    public Tutor(String name, String bio, String pfp, String email, ArrayList<String>  skills, ArrayList<TimeRange> availability, ArrayList<TutorSession> scheduledSessions, int ratingCount, int ratingSum, String id) {
        this.name = name;
        this.bio = bio;
        this.pfp = pfp;
        this.email = email;
        this.skills = skills;
        this.availability = availability;
        this.scheduledSessions = scheduledSessions;
        this.ratingCount = ratingCount;
        this.ratingSum = ratingSum;
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

    public ArrayList<String>  getSkills() {
        return this.skills;
    }

    public ArrayList<TimeRange> getAvailability() {
        return this.availability;
    }

    public ArrayList<TutorSession> getScheduledSessions() {
        return this.scheduledSessions;
    }

    public Float getRating() {
        return (float) this.ratingSum / this.ratingCount;
    }

    public String getUserId() {
        return this.userId;
    }

    /**
    * Used for testing.
    */
    public void addRating(int rating) {
        this.ratingCount++;
        this.ratingSum += rating;
    }
}
