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

import com.google.sps.data.TimeRange;
import java.lang.String;
import java.util.List;

/** Interface for accessing datastore to get tutor availability . */
public interface AvailabilityDatastoreService {

    /**
    * Gets the availability of a tutor with the given email.
    * @return List<TimeRange>
    */
    public List<TimeRange> getAvailabilityForTutor(String email);

    /**
    * Adds a new time range to a tutor's availability.
    * @return boolean, true if time was added, false if there was a problem adding it
    */
    public boolean addAvailability(String email, TimeRange time);

    /**
    * Deletes a time range from a tutor's availability.
    * @return boolean, true if time was deleted, false if there was a problem deleting it
    */
    public boolean deleteAvailability(String email, TimeRange time);
}
