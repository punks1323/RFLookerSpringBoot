package com.abcapps.controllers;


import com.abcapps.service.DownloadManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/download")
public class DownloadController {


    @Autowired
    DownloadManagerService downloadManagerService;

    @GetMapping(value = "/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getFileJson() {
        return new ResponseEntity(downloadManagerService.getActiveFileUpload(), HttpStatus.OK);
    }
}
