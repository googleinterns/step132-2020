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
import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.mockito.Mockito.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.*;
import com.google.sps.servlets.ManageGroupsServlet;
import com.google.sps.data.Group;
import com.google.gson.Gson;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

@RunWith(JUnit4.class)
public final class ManageGroupsTest {

    private final LocalServiceTestHelper helper =  new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()); 

    private ManageGroupsServlet servlet;

    @Before
    public void setUp() {
        helper.setUp();

        servlet = new ManageGroupsServlet();
        servlet.init();

    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testDoGetWithNoResults() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Adds group entity to the local datastore that will not match the query
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity groupEntity = new Entity("Group");
        groupEntity.setProperty("name", "real");
        groupEntity.setProperty("topic", "math");
        groupEntity.setProperty("description", "");
        groupEntity.setProperty("owner", "123");
        datastore.put(groupEntity);

        when(request.getParameter("group")).thenReturn("test");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        when(request.getContentType()).thenReturn("application/json");

        servlet.doGet(request, response);

        verify(request, times(1)).getParameter("group");

        String expected = "[]";
      
        writer.flush();

        // No groups should have been returned
        Assert.assertTrue(stringWriter.toString().contains(expected));
    }

    @Test
    public void testDoGetWithResults() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Adds group entity to the local datastore
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity groupEntity = new Entity("Group");
        groupEntity.setProperty("name", "test");
        groupEntity.setProperty("topic", "math");
        groupEntity.setProperty("description", "");
        groupEntity.setProperty("owner", "123");
        datastore.put(groupEntity);

        when(request.getParameter("group")).thenReturn("test");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        when(request.getContentType()).thenReturn("application/json");

        servlet.doGet(request, response);

        verify(request, times(1)).getParameter("group");

        String expected = new Gson()
                            .toJson(new ArrayList<Group> (Arrays.asList(new Group("test", "math", "", "123", 1))));
      
        writer.flush();

        // The correct list of experiences should have been returned
        Assert.assertTrue(stringWriter.toString().contains(expected));
    }

    @Test
    public void testDoGetWithError() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("group")).thenReturn(null);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        when(request.getContentType()).thenReturn("application/json");

        servlet.doGet(request, response);

        verify(request, times(1)).getParameter("group");

        String expected = "{\"error\": \"Invalid search request.\"}";
      
        writer.flush();

        // Error should have been returned
        Assert.assertTrue(stringWriter.toString().contains(expected));
    }

    @Test
    public void testDoPost() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class);

        TestUtilities.setSessionId(request, "123");   
        when(request.getParameter("name")).thenReturn("test");
        when(request.getParameter("topic")).thenReturn("test");
        when(request.getParameter("group-description")).thenReturn("test");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        when(request.getContentType()).thenReturn("application/json");

        servlet.doPost(request, response);

        verify(request, times(1)).getParameter("name");
        verify(request, times(1)).getParameter("topic");
        verify(request, times(1)).getParameter("group-description");

        String expected = new Gson()
                            .toJson(new ArrayList<Group> (Arrays.asList(new Group("test", "test", "test", "123", 1))));
      
        writer.flush();

        // New group should have been added
        Assert.assertTrue(stringWriter.toString().contains(expected));
    }

    @Test
    public void testDoPostWithOtherTopic() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class);

        TestUtilities.setSessionId(request, "123");   
        when(request.getParameter("name")).thenReturn("test");
        when(request.getParameter("topic")).thenReturn("other");
        when(request.getParameter("group-description")).thenReturn("test");
        when(request.getParameter("otherTopic")).thenReturn("other test");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        when(request.getContentType()).thenReturn("application/json");

        servlet.doPost(request, response);

        verify(request, times(1)).getParameter("name");
        verify(request, times(1)).getParameter("topic");
        verify(request, times(1)).getParameter("group-description");
        verify(request, times(1)).getParameter("otherTopic");

        String expected = new Gson()
                            .toJson(new ArrayList<Group> (Arrays.asList(new Group("test", "other test", "test", "123", 1))));
      
        writer.flush();

        // New group should have been added
        Assert.assertTrue(stringWriter.toString().contains(expected));
    }

    @Test
    public void testDoPostWithError() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class);

        TestUtilities.setSessionId(request, null);           
        when(request.getParameter("name")).thenReturn("");
        when(request.getParameter("topic")).thenReturn("");
        when(request.getParameter("group-description")).thenReturn("");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        when(request.getContentType()).thenReturn("application/json");

        servlet.doPost(request, response);

        verify(request, times(1)).getParameter("name");
        verify(request, times(1)).getParameter("topic");
        verify(request, times(1)).getParameter("group-description");

        String expected = "{\"error\": \"There was an error creating group.\"}";
      
        writer.flush();

        // Error should have been returned
        Assert.assertTrue(stringWriter.toString().contains(expected));
    }
}
