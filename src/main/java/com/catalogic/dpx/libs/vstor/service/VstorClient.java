package com.catalogic.dpx.libs.vstor.service;

import com.catalogic.dpx.libs.vstor.exception.JsonParsingException;
import com.catalogic.dpx.libs.vstor.exception.VstorConnectionException;
import com.catalogic.dpx.libs.vstor.model.ErrorResponse;
import com.catalogic.dpx.libs.vstor.model.VstorConnection;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpResponse;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;
import org.apache.commons.lang3.StringUtils;

public abstract class VstorClient {
  private static final String VSTOR_REQUEST_FAIL_MESSAGE = "Failed to send request to vStor";
  private static final String API_URL_FORMAT = "https://%s:%s/api";

  private final HttpClient httpClient;
  private final ObjectMapper objectMapper;
  private final VstorConnection vstorConnection;
  private final String apiUrl;

  public VstorClient(VstorConnection vstorConnection) {
    this.httpClient = HttpClient.newBuilder().sslContext(insecureContext()).build();
    this.objectMapper = buildObjectMapper();
    this.vstorConnection = vstorConnection;
    this.apiUrl = String.format(API_URL_FORMAT, vstorConnection.address(), vstorConnection.port());
  }

  public String getApiUrl() {
    return apiUrl;
  }

  protected <T> T post(String url, BodyPublisher bodyPublisher, Class<T> responseType) {
    var builder = HttpRequest.newBuilder(URI.create(url)).POST(bodyPublisher);
    return send(builder, responseType);
  }

  protected <T> T get(String url, Class<T> responseType) {
    var builder = HttpRequest.newBuilder(URI.create(url)).GET();
    return send(builder, responseType);
  }

  private static SSLContext insecureContext() {

    try {
      SSLContext sc = SSLContext.getInstance("TLSv1.2");
      sc.init(null, new TrustManager[] {insecureTrustManager()}, null);
      return sc;
    } catch (KeyManagementException | NoSuchAlgorithmException ex) {
      throw new VstorConnectionException("Cannot create SSL Context");
    }
  }

  private static TrustManager insecureTrustManager() {
    return new X509ExtendedTrustManager() {
      @Override
      public java.security.cert.X509Certificate[] getAcceptedIssuers() {
        return new java.security.cert.X509Certificate[0];
      }

      @Override
      public void checkClientTrusted(X509Certificate[] chain, String authType)
          throws CertificateException {}

      @Override
      public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine)
          throws CertificateException {}

      @Override
      public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket)
          throws CertificateException {}

      @Override
      public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket)
          throws CertificateException {}

      @Override
      public void checkServerTrusted(X509Certificate[] chain, String authType)
          throws CertificateException {}

      @Override
      public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine)
          throws CertificateException {}
    };
  }

  private <T> T send(HttpRequest.Builder requestBuilder, Class<T> responseType) {
    var request =
        requestBuilder.header("Authorization", authorizationHeader(vstorConnection)).build();
    var response = send(request);
    return parseResponse(response, responseType);
  }

  private HttpResponse<String> send(HttpRequest request) {
    try {
      var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      if (isFailureHttpCode(response.statusCode())) {
        throw new VstorConnectionException(getErrorMessage(request, response));
      }
      return response;
    } catch (IOException e) {
      throw new VstorConnectionException(
          VSTOR_REQUEST_FAIL_MESSAGE + ", cause: " + e.getMessage(), e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new VstorConnectionException(
          VSTOR_REQUEST_FAIL_MESSAGE + ", cause: " + e.getMessage(), e);
    }
  }

  private String authorizationHeader(VstorConnection vstorConnection) {
    var encodedPass =
        Base64.getEncoder()
            .encodeToString(
                (vstorConnection.username() + ":" + vstorConnection.password()).getBytes());
    return String.format("Basic %s", encodedPass);
  }

  private <T> T parseResponse(HttpResponse<String> response, Class<T> type) {
    try {
      return objectMapper.readValue(response.body(), type);
    } catch (JsonProcessingException e) {
      throw new VstorConnectionException("Failed to parse response to: " + type.getTypeName(), e);
    }
  }

  private String getErrorMessage(HttpRequest request, HttpResponse<String> response) {
    var builder = new StringBuilder();
    builder.append(String.format("HTTP Request %s %s ", request.method(), request.uri()));
    builder.append(String.format("failed with code %s", response.statusCode()));
    var body = response.body();
    try {
      var errorResponse = objectMapper.readValue(body, ErrorResponse.class);
      builder.append(
          String.format(
              ", error type: %s, error message: '%s'",
              errorResponse.error().type(), errorResponse.error().message()));
    } catch (JsonProcessingException e) {
      throw new JsonParsingException(
          "Failed to parse vStor error message. Raw response: %s"
              .formatted(StringUtils.normalizeSpace(body)));
    }
    return builder.toString();
  }

  private boolean isFailureHttpCode(int statusCode) {
    return statusCode < 200 || statusCode > 299;
  }

  private ObjectMapper buildObjectMapper() {
    return JsonMapper.builder()
        .addModule(new JavaTimeModule())
        .disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .build();
  }
}
