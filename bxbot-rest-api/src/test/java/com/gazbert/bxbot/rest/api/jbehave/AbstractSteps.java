/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 gazbert
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.gazbert.bxbot.rest.api.jbehave;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gazbert.bxbot.rest.api.security.authentication.JwtAuthenticationRequest;
import com.gazbert.bxbot.rest.api.security.authentication.JwtAuthenticationResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.apache.commons.codec.Charsets;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * Base class for all JBehave test steps.
 *
 * @author gazbert
 */
public abstract class AbstractSteps {

  // Creds must match real user entries in the ./java/resources/import.sql file
  private static final String AUTH_URI = "http://localhost:8080/api/token";
  private static final String USER_USERNAME = "user";
  private static final String USER_PASSWORD = "user";
  private static final String ADMIN_USERNAME = "admin";
  private static final String ADMIN_PASSWORD = "admin";

  private enum UserType {
    USER,
    ADMIN
  }

  protected String getUserToken() throws IOException {
    return getToken(UserType.USER);
  }

  protected String getAdminToken() throws IOException {
    return getToken(UserType.ADMIN);
  }

  protected HttpResponse makeApiCallWithoutToken(String api) throws IOException {
    return makeApiCall(api, null);
  }

  protected HttpResponse makeApiCallWithToken(String api, String token) throws IOException {
    return makeApiCall(api, token);
  }

  // --------------------------------------------------------------------------
  // Private utils
  // --------------------------------------------------------------------------

  private String getToken(UserType userType) throws IOException {

    final JwtAuthenticationRequest jwtAuthenticationRequest = new JwtAuthenticationRequest();
    if (userType == UserType.USER) {
      jwtAuthenticationRequest.setUsername(USER_USERNAME);
      jwtAuthenticationRequest.setPassword(USER_PASSWORD);
    } else if (userType == UserType.ADMIN) {
      jwtAuthenticationRequest.setUsername(ADMIN_USERNAME);
      jwtAuthenticationRequest.setPassword(ADMIN_PASSWORD);
    }

    final ObjectMapper mapper = new ObjectMapper();
    final String credentialsJson = mapper.writeValueAsString(jwtAuthenticationRequest);
    final StringEntity requestEntity =
        new StringEntity(credentialsJson, ContentType.APPLICATION_JSON);
    final HttpPost postMethod = new HttpPost(AUTH_URI);
    postMethod.setEntity(requestEntity);

    final CloseableHttpClient httpclient = HttpClients.createDefault();
    final HttpResponse response = httpclient.execute(postMethod);

    final HttpEntity responseEntity = response.getEntity();
    final Header encodingHeader = responseEntity.getContentEncoding();

    final Charset encoding =
        encodingHeader == null
            ? StandardCharsets.UTF_8
            : Charsets.toCharset(encodingHeader.getValue());

    final String responseJson = EntityUtils.toString(responseEntity, encoding);
    final JwtAuthenticationResponse authenticationResponse =
        mapper.readValue(responseJson, JwtAuthenticationResponse.class);

    httpclient.close();

    return authenticationResponse.getToken();
  }

  private HttpResponse makeApiCall(String api, String token) throws IOException {
    final HttpUriRequest request = new HttpGet(api);
    if (token != null) {
      request.setHeader("Authorization", "Bearer " + token);
    }
    return HttpClientBuilder.create().build().execute(request);
  }
}
