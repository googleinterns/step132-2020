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
 * Keeps track of a student's goal.
 * Goal connects to the overarching database as a list of Goals inside the database. 
 */
public final class Goal {
    
    private String studentID;
    private String goal;
    private long id;

    /**
     * Creates a new Goal. (This constructor is used for testing.)
     *
     * @param studentID The student's id. Must be non-null.
     * @param goal The student's goal.
     */
    public Goal(String studentID, String goal) {
        this.studentID = studentID;
        this.goal = goal;
    }

    /**
     * Creates a new Goal with an id.
     *
     * @param studentID The student's id. Must be non-null.
     * @param goal The student's goal.
     * @param id A unique id for the goal
     */
    public Goal(String studentID, String goal, long id) {
        this.studentID = studentID;
        this.goal = goal;
        this.id = id;
    }

    public String getStudentID() {
        return this.studentID;
    }

    public String getGoal() {
        return this.goal;
    }

    public long getId(){
        return this.id;
    }
}
