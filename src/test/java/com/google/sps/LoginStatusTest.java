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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.sps.data.LoginStatus;
import com.google.sps.servlets.LoginStatusServlet;
import com.google.sps.data.TutorSession;
import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.*;
import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.*;

@RunWith(JUnit4.class)
public final class LoginStatusTest {
    private final String USER_EMAIL = "tutorguy@gmail.com";
    private final String USER_ID = "awesomeID";

    private final LocalServiceTestHelper helper = 
        new LocalServiceTestHelper(new LocalUserServiceTestConfig(), new LocalDatastoreServiceTestConfig())
            .setEnvEmail(USER_EMAIL)
            .setEnvAuthDomain("gmail.com")
            .setEnvAttributes(
              new HashMap(
                  ImmutableMap.of(
                      "com.google.appengine.api.users.UserService.user_id_key", USER_ID)));
    
    private HttpServletRequest request;
    private HttpServletResponse response;
    private LoginStatusServlet servlet;

    @Before 
    public void setUp() {
        helper.setUp();

        request = mock(HttpServletRequest.class);       
        response = mock(HttpServletResponse.class);
        servlet = new LoginStatusServlet();
        TutorSession.resetIds();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void userNotLoggedIn() throws IOException {
        helper.setEnvIsLoggedIn(false);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        when(request.getContentType()).thenReturn("application/json");

        servlet.doGet(request, response);

        writer.flush();
        //Remove new line at the end to compare to expected String
        String actual = stringWriter.toString().replace("\n", "");
        LoginStatus expectedStatus = new LoginStatus(false, false, "/_ah/login?continue=%2Fregistration.html");
        String expected = new Gson().toJson(expectedStatus);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void userLoggedInNotRegistered() throws IOException {
        helper.setEnvIsLoggedIn(true);

        String name = null;

        when(request.getHeader("referer")).thenReturn("/homepage.html");


        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        when(request.getContentType()).thenReturn("application/json");

        servlet.setRegistered(request, response, name);

        writer.flush();
        //Remove new line at the end to compare to expected String
        String actual = stringWriter.toString().replace("\n", "");
        LoginStatus expectedStatus = new LoginStatus(true, true, "/_ah/logout?continue=%2Fhomepage.html");
        String expected = new Gson().toJson(expectedStatus);
        System.out.println("Actual: " + actual);
        System.out.println("Expected: " + expected);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void userLoggedInAndRegistered() throws IOException {
        helper.setEnvIsLoggedIn(true);

        String name = "Sam Falberg";

        when(request.getHeader("referer")).thenReturn("/homepage.html");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        when(request.getContentType()).thenReturn("application/json");

        servlet.setRegistered(request, response, name);

        writer.flush();
        //Remove new line at the end to compare to expected String
        String actual = stringWriter.toString().replace("\n", "");
        LoginStatus expectedStatus = new LoginStatus(true, false, "/_ah/logout?continue=%2Fhomepage.html");
        String expected = new Gson().toJson(expectedStatus);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getNameUserRegistered() {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity userEntity = new Entity("User");
        userEntity.setProperty("name", "Student McKnowledge");
        userEntity.setProperty("userId", USER_ID);
        datastore.put(userEntity);
        
        String actual = servlet.getName(USER_ID, datastore);
        String expected = "Student McKnowledge";

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getNameUserNotRegistered() {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        String actual = servlet.getName(USER_ID, datastore);
        String expected = null;

        Assert.assertEquals(expected, actual);
    }
}
