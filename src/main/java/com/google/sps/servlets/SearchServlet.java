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
import java.lang.String;
import com.google.gson.Gson;
import java.util.Optional;


/** Servlet that returns search results (tutors and books) for a topic. */
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
        response.getWriter().println(convertListToJson(results));
       
    }

    /**
    * This method converts a list of tutors to a JSON string.
    * @return String, the list of tutors as a JSON string
    */
    private String convertListToJson(ArrayList<Tutor> tutors) {
        ArrayList<String> jsonTutors = new ArrayList<String>();
        Gson gson = new Gson();
        //convert all Tutor objects to JSON
        for(Tutor t : tutors) {
            jsonTutors.add(gson.toJson(t));
        }

        //convert list to JSON
        return gson.toJson(jsonTutors);
    }

    /**
    * Gets a list of tutors that have the specified topic as a skill.
    * @return ArrayList<Tutor>
    */
    private ArrayList<Tutor> getTutorsForTopic(String topic) {
        ArrayList<Tutor> results = new ArrayList<Tutor>();

        for(Tutor tutor : SampleData.getSampleTutors()) {
            String[] skills = tutor.getSkills();

            for(String skill : skills) {
                if(skill.toLowerCase().equals(topic.toLowerCase())) {
                    results.add(tutor);
                    break;
                }
            }
        }

        return results;
    }

    /**
    * @return an Optional of the request parameter
    */
    private Optional<String> getParameter(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        return Optional.ofNullable(value);
    }

}
