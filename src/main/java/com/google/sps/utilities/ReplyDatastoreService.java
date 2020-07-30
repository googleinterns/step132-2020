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

import com.google.sps.data.Reply;
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

/** Accesses Datastore to manage replies. */ 
public final class ReplyDatastoreService {

    /**
    * Retrieves a list of replies for a given post's id.
    */
    public List<Reply> getRepliesByPostId(String postId) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Filter replyFilter = new FilterPredicate("postId", FilterOperator.EQUAL, postId);
        Query replyQuery = new Query("Reply").setFilter(replyFilter);
        
        ArrayList<Reply> replies = new ArrayList<Reply>();

        PreparedQuery replyResults = datastore.prepare(replyQuery);

        for (Entity replyEntity : replyResults.asIterable()) {            
            String userId = (String) replyEntity.getProperty("userId");
            String content = (String) replyEntity.getProperty("content");
            long id = (long) replyEntity.getKey().getId();

            Reply reply = new Reply(userId, postId, content, id);

            replies.add(reply);
        }

        return replies;
    }

    /**
    * Adds a new reply.
    */
    public void addReply(Reply reply) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        TransactionOptions options = TransactionOptions.Builder.withXG(true);
        Transaction txn = datastore.beginTransaction(options);

        try {
            Entity replyEntity = new Entity("Reply");

            replyEntity.setProperty("userId", reply.getUserID().toLowerCase());
            replyEntity.setProperty("postId", reply.getPostId());
            replyEntity.setProperty("content", reply.getContent());

            datastore.put(txn, replyEntity);

            txn.commit();
        } finally {
          if (txn.isActive()) {
            txn.rollback();
          }
        }
    }
}
