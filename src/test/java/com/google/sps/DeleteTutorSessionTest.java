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

import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.mockito.Mockito.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.*;
import com.google.sps.servlets.DeleteTutorSessionServlet;
import com.google.sps.utilities.TutorSessionDatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.sps.data.SampleData;
import com.google.sps.data.TimeRange;
import com.google.sps.data.Tutor;
import com.google.sps.data.TutorSession;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Arrays;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

@RunWith(JUnit4.class)
public final class DeleteTutorSessionTest {

    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()); 

    private DeleteTutorSessionServlet servlet;

    @Before
    public void setUp() {
        helper.setUp();

        servlet = new DeleteTutorSessionServlet();
        servlet.init();

        SampleData sample = new SampleData();
        sample.addTutorsToDatastore();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testDoPost() throws Exception {

        Calendar date = new Calendar.Builder()
                                            .setCalendarType("iso8601")
                                            .setDate(2020, 7, 18)
                                            .build();

        TimeRange time = TimeRange.fromStartToEnd(540, 600, date);
        // addScheduledTimeRange(time);

        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("tutorID")).thenReturn("2");
        when(request.getParameter("tutorID")).thenReturn("2");
        when(request.getParameter("start")).thenReturn("540");
        when(request.getParameter("end")).thenReturn("600");
        when(request.getParameter("day")).thenReturn("18");
        when(request.getParameter("month")).thenReturn("7");
        when(request.getParameter("year")).thenReturn("2020");
        when(request.getParameter("subtopics")).thenReturn(null);
        when(request.getParameter("questions")).thenReturn(null);
        when(request.getParameter("rating")).thenReturn("5");
        when(request.getParameter("id")).thenReturn("14");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        when(request.getContentType()).thenReturn("application/json");

        servlet.doPost(request, response);

        verify(request, times(1)).getParameter("tutorID");
        verify(request, times(1)).getParameter("studentID");
        verify(request, times(1)).getParameter("start");
        verify(request, times(1)).getParameter("end");
        verify(request, times(1)).getParameter("day");
        verify(request, times(1)).getParameter("month");
        verify(request, times(1)).getParameter("year");
        verify(request, times(1)).getParameter("subtopics");
        verify(request, times(1)).getParameter("questions");
        verify(request, times(1)).getParameter("rating");
        verify(request, times(1)).getParameter("id");

        String unexpected = new Gson()
                            .toJson(Arrays.asList(new TutorSession("2",
                                            "2",
                                            null,
                                            null,
                                            time, 14)));

        writer.flush();
        // Tutor session should no longer be scheduled
        Assert.assertFalse(stringWriter.toString().contains(unexpected));
    }

    // private void addScheduledTimeRange(TimeRange time) {
    //     DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    //     Entity timeEntity = new Entity("TimeRange");

    //     //the id of the session we are testing
    //     timeEntity.setProperty("tutorID", "14");
    //     timeEntity.setProperty("start", time.getStart());
    //     timeEntity.setProperty("end", time.getEnd());
    //     timeEntity.setProperty("date", new Gson().toJson(time.getDate()));

    //     datastore.put(timeEntity);
    // }

}
