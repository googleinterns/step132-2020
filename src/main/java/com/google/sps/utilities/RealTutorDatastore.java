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

// import com.google.sps.data.TutorSession;
// import com.google.sps.data.TimeRange;
// import com.google.sps.data.Tutor;
// import com.google.appengine.api.datastore.Entity;
// import com.google.appengine.api.datastore.EntityNotFoundException;
// import com.google.appengine.api.datastore.Key;
// import com.google.appengine.api.datastore.KeyFactory;
// import com.google.appengine.api.datastore.DatastoreService;
// import com.google.appengine.api.datastore.DatastoreServiceFactory;
// import com.google.appengine.api.datastore.Transaction;
// import com.google.appengine.api.datastore.TransactionOptions;
// import com.google.appengine.api.datastore.PreparedQuery;
// import com.google.appengine.api.datastore.Query;
// import com.google.appengine.api.datastore.Query.FilterOperator;
// import com.google.appengine.api.datastore.Query.FilterPredicate;
// import com.google.appengine.api.datastore.Query.Filter;
// import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
// import com.google.appengine.api.datastore.Query.CompositeFilter;
// import java.lang.String;
// import java.util.List;
// import java.util.ArrayList;
// import java.util.Calendar;
// import com.google.gson.Gson;

// /** Accesses Datastore to manage tutors. */ 
// public final class RealTutorDatastore implements TutorDatastoreService {
//     /**
//     * Deletes the given timeslot from the given tutor's availability
//     * @return void
//     */
//     public void deleteAvailabilityByTimeRange(String tutorID, TimeRange timeslot) {
//         // TODO
//         return;
//     }

//     /**
//     * Adds the given timeslot to the given tutor's availability
//     * @return void
//     */
//     public void addAvailabilityByTutorEmail(String tutorID, TimeRange timeslot) {
//         // TODO
//         return;
//     }

//     /**
//     * Retrieves all the tutors.
//     * @return a list of Tutor
//     */
//     public ArrayList<Tutor> getTutors() {
//         // TO BE IMPLEMENTED
//         return null;
//     }
// }
