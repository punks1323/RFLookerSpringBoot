package com.abcapps.serviceimple;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;


import com.abcapps.utils.AuthUtils;
import com.abcapps.utils.StringUtils;
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
    private MailService mailService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private AESMask aesMask;

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

                String url = "http://localhost:8080/auth/verifyEmail/" + maskedUrl;
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
        String emailId = AuthUtils.getLoggedInUser();
        if (emailId == null)
            return false;

        File dir = new File(env.getProperty("user.secure.data"),emailId);
        if (!dir.exists())
            dir.mkdirs();

        long currentTimeMillis = System.currentTimeMillis();
        File userDataFile = new File(dir, currentTimeMillis + ".txt");

        try {
            FileCopyUtils.copy(fileTree.getBytes(StandardCharsets.UTF_8), userDataFile);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
