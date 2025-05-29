package com.kasa.adr.service;

import com.kasa.adr.dto.*;
import com.kasa.adr.model.*;
import com.kasa.adr.repo.PasswordResetTokenRepo;
import com.kasa.adr.repo.UserRepository;
import com.kasa.adr.service.external.EmailService;
import com.kasa.adr.util.CommonUtils;
import com.kasa.adr.util.PasswordGenerator;
import com.mongodb.MongoException;
import jakarta.mail.MessagingException;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class UserService {
    Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordResetTokenRepo passwordResetTokenRepo;

    @Autowired
    EmailService emailService;

    @Value("${frontend.url}")
    private String frontendUrl;

    private static ArbitratorProfile getArbitratorProfile(ArbitratorCreateRequest loginRequest, User user) {
        ArbitratorProfile profile = user.getArbitratorProfile();
        profile.setCorrespondenceAddress(loginRequest.getCorrespondenceAddress());
        profile.setDiscloses(loginRequest.getDiscloses());
        profile.setExperience(loginRequest.getExperience());
        profile.setQualification(loginRequest.getQualification());
        profile.setLimitation(loginRequest.getLimitation());
        profile.setSpecialization(loginRequest.getSpecialization());
        profile.setNoOfContestedArbitration(loginRequest.getNoOfContestedArbitration());
        return profile;
    }

    public ResponseEntity<Object> registerArbitratorUser(ArbitratorCreateRequest arbitratorCreateRequest) {
        String password = PasswordGenerator.generatePassword();
        ArbitratorProfile profile = ArbitratorProfile.builder().specialization(arbitratorCreateRequest.getSpecialization()).noOfContestedArbitration(arbitratorCreateRequest.getNoOfContestedArbitration()).correspondenceAddress(arbitratorCreateRequest.getCorrespondenceAddress()).discloses(arbitratorCreateRequest.getDiscloses()).experience(arbitratorCreateRequest.getExperience()).qualification(arbitratorCreateRequest.getQualification()).limitation(arbitratorCreateRequest.getLimitation()).build();
        User user = User.builder()
                .email(arbitratorCreateRequest.getEmail())
                .mobile(arbitratorCreateRequest.getMobile())
                .password(passwordEncoder.encode(password))
                .userType(UserType.ARBITRATOR)
                .name(arbitratorCreateRequest.getName())
                .emailVerified(false)
                .mobileVerified(false)
                .passwordChangeRequired(true)
                .profileImageUrl(arbitratorCreateRequest.getProfileImageUrl())
                .status(true)
                .role(Role.MANAGER)
                .createdAt(Instant.now())
                .arbitratorProfile(profile)
                .build();
        try {

            userRepository.save(user);
            emailService.welcomeEmailArbitrator(user.getName(), user.getEmail(), password);
//            String msg = "Hello " + user.getName() + "\n\t You can login as arbitrator in https://virturesolve360.com with you email and password : " + password;
//            emailService.sendEmail(user.getEmail(),"Welcome to Virturesolve360", msg);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (MongoException e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<Object> newClaimant(ClaimantCreateRequest nbfcProfile) {
        logger.info("Creating new institution");
        String password = PasswordGenerator.generatePassword();
        ClaimantProfile profile = ClaimantProfile.builder().address(nbfcProfile.getAddress()).branch(nbfcProfile.getBranch()).designation(nbfcProfile.getDesignation()).authorizedPersonName(nbfcProfile.getAuthorizedPersonName()).institutionType(nbfcProfile.getInstitutionType()).build();
        User user = User.builder()
                .email(nbfcProfile.getEmail())
                .mobile(nbfcProfile.getMobile())
                .password(passwordEncoder.encode(password))
                .userType(UserType.CLAIMANT)
                .emailVerified(false)
                .mobileVerified(false)
                .name(nbfcProfile.getName())
                .passwordChangeRequired(true)
                .profileImageUrl(nbfcProfile.getProfileImageUrl())
                .status(true)
                .role(Role.ADMIN)
                .createdAt(Instant.now())
                .institutionProfile(profile)
                .build();
        try {
            emailService.welcomeEmailClaimant(user.getName(), user.getEmail(), profile.getAuthorizedPersonName(), password);
            return new ResponseEntity<>(userRepository.save(user), HttpStatus.OK);
        } catch (Exception mongoWriteException) {
            return new ResponseEntity<>(mongoWriteException.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    public ResponseEntity<Object> registerAdmin(AdminCreateRequest request) {
        String password = "12341234";
        User user = User.builder().build();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setMobile(request.getMobile());
        user.setPassword(passwordEncoder.encode(password));
        user.setUserType(UserType.ADMIN);
        user.setEmailVerified(false);
        user.setMobileVerified(false);
        user.setPasswordChangeRequired(true);
        user.setStatus(true);
        user.setRole(Role.ADMIN);
        user.setUserType(UserType.ADMIN);
        user.setCreatedAt(Instant.now());
        try {
            return new ResponseEntity<>(userRepository.save(user), HttpStatus.OK);
        } catch (Exception mongoWriteException) {
            return new ResponseEntity<>(mongoWriteException.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public User updateUser() {
        return User.builder().build();
    }

    public void changePassword(String currentPassword, String newPassword, Principal connectedUser) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        // check if the current password is correct
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalStateException("Wrong password");
        }
        // check if the two new passwords are the same
        // update the password
        user.setPassword(passwordEncoder.encode(newPassword));
        // save the new password
        userRepository.save(user);
    }

    public ResponseEntity logout(String token) {

        return null;
    }

    public ResponseEntity loginOtp(LoginRequest loginRequest) {
        return null;
    }

    public ResponseEntity<?> forgotPassword(String userName) {
        if (userRepository.existsByEmail(userName)) {
            User byUsername = userRepository.findByEmail(userName).get();
            String token = UUID.randomUUID().toString();
            PasswordResetToken passwordResetToken = PasswordResetToken.builder().build();
            passwordResetToken.setUserName(userName);
            passwordResetToken.setToken(token);
            passwordResetToken.setExpiryDate(Instant.now().plus(1, ChronoUnit.HOURS));
            String link = frontendUrl + "/reset-password?token=" + token;
            System.out.println("Token -->" + link);
            passwordResetTokenRepo.save(passwordResetToken);
            try {
                emailService.sendEmail(userName, "virturesolve360: Password Reset Link", "Please click on the below link to reset new password \n" + link);
            } catch (Exception e) {
                logger.info("Exception while sending password reset link");
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> me(String token) {
        String username = null;//jwtUtils.getUserNameFromJwtToken(CommonUtils.parseJwt(token));
        User user = userRepository.findByEmail(username).get();
        // System.out.println("user--->" + user);
        LoginResponse loginResponse = createLoginResponse(user, token);
        return ResponseEntity.ok(loginResponse);
    }

    private LoginResponse createLoginResponse(User user, String token) {
        return LoginResponse.builder().build();
    }

    public ResponseEntity<String> resetPassword(String token, String newPassword) {
        String response = "";
        PasswordValidator passwordValidator = new PasswordValidator(Collections.singletonList(new LengthRule(8, 32)));
        RuleResult result = passwordValidator.validate(new PasswordData(newPassword));
        if (!result.isValid()) {
            response = "Error: Password length must be 8";
        } else {
            List<PasswordResetToken> passTokens = passwordResetTokenRepo.findByToken(token);
            System.out.println("Token-->" + passTokens.toString());

            if (!passTokens.isEmpty()) {
                PasswordResetToken passToken = passTokens.get(0);
                if (!isTokenFound(passToken)) {
                    response = "Error: Invalid Token";
                } else if (!isTokenExpired(passToken)) {
                    response = "Error: Token Expired";
                } else {
                    try {
                        String userName = passToken.getUserName();
                        User user = userRepository.findByEmail(userName).get();
                        user.setPassword(passwordEncoder.encode(newPassword));
                        userRepository.save(user);
                        response = "Password Updated. Please login with the new password";
                        emailService.sendEmail(user.getEmail(), "virturesolve360: Password Updated", "You have updated your virturesolve360 login password!<br> Call you admin if this is not you.");
                    } catch (Exception e) {
                        e.printStackTrace();
                        response = "Error: Some Exception";
                    }
                }
            }
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private boolean isTokenFound(PasswordResetToken passToken) {
        return passToken != null;
    }

    private boolean isTokenExpired(PasswordResetToken passToken) {
        return passToken.getExpiryDate().isAfter(Instant.now());
    }

    public List<User> getAllArbitrator(String status) {
        if (status == null || status.isEmpty())
            return userRepository.findAllByType(UserType.ARBITRATOR.name());
        else
            return userRepository.findAllByTypeAndStatus(UserType.ARBITRATOR.name(), true);
    }

    public Object getArbitratorById(String id) {
        return userRepository.findById(id);
    }

    public Object updateInstitution(String id, ClaimantCreateRequest loginRequest) {
        //User updatedUser = User.builder().build();
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setEmail(loginRequest.getEmail());
            user.setName(loginRequest.getName());
            user.setMobile(loginRequest.getMobile());
            ClaimantProfile profile = user.getInstitutionProfile();
            profile.setAddress(loginRequest.getAddress());
            profile.setBranch(loginRequest.getBranch());
            profile.setDesignation(loginRequest.getDesignation());
            profile.setAuthorizedPersonName(loginRequest.getAuthorizedPersonName());
            user.setProfileImageUrl(loginRequest.getProfileImageUrl());
            user.setInstitutionProfile(profile);
            user.setStatus(loginRequest.isStatus());
            try {
                return new ResponseEntity<>(userRepository.save(user), HttpStatus.OK);
            } catch (Exception mongoWriteException) {
                return new ResponseEntity<>(mongoWriteException.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>("No User Found", HttpStatus.BAD_REQUEST);
    }

    public Object institutionById(String id) {
        return userRepository.findById(id);
    }

    public List<User> allInstitutions() {
        return userRepository.findAllByType(UserType.CLAIMANT.name());
    }

    public Object updateArbitrator(String id, ArbitratorCreateRequest loginRequest) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setEmail(loginRequest.getEmail());
            user.setName(loginRequest.getName());
            user.setMobile(loginRequest.getMobile());
            ArbitratorProfile profile = getArbitratorProfile(loginRequest, user);
            user.setProfileImageUrl(loginRequest.getProfileImageUrl());
            user.setArbitratorProfile(profile);
            user.setStatus(loginRequest.isStatus());
            try {
                return new ResponseEntity<>(userRepository.save(user), HttpStatus.OK);
            } catch (Exception mongoWriteException) {
                return new ResponseEntity<>(mongoWriteException.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>("No User Found", HttpStatus.BAD_REQUEST);
    }

    public List<User> getAllAdmin() {
        return userRepository.findAllByType(UserType.ADMIN.name());
    }

    public ResponseEntity<Object> createClaimantUser(String id, ClaimantCreateRequest claimantCreateRequest) {
        logger.info("Creating new claimant user for admin-->" + id);
        String password = CommonUtils.generatePassword();
        ClaimantProfile profile = ClaimantProfile.builder().address(claimantCreateRequest.getAddress()).branch(claimantCreateRequest.getBranch()).designation(claimantCreateRequest.getDesignation()).authorizedPersonName(claimantCreateRequest.getAuthorizedPersonName()).institutionType(claimantCreateRequest.getInstitutionType()).build();
        User user = User.builder()
                .email(claimantCreateRequest.getEmail())
                .mobile(claimantCreateRequest.getMobile())
                .password(passwordEncoder.encode(password))
                .userType(UserType.CLAIMANT)
                .emailVerified(false)
                .mobileVerified(false)
                .name(claimantCreateRequest.getName())
                .passwordChangeRequired(true)
                .status(true)
                .role(Role.USER)
                .createdAt(Instant.now())
                .institutionProfile(profile)
                .claimantAdminUserId(id)
                .build();
        try {
            return new ResponseEntity<>(userRepository.save(user), HttpStatus.OK);
        } catch (Exception mongoWriteException) {
            return new ResponseEntity<>(mongoWriteException.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public List<User> getClaimantUser(String id) {
        return userRepository.findAllByTypeAndClaimantId(UserType.CLAIMANT.name(), id);
    }

    public List<User> claimantAdmin() {

        return userRepository.findAllClaimantAdmins(UserType.CLAIMANT.name(), Role.ADMIN);
    }

    public Page<User> findAllByPage(String userType, Pageable pageable) {
        return userRepository.findAllByTypeAndPageable(userType, pageable);
    }

    public List<User> getRandomArbitrator() {

        return getRandomItems(getAllArbitrator("active"), 3);
    }

    private <T> List<T> getRandomItems(List<T> list, int count) {
        System.out.println(list);
        if (list == null || list.size() < count) {
            throw new IllegalArgumentException("List size must be greater than or equal to the count.");
        }

        // Shuffle the list
        List<T> shuffledList = new ArrayList<>(list);
        Collections.shuffle(shuffledList);

        // Return the first 'count' elements
        return shuffledList.subList(0, count);
    }

    public User findUserById(String id) {
        return userRepository.findById(id).orElseThrow();
    }

    public ResponseEntity<String> resetPasswordByUserId(String newPassword, String userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        } else {
            return new ResponseEntity<>("Something wrong!", HttpStatus.OK);

        }
        return new ResponseEntity<>("Password reset successfully", HttpStatus.OK);
    }
}