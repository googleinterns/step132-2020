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
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.mockito.Mockito.*;
import com.google.sps.servlets.LogoutServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.*;
import javax.servlet.http.HttpSession;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

@RunWith(JUnit4.class)
public final class LogoutTest {

    private LogoutServlet servlet;

    @Before
    public void setUp() {
        servlet = new LogoutServlet();
    }

    @Test
    public void sessionIsInvalidated() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        
        when(request.getSession(false)).thenReturn(session);
        
        //mock the invalidate method for session
         doAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) {
            when(request.getSession(false)).thenReturn(null);
            return null;
        }}).when(session).invalidate();

         servlet.doPost(request, response);

         Assert.assertNull(request.getSession(false));
    }
}
