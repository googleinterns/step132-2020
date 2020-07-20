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

package com.google.utilities;

import org.junit.runners.JUnit4;
import static org.mockito.Mockito.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/** Common methods used to set up tests. */
public final class TestUtilities {

    /**
    * Sets the mock request's session id.
    */
    public static void setSessionId(HttpServletRequest request, String id) {
        HttpSession session = mock(HttpSession.class);

        when(request.getSession(false)).thenReturn(session); 
        when(session.getAttribute("userId")).thenReturn(id);  
    }

}
