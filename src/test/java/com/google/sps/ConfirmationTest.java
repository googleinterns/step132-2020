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

import com.google.utilities.TestUtilities;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
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
import com.google.sps.servlets.ConfirmationServlet;
import com.google.gson.Gson;
import com.google.sps.data.Tutor;
import com.google.sps.data.TimeRange;
import com.google.sps.data.TutorSession;
import com.google.sps.data.SampleData;
import com.google.sps.data.Student;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.*;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;


@RunWith(JUnit4.class)
public final class ConfirmationTest {
    private final Calendar AUGUST182020 = new Calendar.Builder()
                                                        .setCalendarType("iso8601")
                                                        .setDate(2020, 7, 18)
                                                        .build();  

    private final LocalServiceTestHelper helper =  new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()); 

    private ConfirmationServlet servlet;

    @Before
    public void setUp() {
        helper.setUp();

        servlet = new ConfirmationServlet();
        servlet.init();

        SampleData sample  = new SampleData();
        sample.addTutorsToDatastore();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testDoGetNoSession() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class);
        //there is no user with session id = 10
        TestUtilities.setSessionId(request, "10");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        when(request.getContentType()).thenReturn("application/json");

        servlet.doGet(request, response);

        writer.flush();
        // If the user has no scheduled sessions, the return json string should be an empty array
        Assert.assertTrue(stringWriter.toString().contains("[]"));
    }

    @Test
    public void testDoGetWithSessions() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class);
        TestUtilities.setSessionId(request, "2"); 


        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        when(request.getContentType()).thenReturn("application/json");
      
        servlet.doGet(request, response);

        String expected = new Gson().toJson(Arrays.asList(new TutorSession("2","2", null, null,
                                                                        TimeRange.fromStartToEnd(540, 600, AUGUST182020), 14)));

        writer.flush();
        Assert.assertTrue(stringWriter.toString().contains(expected));
    }
}
