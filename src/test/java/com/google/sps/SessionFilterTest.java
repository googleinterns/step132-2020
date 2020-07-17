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

import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import javax.servlet.ServletContext;
import javax.servlet.FilterConfig;
import static org.mockito.Mockito.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.*;
import com.google.sps.filters.SessionFilter;

@RunWith(JUnit4.class)
public final class SessionFilterTest { 

    private String sessionId = "123";

    private SessionFilter filter;

    @Before
    public void setUp() {

        filter = new SessionFilter();

    }

    @Test
    public void testUserPermissionsValidSession() throws IOException, ServletException {
        ServletContext context = mock(ServletContext.class);
        FilterConfig filterConfig = mock(FilterConfig.class);
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getSession(false)).thenReturn(session); 
        when(session.getId()).thenReturn(sessionId);      
        when(filterConfig.getServletContext()).thenReturn(context);

        filter.init(filterConfig);

        Assert.assertTrue(filter.userHasPermissions(request, response));
    }

    @Test
    public void testUserPermissionsNoSession() throws IOException, ServletException {
        ServletContext context = mock(ServletContext.class);
        FilterConfig filterConfig = mock(FilterConfig.class);
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getSession(false)).thenReturn(null); 
        when(filterConfig.getServletContext()).thenReturn(context);

        filter.init(filterConfig);

        Assert.assertFalse(filter.userHasPermissions(request, response));
        verify(context, times(1)).log("Invalid session.");
    }

}
