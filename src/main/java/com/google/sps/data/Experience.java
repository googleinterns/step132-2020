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
 * Keeps track of a student's past experience.
 * Experience connects to the overarching database as a list of Experience inside the database. 
 */
public final class Experience {
    
    private String studentID;
    private String experience;

    /**
     * Creates a new Experience.
     *
     * @param studentID The student's id. Must be non-null.
     * @param experience The student's experience.
     */
    public Experience(String studentID, String experience) {
        this.studentID = studentID;
        this.experience = experience;
    }

    public String getStudentID() {
        return this.studentID;
    }

    public String getExperience() {
        return this.experience;
    }
}
