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


import com.abcapps.Entities.SDCard;
import com.abcapps.repo.DirectoryRepository;
import com.abcapps.utils.AuthUtils;
import com.abcapps.utils.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.abcapps.Entities.Role;
import com.abcapps.Entities.User;
import com.abcapps.component.AESMask;
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

    private Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private DirectoryRepository directoryRepository;

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
    Environment env;

    @Override
    public User saveUser(final User user)
            throws EmailIdAlreadyExists, PasswordNotMatchException, MobileNoAlreadyExists {
        log.debug("Registration request for " + user.getEmailId() + "\t" + user.getMobileNo());

        if (!user.getPassword().equals(user.getConfirmPassword())) {
            throw new PasswordNotMatchException();
        }

        User anyUser = userRepo.findByEmailId(user.getEmailId());
        if (anyUser != null) {
            log.warn("User already registered with : " + user.getEmailId());
            throw new EmailIdAlreadyExists();
        }

        anyUser = userRepo.findByMobile(user.getMobile());
        if (anyUser != null) {
            log.warn("User already registered with Mobile no : " + user.getMobileNo());
            throw new MobileNoAlreadyExists();
        }

        user.setEmailIdVerified(false);
        user.setEmailIdUuid(UUID.randomUUID().toString());
        user.setMobileNoVerified(false);
        user.setRegistrationDate(new Date());
        user.setActive(false);
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

                String url = "http://localhost:8080/security/verifyEmail/" + maskedUrl;
                boolean sendMail = mailService.sendMail(user.getEmailId(),
                        StringUtils.getVerifyEmailMsg(url),
                        "Please verify your email id");
                if (sendMail) {
                    log.info("Mail sent to..." + user.getEmailId());
                } else {
                    log.info("Mail not sent to..." + user.getEmailId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        };

        new Thread(emailRunnable).start();

        log.warn("USER REGISTERED :: " + user);
        return userRepo.save(user);
    }

    @Override
    public boolean verifyEmailByEmailUUID(String uuid) {
        try {

            uuid = uuid.replaceAll("\\*", "/");
            User user = userRepo.findByEmailIdUuid(URLDecoder.decode(aesMask.decode(uuid), "UTF-8"));
            if (user != null) {
                user.setEmailIdVerified(true);
                user.setActive(true);
                user = userRepo.save(user);
                log.info(user.getEmailId() + " is verified.");
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
    public boolean saveFileTree(String fileTree) {
        try {
            String emailId = AuthUtils.getLoggedInUser();
            if (emailId == null)
                return false;

            File dir = new File(env.getProperty("user.secure.data"), emailId);
            if (!dir.exists())
                dir.mkdirs();
            else
                FileUtils.cleanDirectory(dir);

            long currentTimeMillis = System.currentTimeMillis();
            File userDataFile = new File(dir, currentTimeMillis + ".txt");


            FileCopyUtils.copy(fileTree.getBytes(StandardCharsets.UTF_8), userDataFile);
            User byEmailId = userRepo.findByEmailId(emailId);
            SDCard SDCard = directoryRepository.findByUser(byEmailId);
            if (SDCard == null) {
                SDCard = new SDCard();
                SDCard.setUser(byEmailId);
                SDCard.setJsonLocation(userDataFile.getCanonicalPath());
            } else {
                SDCard.setJsonLocation(userDataFile.getCanonicalPath());
            }
            directoryRepository.save(SDCard);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String getFileTree() throws IOException {
        String emailId = AuthUtils.getLoggedInUser();
        if (emailId == null)
            return null;
        SDCard byUser = directoryRepository.findByUser(userRepo.findByEmailId(emailId));
        return new String(Files.readAllBytes(Paths.get(byUser.getJsonLocation())));
    }

}
