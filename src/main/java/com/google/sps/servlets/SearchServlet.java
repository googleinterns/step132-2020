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
import com.google.sps.utilities.RealSearchDatastore;
import com.google.sps.utilities.MockSearchDatastore;
import com.google.sps.utilities.SearchDatastoreService;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.lang.String;
import com.google.gson.Gson;


/** Servlet that returns a list of tutors for a searched topic. */
@WebServlet("/search")
public class SearchServlet extends HttpServlet {
    private SearchDatastoreService datastore;
    
    /**
    * Because we specified a constructor with a parameter (the testing one), the default empty constructor does not work anymore so we have to explicitly create it. 
    * We need the default one for deployment because the servlet is created without parameters.
    */
    public SearchServlet() {}

    /**
    * Constructor used for testing. It will create a new instance of the mock datastore service when called with test = true.
    */
    public SearchServlet(boolean test) {
        if(test) {
            datastore = new MockSearchDatastore();
        }
    }

    public void init() {
        datastore = new RealSearchDatastore();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String topic = request.getParameter("topic");

        response.setContentType("application/json;");

        //send error message if the search was invalid
        if(topic == null || topic.equals("")) {
            response.getWriter().println("{\"error\": \"Invalid search request.\"}");
            return;
        }

        List<Tutor> results = datastore.getTutorsForTopic(topic);

        response.setCharacterEncoding("UTF-8");

        String jsonResults = new Gson().toJson(results);

        response.getWriter().println(jsonResults);
       
    }
}
