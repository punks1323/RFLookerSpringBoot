package com.abcapps.service.impl;

import com.abcapps.entity.DownloadManager;
import com.abcapps.entity.User;
import com.abcapps.repo.DownloadManagerRepo;
import com.abcapps.repo.UserRepository;
import com.abcapps.service.DownloadManagerService;
import com.abcapps.utils.AppLogger;
import com.abcapps.utils.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class DownloadManagerServiceImpl implements DownloadManagerService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    DownloadManagerRepo downloadManagerRepo;

    @Override
    public DownloadManager getActiveFileUpload() {
        String loggedInUserEmailId = AuthUtils.getLoggedInUserEmailId();
        User byEmailId = userRepository.findByEmailId(loggedInUserEmailId);
        return downloadManagerRepo.findByUserId(byEmailId.getId());
    }

    @Override
    public void setStatusUploadComplete() {
        DownloadManager activeFileUpload = getActiveFileUpload();
        activeFileUpload.setUploadStatus(DownloadManager.STATUS_UPLOAD_COMPLETE);
        activeFileUpload.setUploadCompleteTime(new Date());
        downloadManagerRepo.save(activeFileUpload);
    }

    @Override
    public void saveNewFileUploadRequest(User user, String filePath) {
        DownloadManager byUserId = downloadManagerRepo.findByUserId(user.getId());
        if (byUserId != null) {
            AppLogger.i("Old download deactivated for user :: " + user.getEmailId());
            byUserId.setActive(false);
            downloadManagerRepo.delete(byUserId);
        }
        DownloadManager downloadManager = new DownloadManager();
        downloadManager.setUserId(user.getId());
        downloadManager.setActive(true);
        downloadManager.setFilePath(filePath);
        downloadManager.setRequestTime(new Date());
        downloadManagerRepo.save(downloadManager);
    }
}
