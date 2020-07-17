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

package com.google.sps.filters;
 
import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Cookie;
import javax.servlet.ServletContext;
import java.util.stream.*;
import java.util.Optional;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/** Servlet filter that checks if the user has valid session to make a request.*/
public class SessionFilter implements Filter {

    private ServletContext context;
    private FilterConfig filterConfig;

    /**
    * This method is called automatically before the request is sent to the servlet (specified in the web.xml file) to make sure
    * the session is valid.
    */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;

        if(userHasPermissions(req, resp)) {
            filterChain.doFilter(request, response);
        } else {
            resp.sendRedirect("/homepage.html");
            return;
        }

    }

    /**
    * Determines if the user has the right permissions to make the request based on their session id.
    * @return boolean
    */
    public boolean userHasPermissions(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        Cookie[] cookies = request.getCookies();

        //user has not logged in, no cookies have been created
        if(cookies == null) {
            this.context.log("Unauthorized access request");
            return false;
        }

        String sessionId = "";
        
        for(Cookie cookie : cookies) {
            if(cookie.getName().equals("JSESSIONID")) {
                sessionId = cookie.getValue();
                break;
            }
        }
    
        HttpSession session = request.getSession(false);

        //The sessionId from the cookie has ".node0" as a suffix
        if(session == null || !(session.getId()+".node0").equals(sessionId)) {
            this.context.log("Unauthorized access request");
			
            return false;
        }

        return true;
    }

    public FilterConfig getFilterConfig() {
        return filterConfig;
    }

    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
        this.context = filterConfig.getServletContext();
		this.context.log("SessionFilter initialized");
    }

    public void destroy() {}
}
