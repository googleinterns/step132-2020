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
import com.google.sps.data.TimeRange;
import com.google.sps.data.SampleData;
import com.google.sps.servlets.AvailabilityServlet;
import com.google.gson.Gson;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.*;

@RunWith(JUnit4.class)
public final class AvailabilityTest {
  
    @Test
    public void testDoGet() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("tutorID")).thenReturn("kashisharora@google.com");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        when(request.getContentType()).thenReturn("application/json");

        AvailabilityServlet servlet = new AvailabilityServlet();
        servlet.doGet(request, response);

        String expected = new Gson().toJson(SampleData.getTutorByEmail("kashisharora@google.com").getAvailability());

        verify(request, times(1)).getParameter("tutorID");
        writer.flush();
        Assert.assertTrue(stringWriter.toString().contains(expected));
    }
}
