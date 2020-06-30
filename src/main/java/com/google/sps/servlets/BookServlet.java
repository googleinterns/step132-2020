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

package com.google.sps;

import com.google.sps.data.Book;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import com.google.gson.Gson;

/** Test servlet that returns a list of books for a searched topic. */
@WebServlet("/books")
public class BookServlet extends HttpServlet {

    private static ArrayList<Book> books = new ArrayList<Book> (Arrays.asList(
        new Book("Calculus", "James Stewart", "Math", "/images/book-cover.png"),
        new Book("Elementary Algebra", "John Redden", "Math", "/images/book-cover.png"),
        new Book("Computer Science", "Suzie ", "Haminghton", "/images/book-cover.png")
    ));

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String topic = request.getParameter("topic");

        response.setContentType("application/json;");

        //send error message if the search was invalid
        if(topic == null || topic.equals("")) {
            response.getWriter().println("{\"error\": \"Invalid search request.\"}");
            return;
        }

        ArrayList<Book> results = getBooksForTopic(topic);

        response.setCharacterEncoding("UTF-8");

        String jsonResults = new Gson().toJson(results);

        response.getWriter().println(jsonResults);
    }

    /**
    * Gets a list of books that have the specified topic as their subject.
    * @return ArrayList<Book>
    */
    private ArrayList<Book> getBooksForTopic(String topic) {
        ArrayList<Book> results = new ArrayList<Book>();

        for(Book book : books) {
            if(book.getSubject().toLowerCase().equals(topic.toLowerCase())) {
                results.add(book);
            }
        }

        return results;
    }
}
