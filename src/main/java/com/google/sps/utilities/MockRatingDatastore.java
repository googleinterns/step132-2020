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
import com.google.sps.data.TimeRange;
import com.google.sps.data.Tutor;
import com.google.sps.data.Student;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;

/** Mock datastore service class used for managing ratings. */
public final class MockRatingDatastore implements RatingDatastoreService {
    private SampleData sample;

    public MockRatingDatastore() {
        sample = new SampleData();
    }

    /**
    * Rates a tutor session given the tutor's email, the student's email, and the rating.
    * @return void
    */
    public void rateTutor(String tutorEmail, String studentEmail, int rating) {
        sample.rateTutorByEmail(tutorEmail, studentEmail, rating);
        return;
    }

    /**
    * Retrieves all the tutors.
    * @return a list of Tutor
    */
    public ArrayList<Tutor> getTutors() {
        return sample.getSampleTutors();
    }

    /**
    * Gets all the students
    * @return a list of Student
    */
    public ArrayList<Student> getStudents() {
        return sample.getSampleStudents();
    }
}
