package com.catalogic.dpx.libs.vstor.service;

import com.catalogic.dpx.libs.vstor.model.Authenticator;
import com.catalogic.dpx.libs.vstor.model.VstorConnection;
import java.net.http.HttpRequest.BodyPublishers;

public class AuthenticatorServiceImpl extends VstorClient implements AuthenticatorService {
  private static final String AUTHENTICATOR_ENDPOINT = "/authenticator";

  public AuthenticatorServiceImpl(VstorConnection vstorConnection) {
    super(vstorConnection);
  }

  @Override
  public void registerAsAuthenticator() {
    post(getApiUrl() + AUTHENTICATOR_ENDPOINT, BodyPublishers.noBody(), Authenticator.class);
  }
}
