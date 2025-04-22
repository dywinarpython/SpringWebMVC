package com.webapp.springBoot.util;

import com.twilio.Twilio;
import com.webapp.springBoot.entity.UsersApp;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Random;
import java.util.UUID;

@Service
public class VerifyPhoneService {


    @Value("${twillio.sid}")
    private String accountSid;

    @Value("${twillio.authToken}")
    private String authToken;

    @Value("${twillio.number}")
    private String fromNumber;

    @Autowired
    private CacheManager cacheManager;

    public String sendConfirmationCode(String toNumber, UsersApp usersApp, HttpServletResponse response) {
        String generateValue = generateSixDigitNumber();
        String text = "Ваш код подтверждения: " + generateValue;
        System.out.println(text);
        // Сделать оабработку исключений
//        Message.creator(
//                new PhoneNumber(toNumber),
//                new PhoneNumber(fromNumber),
//                text
//        ).create();
        String uuid = UUID.randomUUID().toString();
        Cookie cookie = new Cookie("VERIF_PHONE", uuid);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 15);
        response.addCookie(cookie);
        Objects.requireNonNull(cacheManager.getCache("VERIF_PHONE")).put(uuid, generateValue);
        Objects.requireNonNull(cacheManager.getCache("USERS_APP")).put(uuid, usersApp);
        return uuid;
    }

    private String generateSixDigitNumber() {
        Random random = new Random();
        return String.valueOf(random.nextInt(900000) + 100000);
    }
}
