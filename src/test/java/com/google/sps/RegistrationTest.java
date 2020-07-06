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
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.common.collect.ImmutableMap;
import com.google.sps.servlets.RegistrationServlet;
import java.io.*;
import java.util.Arrays;
import java.util.ArrayList;
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
public final class RegistrationTest {
    private static final String USER_EMAIL = "tutorguy@gmail.com";
    private static final String USER_ID = "blahblahid";

    private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalUserServiceTestConfig())
          .setEnvEmail(USER_EMAIL)
          .setEnvAuthDomain("gmail.com")
          .setEnvIsLoggedIn(true)
          .setEnvAttributes(
              new HashMap(
                  ImmutableMap.of(
                      "com.google.appengine.api.users.UserService.user_id_key", USER_ID)));
    
    private HttpServletRequest request;
    private HttpServletResponse response;
    private DatastoreService datastore;
    private RegistrationServlet servlet;

    @Before 
    public void setUp() {
        helper.setUp();

        request = mock(HttpServletRequest.class);       
        response = mock(HttpServletResponse.class);
        datastore = mock(DatastoreService.class);
        servlet = new RegistrationServlet();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void doPostCorrectlyRedirects() throws IOException {
        when(request.getParameter("role")).thenReturn("tutor");
        when(request.getParameter("first-name")).thenReturn("Sam");
        when(request.getParameter("last-name")).thenReturn("Falberg");
        when(request.getParameter("math")).thenReturn("math");
        when(request.getParameter("english")).thenReturn(null);
        when(request.getParameter("other")).thenReturn("other");

        servlet.doPost(request, response);

        ArgumentCaptor<String> url = ArgumentCaptor.forClass(String.class);
        verify(response).sendRedirect(url.capture());
        List<String> expected = Arrays.asList("/scheduling.html");
        // Response redirected to correct URL
        Assert.assertEquals(expected, url.getAllValues());
    }

    @Test
    public void doPostCreatesAndStoresEntities() {
        List<String> mockTopics = Arrays.asList("math", "biology");

        Entity expectedUser = new Entity("User");
        expectedUser.setProperty("role", "tutor");
        expectedUser.setProperty("userId", USER_ID);

        Entity expectedTutor = new Entity("Tutor");
        expectedTutor.setProperty("name", "Sam F");
        expectedTutor.setProperty("email", USER_EMAIL);
        expectedTutor.setProperty("availability", new ArrayList<Long>());
        expectedTutor.setProperty("scheduledSessions", new ArrayList<Long>());
        expectedTutor.setProperty("topics", mockTopics);
        expectedTutor.setProperty("userId", USER_ID);

        Entity actualUser = new Entity("User");
        servlet.createUserEntityAndPutInDatastore(datastore, actualUser, "tutor", USER_ID);

        Entity actualTutor = new Entity("Tutor");
        servlet.createTutorEntityAndPutInDatastore(datastore, actualTutor, "Sam F", USER_EMAIL, mockTopics, USER_ID);

        // Entity was put in datastore
        verify(datastore).put(actualUser);
        verify(datastore).put(actualTutor);

        // Compare stringified versions of entities to check if values are same
        // Comparing entities themselves won't work because they're two different objects
        Assert.assertEquals(expectedUser.toString(), actualUser.toString());
        Assert.assertEquals(expectedTutor.toString(), actualTutor.toString());
    }

}
