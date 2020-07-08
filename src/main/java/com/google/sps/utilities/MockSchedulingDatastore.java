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

/** Mock datastore service class used for scheduling tutor sessions. */
public final class MockSchedulingDatastore implements SchedulingDatastoreService {

    /**
    * Adds a new TutorSession for the tutor and student.
    */
    @Override
    public void addTutorSession(String tutorEmail, String studentEmail, TutorSession session) {
        SampleData.addToTutorScheduledSessionsByEmail(tutorEmail, session);
        SampleData.addToStudentScheduledSessionsByEmail(studentEmail, session);
        SampleData.deleteAvailabilityByTimeRange(tutorEmail, session.getTimeslot());
    }

    /**
    * Gets a list of all scheduled sessions for a tutor with the given email.
    * @return List<TutorSession>
    */
    @Override
    public List<TutorSession> getScheduledSessionForTutor(String email) {
        Tutor tutor = SampleData.getTutorByEmail(email);
        return tutor.getScheduledSessions();
    }

    /**
    * Gets a list of all scheduled sessions for a student with the given email.
    * @return List<TutorSession>
    */
    @Override
    public List<TutorSession> getScheduledSessionForStudent(String email) {
        Student student = SampleData.getStudentByEmail(email);
        return student.getScheduledSessions();
    }

}
