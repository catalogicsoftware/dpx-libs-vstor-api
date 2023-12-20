package com.catalogic.dpx.libs.vstor;

public class VstorConnectionException extends RuntimeException {

  public VstorConnectionException(String message) {
    super(message);
  }

  public VstorConnectionException(String message, Throwable cause) {
    super(message, cause);
  }
}
