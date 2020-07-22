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

import java.lang.String;
import java.util.ArrayList;


/** Stores a list of books along with the name and topic of the list and the name of the tutor who created the list.*/
public final class BookList {

    private ArrayList<String> books;
    private String name;
    private String topic;
    private String tutorID;
    private long id;

    public BookList(ArrayList<String> books, String name, String topic, String tutorID) {
        this.books = books;
        this.name = name;
        this.topic = topic;
        this.tutorID = tutorID;
    }

    public BookList(ArrayList<String> books, String name, String tutorID, String topic, long id) {
        this.books = books;
        this.name = name;
        this.topic = topic;
        this.tutorID = tutorID;
        this.id = id;
    }

    public ArrayList<String> getBooks() {
        return this.books;
    }

    public String getName() {
        return this.name;
    }

    public String getTutorId() {
        return this.tutorID;
    }

    public String getTopic() {
        return this.topic;
    }

    public long getId() {
        return this.id;
    }

}
