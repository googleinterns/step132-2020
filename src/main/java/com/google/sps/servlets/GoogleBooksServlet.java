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

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/** Fetches data from the Google Books API. */
@WebServlet("/books")
public class GoogleBooksServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String topic = request.getParameter("topic");
        //number of books already loaded
        int startIndex = Integer.parseInt(Optional.ofNullable(request.getParameter("startIndex")).orElse("-1"));
        //title and author is for getting specific book data for book lists
        String title = request.getParameter("title");
        String author = request.getParameter("author");

        response.setContentType("application/json;");
        response.setCharacterEncoding("UTF-8");

        String params = "q=";

        if(topic != null) {
            params += topic;
        }

        if(title != null) {
            params += "intitle:" + title;
        }

        if(author != null) {
            params += "+inauthor:" + author;
        }

        if(startIndex >= 0) {
            params += "&startIndex=" + startIndex;
        }

        //send error message if the search was invalid
        if(topic == null || topic.equals("")) {
            response.getWriter().println("{\"error\": \"Invalid search request.\"}");
            return;
        }

        URL url = new URL("https://www.googleapis.com/books/v1/volumes?" + params + "&key=" + System.getenv("GOOGLE_API_KEY"));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader stream = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        StringBuffer output = new StringBuffer();

        while ((line = stream.readLine()) != null) {
            output.append(line);
        }

        stream.close();

        JsonObject jsonObject = new JsonParser().parse(output.toString()).getAsJsonObject();

        response.getWriter().println(jsonObject);
        
    }
}
