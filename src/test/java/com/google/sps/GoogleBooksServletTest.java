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
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.mockito.Mockito.*;
import com.google.sps.servlets.GoogleBooksServlet;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.HttpURLConnection;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import javax.servlet.*;

@RunWith(JUnit4.class)
public final class GoogleBooksServletTest {
    
    @Test
    public void testDoGet() throws Exception {

        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpURLConnection mockConnection = mock(HttpURLConnection.class);
        //create a spy for the servlet
        GoogleBooksServlet servletSpy = spy(new GoogleBooksServlet());

        String jsonString = "{\"totalItems\": 1, \"items\":[\"book 1\"]}";
        InputStream inputStream = new ByteArrayInputStream(jsonString.getBytes());

        //spy on getConnection, return mockConnection
        doReturn(mockConnection).when(servletSpy).getConnection(anyString());
        //spy on getApiKey, return test api key
        doReturn("123").when(servletSpy).getApiKey();
        //spy on readStream, return test json string
        doReturn(jsonString).when(servletSpy).readStream(any(InputStream.class));

        when(mockConnection.getInputStream()).thenReturn(inputStream);
        when(request.getParameter("topic")).thenReturn("test");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        when(request.getContentType()).thenReturn("application/json");

        //call the real implementation of doGet
        servletSpy.doGet(request, response);

        verify(request, times(1)).getParameter("topic");
        writer.flush();
        Assert.assertTrue(stringWriter.toString().contains(new JsonParser().parse(jsonString).toString()));
    }

}
