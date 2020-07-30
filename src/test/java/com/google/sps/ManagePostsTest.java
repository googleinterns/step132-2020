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
import com.google.sps.servlets.ManagePostsServlet;
import com.google.sps.data.Post;
import com.google.gson.Gson;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

@RunWith(JUnit4.class)
public final class ManagePostsTest {

    private final LocalServiceTestHelper helper =  new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()); 

    private ManagePostsServlet servlet;

    @Before
    public void setUp() {
        helper.setUp();

        servlet = new ManagePostsServlet();
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

        // Adds post entity to the local datastore that will not match the query
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity postEntity = new Entity("Post");
        postEntity.setProperty("groupId", "123");
        postEntity.setProperty("userId", "321");
        postEntity.setProperty("content", "test");
        datastore.put(postEntity);

        when(request.getParameter("groupId")).thenReturn("456");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        when(request.getContentType()).thenReturn("application/json");

        servlet.doGet(request, response);

        verify(request, times(1)).getParameter("groupId");

        String expected = "[]";
      
        writer.flush();

        // No posts should have been returned
        Assert.assertTrue(stringWriter.toString().contains(expected));
    }

    @Test
    public void testDoGetWithResults() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Adds post entity to the local datastore
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity postEntity = new Entity("Post");
        postEntity.setProperty("groupId", "123");
        postEntity.setProperty("userId", "321");
        postEntity.setProperty("content", "test");
        datastore.put(postEntity);

        when(request.getParameter("groupId")).thenReturn("123");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        when(request.getContentType()).thenReturn("application/json");

        servlet.doGet(request, response);

        verify(request, times(1)).getParameter("groupId");

        String expected = new Gson()
                            .toJson(new ArrayList<Post> (Arrays.asList(new Post("321", "123", "test", 1))));
      
        writer.flush();

        // The correct list of posts should have been returned
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

    @Test
    public void testDoPost() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class);

        TestUtilities.setSessionId(request, "123");
        when(request.getParameter("anonymous")).thenReturn("false");   
        when(request.getParameter("post-content")).thenReturn("test");
        when(request.getParameter("groupId")).thenReturn("321");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        when(request.getContentType()).thenReturn("application/json");

        servlet.doPost(request, response);

        verify(request, times(1)).getParameter("anonymous");
        verify(request, times(1)).getParameter("post-content");
        verify(request, times(1)).getParameter("groupId");

        String expected = new Gson()
                            .toJson(new ArrayList<Post> (Arrays.asList(new Post("123", "321", "test", 1))));
      
        writer.flush();

        // New post should have been added
        Assert.assertTrue(stringWriter.toString().contains(expected));
    }

    @Test
    public void testDoPostWithAnonymousOn() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class);

        TestUtilities.setSessionId(request, "123");
        when(request.getParameter("anonymous")).thenReturn("true");   
        when(request.getParameter("post-content")).thenReturn("test");
        when(request.getParameter("groupId")).thenReturn("321");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        when(request.getContentType()).thenReturn("application/json");

        servlet.doPost(request, response);

        verify(request, times(1)).getParameter("anonymous");
        verify(request, times(1)).getParameter("post-content");
        verify(request, times(1)).getParameter("groupId");

        String expected = new Gson()
                            .toJson(new ArrayList<Post> (Arrays.asList(new Post("anonymous", "321", "test", 1))));
      
        writer.flush();
        
        // New post should have been added
        Assert.assertTrue(stringWriter.toString().contains(expected));
    }

    @Test
    public void testDoPostWithErrorfromUserID() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class);

        TestUtilities.setSessionId(request, null);
        when(request.getParameter("anonymous")).thenReturn("false");   
        when(request.getParameter("post-content")).thenReturn("test");
        when(request.getParameter("groupId")).thenReturn("321");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        when(request.getContentType()).thenReturn("application/json");

        servlet.doPost(request, response);

        verify(request, times(1)).getParameter("post-content");
        verify(request, times(1)).getParameter("groupId");

        String expected = "{\"error\": \"There was an error posting to this group.\"}";
      
        writer.flush();

        // Error should have been returned
        Assert.assertTrue(stringWriter.toString().contains(expected));
    }

    @Test
    public void testDoPostWithErrorWithErrorFromGroupID() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class);

        TestUtilities.setSessionId(request, "123");
        when(request.getParameter("anonymous")).thenReturn("true");   
        when(request.getParameter("post-content")).thenReturn("test");
        when(request.getParameter("groupId")).thenReturn(null);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        when(request.getContentType()).thenReturn("application/json");

        servlet.doPost(request, response);

        verify(request, times(1)).getParameter("post-content");
        verify(request, times(1)).getParameter("groupId");

        String expected = "{\"error\": \"There was an error posting to this group.\"}";
      
        writer.flush();

        // Error should have been returned
        Assert.assertTrue(stringWriter.toString().contains(expected));
    }
}
