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

import com.google.sps.data.SampleData;
import com.google.sps.data.Tutor;
import com.google.sps.data.Student;
import com.google.sps.data.TimeRange;
import com.google.sps.data.TutorSession;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.mockito.Mockito.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Calendar;
import com.google.gson.Gson;
import java.io.*;
import javax.servlet.http.*;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

@RunWith(JUnit4.class)
public final class GetStudentsTest {
    private final LocalServiceTestHelper helper =  new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    private GetStudentsServlet servlet;
    private SampleData sample = new SampleData();

    @Before
    public void setUp() {
        helper.setUp();	  

        sample.addTutorsToDatastore();
        sample.addStudentsToDatastore();

        servlet = new GetStudentsServlet();
        servlet.init();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void doGetReturnsError() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class); 

        when(request.getParameter("tutorID")).thenReturn("");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        servlet.doGet(request, response);

        //verify that getParameter was called
        verify(request, times(1)).getParameter("tutorID"); 
        writer.flush(); // it may not have been flushed yet...
        

        String expected = "{\"error\": \"Invalid tutor.\"}";
        Assert.assertTrue(stringWriter.toString().contains(expected));
    }

    @Test
    public void doGetReturnsNoStudent() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class); 

        when(request.getParameter("tutorID")).thenReturn("1");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        servlet.doGet(request, response);

        //verify that getParameter was called
        verify(request, times(1)).getParameter("tutorID"); 
        writer.flush(); // it may not have been flushed yet...
        
        String expected = "[]";
        Assert.assertTrue(stringWriter.toString().contains(expected));
    }

    @Test
    public void doGetReturnsWithStudents() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class); 

        when(request.getParameter("tutorID")).thenReturn("0");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        servlet.doGet(request, response);

        //verify that getParameter was called
        verify(request, times(1)).getParameter("tutorID"); 
        writer.flush(); // it may not have been flushed yet...
        
        ArrayList<Student> expectedStudents = new ArrayList<Student> (Arrays.asList(sample.getStudentByEmail("thegoogler@google.com"), sample.getStudentByEmail("elian@google.com")));
        String expected = new Gson().toJson(expectedStudents);
        Assert.assertTrue(stringWriter.toString().contains(expected));
    }
}
