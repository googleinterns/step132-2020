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

/** Stores a list of books along with the name and subject of the list and the name of the tutor who created the list.*/
public final class List {

    private ArrayList<String> books;
    private String name;
    private String tutorName;
    private String subject;

    public List(ArrayList<String> books, String name, String tutorName, String subject) {
        this.books = books;
        this.name = name;
        this.tutorName = tutorName;
        this.subject = subject;
    }

    public ArrayList<String> getBooks() {
        return this.books;
    }

    public String getName() {
        return this.name
    }

    public String getTutorName() {
        return this.tutorName;
    }

    public String getSubject() {
        return this.subject;
    }

}
