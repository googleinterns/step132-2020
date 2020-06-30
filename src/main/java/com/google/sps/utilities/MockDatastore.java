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
import java.lang.String;
import java.util.Collection;
import java.util.ArrayList;

public final class MockDatastore implements DatabaseService {

    @Override
    public Collection<Tutor> getTutorsForTopic(String topic) {
        ArrayList<Tutor> results = new ArrayList<Tutor>();

        for(Tutor tutor : SampleData.getSampleTutors()) {
            String[] skills = tutor.getSkills();

            for(String skill : skills) {
                if(skill.toLowerCase().equals(topic.toLowerCase())) {
                    results.add(tutor);
                    break;
                }
            }
        }

        return results;
    }
}
