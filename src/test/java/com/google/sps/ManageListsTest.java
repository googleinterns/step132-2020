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
import java.util.Calendar;
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
import com.google.sps.servlets.ManageListsServlet;
import com.google.sps.data.BookList;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Arrays;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

@RunWith(JUnit4.class)
public final class ManageListsTest {

    private final LocalServiceTestHelper helper =  new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()); 

    private ManageListsServlet servlet;

    @Before
    public void setUp() {
        helper.setUp();

        servlet = new ManageListsServlet();
        servlet.init();

    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testDoGet() throws Exception {
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

        TestUtilities.setSessionId(request, "123");   

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        when(request.getContentType()).thenReturn("application/json");
      
        servlet.doGet(request, response);

        String expected = new Gson()
                            .toJson(new ArrayList<BookList> (Arrays.asList(new BookList(books, "My Math Books", "math", "123", 1))));

        writer.flush();
        Assert.assertTrue(stringWriter.toString().contains(expected));
    }

    @Test
    public void testDoPost() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class);
        TestUtilities.setSessionId(request, "123");   

        when(request.getParameter("books")).thenReturn("New Book 1 by Person 1 \n New Book 2 by Person 2 \n New Book 3 by Person 3");
        when(request.getParameter("name")).thenReturn("My New List");
        when(request.getParameter("topic")).thenReturn("science");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        when(request.getContentType()).thenReturn("application/json");

        servlet.doPost(request, response);

        verify(request, times(1)).getParameter("books");
        verify(request, times(1)).getParameter("name");
        verify(request, times(1)).getParameter("topic");

        ArrayList<String> books = new ArrayList<String>(Arrays.asList("New Book 1 by Person 1", "New Book 2 by Person 2", "New Book 3 by Person 3"));

        String expected = new Gson()
                            .toJson(new ArrayList<BookList> (Arrays.asList(new BookList(books, "My New List", "science", "123", 1))));
      
        writer.flush();
        Assert.assertTrue(stringWriter.toString().contains(expected));
    }

}
