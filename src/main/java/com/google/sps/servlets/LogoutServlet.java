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
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Cookie;
import com.google.appengine.api.utils.SystemProperty;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/** Servlet that invalidates the user's session after they log out. */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        if(request.getSession(false) != null) {
            request.getSession(false).invalidate();
        }

        //log the user out if we're on the development server
        if (SystemProperty.environment.value() != SystemProperty.Environment.Value.Production) {
            UserService userService = UserServiceFactory.getUserService();
            String referrer = request.getHeader("referer");
            response.sendRedirect(userService.createLogoutURL(referrer));

            return;
        }
        
        return;
    }

}
