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

/**
 * Keeps track of a group.
 * Group connects to the overarching database as a list of Groups inside the database. 
 */
public final class Group {
    
    private String name;
    private String topic;
    private String description;
    private String owner;
    private long id;

    /**
     * Creates a new Group. (This constructor is used for testing.)
     *
     * @param name The group's name. Must be non-null.
     * @param topic The group's goal.
     * @param description The group's description.
     * @param owner The group's owner/creator.
     */
    public Group(String name, String topic, String description, String owner) {
        this.name = name;
        this.topic = topic;
        this.description = description;
        this.owner = owner;
    }

    /**
     * Creates a new Group with an id.
     *
     * @param name The group's name. Must be non-null.
     * @param topic The group's goal.
     * @param description The group's description.
     * @param owner The group's owner/creator.
     * @param id A unique id for the group.
     */
    public Group(String name, String topic, String description, String owner, long id) {
        this.name = name;
        this.topic = topic;
        this.description = description;
        this.owner = owner;
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public String getTopic() {
        return this.topic;
    }

    public String getDescription() {
        return this.description;
    }

    public String getOwner() {
        return this.owner;
    }

    public long getId(){
        return this.id;
    }
}
