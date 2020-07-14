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
import com.google.sps.servlets.DeleteTutorSessionServlet;
import com.google.sps.data.SampleData;
import com.google.sps.data.TimeRange;
import com.google.sps.data.Tutor;
import com.google.sps.data.TutorSession;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Arrays;


@RunWith(JUnit4.class)
public final class DeleteTutorSessionTest {
    private DeleteTutorSessionServlet servlet;

    @Before
    public void setUp() {		        
        servlet = new DeleteTutorSessionServlet(true);
        TutorSession.resetIds();
    }

    @Test
    public void testDoPost() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("tutorEmail")).thenReturn("sfalberg@google.com");
        when(request.getParameter("studentEmail")).thenReturn("sfalberg@google.com");
        when(request.getParameter("start")).thenReturn("540");
        when(request.getParameter("end")).thenReturn("600");
        when(request.getParameter("day")).thenReturn("18");
        when(request.getParameter("month")).thenReturn("7");
        when(request.getParameter("year")).thenReturn("2020");
        when(request.getParameter("subtopics")).thenReturn(null);
        when(request.getParameter("questions")).thenReturn(null);
        when(request.getParameter("rating")).thenReturn("5");
        when(request.getParameter("id")).thenReturn("1");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        when(request.getContentType()).thenReturn("application/json");

        servlet.doPost(request, response);

        verify(request, times(1)).getParameter("tutorEmail");
        verify(request, times(1)).getParameter("studentEmail");
        verify(request, times(1)).getParameter("start");
        verify(request, times(1)).getParameter("end");
        verify(request, times(1)).getParameter("day");
        verify(request, times(1)).getParameter("month");
        verify(request, times(1)).getParameter("year");
        verify(request, times(1)).getParameter("subtopics");
        verify(request, times(1)).getParameter("questions");
        verify(request, times(1)).getParameter("rating");
        verify(request, times(1)).getParameter("id");

        Calendar date = new Calendar.Builder()
                                            .setCalendarType("iso8601")
                                            .setDate(2020, 7, 18)
                                            .build();

        String unexpected = new Gson()
                            .toJson(Arrays.asList( new TutorSession("sfalberg@google.com",
                                            "sfalberg@google.com",
                                            null,
                                            null,
                                            TimeRange.fromStartToEnd(540, 600, date), 1)));

        writer.flush();
        // Tutor session should no longer be scheduled
        Assert.assertFalse(stringWriter.toString().contains(unexpected));
    }

}
