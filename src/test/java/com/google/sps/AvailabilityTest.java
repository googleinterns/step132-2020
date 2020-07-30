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
import com.google.sps.servlets.AvailabilityServlet;
import com.google.gson.Gson;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import javax.servlet.*;

@RunWith(JUnit4.class)
public final class AvailabilityTest {
    private final int TIME_1200AM = TimeRange.getTimeInMinutes(12, 00);
    private final int TIME_0100PM = TimeRange.getTimeInMinutes(13, 00);
    private final int TIME_0300PM = TimeRange.getTimeInMinutes(15, 00);
    private final int TIME_0500PM = TimeRange.getTimeInMinutes(17, 00);
    private final Calendar NOV182020 = new Calendar.Builder()
                                                        .setCalendarType("iso8601")
                                                        .setDate(2020, 10, 18)
                                                        .build();
    private final Calendar DEC102020 = new Calendar.Builder()
                                                        .setCalendarType("iso8601")
                                                        .setDate(2020, 11, 10)
                                                        .build();
    
    private final LocalServiceTestHelper helper =  new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    
    private AvailabilityServlet servlet;

    @Before
    public void setUp() {
        helper.setUp();	        

        servlet = new AvailabilityServlet();
        servlet.init();

        SampleData sample  = new SampleData();
        sample.addTutorsToDatastore();

    }

    @After
    public void tearDown() {
        helper.tearDown();
    }
  
    @Test
    public void testDoGetWithValidId() throws Exception {

        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("tutorID")).thenReturn("0");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        when(request.getContentType()).thenReturn("application/json");
      
        servlet.doGet(request, response);

        String expected = new Gson().toJson(Arrays.asList(TimeRange.fromStartToEnd(TIME_1200AM, TIME_0100PM, NOV182020),
                                                    TimeRange.fromStartToEnd(TIME_0300PM,TIME_0500PM, DEC102020)));

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
}
