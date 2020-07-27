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

package com.google.sps.servlets;

import com.google.sps.data.BookList;
import com.google.sps.utilities.ListDatastoreService;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that gets and adds to a tutor's availability. */
@WebServlet("/manage-lists")
public class ManageListsServlet extends HttpServlet {
    private ListDatastoreService datastore;

    public void init() {
        datastore = new ListDatastoreService();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Get the id of the tutor 
        // if id is null, use default id -1
        String tutorID = Optional.ofNullable((String)request.getSession(false).getAttribute("userId")).orElse("-1");

        if(tutorID.equals("-1")) {
            response.setContentType("application/json");
            response.getWriter().println("{\"error\": \"There was an error getting your lists.\"}");
        }

        List<BookList> lists = datastore.getListsByTutor(tutorID);

        String json = new Gson().toJson(lists);
        response.setContentType("application/json;");
        response.getWriter().println(json);
        return; 
 
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        //Set default value to -1 
        String tutorID = Optional.ofNullable((String)request.getSession(false).getAttribute("userId")).orElse("-1");
        String books = request.getParameter("books");
        String name = request.getParameter("name");
        String topic = request.getParameter("topic");

        if(tutorID.equals("-1")) {
            response.getWriter().println("{\"error\": \"There was an error creating list.\"}");
            return;
        }

        if(topic.toLowerCase().equals("other")) {
            topic = request.getParameter("otherTopic");
        }

        //\\s*\n\\s* splits on a new line and gets rid of whitespace before and after the word
        BookList list = new BookList(new ArrayList<String>(Arrays.asList(books.trim().split("\\s*\n\\s*"))), name, topic, tutorID);

        datastore.createList(list);

        String json = new Gson().toJson(datastore.getListsByTutor(tutorID));
        response.getWriter().println(json);
        response.sendRedirect("/my-lists.html");
        return;
    }
}
