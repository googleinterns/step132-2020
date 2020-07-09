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
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.mockito.Mockito.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.*;
import com.google.sps.servlets.SchedulingServlet;
import com.google.sps.data.SampleData;
import com.google.sps.data.TimeRange;
import com.google.sps.data.Tutor;
import com.google.sps.data.TutorSession;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Arrays;


@RunWith(JUnit4.class)
public final class SchedulingTest {
    private static final Calendar MAY182020 = new Calendar.Builder()
                                                        .setCalendarType("iso8601")
                                                        .setDate(2020, 4, 18)
                                                        .build();

    private static final Calendar AUGUST102020 = new Calendar.Builder()
                                                        .setCalendarType("iso8601")
                                                        .setDate(2020, 7, 10)
                                                        .build();

    private static final Calendar AUGUST72020 = new Calendar.Builder()
                                                        .setCalendarType("iso8601")
                                                        .setDate(2020, 7, 7)
                                                        .build();

    private static final int TIME_0800AM = TimeRange.getTimeInMinutes(8, 00);
    private static final int TIME_0900AM = TimeRange.getTimeInMinutes(9, 00);
    private static final int TIME_1000AM = TimeRange.getTimeInMinutes(10, 00);
    private static final int TIME_1100AM = TimeRange.getTimeInMinutes(11, 00);
    private static final int TIME_1200AM = TimeRange.getTimeInMinutes(12, 00);
    private static final int TIME_0100PM = TimeRange.getTimeInMinutes(13, 00);
    private static final int TIME_0200PM = TimeRange.getTimeInMinutes(14, 00);
    private static final int TIME_0300PM = TimeRange.getTimeInMinutes(15, 00);
    private static final int TIME_0500PM = TimeRange.getTimeInMinutes(17, 00);

    private SchedulingServlet servlet;

    @Before
    public void setUp() {
        servlet = new SchedulingServlet(true);
    }

    @Test
    public void testDoPost() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("tutorID")).thenReturn("btrevisan@google.com");
        when(request.getParameter("start")).thenReturn("480");
        when(request.getParameter("end")).thenReturn("600");
        when(request.getParameter("year")).thenReturn("2020");
        when(request.getParameter("month")).thenReturn("4");
        when(request.getParameter("day")).thenReturn("18");
        when(request.getParameter("studentEmail")).thenReturn("thegoogler@google.com");
        when(request.getParameter("subtopics")).thenReturn("algebra");
        when(request.getParameter("questions")).thenReturn("How does it work?");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        when(request.getContentType()).thenReturn("application/json");

        servlet.doPost(request, response);

        verify(request, times(1)).getParameter("tutorID");
        verify(request, times(1)).getParameter("start");
        verify(request, times(1)).getParameter("end");
        verify(request, times(1)).getParameter("year");
        verify(request, times(1)).getParameter("month");
        verify(request, times(1)).getParameter("day");
        verify(request, times(1)).getParameter("studentEmail");
        verify(request, times(1)).getParameter("subtopics");
        verify(request, times(1)).getParameter("questions");

        String expected = new Gson()
                            .toJson(new TutorSession("thegoogler@google.com",
                                            "btrevisan@google.com",
                                            "algebra",
                                            "How does it work?",
                                            TimeRange.fromStartToEnd(TIME_0800AM, TIME_1000AM, MAY182020)));
        String unexpected = new Gson()
                            .toJson(new Tutor("Bernardo Eilert Trevisan",
                                            "Bernardo\'s bio",
                                            "images/pfp.jpg",
                                            "btrevisan@google.com",
                                            new ArrayList<String> (Arrays.asList("English", "Physics")),
                                            new ArrayList<TimeRange> (Arrays.asList(TimeRange.fromStartToEnd(TIME_0800AM, TIME_1000AM, MAY182020),
                                                        TimeRange.fromStartToEnd(TIME_1100AM,TIME_0100PM, AUGUST102020),
                                                        TimeRange.fromStartToEnd(TIME_0100PM, TIME_0300PM, AUGUST72020))),
                                            new ArrayList<TutorSession> (Arrays.asList())));

        writer.flush();
        // Tutoring session should have been scheduled
        Assert.assertTrue(stringWriter.toString().contains(expected));
        // Previously available timeslot should no longer be available
        Assert.assertFalse(stringWriter.toString().contains(unexpected));
    }

}
