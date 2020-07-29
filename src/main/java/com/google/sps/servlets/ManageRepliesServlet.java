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
import com.google.sps.data.Reply;
import com.google.sps.utilities.ReplyDatastoreService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that retrieves and creates replies. */
@WebServlet("/manage-replies")
public class ManageRepliesServlet extends HttpServlet {
    private ReplyDatastoreService datastore;

    public void init() {
        datastore = new ReplyDatastoreService();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String postId = request.getParameter("postId");

        response.setContentType("application/json;");

        // Send error message if the search was invalid.
        if (postId == null || postId.equals("")) {
            response.getWriter().println("{\"error\": \"Invalid search request.\"}");
            return;
         }

        List<Reply> results = datastore.getRepliesByPostId(postId);

        response.setCharacterEncoding("UTF-8");

        String json = new Gson().toJson(results);

        response.getWriter().println(json);
        return;
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        //Set default value to -1 
        String userID = Optional.ofNullable((String)request.getSession(false).getAttribute("userId")).orElse("-1");
        String content = request.getParameter("content");
        String postId = Optional.ofNullable((String) request.getParameter("postId")).orElse("-1");

        if(userID.equals("-1") || postId.equals("-1")) {
            response.getWriter().println("{\"error\": \"There was an error adding your reply to this post.\"}");
            return;
        }

        Reply newReply = new Reply(userID, postId, content);

        datastore.addReply(newReply);

        String json = new Gson().toJson(datastore.getRepliesByPostId(postId));
        response.getWriter().println(json);
        return;
    }
}
