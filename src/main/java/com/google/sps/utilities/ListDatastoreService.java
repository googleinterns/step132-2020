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

import com.google.sps.data.BookList;
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

/** Accesses Datastore to manage book lists. */ 
public final class ListDatastoreService {

    public ArrayList<BookList> getListsByTopic(String topic) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Filter topicFilter = new FilterPredicate("topic", FilterOperator.EQUAL, topic.toLowerCase());
        Query query = new Query("BookList").setFilter(topicFilter);

        ArrayList<BookList> lists = new ArrayList<BookList>();

        PreparedQuery results = datastore.prepare(query);

        for (Entity entity : results.asIterable()) {

            lists.add(createBookList(entity));
            
        }

        return lists;
    }

    public ArrayList<BookList> getListsByTutor(String tutorID) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Filter filter = new FilterPredicate("tutorID", FilterOperator.EQUAL, tutorID);
        Query query = new Query("BookList").setFilter(filter);

        ArrayList<BookList> lists = new ArrayList<BookList>();

        PreparedQuery results = datastore.prepare(query);

        for (Entity entity : results.asIterable()) {

            lists.add(createBookList(entity));
            
        }

        return lists;

    }

    /**
    * Adds a new List entity to datastore. 
    */
    public void createList(BookList list) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        TransactionOptions options = TransactionOptions.Builder.withXG(true);
        Transaction txn = datastore.beginTransaction(options);

        try {
            Entity listEntity = new Entity("BookList");

            listEntity.setProperty("tutorID", list.getTutorId());
            listEntity.setProperty("books", list.getBooks());
            listEntity.setProperty("name", list.getName());
            listEntity.setProperty("topic", list.getTopic().toLowerCase());

            datastore.put(txn, listEntity);

            txn.commit();
        } finally {
          if (txn.isActive()) {
            txn.rollback();
          }
        }
    }

    private BookList createBookList(Entity entity) {
        long id = (long) entity.getKey().getId();
        ArrayList books = (ArrayList) entity.getProperty("books");
        String name = (String) entity.getProperty("name");
        String topic = (String) entity.getProperty("topic");
        String tutorID = (String) entity.getProperty("tutorID");

        return new BookList(books, name, topic, tutorID, id);

    }
}
