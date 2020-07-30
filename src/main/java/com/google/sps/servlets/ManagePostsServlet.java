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
import com.google.sps.data.Post;
import com.google.sps.utilities.PostDatastoreService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that retrieves and creates posts. */
@WebServlet("/manage-posts")
public class ManagePostsServlet extends HttpServlet {
    private PostDatastoreService datastore;

    public void init() {
        datastore = new PostDatastoreService();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String groupId = request.getParameter("groupId");

        response.setContentType("application/json;");

        // Send error message if the search was invalid.
        if (groupId == null || groupId.equals("")) {
            response.getWriter().println("{\"error\": \"Invalid group id.\"}");
            return;
         }

        List<Post> results = datastore.getPostsByGroupId(groupId);

        response.setCharacterEncoding("UTF-8");

        String json = new Gson().toJson(results);

        response.getWriter().println(json);
        return;
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        String userID;

        // If the user opted to post anonymously, make their userId anonymous.
        String anonymous = Optional.ofNullable(request.getParameter("anonymous")).orElse("false");
        if (anonymous.equals("true")) {
            userID = "anonymous";
        } else {
            //Set default value to -1 
            userID = Optional.ofNullable((String)request.getSession(false).getAttribute("userId")).orElse("-1");
        }

        String content = request.getParameter("post-content");
        String groupID = Optional.ofNullable((String) request.getParameter("groupId")).orElse("-1");

        if(userID.equals("-1") || groupID.equals("-1")) {
            response.getWriter().println("{\"error\": \"There was an error posting to this group.\"}");
            return;
        }

        Post newPost = new Post(userID, groupID, content);

        datastore.addPost(newPost);

        String json = new Gson().toJson(datastore.getPostsByGroupId(groupID));
        response.getWriter().println(json);
        response.sendRedirect("/group.html?groudId=" + groupID);
        return;
    }
}
