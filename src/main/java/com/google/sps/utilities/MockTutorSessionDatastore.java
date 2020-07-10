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
import com.google.sps.data.Student;
import com.google.sps.data.TutorSession;
import java.lang.String;
import java.util.List;
import java.util.ArrayList;

/** Mock datastore service class used for scheduling tutor sessions. */
public final class MockTutorSessionDatastore implements TutorSessionDatastoreService {
    private SampleData sample;

    public MockTutorSessionDatastore() {
        sample = new SampleData();
    }

    /**
    * Adds a new TutorSession for the tutor and student.
    */
    @Override
    public void addTutorSession(String tutorEmail, String studentEmail, TutorSession session) {
        sample.addToTutorScheduledSessionsByEmail(tutorEmail, session);
        sample.addToStudentScheduledSessionsByEmail(studentEmail, session);
        sample.deleteAvailabilityByTimeRange(tutorEmail, session.getTimeslot());
    }

    /**
    * Gets a list of all scheduled sessions for a tutor with the given email.
    * @return List<TutorSession>, empty list if the tutor does not exist
    */
    @Override
    public List<TutorSession> getScheduledSessionsForTutor(String email) {
        Tutor tutor = sample.getTutorByEmail(email);

        if(tutor == null) {
            return new ArrayList<TutorSession>();
        }
        
        return tutor.getScheduledSessions();
    }

    /**
    * Gets a list of all scheduled sessions for a student with the given email.
    * @return List<TutorSession>, empty list if the student does not exist
    */
    @Override
    public List<TutorSession> getScheduledSessionsForStudent(String email) {
        Student student = sample.getStudentByEmail(email);

        if(student == null) {
            return new ArrayList<TutorSession>();
        }

        return student.getScheduledSessions();
    }

}
