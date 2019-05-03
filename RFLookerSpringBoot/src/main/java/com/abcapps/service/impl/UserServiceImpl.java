package com.abcapps.service.impl;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;


import com.abcapps.entity.SDCard;
import com.abcapps.properties.UserProperties;
import com.abcapps.repo.SDCardRepository;
import com.abcapps.utils.AppLogger;
import com.abcapps.utils.AuthUtils;
import com.abcapps.utils.StringUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.abcapps.entity.Role;
import com.abcapps.entity.User;
import com.abcapps.service.AESMask;
import com.abcapps.exception.EmailIdAlreadyExists;
import com.abcapps.exception.MobileNoAlreadyExists;
import com.abcapps.exception.PasswordNotMatchException;
import com.abcapps.repo.RoleRepository;
import com.abcapps.repo.UserRepository;
import com.abcapps.service.MailService;
import com.abcapps.service.UserService;
import org.springframework.util.FileCopyUtils;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private SDCardRepository SDCardRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private AESMask aesMask;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    UserProperties userProperties;

    @Override
    public User saveUser(final User user)
            throws EmailIdAlreadyExists, PasswordNotMatchException, MobileNoAlreadyExists {
        AppLogger.d("Registration request for " + user.getEmailId() + "\t" + user.getMobileNo());

        if (!user.getPassword().equals(user.getConfirmPassword())) {
            throw new PasswordNotMatchException();
        }

        User anyUser = userRepo.findByEmailId(user.getEmailId());
        if (anyUser != null) {
            AppLogger.w("User already registered with : " + user.getEmailId());
            throw new EmailIdAlreadyExists();
        }

        anyUser = userRepo.findByMobile(user.getMobile());
        if (anyUser != null) {
            AppLogger.w("User already registered with Mobile no : " + user.getMobileNo());
            throw new MobileNoAlreadyExists();
        }

        user.setEmailIdVerified(false);
        user.setEmailIdUuid(UUID.randomUUID().toString());
        user.setMobileNoVerified(false);
        user.setRegistrationDate(new Date());
        user.setIsActive(Boolean.FALSE);
        user.getMobile().setLastAccess(new Date());

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        HashSet<Role> roles = new HashSet<Role>();
        roles.add(roleRepository.findByName("ROLE_USER"));
        user.setRoles(roles);

        Runnable emailRunnable = () -> {
            try {

                String encode = aesMask.encode(user.getEmailIdUuid());
                encode = encode.replaceAll("/", "*");
                String maskedUrl = URLEncoder.encode(encode, "UTF-8");

                String url = "http://localhost:8080/auth/verifyEmail/" + maskedUrl;
                boolean sendMail = mailService.sendMail(user.getEmailId(),
                        StringUtils.getVerifyEmailMsg(url),
                        "Please verify your email id");
                if (sendMail) {
                    AppLogger.i("Mail sent to..." + user.getEmailId());
                } else {
                    AppLogger.i("Mail not sent to..." + user.getEmailId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        };

        new Thread(emailRunnable).start();

        AppLogger.w("USER REGISTERED :: " + user);
        return userRepo.save(user);
    }

    @Override
    public boolean verifyEmailByEmailUUID(String uuid) {
        try {

            uuid = uuid.replaceAll("\\*", "/");
            User user = userRepo.findByEmailIdUuid(URLDecoder.decode(aesMask.decode(uuid), "UTF-8"));
            if (user != null) {
                user.setEmailIdVerified(true);
                user.setIsActive(Boolean.TRUE);
                user = userRepo.save(user);
                AppLogger.i(user.getEmailId() + " is verified.");
                return true;
            } else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public boolean saveFileTree(String fileTree, String deviceDetails) {
        try {
            String emailId = AuthUtils.getLoggedInUserEmailId();
            if (emailId == null)
                return false;

            //File dir = new File(env.getProperty("user.secure.data"), emailId);
            File dir = new File(userProperties.getSecureDataDir(), emailId);
            if (!dir.exists())
                dir.mkdirs();

            File fileTreeFile = new File(dir, userProperties.getSecureDataDirFiles().getFileTree());
            FileCopyUtils.copy(fileTree.getBytes(StandardCharsets.UTF_8), fileTreeFile);


            File deviceDetailsFile = new File(dir, userProperties.getSecureDataDirFiles().getFileDeviceDetails());
            if (!deviceDetailsFile.exists()) {
                FileCopyUtils.copy(deviceDetails.getBytes(StandardCharsets.UTF_8), deviceDetailsFile);
                AppLogger.i("New device details received for user :: " + emailId);
            } else {
                JsonNode fileJsonNode = mapper.readValue(deviceDetailsFile, JsonNode.class);
                JsonNode receivedJsonNode = mapper.readValue(deviceDetails, JsonNode.class);

                if (fileJsonNode != null && receivedJsonNode != null && !fileJsonNode.get("androidSecureId").isNull() && !fileJsonNode.get("androidSecureId").asText().equals(receivedJsonNode.get("androidSecureId").asText())) {
                    AppLogger.i("New device details updated for user :: " + emailId);
                    FileCopyUtils.copy(deviceDetailsFile, new File(dir, "deviceDetails_" + System.currentTimeMillis() + ".txt"));
                    FileCopyUtils.copy(deviceDetails.getBytes(StandardCharsets.UTF_8), deviceDetailsFile);
                } else {
                    AppLogger   .i("This device id is already saved.");
                }
            }

            User byEmailId = userRepo.findByEmailId(emailId);
            SDCard sdCard = SDCardRepository.findByUser(byEmailId);
            if (sdCard == null) {
                sdCard = new SDCard();
                sdCard.setUser(byEmailId);
                sdCard.setFileTreeLocation(fileTreeFile.getCanonicalPath());
                sdCard.setDeviceDetailsLocation(deviceDetailsFile.getCanonicalPath());
            } else {
                sdCard.setFileTreeLocation(fileTreeFile.getCanonicalPath());
                sdCard.setDeviceDetailsLocation(deviceDetailsFile.getCanonicalPath());
            }
            SDCard sdCard1 = SDCardRepository.save(sdCard);
            return sdCard1 != null;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String getFileTree() throws IOException {
        SDCard byUser = SDCardRepository.findByUser(userRepo.findByEmailId(AuthUtils.getLoggedInUserEmailId()));

        if (byUser == null || byUser.getFileTreeLocation() == null) {
            return "no record found 1";
        } else if (!new File(byUser.getFileTreeLocation()).exists() || !new File(byUser.getFileTreeLocation()).isFile()) {
            return "no record found 2";
        } else
            return new String(Files.readAllBytes(Paths.get(byUser.getFileTreeLocation())));
    }
}
