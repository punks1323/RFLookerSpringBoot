package com.abcapps.service;

import com.abcapps.entity.DownloadManager;
import com.abcapps.entity.User;

public interface DownloadManagerService {

    DownloadManager getActiveFileUpload();

    void setStatusUploadComplete();

    void saveNewFileUploadRequest(User user, String filePath);

}
