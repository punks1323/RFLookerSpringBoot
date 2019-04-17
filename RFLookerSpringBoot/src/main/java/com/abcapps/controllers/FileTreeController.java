package com.abcapps.controllers;


import com.abcapps.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class FileTreeController {
    Logger log = LoggerFactory.getLogger(FileTreeController.class);

    @Autowired
    UserService userService;

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

    @GetMapping(value = "/getAllUsers")
    public ResponseEntity<Object> getAllUsers() {
        try {
            return new ResponseEntity<>(
                    userService.getAllUsers(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
