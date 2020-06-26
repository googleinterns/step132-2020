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
import com.google.sps.servlets.AvailabilityServlet;
import com.google.gson.Gson;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.*;

@RunWith(JUnit4.class)
public final class AvailabilityTest {
    private static final int TIME_0800AM = TimeRange.getTimeInMinutes(8, 00);
    private static final int TIME_0900AM = TimeRange.getTimeInMinutes(9, 00);
    private static final int TIME_1000AM = TimeRange.getTimeInMinutes(10, 00);
    private static final int TIME_1100AM = TimeRange.getTimeInMinutes(11, 00);
    private static final int TIME_1200AM = TimeRange.getTimeInMinutes(12, 00);

    @Test
    public void testDoPost() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class);
        ServletConfig config = mock(ServletConfig.class);    

        when(request.getParameter("tutorID")).thenReturn("kashisharora@google.com");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        when(request.getContentType()).thenReturn("application/json");

        AvailabilityServlet servlet = new AvailabilityServlet();
        servlet.init(config);
        servlet.doPost(request, response);

        verify(request, atLeast(1)).getParameter("tutorID");
        writer.flush();
        Assert.assertTrue(stringWriter.toString().contains("{\"start\":720,\"duration\":60,\"end\":780},{\"start\":900,\"duration\":120,\"end\":1020}"));
    }

    @Test
    public void testConvertToJsonUsingGson() {
        TimeRange[] availability = new TimeRange[]{TimeRange.fromStartToEnd(TIME_0800AM, TIME_0900AM), TimeRange.fromStartToEnd(TIME_1100AM, TIME_1200AM)};

        String actual = convertToJsonUsingGson(Arrays.asList(availability));
        String expected = "[{\"start\":480,\"duration\":60,\"end\":540},{\"start\":660,\"duration\":60,\"end\":720}]";

        Assert.assertEquals(actual, expected);
    }

    // Converts the time slot array into a JSON string using the Gson library.
    private String convertToJsonUsingGson(List<TimeRange> timeslots) {
        Gson gson = new Gson();
        String json = gson.toJson(timeslots);
        return json;
    }
}
