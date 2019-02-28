package com.abcapps.controllers;


import com.abcapps.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class RFLController {
    Logger log = LoggerFactory.getLogger(RFLController.class);

    @Autowired
    UserService userService;

    @PostMapping(value = "/saveRFLFileTree")
    public ResponseEntity<Object> saveRFLFileTree(@RequestParam("fileTree") String fileTree) {
        try {
            if (userService.saveFileTree(fileTree)) {
                return new ResponseEntity<>("OK"
                        , HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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
