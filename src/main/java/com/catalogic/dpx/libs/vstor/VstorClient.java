package com.catalogic.dpx.libs.vstor;

import com.catalogic.dpx.libs.vstor.model.Authenticator;
import com.catalogic.dpx.libs.vstor.model.ErrorResponse;
import com.catalogic.dpx.libs.vstor.model.GfrOptions;
import com.catalogic.dpx.libs.vstor.model.MountedFilesystems;
import com.catalogic.dpx.libs.vstor.model.Share;
import com.catalogic.dpx.libs.vstor.model.Snapshot;
import com.catalogic.dpx.libs.vstor.model.SnapshotsByName;
import com.catalogic.dpx.libs.vstor.model.Volume;
import com.catalogic.dpx.libs.vstor.model.VolumeList;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;
import org.apache.commons.lang3.StringUtils;

public class VstorClient {
  private static final String API_URL_FORMAT = "https://%s:%s/api";
  private static final String SNAPSHOTS_BY_NAME = "/snapshot?name=%s";
  private static final String VOLUME_BY_ID = "/volume?id=%s";
  private static final String VOLUMES = "/volume";
  private static final String VOLUMES_BY_TYPE = "/volume?type=%s";
  private static final String VOLUME_BY_NAME = "/volume?name=%s";
  private static final String SHARE_BY_ID = "/share/%s";
  private static final String MOUNTED_FILESYSTEMS_ENDPOINT = "/volume?type=mountedfilesystem";
  private static final String AUTHENTICATOR_ENDPOINT = "/authenticator";
  private static final String VSTOR_REQUEST_FAIL_MESSAGE = "Failed to send request to vStor";

  private final HttpClient httpClient;
  private final ObjectMapper objectMapper;
  private final VstorConnection vstorConnection;
  private final String apiUrl;

  public VstorClient(
      VstorConnection vstorConnection,
      ObjectMapper objectMapper,
      HttpClient.Builder httpClientBuilder) {
    this.httpClient = httpClientBuilder.sslContext(insecureContext()).build();
    this.objectMapper = objectMapper;
    this.vstorConnection = vstorConnection;
    this.apiUrl = String.format(API_URL_FORMAT, vstorConnection.address(), vstorConnection.port());
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

  public MountedFilesystems getMountedFilesystems() {
    return get(apiUrl + MOUNTED_FILESYSTEMS_ENDPOINT, MountedFilesystems.class);
  }

  public GfrOptions getGfrOptions(int volumeId) {
    return get(apiUrl + getGfrOptionsEndpoint(volumeId), GfrOptions.class);
  }

  public void registerAsAuthenticator() {
    post(apiUrl + AUTHENTICATOR_ENDPOINT, BodyPublishers.noBody(), Authenticator.class);
  }

  public SnapshotsByName getSnapshotsByName(String snapshotName) {
    var requestUrl = apiUrl + SNAPSHOTS_BY_NAME.formatted(snapshotName);
    var snapshotByName = get(requestUrl, SnapshotsByName.class);
    return (snapshotByName == null || snapshotByName.snapshots() == null)
        ? getSingleSnapshotByName(requestUrl)
        : snapshotByName;
  }

  public Volume getVolumeById(int volumeId) {
    return get(apiUrl + VOLUME_BY_ID.formatted(volumeId), Volume.class);
  }

  public VolumeList getVolumes() {
    return get(apiUrl + VOLUMES, VolumeList.class);
  }

  public VolumeList getVolumesByType(String type) {
    return get(apiUrl + VOLUMES_BY_TYPE.formatted(type), VolumeList.class);
  }

  public Volume getVolumeByName(String volumeName) {
    return get(apiUrl + VOLUME_BY_NAME.formatted(volumeName), Volume.class);
  }

  public Share getShareById(int shareId) {
    return get(apiUrl + SHARE_BY_ID.formatted(shareId), Share.class);
  }

  private SnapshotsByName getSingleSnapshotByName(String requestUrl) {
    var singleSnapshot = get(requestUrl, Snapshot.class);
    return singleSnapshot != null
        ? new SnapshotsByName(1, List.of(singleSnapshot))
        : new SnapshotsByName(0, Collections.emptyList());
  }

  private <T> T post(String url, BodyPublisher bodyPublisher, Class<T> responseType) {
    var builder = HttpRequest.newBuilder(URI.create(url)).POST(bodyPublisher);
    return send(builder, responseType);
  }

  private <T> T get(String url, Class<T> responseType) {
    var builder = HttpRequest.newBuilder(URI.create(url)).GET();
    return send(builder, responseType);
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

  private String getGfrOptionsEndpoint(int volumeId) {
    var gfrOptionsEndpointFormat = "/volume/%s/gfr_options";
    return String.format(gfrOptionsEndpointFormat, volumeId);
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
}
