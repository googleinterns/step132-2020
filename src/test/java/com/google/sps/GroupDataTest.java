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
import com.google.sps.servlets.GroupDataServlet;
import com.google.sps.data.Group;
import com.google.gson.Gson;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

@RunWith(JUnit4.class)
public final class GroupDataTest {

    private final LocalServiceTestHelper helper =  new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()); 

    private GroupDataServlet servlet;

    @Before
    public void setUp() {
        helper.setUp();

        servlet = new GroupDataServlet();
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

        when(request.getParameter("groupId")).thenReturn("2");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        when(request.getContentType()).thenReturn("application/json");

        servlet.doGet(request, response);

        verify(request, times(1)).getParameter("groupId");
      
        writer.flush();

        String expected = new Gson()
                            .toJson(null);
 
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

        when(request.getParameter("groupId")).thenReturn("1");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        when(request.getContentType()).thenReturn("application/json");

        servlet.doGet(request, response);

        verify(request, times(1)).getParameter("groupId");

        String expected = new Gson()
                            .toJson(new Group("test", "math", "", "123", 1));
      
        writer.flush();

        // The correct list of experiences should have been returned
        Assert.assertTrue(stringWriter.toString().contains(expected));
    }

    @Test
    public void testDoGetWithError() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("groupId")).thenReturn(null);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        when(request.getContentType()).thenReturn("application/json");

        servlet.doGet(request, response);

        verify(request, times(1)).getParameter("groupId");

        String expected = "{\"error\": \"Invalid group id.\"}";
      
        writer.flush();

        // Error should have been returned
        Assert.assertTrue(stringWriter.toString().contains(expected));
    }
}
