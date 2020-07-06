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

import com.google.sps.data.SampleData;
import com.google.sps.data.Tutor;
import com.google.sps.data.TimeRange;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.lang.String;
import com.google.gson.Gson;


/** Servlet that returns a list of tutors for a searched topic. */
@WebServlet("/search")
public class SearchServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String topic = request.getParameter("topic");

        response.setContentType("application/json;");

        //send error message if the search was invalid
        if(topic == null || topic.equals("")) {
            response.getWriter().println("{\"error\": \"Invalid search request.\"}");
            return;
        }

        ArrayList<Tutor> results = getTutorsForTopic(topic);

        response.setCharacterEncoding("UTF-8");

        String jsonResults = new Gson().toJson(results);

        response.getWriter().println(jsonResults);
       
    }

    /**
    * Gets a list of tutors that have the specified topic as a skill.
    * @return ArrayList<Tutor>
    */
    private ArrayList<Tutor> getTutorsForTopic(String topic) {
        ArrayList<Tutor> results = new ArrayList<Tutor>();

        for(Tutor tutor : SampleData.getSampleTutors()) {
            List<String> skills = tutor.getSkills();

            for(String skill : skills) {
                if(skill.toLowerCase().equals(topic.toLowerCase())) {
                    results.add(tutor);
                    break;
                }
            }
        }

        return results;
    }

}
