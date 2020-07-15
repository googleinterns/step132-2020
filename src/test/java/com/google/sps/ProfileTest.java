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
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.sps.data.SampleData;
import com.google.sps.data.Student;
import com.google.sps.data.Tutor;
import com.google.sps.servlets.ProfileServlet;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RunWith(JUnit4.class)
public final class ProfileTest {
    private final String USER_EMAIL = "profile@gmail.com";
    private final String USER_ID = "id123";

    private final LocalServiceTestHelper helper = 
         new LocalServiceTestHelper(new LocalUserServiceTestConfig(), new LocalDatastoreServiceTestConfig())
             .setEnvEmail(USER_EMAIL)
             .setEnvAuthDomain("gmail.com")
             .setEnvIsLoggedIn(true)
             .setEnvAttributes(
               new HashMap(
                   ImmutableMap.of(
                       "com.google.appengine.api.users.UserService.user_id_key", USER_ID)));

    private HttpServletRequest request;
    private HttpServletResponse response;
    private ProfileServlet servlet;
    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private SampleData sample = new SampleData();

    @Before 
    public void setUp() {
        helper.setUp();

        request = mock(HttpServletRequest.class);       
        response = mock(HttpServletResponse.class);
        servlet = new ProfileServlet(true);
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void doGetReturnsCorrectResponseForStudent() throws Exception {
        // Add student user entity to the local datastore so there is data to query in the function
        Entity userEntity = new Entity("User");
        userEntity.setProperty("role", "student");
        userEntity.setProperty("userId", "123");
        datastore.put(userEntity);

        // Add student entity that matches sample data
        Entity studentEntity = new Entity("Student");
        studentEntity.setProperty("name", "Bernardo Eilert Trevisan");
        studentEntity.setProperty("bio", "Bernardo\'s bio");
        studentEntity.setProperty("pfp", "images/pfp.jpg");
        studentEntity.setProperty("email", "btrevisan@google.com");
        studentEntity.setProperty("learning", new ArrayList<String> (Arrays.asList("Math", "History")));
        studentEntity.setProperty("userId", "123");
        datastore.put(studentEntity);
        
        when(request.getParameter("user-id")).thenReturn("123");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        
        servlet.doGet(request, response);

        Student expectedStudent = sample.getStudentByEmail("btrevisan@google.com");
        String expected = new Gson().toJson(expectedStudent);
        Assert.assertTrue(stringWriter.toString().contains(expected));
    }

    @Test
    public void doGetReturnsCorrectResponseForTutor() throws Exception {
        // Add tutor user entity to the local datastore so there is data to query in the function
        Entity userEntity = new Entity("User");
        userEntity.setProperty("role", "tutor");
        userEntity.setProperty("userId", "123");
        datastore.put(userEntity);

        // Add tutor entity that matches sample data
        Entity tutorEntity = new Entity("Tutor");
        tutorEntity.setProperty("name", "Kashish Arora");
        tutorEntity.setProperty("bio", "Kashish\'s bio");
        tutorEntity.setProperty("pfp", "images/pfp.jpg");
        tutorEntity.setProperty("email", "kashisharora@google.com");
        tutorEntity.setProperty("topics", new ArrayList<String> (Arrays.asList("Math", "History")));
        tutorEntity.setProperty("userId", "123");
        datastore.put(tutorEntity);

        when(request.getParameter("user-id")).thenReturn("123");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        servlet.doGet(request, response);

        Tutor expectedTutor = sample.getTutorByEmail("kashisharora@google.com");
        String expected = new Gson().toJson(expectedTutor);
        Assert.assertTrue(stringWriter.toString().contains(expected));
    }

    @Test
    public void doPostCancelsFormSubmit() throws Exception {
        when(request.getParameter("submit")).thenReturn("Cancel");

        servlet.doPost(request, response);

        ArgumentCaptor<String> url = ArgumentCaptor.forClass(String.class);
        verify(response).sendRedirect(url.capture());
        List<String> expected = Arrays.asList("/profile.html?userID="+USER_ID);
        // Response redirected to correct URL
        Assert.assertEquals(expected, url.getAllValues());
    }

    @Test
    public void doPostCorrectlyUpdatesEntities() throws Exception {
        Entity studentEntity = new Entity("Student");
        studentEntity.setProperty("bio", "blah");
        studentEntity.setProperty("learning", new ArrayList<String> (Arrays.asList("Math", "History")));
        studentEntity.setProperty("userId", USER_ID);
        datastore.put(studentEntity);

        String newBio = "Updated bio";
        ArrayList<String> newTopics = new ArrayList<String> (Arrays.asList("Orthodontics", "Fortune Telling"));
        
        servlet.updateStudentEntityAndPutInDatastore(datastore, studentEntity, USER_ID, newBio, newTopics);
        
        Query query = new Query("Student").setFilter(new Query.FilterPredicate("userId", Query.FilterOperator.EQUAL, USER_ID));
        PreparedQuery results = datastore.prepare(query);
        Entity actual = results.asSingleEntity();

        Assert.assertEquals(newBio, (String) actual.getProperty("bio"));
        Assert.assertEquals(newTopics, (ArrayList) actual.getProperty("learning"));
    }
}
