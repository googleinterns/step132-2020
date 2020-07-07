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
import java.lang.String;
import java.util.ArrayList;
import java.util.List;

/** Mock datastore service class used for testing getting tutor availability. */
public final class MockAvailabilityDatastore implements AvailabilityDatastoreService {

    /**
    * Gets the availability of a tutor with the given email.
    * @return List<TimeRange>
    */
    @Override
    public List<TimeRange> getAvailabilityForTutor(String email) {

        Tutor tutor = SampleData.getTutorByEmail(email);
        return tutor.getAvailability();
    }

    /**
    * Adds a new time range to a tutor's availability.
    */
    @Override
    public void addAvailability(String email, TimeRange time) {
        Tutor tutor = SampleData.getTutorByEmail(email);
        tutor.addAvailabilityByTimeRange(time);
    }

    /**
    * Deletes a time range from a tutor's availability.
    */
    @Override
    public void deleteAvailability(String email, TimeRange time) {
        SampleData.deleteAvailabilityByTimeRange(email, time);
    }
}
