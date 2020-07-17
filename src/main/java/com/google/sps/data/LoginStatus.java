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

/* LoginStatus class for Sullivan users */
public class LoginStatus {
    
    private boolean isLoggedIn;
    private boolean needsToRegister;
    private String url;
    private String userId;
    private String role;

    public LoginStatus(boolean isLoggedIn, boolean needsToRegister, String url, String userId, String role) {
        this.isLoggedIn = isLoggedIn;
        this.needsToRegister = needsToRegister;
        this.url = url;
        this.userId = userId;
        this.role = role;
    }
}
