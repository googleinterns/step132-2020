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

public final class SampleData {
    private static final int TIME_0800AM = TimeRange.getTimeInMinutes(8, 00);
    private static final int TIME_0900AM = TimeRange.getTimeInMinutes(9, 00);
    private static final int TIME_1000AM = TimeRange.getTimeInMinutes(10, 00);
    private static final int TIME_1100AM = TimeRange.getTimeInMinutes(11, 00);
    private static final int TIME_1200AM = TimeRange.getTimeInMinutes(12, 00);
    private static final int TIME_0100PM = TimeRange.getTimeInMinutes(13, 00);
    private static final int TIME_0200PM = TimeRange.getTimeInMinutes(14, 00);
    private static final int TIME_0300PM = TimeRange.getTimeInMinutes(15, 00);
    private static final int TIME_0500PM = TimeRange.getTimeInMinutes(17, 00);
    
    private static ArrayList<Tutor> tutors = new ArrayList<Tutor> (Arrays.asList(
        new Tutor("Kashish Arora", "kashisharora@google.com", new String[]{"Math", "History"}, new TimeRange[]{TimeRange.fromStartToEnd(TIME_1200AM, TIME_0100PM), TimeRange.fromStartToEnd(TIME_0300PM,TIME_0500PM)}),
        new Tutor("Bernardo Eilert Trevisan", "btrevisan@google.com", new String[]{"English", "Physics"}, new TimeRange[]{TimeRange.fromStartToEnd(TIME_0800AM, TIME_1000AM), TimeRange.fromStartToEnd(TIME_1100AM,TIME_0100PM)}),
        new Tutor("Sam Falberg", "sfalberg@google.com", new String[]{"Geology", "English"}, new TimeRange[]{TimeRange.fromStartToEnd(TIME_1000AM, TIME_1200AM), TimeRange.fromStartToEnd(TIME_0100PM,TIME_0200PM)}),
        new Tutor("Anand Desai", "thegoogler@google.com", new String[]{"Finance", "Chemistry"}, new TimeRange[]{TimeRange.fromStartToEnd(TIME_1000AM, TIME_1200AM), TimeRange.fromStartToEnd(TIME_0100PM,TIME_0200PM)}),
        new Tutor("Elian Dumitru", "elian@google.com", new String[]{"Geology", "Math"}, new TimeRange[]{TimeRange.fromStartToEnd(TIME_1000AM, TIME_1200AM), TimeRange.fromStartToEnd(TIME_0100PM,TIME_0200PM)})
    ));

    public static ArrayList<Tutor> getSampleTutors() {
        return tutors;
    }

    public static void addTutor(Tutor tutor) {
        tutors.add(tutor);
    }

    public static void deleteTutorByEmail(String email) {
        for(Tutor tutor : tutors) {
            if(tutor.getEmail().toLowerCase().equals(email.toLowerCase())) {
                tutors.remove(tutor);
                break;
            }
        }
    }

    public static Tutor getTutorByEmail(String email) {
        for(Tutor tutor : tutors) {
            if(tutor.getEmail().toLowerCase().equals(email.toLowerCase())) {
                return tutor;
            }
        }

        return null;
    }

}
