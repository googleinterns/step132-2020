
// // Copyright 2019 Google LLC
// //
// // Licensed under the Apache License, Version 2.0 (the "License");
// // you may not use this file except in compliance with the License.
// // You may obtain a copy of the License at
// //
// //     https://www.apache.org/licenses/LICENSE-2.0
// //
// // Unless required by applicable law or agreed to in writing, software
// // distributed under the License is distributed on an "AS IS" BASIS,
// // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// // See the License for the specific language governing permissions and
// // limitations under the License.

// package com.google.sps.utilities;

// import com.google.sps.data.SampleData;
// import com.google.sps.data.Tutor;
// import java.lang.String;
// import java.util.Collection;
// import java.util.ArrayList;
// import java.util.List;


// /** Mock datastore service class used for search testing. */
// public final class MockSearchDatastore implements SearchDatastoreService {
//     private SampleData sample;

//     public MockSearchDatastore() {
//         sample = new SampleData();
//     }

//     /**
//     * Gets a list of tutors that have the specified topic as a skill.
//     * @return List<Tutor>
//     */
//     @Override
//     public List<Tutor> getTutorsForTopic(String topic) {
//         ArrayList<Tutor> results = new ArrayList<Tutor>();

//         for(Tutor tutor : sample.getSampleTutors()) {
//             List<String> skills = tutor.getSkills();

//             for(String skill : skills) {
//                 if(skill.toLowerCase().equals(topic.toLowerCase())) {
//                     results.add(tutor);
//                     break;
//                 }
//             }
//         }

//         return results;
//     }
// }
