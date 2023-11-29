package com.example.userservicenovttseve.services;

import com.example.userservicenovttseve.dtos.UserDto;
import com.example.userservicenovttseve.exceptions.UserAlreadyExistsException;
import com.example.userservicenovttseve.exceptions.UserDoesNotExistException;
import com.example.userservicenovttseve.models.Session;
import com.example.userservicenovttseve.models.SessionStatus;
import com.example.userservicenovttseve.models.User;
import com.example.userservicenovttseve.repositories.SessionRepository;
import com.example.userservicenovttseve.repositories.UserRepository;
import io.jsonwebtoken.Jwts;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMapAdapter;

import javax.crypto.SecretKey;
import java.time.LocalDate;
import java.util.*;

@Service
public class AuthService {
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserRepository userRepository;
    private SessionRepository sessionRepository;
//    private BCryptPasswordEncoder bCryptPasswordEncodersswordEncoder;

    public AuthService(UserRepository userRepository, SessionRepository sessionRepository) {//} ,BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
    }

    public ResponseEntity<UserDto> login(String email, String password) throws UserDoesNotExistException {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isEmpty()){
            throw new UserDoesNotExistException("User with email "+ email+ " doesn't exists");
        }
        User user = userOptional.get();
        if(!bCryptPasswordEncoder.matches(password,user.getPassword())){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
//        String token = RandomStringUtils.randomAscii(20);
        // TODO: update here to use jwt
        // playload should have
        //{ userID:, email:, roles:[]}
        // Map<String, Object> claimsMap
        // claimsMap.add(userID)
        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put("userID", user.getId());
        claimsMap.put("email", user.getEmail());
        claimsMap.put("roles", user.getRoles());
        SecretKey testKey = Jwts.SIG.HS512.key().build();
        String token = Jwts.builder()
                .claims(claimsMap)
                .signWith(testKey)
                .compact();

        MultiValueMapAdapter<String, String> headers = new MultiValueMapAdapter<>(new HashMap<>());
        headers.add("AUTO_TOKEN", token);

        // Store token in session
        Session session = new Session();
        session.setSessionStatus(SessionStatus.ACTIVE);
        session.setToken(token);
        session.setUser(user);
        sessionRepository.save(session);

        UserDto userDto = UserDto.from(user);
        ResponseEntity<UserDto> response = new ResponseEntity<>(
                userDto,
                headers,
                HttpStatus.OK
        );
        return response;
    }

    public ResponseEntity<Void> logout(String token, Long userId) {
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_Id(token, userId);

        if (sessionOptional.isEmpty()) {
            return null;
        }

        Session session = sessionOptional.get();

        session.setSessionStatus(SessionStatus.LOGGED_OUT);

        sessionRepository.save(session);

        return ResponseEntity.ok().build();
    }

    public UserDto signUp(String email, String password) throws UserAlreadyExistsException {

        Optional<User> userOptional = userRepository.findByEmail(email);

        if (!userOptional.isEmpty()) {
            throw new UserAlreadyExistsException("User with " + email + " already exists.");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));

        User savedUser = userRepository.save(user);

        return UserDto.from(savedUser);
    }

    public Optional<UserDto> validate(String token, Long userId) {
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_Id(token, userId);

        if (sessionOptional.isEmpty()) {
            return Optional.empty();
        }

        Session session = sessionOptional.get();

        if (!session.getSessionStatus().equals(SessionStatus.ACTIVE)) {
            return Optional.empty();
        }
        User user = userRepository.findById(userId).get();

//        if (!session.getExpiringAt() > new Date()) {
//            return SessionStatus.EXPIRED;
//        }

//        return SessionStatus.ACTIVE;
        return Optional.of(UserDto.from(user));
    }

}

/// GET /products/1 -> abcd1234abcd
//        /authentication/validate/abcd1234abcd>u_id=12 -> true
// if the token is any random strong, we will first need to make
// a db call to validate the token
// and then another call to get the details of the user
// auth-token%3AeyJjdHkiOiJ0ZXh0L3BsYWluIiwiYWxnIjoiSFMyNTYifQ.ewogICAiZW1haWwiOiAibmFtYW5Ac2NhbGVyLmNvbSIsCiAgICJyb2xlcyI6IFsKICAgICAgIm1lbnRvciIsCiAgICAgICJ0YSIKICAgXSwKICAgImV4cGlyYXRpb25EYXRlIjogIjIzcmRPY3RvYmVyMjAyMyIKfQ.r2FVQUCn6DNHir5AlEBT2XQMgO7aN4m3xg9zcuB-zxQ
// auth-token%3AeyJhbGciOiJIUzI1NiJ9.eyJjcmVhdGVkQXQiOjE2OTgwNzgzNDg0NTQsInJvbGVzIjpbXSwiZXhwaXJ5QXQiOjE5NjU2LCJlbWFpbCI6Im5hbWFuQHNjYWxlci5jb20ifQ._v1af8cc1YA-cEyHlX1BASwveBiASQeteWFM8UzWxfY