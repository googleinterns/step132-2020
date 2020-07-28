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
import com.google.sps.data.Group;
import com.google.sps.utilities.GroupDatastoreService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that retrieves and creates groups. */
@WebServlet("/manage-groups")
public class ManageGroupsServlet extends HttpServlet {
    private GroupDatastoreService datastore;

    public void init() {
        datastore = new GroupDatastoreService();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String group = request.getParameter("group");

        response.setContentType("application/json;");

        // Send error message if the search was invalid.
        if (group == null || group.equals("")) {
            response.getWriter().println("{\"error\": \"Invalid search request.\"}");
            return;
        }

        List<Group> results = datastore.getGroupsByName(group);

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
        String name = request.getParameter("name");
        String topic = request.getParameter("topic");
        String description = request.getParameter("group-description");

        if(userID.equals("-1")) {
            response.getWriter().println("{\"error\": \"There was an error creating group.\"}");
            return;
        }

        if(topic.toLowerCase().equals("other")) {
            topic = request.getParameter("otherTopic");
        }

        Group newGroup = new Group(name, topic, description, userID);

        datastore.createGroup(newGroup);

        String json = new Gson().toJson(datastore.getGroupsByName(name));
        response.getWriter().println(json);
        response.sendRedirect("/groups.html");
        return;
    }
}
