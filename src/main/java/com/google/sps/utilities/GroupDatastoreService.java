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

import com.google.sps.data.Group;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import java.lang.String;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import com.google.gson.Gson;

/** Accesses Datastore to manage groups. */ 
public final class GroupDatastoreService {

    /**
    * Retrieves a list of groups for the given group name.
    */
    public List<Group> getGroupsByName(String groupName) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Filter groupFilter = new FilterPredicate("name", FilterOperator.EQUAL, groupName.toLowerCase());
        Query groupQuery = new Query("Group").setFilter(groupFilter);
        
        ArrayList<Group> groups = new ArrayList<Group>();

        PreparedQuery groupResults = datastore.prepare(groupQuery);

        for (Entity groupEntity : groupResults.asIterable()) {            
            String name = (String) groupEntity.getProperty("name");
            String topic = (String) groupEntity.getProperty("topic");
            String description = (String) groupEntity.getProperty("description");
            String owner = (String) groupEntity.getProperty("owner");
            long id = (long) groupEntity.getKey().getId();

            Group group = new Group(name, topic, description, owner, id);

            groups.add(group);
            
        }

        return groups;
    }

    /**
    * Creates a new group.
    */
    public void createGroup(Group group) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        TransactionOptions options = TransactionOptions.Builder.withXG(true);
        Transaction txn = datastore.beginTransaction(options);

        try {
            Entity groupEntity = new Entity("Group");

            groupEntity.setProperty("name", group.getName().toLowerCase());
            groupEntity.setProperty("topic", group.getTopic());
            groupEntity.setProperty("description", group.getDescription());
            groupEntity.setProperty("owner", group.getOwner());

            datastore.put(txn, groupEntity);

            txn.commit();
        } finally {
          if (txn.isActive()) {
            txn.rollback();
          }
        }
    }
}
