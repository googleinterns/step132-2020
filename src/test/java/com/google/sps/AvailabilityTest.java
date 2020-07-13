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

import java.util.List;
import java.util.Arrays;
import java.util.Calendar;
import java.io.StringWriter;
import java.io.PrintWriter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.mockito.Mockito.*;
import com.google.sps.data.TimeRange;
import com.google.sps.data.SampleData;
import com.google.sps.data.TutorSession;
import com.google.sps.servlets.AvailabilityServlet;
import com.google.gson.Gson;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;
import com.google.appengine.api.datastore.Query;

import javax.servlet.*;

@RunWith(JUnit4.class)
public final class AvailabilityTest {
    private final int TIME_1200AM = TimeRange.getTimeInMinutes(12, 00);
    private final int TIME_0100PM = TimeRange.getTimeInMinutes(13, 00);
    private final int TIME_0300PM = TimeRange.getTimeInMinutes(15, 00);
    private final int TIME_0500PM = TimeRange.getTimeInMinutes(17, 00);
    private final Calendar MAY182020 = new Calendar.Builder()
                                                        .setCalendarType("iso8601")
                                                        .setDate(2020, 4, 18)
                                                        .build();
    private final Calendar AUGUST102020 = new Calendar.Builder()
                                                        .setCalendarType("iso8601")
                                                        .setDate(2020, 7, 10)
                                                        .build();
    
    private final String USER_ID = "123";

    private final LocalServiceTestHelper helper =  new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    
    private AvailabilityServlet servlet;

    @Before
    public void setUp() {
        helper.setUp();	        

        servlet = new AvailabilityServlet();
        TutorSession.resetIds();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }
  
    @Test
    public void testDoGetWithValidId() throws Exception {
        addAvailabilityToTestDatastore();

        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("tutorID")).thenReturn(USER_ID);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        when(request.getContentType()).thenReturn("application/json");
      
        servlet.doGet(request, response);

        String expected = new Gson().toJson(Arrays.asList(TimeRange.fromStartToEnd(TIME_1200AM, TIME_0100PM, MAY182020),
                                                    TimeRange.fromStartToEnd(TIME_0300PM,TIME_0500PM, AUGUST102020)));

        verify(request, times(1)).getParameter("tutorID");
        writer.flush();
        Assert.assertTrue(stringWriter.toString().contains(expected));
    }

    @Test
    public void testDoGetWithInvalidId() throws Exception {

        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("tutorID")).thenReturn(null);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        when(request.getContentType()).thenReturn("application/json");
      
        servlet.doGet(request, response);

        String expected = "";

        writer.flush();
        Assert.assertTrue(stringWriter.toString().contains(expected));
    }

    /**
    * Adds sample availabilities to local datastore for testing.
    */
    private void addAvailabilityToTestDatastore() {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Assert.assertEquals(0, datastore.prepare(new Query("TimeRange")).countEntities(withLimit(10)));

        Entity timeEntity1 = new Entity("TimeRange");

        timeEntity1.setProperty("userId", Long.parseLong(USER_ID));
        timeEntity1.setProperty("start", TIME_1200AM);
        timeEntity1.setProperty("end", TIME_0100PM);
        timeEntity1.setProperty("date", new Gson().toJson(MAY182020));

        datastore.put(timeEntity1);

        Entity timeEntity2 = new Entity("TimeRange");

        timeEntity2.setProperty("userId", Long.parseLong(USER_ID));
        timeEntity2.setProperty("start", TIME_0300PM);
        timeEntity2.setProperty("end", TIME_0500PM);
        timeEntity2.setProperty("date", new Gson().toJson(AUGUST102020));

        datastore.put(timeEntity2);

        Assert.assertEquals(2, datastore.prepare(new Query("TimeRange")).countEntities(withLimit(10)));
    }
}
