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

import com.google.sps.data.TutorSession;
import java.lang.String;
import java.util.List;

/** Interface for accessing datastore to manage tutor sessions. */
public interface TutorSessionDatastoreService {

    /**
    * Adds a new TutorSession for the tutor and student.
    */
    public void addTutorSession(String tutorEmail, String studentEmail, TutorSession session);

    /**
    * Gets a list of all scheduled sessions for a tutor with the given email.
    * @return List<TutorSession>, empty list if the tutor does not exist
    */
    public List<TutorSession> getScheduledSessionsForTutor(String email);

    /**
    * Gets a list of all scheduled sessions for a student with the given email.
    * @return List<TutorSession>, empty list if the student does not exist
    */
    public List<TutorSession> getScheduledSessionsForStudent(String email);

    /**
    * Adds the given rating to a tutor session and updates the tutor's overall rating.
    * @return boolean, true if session was rated successfully, false otherwise
    */
    public boolean rateTutorSession(long sessionId, int rating);
}
