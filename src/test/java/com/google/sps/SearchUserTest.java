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

import com.google.sps.data.SampleData;
import com.google.sps.data.User;
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
public final class SearchUserTest {
    private final LocalServiceTestHelper helper =  new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    private SearchUserServlet servlet;

    @Before
    public void setUp() {
        helper.setUp();	  

        SampleData sample = new SampleData();
        sample.addUsersToDatastore();

        servlet = new SearchUserServlet();
        servlet.init();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void doGetWithFullName() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class); 

        when(request.getParameter("name")).thenReturn("test tester");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        servlet.doGet(request, response);

        // verify that getParameter was called
        verify(request, times(1)).getParameter("name"); 
        writer.flush(); // it may not have been flushed yet...
        List<User> expectedUserList = Arrays.asList(new User("test tester", "0"));
        String expected = new Gson().toJson(expectedUserList);
        Assert.assertTrue(stringWriter.toString().contains(expected));
    }

    @Test
    public void doGetWithUpperCase() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class); 

        when(request.getParameter("name")).thenReturn("Tester Test");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        servlet.doGet(request, response);

        // verify that getParameter was called
        verify(request, times(1)).getParameter("name"); 
        writer.flush(); // it may not have been flushed yet...
        List<User> expectedUserList = Arrays.asList(new User("tester test", "1"));
        String expected = new Gson().toJson(expectedUserList);
        Assert.assertTrue(stringWriter.toString().contains(expected));
    }

    @Test
    public void doGetWithPartialName() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class); 

        when(request.getParameter("name")).thenReturn("test");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        servlet.doGet(request, response);

        // verify that getParameter was called
        verify(request, times(1)).getParameter("name"); 
        writer.flush(); // it may not have been flushed yet...
        List<User> expectedUserList = Arrays.asList(new User("test tester", "0"), new User("tester test", "1"));
        String expected = new Gson().toJson(expectedUserList);
        Assert.assertTrue(stringWriter.toString().contains(expected));
    }

    @Test
    public void doGetWithNoMatch() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class); 

        when(request.getParameter("name")).thenReturn("empty");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        servlet.doGet(request, response);

        // verify that getParameter was called
        verify(request, times(1)).getParameter("name"); 
        writer.flush(); // it may not have been flushed yet...
        List<User> expectedUserList = Arrays.asList();
        String expected = new Gson().toJson(expectedUserList);
        Assert.assertTrue(stringWriter.toString().contains(expected));
    }

 

    @Test
    public void doGetWithError() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class); 

        when(request.getParameter("topic")).thenReturn("");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        servlet.doGet(request, response);

        //verify that getParameter was called
        verify(request, times(1)).getParameter("name"); 
        writer.flush(); // it may not have been flushed yet...

        String expected = "{\"error\": \"Invalid search request.\"}";
        Assert.assertTrue(stringWriter.toString().contains(expected));
    }

}
