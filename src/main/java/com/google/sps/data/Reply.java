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
 * Keeps track of a post reply.
 * Reply connects to the overarching database as a list of Replies inside the database. 
 */
public final class Reply {
    
    private String userID;
    private String postId;
    private String content;
    private long id;

    /**
     * Creates a new Reply. (This constructor is used for testing.)
     *
     * @param userID The id of the user who made the post.
     * @param postId The id of the post to which the reply is connected.
     * @param content The reply's content.
     */
    public Reply(String userID, String postId, String content) {
        this.userID = userID;
        this.postId = postId;
        this.content = content;
    }

    /**
     * Creates a new Reply. (This constructor is used for testing.)
     *
     * @param userID The id of the user who made the post.
     * @param postId The id of the post to which the reply is connected.
     * @param content The reply's content.
     * @param id A unique id for the reply.
     */
    public Reply(String userID, String postId, String content, long id) {
        this.userID = userID;
        this.postId = postId;
        this.content = content;
        this.id = id;
    }

    public String getUserID() {
        return this.userID;
    }

    public String getPostId() {
        return this.postId;
    }

    public String getContent() {
        return this.content;
    }

    public long getId(){
        return this.id;
    }
}
