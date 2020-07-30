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

package com.google.sps;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.sps.servlets.BlobUploadUrlServlet;
import java.io.*;
import javax.servlet.http.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.mockito.Mockito.*;

@RunWith(JUnit4.class)
public final class BlobUploadUrlTest {
   
    private HttpServletRequest request;
    private HttpServletResponse response;
    private BlobstoreService blobstore;
    private BlobUploadUrlServlet servlet;

    @Before 
    public void setUp() {
        request = mock(HttpServletRequest.class);       
        response = mock(HttpServletResponse.class);
        blobstore = mock(BlobstoreService.class);
        servlet = new BlobUploadUrlServlet();
    }

    @Test
    public void doGetRespondsWithCorrectUrl() throws IOException {
        when(blobstore.createUploadUrl("/registration")).thenReturn("/registration");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        servlet.setResponse(request, response, blobstore);

        String expected = "/registration";
        Assert.assertTrue(stringWriter.toString().contains(expected));
    }

}
