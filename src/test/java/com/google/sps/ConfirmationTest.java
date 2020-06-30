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
import java.io.StringWriter;
import java.io.PrintWriter;
import org.junit.Assert;
import org.junit.Test;
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

@RunWith(JUnit4.class)
public final class ConfirmationTest {

    @Test
    public void testDoPostNoSession() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("studentEmail")).thenReturn("elian@google.com");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        when(request.getContentType()).thenReturn("application/json");

        ConfirmationServlet servlet = new ConfirmationServlet();
        servlet.doPost(request, response);

        verify(request, atLeast(1)).getParameter("studentEmail");
        writer.flush();
        // If the user has no scheduled sessions, the return json string should be an empty array
        Assert.assertTrue(stringWriter.toString().contains("[]"));
    }

    @Test
    public void testDoPostWithSessions() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("studentEmail")).thenReturn("sfalberg@google.com");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        when(request.getContentType()).thenReturn("application/json");

        TutorSession tutoringSessionFake = new TutorSession("sfalberg@google.com", "sfalberg@google.com", null, null, TimeRange.fromStartToEnd(540, 600));
        SampleData.addToStudentScheduledSessionsByEmail("sfalberg@google.com", tutoringSessionFake);

        ConfirmationServlet servlet = new ConfirmationServlet();
        servlet.doPost(request, response);

        String expected = new Gson().toJson(new TutorSession[]{tutoringSessionFake});

        verify(request, atLeast(1)).getParameter("studentEmail");
        writer.flush();
        // If the user has no scheduled sessions, the return json string should be an empty array
        Assert.assertTrue(stringWriter.toString().contains(expected));
    }
}
