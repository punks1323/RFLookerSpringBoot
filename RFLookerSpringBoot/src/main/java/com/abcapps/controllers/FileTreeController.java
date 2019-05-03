package com.abcapps.controllers;


import com.abcapps.service.FileStorageService;
import com.abcapps.service.UserService;
import com.abcapps.utils.AppLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/user")
public class FileTreeController {

    @Autowired
    UserService userService;

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping(value = "/saveRFLFileTree")
    public ResponseEntity<Object> saveRFLFileTree(@RequestParam("fileTree") String fileTree, @RequestParam("deviceDetails") String deviceDetails) {
        try {
            if (userService.saveFileTree(fileTree, deviceDetails)) {
                return new ResponseEntity<>("OK"
                        , HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value = "/getRFLFileTree", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getFileJson() {
        try {
            return new ResponseEntity<>(userService.getFileTree()
                    , HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("ERROR", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/uploadFile")
    public ResponseEntity<Object> uploadFile(@RequestParam("file") MultipartFile file) {
        String fileName = fileStorageService.storeFile(file);
        AppLogger.i("File saved :: " + fileName);
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/downloadFile")
    public ResponseEntity<Resource> downloadFile(HttpServletRequest request) {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource();

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            AppLogger.i("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @PostMapping
    public ResponseEntity<Object> downloadFromTree(String node) {

        return ResponseEntity.ok("OK");
    }
}
