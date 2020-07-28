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

import com.google.sps.data.Post;
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

/** Accesses Datastore to manage posts. */ 
public final class PostDatastoreService {

    /**
    * Retrieves a list of posts for a given group's id.
    */
    public List<Post> getPostsByGroupID(String groupId) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Filter postFilter = new FilterPredicate("groupId", FilterOperator.EQUAL, groupId);
        Query postQuery = new Query("Post").setFilter(postFilter);
        
        ArrayList<Post> posts = new ArrayList<Post>();

        PreparedQuery postResults = datastore.prepare(postQuery);

        for (Entity postEntity : postResults.asIterable()) {            
            String userId = (String) postEntity.getProperty("userId");
            String content = (String) postEntity.getProperty("content");
            long id = (long) postEntity.getKey().getId();

            Post post = new Post(userId, groupId, content, id);

            posts.add(post);
            
        }

        return posts;
    }

    /**
    * Adds a new post.
    */
    public void addPost(Post post) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        TransactionOptions options = TransactionOptions.Builder.withXG(true);
        Transaction txn = datastore.beginTransaction(options);

        try {
            Entity postEntity = new Entity("Post");

            postEntity.setProperty("userId", post.getUserID().toLowerCase());
            postEntity.setProperty("groupId", post.getGroupID());
            postEntity.setProperty("content", post.getContent());

            datastore.put(txn, postEntity);

            txn.commit();
        } finally {
          if (txn.isActive()) {
            txn.rollback();
          }
        }
    }
}
