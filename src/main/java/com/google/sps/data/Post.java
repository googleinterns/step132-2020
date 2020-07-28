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

package com.google.sps.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;

/**
 * Keeps track of a group post.
 * Post connects to the overarching database as a list of Posts inside the database. 
 */
public final class Post {
    
    private String userID;
    private String groupID;
    private String content;
    private long id;

    /**
     * Creates a new Post. (This constructor is used for testing.)
     *
     * @param userID The id of the user who made the post.
     * @param groupID The id of the group to where the post was added.
     * @param content The post's content.
     */
    public Post(String userID, String groupID, String content) {
        this.userID = userID;
        this.groupID = groupID;
        this.content = content;
    }

    /**
     * Creates a new Post with an id.
     *
     * @param userID The id of the user who made the post.
     * @param groupID The id of the group to where the post was added.
     * @param content The post's content.
     * @param id A unique id for the post.
     */
    public Post(String userID, String groupID, String content, long id) {
        this.userID = userID;
        this.groupID = groupID;
        this.content = content;
        this.id = id;
    }

    public String getUserID() {
        return this.userID;
    }

    public String getGroupID() {
        return this.groupID;
    }

    public String getContent() {
        return this.content;
    }

    public long getId(){
        return this.id;
    }
}
