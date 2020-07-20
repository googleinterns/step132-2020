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
import com.google.sps.servlets.DeleteGoalServlet;
import com.google.sps.data.SampleData;
import com.google.sps.data.TimeRange;
import com.google.sps.data.Tutor;
import com.google.sps.data.Goal;
import com.google.gson.Gson;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;


@RunWith(JUnit4.class)
public final class DeleteGoalTest {
    private final LocalServiceTestHelper helper =  new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()); 

    private DeleteGoalServlet servlet;

    @Before
    public void setUp() {
        helper.setUp();

        servlet = new DeleteGoalServlet();
        servlet.init();

        SampleData sample  = new SampleData();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testDoPost() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Adds goal entity to the local datastore
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity goalEntity = new Entity("Goal");
        goalEntity.setProperty("studentID", "123");
        goalEntity.setProperty("goal", "testing");
        datastore.put(goalEntity);

        TestUtilities.setSessionId(request, "123");   
        when(request.getParameter("id")).thenReturn("1");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        when(request.getContentType()).thenReturn("application/json");

        servlet.doPost(request, response);

        verify(request, times(1)).getParameter("id");

        String expected = "[]";

        writer.flush();
        // There should be no goals as the only goal should have been delete
        Assert.assertTrue(stringWriter.toString().contains(expected));
    }

    @Test
    public void testDoPostWithMultipleExperiences() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Adds goal entities to the local datastore
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity goalEntity1 = new Entity("Goal");
        goalEntity1.setProperty("studentID", "123");
        goalEntity1.setProperty("goal", "testing");
        datastore.put(goalEntity1);
        Entity goalEntity2 = new Entity("Goal");
        goalEntity2.setProperty("studentID", "123");
        goalEntity2.setProperty("goal", "testing");
        datastore.put(goalEntity2);

        TestUtilities.setSessionId(request, "123");   
        when(request.getParameter("id")).thenReturn("1");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        when(request.getContentType()).thenReturn("application/json");

        servlet.doPost(request, response);

        verify(request, times(1)).getParameter("id");

        String expected = new Gson()
                            .toJson(new ArrayList<Goal> (Arrays.asList(new Goal("123", "testing", 2))));

        writer.flush();
        // Goal with id 2 should be the only one left
        Assert.assertTrue(stringWriter.toString().contains(expected));
    }

}
