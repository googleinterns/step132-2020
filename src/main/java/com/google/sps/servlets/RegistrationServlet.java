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
import com.google.appengine.api.users.User;
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
    String role = Optional.ofNullable(request.getParameter("role")).orElse(null);
    String firstName = Optional.ofNullable(request.getParameter("first-name"))
            .orElseThrow(() -> new IllegalArgumentException("Must fill out first name"));
    String lastName = Optional.ofNullable(request.getParameter("last-name"))
            .orElseThrow(() -> new IllegalArgumentException("Must fill out last name"));
    String fullName = firstName + " " + lastName;
    
    String email = userService.getCurrentUser().getEmail();
    String userId = userService.getCurrentUser().getUserId();
    
    // Make list of selected topics, remove unchecked topics
    List<Optional<String>> topics = new ArrayList<Optional<String>>();
    topics.add(Optional.ofNullable(request.getParameter("math")));
    topics.add(Optional.ofNullable(request.getParameter("english")));
    topics.add(Optional.ofNullable(request.getParameter("other")));
    List<String> topicsToStr = topics
                                .stream()
                                .filter(t -> t.isPresent())
                                .map(t -> t.get().toLowerCase())
                                .collect(Collectors.toList());

    // Make entity for user with all registration info
    Entity userEntity = new Entity("User");
    createUserEntityAndPutInDatastore(datastore, userEntity, role, userId);

    if(role.toLowerCase().equals("tutor")) {
        Entity tutorEntity = new Entity("Tutor");
        createTutorEntityAndPutInDatastore(datastore, tutorEntity, fullName, email, topicsToStr, userId);
    }

    if(role.toLowerCase().equals("student")) {
        Entity studentEntity = new Entity("Student");
        createStudentEntityAndPutInDatastore(datastore, studentEntity, fullName, email, topicsToStr, userId);
    }

    // TODO: Redirect back to page user was at before registration rather than always redirect to homepage, Issue #41
    response.sendRedirect("/homepage.html");
  }

  /**
  * Creates a student entity and puts it in datastore, used for testing
  */
  public void createStudentEntityAndPutInDatastore(DatastoreService ds, Entity entity, String name, String email, List<String> topics, String userId) {
    entity.setProperty("name", name);
    entity.setProperty("email", email);
    entity.setProperty("learning", topics);
    entity.setProperty("userId", userId);
    ds.put(entity);
  }

  /**
  * Creates a tutor entity and puts it in datastore, used for testing
  */
  public void createTutorEntityAndPutInDatastore(DatastoreService ds, Entity entity, String name, String email, List<String> topics, String userId) {
    entity.setProperty("name", name);
    entity.setProperty("email", email);
    entity.setProperty("topics", topics);
    entity.setProperty("ratingSum", 0);
    entity.setProperty("ratingCount", 0);
    entity.setProperty("userId", userId);
    ds.put(entity);
  }

 /**
  * Creates a user entity and puts it in datastore, used for testing
  */
  public void createUserEntityAndPutInDatastore(DatastoreService ds, Entity entity, String role, String userId) {
    entity.setProperty("role", role);
    entity.setProperty("userId", userId);
    ds.put(entity);
  }
}
