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
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that gets the login status of the user and displays appropriate form */
@WebServlet("/login-status")
public class LoginStatusServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        UserService userService = UserServiceFactory.getUserService();

        LoginStatus loginStatus;

        //If user not logged in, show login form
        if (!userService.isUserLoggedIn()) {
            loginStatus = new LoginStatus(false, userService.createLoginURL("/registration.html"));
        } else {   //User is logged in, request can proceed
            loginStatus = new LoginStatus(true, userService.createLogoutURL("/registration.html"));
        }

        String json = new Gson().toJson(loginStatus);
        response.getWriter().println(json);
    }
}
