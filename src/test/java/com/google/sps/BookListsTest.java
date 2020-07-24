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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.mockito.Mockito.*;
import com.google.sps.servlets.BookListsServlet;
import com.google.sps.data.BookList;
import com.google.gson.Gson;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

@RunWith(JUnit4.class)
public final class BookListsTest {
    private final LocalServiceTestHelper helper =  new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()); 

    private BookListsServlet servlet;

    @Before
    public void setUp() {
        helper.setUp();

        servlet = new BookListsServlet();
        servlet.init();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testDoGetWithNoLists() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("topic")).thenReturn("random");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        when(request.getContentType()).thenReturn("application/json");

        servlet.doGet(request, response);

        verify(request, times(1)).getParameter("topic");

        String expected = "[]";
      
        writer.flush();

        // No lists should have been returned
        Assert.assertTrue(stringWriter.toString().contains(expected));
    }

    @Test
    public void testDoGetWithLists() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class);

        ArrayList<String> books = new ArrayList<String>(Arrays.asList("Book 1 by Random Person", "Book 2 by Anonymous"));

        // Adds book list entity to the local datastore
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity listEntity = new Entity("BookList");
        listEntity.setProperty("tutorID", "123");
        listEntity.setProperty("books", books);
        listEntity.setProperty("name", "My Math Books");
        listEntity.setProperty("topic", "math");

        datastore.put(listEntity);

        when(request.getParameter("topic")).thenReturn("math");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        when(request.getContentType()).thenReturn("application/json");

        servlet.doGet(request, response);

        verify(request, times(1)).getParameter("topic");

        String expected = new Gson()
                            .toJson(new ArrayList<BookList> (Arrays.asList(new BookList(books, "My Math Books", "math", "123", 1))));
      
        writer.flush();

        // The correct list of booklists should have been returned
        Assert.assertTrue(stringWriter.toString().contains(expected));
    }

    @Test
    public void testDoGetWithError() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("topic")).thenReturn(null);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        when(request.getContentType()).thenReturn("application/json");

        servlet.doGet(request, response);

        verify(request, times(1)).getParameter("topic");

        String expected = "{\"error\": \"Invalid search request.\"}";
      
        writer.flush();

        // Error should have been returned
        Assert.assertTrue(stringWriter.toString().contains(expected));
    }
}
