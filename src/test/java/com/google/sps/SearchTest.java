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
import com.google.sps.data.SampleData;
import com.google.sps.data.Tutor;
import com.google.sps.data.TimeRange;
import com.google.sps.data.TutorSession;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.mockito.Mockito.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Calendar;
import com.google.gson.Gson;
import java.io.*;
import javax.servlet.http.*;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

@RunWith(JUnit4.class)
public final class SearchTest {
    private final int TIME_0800AM = TimeRange.getTimeInMinutes(8, 00);
    private final int TIME_0900AM = TimeRange.getTimeInMinutes(9, 00);
    private final int TIME_1000AM = TimeRange.getTimeInMinutes(10, 00);
    private final int TIME_1100AM = TimeRange.getTimeInMinutes(11, 00);
    private final int TIME_1200AM = TimeRange.getTimeInMinutes(12, 00);
    private final int TIME_0100PM = TimeRange.getTimeInMinutes(13, 00);
    private final int TIME_0200PM = TimeRange.getTimeInMinutes(14, 00);
    private final int TIME_0300PM = TimeRange.getTimeInMinutes(15, 00);
    private final int TIME_0500PM = TimeRange.getTimeInMinutes(17, 00);
    private final int TIME_1000PM = TimeRange.getTimeInMinutes(22, 00);
    private final int TIME_1100PM = TimeRange.getTimeInMinutes(23, 00);
    private final Calendar MAY182020 = new Calendar.Builder()
                                                        .setCalendarType("iso8601")
                                                        .setDate(2020, 4, 18)
                                                        .build();

    private final Calendar AUGUST102020 = new Calendar.Builder()
                                                        .setCalendarType("iso8601")
                                                        .setDate(2020, 7, 10)
                                                        .build();

    private final LocalServiceTestHelper helper =  new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    private SampleData sample;

    private SearchServlet servlet;

    @Before
    public void setUp() {
        helper.setUp();	  

        sample = new SampleData();
        sample.addTutorsToDatastore();

        servlet = new SearchServlet();
        servlet.init();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void doGetReturnsCorrectResponseForHistory() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class); 
        TestUtilities.setSessionId(request, "");

        when(request.getParameter("topic")).thenReturn("history");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        //create the hard coded data
        servlet.doGet(request, response);

        //verify that getParameter was called
        verify(request, times(1)).getParameter("topic"); 
        writer.flush(); // it may not have been flushed yet...
        List<Tutor> expectedTutorList = Arrays.asList(sample.getTutorByEmail("kashisharora@google.com"));
        String expected = new Gson().toJson(expectedTutorList);
        Assert.assertTrue(stringWriter.toString().contains(expected));
    }

    @Test
    public void testDefaultSortBy() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class); 
        TestUtilities.setSessionId(request, "");

        when(request.getParameter("topic")).thenReturn("english");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        //create the hard coded data
        servlet.doGet(request, response);

        //verify that getParameter was called
        verify(request, times(1)).getParameter("topic"); 
        writer.flush(); // it may not have been flushed yet...
        List<Tutor> expectedTutorList = Arrays.asList(sample.getTutorByEmail("btrevisan@google.com"), sample.getTutorByEmail("sfalberg@google.com"));
        String expected = new Gson().toJson(expectedTutorList);
        Assert.assertTrue(stringWriter.toString().contains(expected));
    }

    @Test
    public void testSortByRating() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class); 
        TestUtilities.setSessionId(request, "");

        //rate Sam
        sample.rateTutor("2", 5);

        when(request.getParameter("topic")).thenReturn("english");
        when(request.getParameter("sort-type")).thenReturn("rating");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        //create the hard coded data
        servlet.doGet(request, response);

        //verify that getParameter was called
        verify(request, times(1)).getParameter("topic"); 
        verify(request, times(1)).getParameter("sort-type"); 
        writer.flush(); // it may not have been flushed yet...
        //Sam should be before Bernardo now
        List<Tutor> expectedTutorList = Arrays.asList(sample.getTutorByEmail("sfalberg@google.com"), sample.getTutorByEmail("btrevisan@google.com"));
        String expected = new Gson().toJson(expectedTutorList);
        Assert.assertTrue(stringWriter.toString().contains(expected));
    }

    @Test
    public void testSortByAvailability() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class); 
        TestUtilities.setSessionId(request, "");

        when(request.getParameter("topic")).thenReturn("english");
        when(request.getParameter("sort-type")).thenReturn("availability");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        //create the hard coded data
        servlet.doGet(request, response);

        //verify that getParameter was called
        verify(request, times(1)).getParameter("topic"); 
        verify(request, times(1)).getParameter("sort-type"); 
        writer.flush(); // it may not have been flushed yet...
        //Bernardo has more available timeslots
        List<Tutor> expectedTutorList = Arrays.asList(sample.getTutorByEmail("btrevisan@google.com"), sample.getTutorByEmail("sfalberg@google.com"));
        String expected = new Gson().toJson(expectedTutorList);
        Assert.assertTrue(stringWriter.toString().contains(expected));
    }

    /**
    * The current searcher is a tutor and that tutor is the only result for history. They should not be able to see themselves in the result.
    */
    @Test
    public void testWhenSearcherIsPartOfResult() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class); 
        TestUtilities.setSessionId(request, "0");

        when(request.getParameter("topic")).thenReturn("history");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        //create the hard coded data
        servlet.doGet(request, response);

        //verify that getParameter was called
        verify(request, times(1)).getParameter("topic"); 
        writer.flush(); // it may not have been flushed yet...

        String expected = "[]";
        System.out.println(stringWriter.toString());
        System.out.println(expected);
        Assert.assertTrue(stringWriter.toString().contains(expected));
    }

    @Test
    public void doGetReturnsCorrectEmptyResponseForBusiness() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class); 
        TestUtilities.setSessionId(request, "");
        when(request.getParameter("topic")).thenReturn("business");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        //create the hard coded data
        servlet.doGet(request, response);

        //verify that getParameter was called
        verify(request, atLeast(1)).getParameter("topic"); 
        writer.flush(); // it may not have been flushed yet...

        //there are no tutors for business, so it should return an empty string
        String expected = "[]";
        Assert.assertTrue(stringWriter.toString().contains(expected));
    }

    @Test
    public void doGetReturnsCorrectErrorResponseForEmptyString() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class); 
        TestUtilities.setSessionId(request, "");

        when(request.getParameter("topic")).thenReturn("");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        //create the hard coded data
        servlet.doGet(request, response);

        //verify that getParameter was called
        verify(request, times(1)).getParameter("topic"); 
        writer.flush(); // it may not have been flushed yet...

        //there are no tutors for business, so it should return an empty string
        String expected = "{\"error\": \"Invalid search request.\"}";
        Assert.assertTrue(stringWriter.toString().contains(expected));
    }

}
