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

import com.google.sps.data.Tutor;
import com.google.sps.data.TimeRange;
import com.google.sps.data.TutorSession;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
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

@RunWith(JUnit4.class)
public final class SearchTest {
    private static final int TIME_0800AM = TimeRange.getTimeInMinutes(8, 00);
    private static final int TIME_0900AM = TimeRange.getTimeInMinutes(9, 00);
    private static final int TIME_1000AM = TimeRange.getTimeInMinutes(10, 00);
    private static final int TIME_1100AM = TimeRange.getTimeInMinutes(11, 00);
    private static final int TIME_1200AM = TimeRange.getTimeInMinutes(12, 00);
    private static final int TIME_0100PM = TimeRange.getTimeInMinutes(13, 00);
    private static final int TIME_0200PM = TimeRange.getTimeInMinutes(14, 00);
    private static final int TIME_0300PM = TimeRange.getTimeInMinutes(15, 00);
    private static final int TIME_0500PM = TimeRange.getTimeInMinutes(17, 00);
    private static final int TIME_1000PM = TimeRange.getTimeInMinutes(22, 00);
    private static final int TIME_1100PM = TimeRange.getTimeInMinutes(23, 00);
    
    private static final Calendar MAY182020 = new Calendar.Builder()
                                                        .setCalendarType("iso8601")
                                                        .setDate(2020, 4, 18)
                                                        .build();

    private static final Calendar AUGUST102020 = new Calendar.Builder()
                                                        .setCalendarType("iso8601")
                                                        .setDate(2020, 7, 10)
                                                        .build();

    private SearchServlet servlet;

    @Before
    public void setUp() {
        servlet = new SearchServlet(true);
    }

    @Test
    public void doGetReturnsCorrectResponseForHistory() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class); 

        when(request.getParameter("topic")).thenReturn("history");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        //create the hard coded data
        servlet.doGet(request, response);

        Calendar expectedDate = new Calendar.Builder()
                                            .setCalendarType("iso8601")
                                            .setDate(2021, 1, 20)
                                            .build();

        //verify that getParameter was called
        verify(request, times(1)).getParameter("topic"); 
        writer.flush(); // it may not have been flushed yet...
        List<Tutor> expectedTutorList = Arrays.asList(new Tutor("Kashish Arora", "kashisharora@google.com", 
                                                            new ArrayList<String> (Arrays.asList("Math", "History")),
                                                            new ArrayList<TimeRange> (Arrays.asList(TimeRange.fromStartToEnd(TIME_1200AM, TIME_0100PM, MAY182020),
                                                                        TimeRange.fromStartToEnd(TIME_0300PM,TIME_0500PM, AUGUST102020), 
                                                                        TimeRange.fromStartToEnd(TIME_1000PM,TIME_1100PM, expectedDate))),
                                                            new ArrayList<TutorSession> (Arrays.asList())));
        System.out.println(stringWriter.toString());
        System.out.println(expected);
        String expected = new Gson().toJson(expectedTutorList);
        Assert.assertTrue(stringWriter.toString().contains(expected));

    }

    @Test
    public void doGetReturnsCorrectEmptyResponseForBusiness() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class); 

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


    /**
    * This method converts a list of tutors to a JSON string.
    * @return String, the list of tutors as a JSON string
    */
    private String convertListToJson(List<Tutor> tutors) {
        ArrayList<String> jsonTutors = new ArrayList<String>();
        Gson gson = new Gson();
        //convert all Tutor objects to JSON
        for(Tutor t : tutors) {
            jsonTutors.add(gson.toJson(t));
        }

        //convert list to JSON
        return gson.toJson(jsonTutors);
    }

}
