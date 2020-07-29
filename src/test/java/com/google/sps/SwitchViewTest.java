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
import com.google.sps.servlets.SwitchViewServlet;
import com.google.sps.data.SampleData;
import com.google.sps.data.TimeRange;
import com.google.sps.data.Tutor;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Arrays;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

@RunWith(JUnit4.class)
public final class SwitchViewTest {
    private final LocalServiceTestHelper helper =  new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()); 

    private SwitchViewServlet servlet;

    @Before
    public void setUp() {
        helper.setUp();

        servlet = new SwitchViewServlet();
        servlet.init();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testDoPostTutorToStudent() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Adds user entity to the local datastore
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity userEntity = new Entity("User");
        userEntity.setProperty("userId", "123");
        userEntity.setProperty("view", "tutor");
        datastore.put(userEntity);

        TestUtilities.setSessionId(request, "123");  

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        when(request.getContentType()).thenReturn("application/json");
      
        servlet.doPost(request, response);

        String expected = "student";

        writer.flush();
        Assert.assertTrue(stringWriter.toString().contains(expected));
    }

    @Test
    public void testDoPostStudentToTutor() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Adds user entity to the local datastore
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity userEntity = new Entity("User");
        userEntity.setProperty("userId", "123");
        userEntity.setProperty("view", "student");
        datastore.put(userEntity);

        TestUtilities.setSessionId(request, "123");  

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        when(request.getContentType()).thenReturn("application/json");
      
        servlet.doPost(request, response);

        String expected = "tutor";

        writer.flush();
        Assert.assertTrue(stringWriter.toString().contains(expected));
    }
}
