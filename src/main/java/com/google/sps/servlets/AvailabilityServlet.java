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
import com.google.sps.data.TutorSession;
import com.google.sps.data.SampleData;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.sps.utilities.RealAvailabilityDatastore;
import com.google.sps.utilities.MockAvailabilityDatastore;
import com.google.sps.utilities.AvailabilityDatastoreService;

/** Servlet that manages availability of a tutor. */
@WebServlet("/availability")
public class AvailabilityServlet extends HttpServlet {

    private AvailabilityDatastoreService datastore;

    /**
    * Because we created a constructor with a parameter (the testing one), the default empty constructor does not work anymore so we have to explicitly create it. 
    * We need the default one for deployment because the servlet is created without parameters.
    */
    public AvailabilityServlet(){}

    public AvailabilityServlet(boolean test) {
        if(test) {
            datastore = new MockAvailabilityDatastore();
        }
    }

    public void init() {
        datastore = new RealAvailabilityDatastore();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Get the id of the tutor whose availability will be displayed.
        String tutorID = request.getParameter("tutorID");

        List<TimeRange> timeslots = datastore.getAvailabilityForTutor(tutorID);

        String json = new Gson().toJson(timeslots);
        response.setContentType("application/json;");
        response.getWriter().println(json);
        return; 
    }
}
