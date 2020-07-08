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

/** Interface for accessing datastore to manage tutor sessions. */
public interface SchedulingDatastoreService {

    /**
    * Creates a new TutorSession for the tutor and student.
    */
<<<<<<< HEAD
    public void createNewTutorSession(String tutorEmail, String studentEmail, TutorSession session);
=======
    public void createNewTutorSession(HttpServletRequest request);
>>>>>>> 5b0d905367e039a6df7a1f75e6dc1649f723abd3
}
