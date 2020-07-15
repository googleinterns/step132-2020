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
import java.util.Calendar;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.mockito.Mockito.*;
import com.google.sps.servlets.RatingServlet;
import com.google.gson.Gson;
import com.google.sps.data.Tutor;
import com.google.sps.data.TimeRange;
import com.google.sps.data.SampleData;
import com.google.sps.data.Student;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.*;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

@RunWith(JUnit4.class)
public final class RatingTest {
    private final Calendar MAY182020 = new Calendar.Builder()
                                                        .setCalendarType("iso8601")
                                                        .setDate(2020, 4, 18)
                                                        .build();
    
    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()); 

    private RatingServlet servlet;

    @Before
    public void setUp() {
        helper.setUp();

        servlet = new RatingServlet();
        servlet.init();

        SampleData sample  = new SampleData();
        sample.addTutorsToDatastore();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testDoPost() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("tutorID")).thenReturn("2");
        when(request.getParameter("studentID")).thenReturn("2");
        //id of the second hard coded tutor session
        when(request.getParameter("sessionId")).thenReturn("14");
        when(request.getParameter("rating")).thenReturn("5");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        when(request.getContentType()).thenReturn("application/json");

        servlet.doPost(request, response);

        verify(request, atLeast(1)).getParameter("tutorID");
        verify(request, atLeast(1)).getParameter("studentID");
        verify(request, atLeast(1)).getParameter("sessionId");
        verify(request, atLeast(1)).getParameter("rating");
        writer.flush();
        System.out.println(stringWriter.toString());
        // Rating should be 5
        Assert.assertTrue(stringWriter.toString().contains("5"));
        // Tutor session should be rated
        Assert.assertTrue(stringWriter.toString().contains("true"));
    }
}
