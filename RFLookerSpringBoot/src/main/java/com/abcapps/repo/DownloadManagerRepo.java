package com.abcapps.repo;

import com.abcapps.entity.DownloadManager;
import com.abcapps.entity.Mobile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DownloadManagerRepo extends JpaRepository<DownloadManager, String> {

    DownloadManager findByUserId(Long userId);

    DownloadManager findByUserIdAndFilePath(Long userId, String filePath);
}
