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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that handles user's registration info */
@WebServlet("/registration")
public class RegistrationServlet extends HttpServlet {

  private UserService userService = UserServiceFactory.getUserService();
  private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String role = getParameter(request, "role").orElse(null);
    
    String firstName = getParameter(request, "first-name").orElse(null);
    String lastName = getParameter(request, "last-name").orElse(null);
    String fullName = firstName + " " + lastName;
    
    String email = userService.getCurrentUser().getEmail();
    
    // Make list of selected topics, remove unchecked topics
    List<String> topics = new ArrayList<String>();
    topics.add(getParameter(request, "math").orElse(null));
    topics.add(getParameter(request, "english").orElse(null));
    topics.add(getParameter(request, "other").orElse(null));
    topics = topics
            .stream()
            .filter(t -> t!= null)
            .collect(Collectors.toList());

    // Make entity for user with all registration info
    Entity userEntity = new Entity("User");
    userEntity.setProperty("role", role);
    userEntity.setProperty("name", fullName);
    userEntity.setProperty("email", email);
    userEntity.setProperty("topics", topics);

    datastore.put(userEntity);

    response.sendRedirect("/scheduling.html");
  }

  /**
   * @return the Optional of the request parameter
   */
   private Optional<String> getParameter(HttpServletRequest request, String name) {
       String value = request.getParameter(name);
       //return empty Optional if string is null or empty
       if (value == null || value.isEmpty()) {
           return Optional.empty();
       }
       return Optional.ofNullable(value);
   }
}
