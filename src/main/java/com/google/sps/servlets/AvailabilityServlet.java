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
import java.util.Optional;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.Filter;

/** Servlet that manages availability of a tutor. */
@WebServlet("/availability")
public class AvailabilityServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Get the id of the tutor whose availability will be displayed
        //if id is null, use default id 0 (no tutor has id -1, so no availability will be displayed)
        long tutorID = Long.parseLong(Optional.ofNullable(request.getParameter("tutorID")).orElse("-1"));

        List<TimeRange> timeslots = getAvailabilityForTutor(tutorID);

        String json = new Gson().toJson(timeslots);
        response.setContentType("application/json;");
        response.getWriter().println(json);
        return; 
    }

    /**
    * Gets the availability of a tutor with the given email.
    * @return List<TimeRange>
    */
    public List<TimeRange> getAvailabilityForTutor(long id) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        ArrayList<TimeRange> availability = new ArrayList<TimeRange>();
        
        //get all time ranges with user id
        Filter filter = new FilterPredicate("userId", FilterOperator.EQUAL, id);
        Query query = new Query("TimeRange").setFilter(filter);

        PreparedQuery timeRanges = datastore.prepare(query);

        for(Entity time : timeRanges.asIterable()) {
            System.out.println("Sdfs");
            availability.add(createTimeRange(time));
        }

        return availability;
    }

    /**
    * Creates a TimeRange object from a given TimeRange entity.
    * @return TimeRange
    */
    private TimeRange createTimeRange(Entity entity) {
        int start = Math.toIntExact((long) entity.getProperty("start"));
        int end = Math.toIntExact((long) entity.getProperty("end"));
        Calendar date = new Gson().fromJson((String) entity.getProperty("date"), Calendar.class);

        return TimeRange.fromStartToEnd(start, end, date);

    }
}
