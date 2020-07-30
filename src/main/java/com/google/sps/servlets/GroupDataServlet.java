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

/** Servlet that retireves information for an specific group. */
@WebServlet("/group-data")
public class GroupDataServlet extends HttpServlet {
    private GroupDatastoreService datastore;

    public void init() {
        datastore = new GroupDatastoreService();
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

        Group result = datastore.getGroupById(Long.parseLong(groupId));

        response.setCharacterEncoding("UTF-8");

        String json = new Gson().toJson(result);

        response.getWriter().println(json);
        return;
    }
}
