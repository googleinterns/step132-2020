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
    private String email;
    private List<String> skills;
    private List<TimeRange> availability;
    private List<TutorSession> scheduledSessions;

    public Tutor(String name, String email, List<String> skills, List<TimeRange> availability, List<TutorSession> scheduledSessions) {
        this.name = name;
        this.email = email;
        this.skills = skills;
        this.availability = availability;
        this.scheduledSessions = scheduledSessions;
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public List<String> getSkills() {
        return this.skills;
    }

    public List<TimeRange> getAvailability() {
        return this.availability;
    }

    public List<TutorSession> getScheduledSessions() {
        return this.scheduledSessions;
    }

    /** Deletes the given timeslot from the availability array. */
    public void deleteAvailabilityByTimeRange(TimeRange timeslot) {
        this.availability.remove(timeslot);
    }

    /** Adds the given Tutor Session to the scheduledSessions array. */
    public void addToScheduledSessions(TutorSession tutoringSession) {
        this.scheduledSessions.add(tutoringSession);
    }

}
