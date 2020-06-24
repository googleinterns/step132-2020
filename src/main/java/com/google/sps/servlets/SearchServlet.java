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

    private ArrayList<Tutor> tutors;

    public void init() {

        tutors = new ArrayList<Tutor>();

        tutors.add(new Tutor("Kashish Arora", "kashisharora@google.com", new String[]{"Math", "History"}, new TimeRange[]{TimeRange.fromStartToEnd(720, 780), TimeRange.fromStartToEnd(900,1020)}));
        tutors.add(new Tutor("Bernardo Eilert Trevisan", "btrevisan@google.com", new String[]{"English", "Physics"}, new TimeRange[]{TimeRange.fromStartToEnd(480, 600), TimeRange.fromStartToEnd(660,930)}));
        tutors.add(new Tutor("Sam Falberg", "sfalberg@google.com", new String[]{"Geology", "English"}, new TimeRange[]{TimeRange.fromStartToEnd(600, 720), TimeRange.fromStartToEnd(780,840)}));
        tutors.add(new Tutor("Anand Desai", "thegoogler@google.com", new String[]{"Finance", "Chemistry"}, new TimeRange[]{TimeRange.fromStartToEnd(600, 720), TimeRange.fromStartToEnd(780,840)}));
        tutors.add(new Tutor("Elian Dumitru", "elian@google.com", new String[]{"Geology", "Math"}, new TimeRange[]{TimeRange.fromStartToEnd(600, 720), TimeRange.fromStartToEnd(780,840)}));

    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String topic =  getParameter(request, "topic").orElse("");

        ArrayList<Tutor> results = getTutorsForTopic(topic);

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;");
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

        for(Tutor tutor : tutors) {
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
