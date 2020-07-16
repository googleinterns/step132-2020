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

import com.google.sps.data.Student;
import com.google.sps.data.TimeRange;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.ArrayList;
import java.lang.String;
import com.google.gson.Gson;
import com.google.sps.utilities.StudentDatastoreService;

/** Servlet that returns a list of students for a given tutor. */
@WebServlet("/my-students")
public class GetStudentsServlet extends HttpServlet {
    private StudentDatastoreService datastore;

    public void init() {
        datastore = new StudentDatastoreService();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String tutorId = request.getParameter("tutorID");
        response.setContentType("application/json;");

        //send error message if the search was invalid
        if(tutorId == null || tutorId.equals("")) {
            response.getWriter().println("{\"error\": \"Invalid tutor.\"}");
            return;
        }

        List<Student> students = datastore.getStudentsForTutor(tutorId);
        
        String json = new Gson().toJson(students);
        response.getWriter().println(json);
        return;
    }

}
