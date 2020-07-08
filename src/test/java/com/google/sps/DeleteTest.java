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
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.mockito.Mockito.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.*;
import com.google.sps.servlets.DeleteServlet;
import com.google.sps.data.SampleData;
import com.google.sps.data.TimeRange;
import com.google.sps.data.Tutor;
import com.google.sps.data.TutorSession;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Arrays;


@RunWith(JUnit4.class)
public final class DeleteTest {
    private static final Calendar MAY182020 = new Calendar.Builder()
                                                        .setCalendarType("iso8601")
                                                        .setDate(2020, 4, 18)
                                                        .set(Calendar.HOUR_OF_DAY, 10)
                                                        .build();
    private static final Calendar AUGUST102020 = new Calendar.Builder()
                                                        .setCalendarType("iso8601")
                                                        .setDate(2020, 7, 10)
                                                        .build();

    private static final int TIME_1000AM = TimeRange.getTimeInMinutes(10, 00);
    private static final int TIME_1200AM = TimeRange.getTimeInMinutes(12, 00);
    private static final int TIME_0100PM = TimeRange.getTimeInMinutes(13, 00);
    private static final int TIME_0200PM = TimeRange.getTimeInMinutes(14, 00);

    @Test
    public void testDoPost() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("tutorID")).thenReturn("elian@google.com");
        when(request.getParameter("start")).thenReturn("780");
        when(request.getParameter("end")).thenReturn("840");
        when(request.getParameter("day")).thenReturn("10");
        when(request.getParameter("month")).thenReturn("7");
        when(request.getParameter("year")).thenReturn("2020");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        when(request.getContentType()).thenReturn("application/json");

        DeleteServlet servlet = new DeleteServlet();
        servlet.doPost(request, response);

        verify(request, times(1)).getParameter("tutorID");
        verify(request, times(1)).getParameter("start");
        verify(request, times(1)).getParameter("end");
        verify(request, times(1)).getParameter("day");
        verify(request, times(1)).getParameter("month");
        verify(request, times(1)).getParameter("year");

        String expected = new Gson()
                            .toJson(new Tutor("Elian Dumitru", "elian@google.com", new ArrayList<String> (Arrays.asList("Geology", "Math")),
                                    new ArrayList<TimeRange> (Arrays.asList(TimeRange.fromStartToEnd(TIME_1000AM, TIME_1200AM, MAY182020))),
                                    new ArrayList<TutorSession> (Arrays.asList())));

        String unexpected = new Gson()
                            .toJson(new Tutor("Elian Dumitru", "elian@google.com", new ArrayList<String> (Arrays.asList("Geology", "Math")),
                                    new ArrayList<TimeRange> (Arrays.asList(TimeRange.fromStartToEnd(TIME_1000AM, TIME_1200AM, MAY182020),
                                                TimeRange.fromStartToEnd(TIME_0100PM,TIME_0200PM, AUGUST102020))),
                                    new ArrayList<TutorSession> (Arrays.asList())));

        writer.flush();
        System.out.println(stringWriter.toString());
        System.out.println(expected);
        // New available timeslot should have been added
        Assert.assertTrue(stringWriter.toString().contains(expected));
        Assert.assertFalse(stringWriter.toString().contains(unexpected));
    }

}
