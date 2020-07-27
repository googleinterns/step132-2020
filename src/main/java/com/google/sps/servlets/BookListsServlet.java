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
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/lists")
public class BookListsServlet extends HttpServlet {
    private ListDatastoreService datastore;

    public void init() {
        datastore = new ListDatastoreService();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String topic = request.getParameter("topic");
        String tutorID = request.getParameter("tutorID");

        response.setContentType("application/json;");
        response.setCharacterEncoding("UTF-8");

        //used for the tutor's profile page
        //if tutor id isn't null, someone is trying to view the tutor's profile which displays the tutor's lists
        if(tutorID != null) {
            List<BookList> results = datastore.getListsByTutor(tutorID);
            String jsonResults = new Gson().toJson(results);
            response.getWriter().println(jsonResults);
            return;
        }
        
        //if the tutor id was null and the topic is null
        //send error message because the search was invalid
        if(topic == null || topic.equals("")) {
            response.getWriter().println("{\"error\": \"Invalid search request.\"}");
            return;
        }
        
        //otherwise return results for the topic
        List<BookList> results = datastore.getListsByTopic(topic);

        String jsonResults = new Gson().toJson(results);

        response.getWriter().println(jsonResults);
        
    }
}
