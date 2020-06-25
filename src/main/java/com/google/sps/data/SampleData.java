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

public final class SampleData {
    
    private ArrayList<Tutor> tutors;

    public SampleData() {
        tutors = new ArrayList<Tutor>();
        tutors.add(new Tutor("Kashish Arora", "kashisharora@google.com", new String[]{"Math", "History"}, new TimeRange[]{TimeRange.fromStartToEnd(720, 780), TimeRange.fromStartToEnd(900,1020)}));
        tutors.add(new Tutor("Bernardo Eilert Trevisan", "btrevisan@google.com", new String[]{"English", "Physics"}, new TimeRange[]{TimeRange.fromStartToEnd(480, 600), TimeRange.fromStartToEnd(660,930)}));
        tutors.add(new Tutor("Sam Falberg", "sfalberg@google.com", new String[]{"Geology", "English"}, new TimeRange[]{TimeRange.fromStartToEnd(600, 720), TimeRange.fromStartToEnd(780,840)}));
        tutors.add(new Tutor("Anand Desai", "thegoogler@google.com", new String[]{"Finance", "Chemistry"}, new TimeRange[]{TimeRange.fromStartToEnd(600, 720), TimeRange.fromStartToEnd(780,840)}));
        tutors.add(new Tutor("Elian Dumitru", "elian@google.com", new String[]{"Geology", "Math"}, new TimeRange[]{TimeRange.fromStartToEnd(600, 720), TimeRange.fromStartToEnd(780,840)}));
    }

    public ArrayList<Tutor> getSampleTutors() {
        return this.tutors;
    }

}