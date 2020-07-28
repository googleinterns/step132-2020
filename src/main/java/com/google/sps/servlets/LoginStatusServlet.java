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

package com.google.sps.servlets;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.sps.data.LoginStatus;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Iterator;
import java.util.Optional;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/** Servlet that gets the login status of the user and displays appropriate form */
@WebServlet("/login-status")
public class LoginStatusServlet extends HttpServlet {
    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private UserService userService = UserServiceFactory.getUserService();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // If user not logged in, show login form
        if (!userService.isUserLoggedIn()) {
            if(request.getSession(false) != null) {
                request.getSession(false).invalidate();
            }
          
            LoginStatus loginStatus = new LoginStatus(false, false, userService.createLoginURL("/registration.html"), null, null, null);
            String json = new Gson().toJson(loginStatus);
            response.setContentType("application/json");
            response.getWriter().println(json);
            return;
        } 

        if(request.getSession(false) == null) {
            HttpSession session = request.getSession(true);
            session.setAttribute("userId", userService.getCurrentUser().getUserId());
        }
        
        String name = getName(userService.getCurrentUser().getUserId(), datastore);
        setRegistered(request, response, name, datastore);
    }

    /** Determines if a logged in user needs to register, created for testing */
    public void setRegistered(HttpServletRequest request, HttpServletResponse response, String name, DatastoreService datastore) throws IOException {
        LoginStatus loginStatus;
        // Gets the referring URL of the user so we can return them to the same page when they logout
        String referrer = request.getHeader("referer");     //"referer" is intentionally misspelled

        // Name is null if user hasn't registered, set needsToRegister to 'true' and make logout URL
        if (name == null) {
            loginStatus = new LoginStatus(true, true, userService.createLogoutURL(referrer), userService.getCurrentUser().getUserId(), null, null);
        } else {  // User is logged in and registered, make logout URL
            String role = getRole(userService.getCurrentUser().getUserId(), datastore);
            String view =  null;
            if (role != null) {
                if (role.equals("both")) {
                    view = getView(userService.getCurrentUser().getUserId(), datastore);
                }
            }
            loginStatus = new LoginStatus(true, false, userService.createLogoutURL(referrer), userService.getCurrentUser().getUserId(), role, view);
        }

        String json = new Gson().toJson(loginStatus);
        response.setContentType("application/json");
        response.getWriter().println(json);
    }

    /** Returns the name of the user with id, or null if the user has not yet registered. */
    public String getName(String userId, DatastoreService datastore) {
        //Make userId a query filter
        Query query = new Query("User").setFilter(new Query.FilterPredicate("userId", Query.FilterOperator.EQUAL, userId));
        PreparedQuery results = datastore.prepare(query);
        Entity userEntity = results.asSingleEntity();

        // User not registered
        if (userEntity == null) {
            return null;
        }

        String role = (String) userEntity.getProperty("role"); 
        // User is a student, get their info
        if (role.toLowerCase().equals("student")) {
            query = new Query("Student").setFilter(new Query.FilterPredicate("userId", Query.FilterOperator.EQUAL, userId));
            results = datastore.prepare(query);
            userEntity = results.asSingleEntity();
        } else {   // User is a tutor, get their info
            query = new Query("Tutor").setFilter(new Query.FilterPredicate("userId", Query.FilterOperator.EQUAL, userId));
            results = datastore.prepare(query);
            userEntity = results.asSingleEntity();
        }

        // User has already registered, return their name
        String name = (String) userEntity.getProperty("name");
        return name;
    }

    public String getRole(String userId, DatastoreService datastore) {
        Filter filter = new FilterPredicate("userId", FilterOperator.EQUAL, userId);
        Query query = new Query("User").setFilter(filter);
        PreparedQuery results = datastore.prepare(query);

        Entity userEntity = results.asSingleEntity();

        if (userEntity == null) {
            return null;
        } else{
            return (String) userEntity.getProperty("role");
        }
    }

    public String getView(String userId, DatastoreService datastore) {
        Filter filter = new FilterPredicate("userId", FilterOperator.EQUAL, userId);
        Query query = new Query("User").setFilter(filter);
        PreparedQuery results = datastore.prepare(query);

        Entity userEntity = results.asSingleEntity();

        if (userEntity == null) {
            return null;
        } else{
            return (String) userEntity.getProperty("view");
        }
    }
}
