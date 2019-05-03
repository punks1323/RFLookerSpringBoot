package com.abcapps.service.impl;

import com.abcapps.exception.FileStorageException;
import com.abcapps.exception.MyFileNotFoundException;
import com.abcapps.properties.UserProperties;
import com.abcapps.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FileStorageServiceImpl implements FileStorageService {
    private final Path fileStorageLocation;

    @Autowired
    public FileStorageServiceImpl(UserProperties userProperties) {
        this.fileStorageLocation = Paths.get(userProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    @Override
    public String storeFile(MultipartFile file) {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    @Override
    public Resource loadFileAsResource() {
        try {
            List<Path> sortedFileList = Files.list(fileStorageLocation).sorted((p1, p2) -> {
                try {
                    return Long.compare(Files.getLastModifiedTime(p1).toMillis(), Files.getLastModifiedTime(p2).toMillis());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return 0;

            }).collect(Collectors.toList());

            if (sortedFileList.size() == 0)
                return null;
            if (sortedFileList.size() > 1)
                sortedFileList.forEach(path -> {
                    if (sortedFileList.indexOf(path) != sortedFileList.size() - 1) {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

            else {
                Path fileName = sortedFileList.get(0);
                Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
                Resource resource = new UrlResource(filePath.toUri());
                if (resource.exists()) {
                    return resource;
                } else {
                    throw new MyFileNotFoundException("File not found " + fileName);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
