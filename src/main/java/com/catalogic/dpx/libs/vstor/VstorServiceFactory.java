package com.catalogic.dpx.libs.vstor;

import com.catalogic.dpx.libs.vstor.model.VstorConnection;
import com.catalogic.dpx.libs.vstor.service.AuthenticatorService;
import com.catalogic.dpx.libs.vstor.service.AuthenticatorServiceImpl;
import com.catalogic.dpx.libs.vstor.service.FilesystemService;
import com.catalogic.dpx.libs.vstor.service.FilesystemServiceImpl;
import com.catalogic.dpx.libs.vstor.service.GfrService;
import com.catalogic.dpx.libs.vstor.service.GfrServiceImpl;
import com.catalogic.dpx.libs.vstor.service.ShareService;
import com.catalogic.dpx.libs.vstor.service.ShareServiceImpl;
import com.catalogic.dpx.libs.vstor.service.SnapshotService;
import com.catalogic.dpx.libs.vstor.service.SnapshotServiceImpl;
import com.catalogic.dpx.libs.vstor.service.VolumeService;
import com.catalogic.dpx.libs.vstor.service.VolumeServiceImpl;

public class VstorServiceFactory {
  public static AuthenticatorService authenticatorService(VstorConnection vstorConnection) {
    return new AuthenticatorServiceImpl(vstorConnection);
  }

  public static FilesystemService filesystemService(VstorConnection vstorConnection) {
    return new FilesystemServiceImpl(vstorConnection);
  }

  public static GfrService gfrService(VstorConnection vstorConnection) {
    return new GfrServiceImpl(vstorConnection);
  }

  public static ShareService shareService(VstorConnection vstorConnection) {
    return new ShareServiceImpl(vstorConnection);
  }

  public static SnapshotService snapshotService(VstorConnection vstorConnection) {
    return new SnapshotServiceImpl(vstorConnection);
  }

  public static VolumeService volumeService(VstorConnection vstorConnection) {
    return new VolumeServiceImpl(vstorConnection);
  }
}
