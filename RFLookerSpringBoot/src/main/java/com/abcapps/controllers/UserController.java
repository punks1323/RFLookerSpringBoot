package com.abcapps.controllers;

import javax.validation.Valid;

import com.abcapps.utils.AppLogger;
import com.amazonaws.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.abcapps.entity.User;
import com.abcapps.exception.EmailIdAlreadyExists;
import com.abcapps.exception.MobileNoAlreadyExists;
import com.abcapps.exception.PasswordNotMatchException;
import com.abcapps.service.UserService;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping(value = "/register")
    public ResponseEntity<Object> register(@Valid @RequestBody User user) {
        try {
            return new ResponseEntity<>(userService.saveUser(user), HttpStatus.CREATED);
        } catch (EmailIdAlreadyExists | MobileNoAlreadyExists | PasswordNotMatchException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/verifyEmail/{uuid}")
    public ResponseEntity<Object> verifyEmail(@PathVariable String uuid) {
        AppLogger.i("Email verification :: " + uuid);
        try {
            return new ResponseEntity<>(
                    userService.verifyEmailByEmailUUID(uuid) ? HttpStatus.OK : HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(
            value = "/get-file",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public @ResponseBody
    byte[] getFile() throws IOException {

        InputStream in = new ByteArrayInputStream("this is line 1....".getBytes());
        return IOUtils.toByteArray(in);
    }

    @GetMapping(
            value = "/get-file2",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public @ResponseBody
    ResponseEntity<Resource> getFile2() throws IOException {


        ServerSocket sockServer = new ServerSocket(8778);
        AppLogger.i("Waiting...");
        Socket sock = sockServer.accept();
        AppLogger.i("Request came");
        byte[] mybytearray = new byte[1024];
        InputStream is = sock.getInputStream();
        FileOutputStream fos = new FileOutputStream("aone.txt");
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        int bytesRead = is.read(mybytearray, 0, mybytearray.length);
        bos.write(mybytearray, 0, bytesRead);
        bos.close();
        sock.close();

        AppLogger.i("Received....bytes " + bytesRead);
        ByteArrayResource byteArrayResource = new ByteArrayResource(mybytearray);

        return ResponseEntity.ok()
                .contentLength(bytesRead)
                .body(byteArrayResource);

    }
}
