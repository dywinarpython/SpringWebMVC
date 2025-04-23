package com.webapp.springBoot.util;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import com.webapp.springBoot.entity.UsersApp;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Random;
import java.util.UUID;

@Slf4j
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

    public void sendConfirmationCode(String toNumber, UsersApp usersApp, HttpServletResponse response) throws Exception {
        String generateValue = generateSixDigitNumber();
        String text = STR."Ваш код подтверждения: \{generateValue}";
        try {
            Message.creator(
                    new PhoneNumber(STR."+7\{toNumber}"),
                    new PhoneNumber(fromNumber),
                    text
            ).create();
        }catch (Exception e){
            log.error("Ошика отправки кода на номер телефона {}", e.getMessage());
            throw new Exception("На данный момент подтверждение по номеру телефона не доступно");
        }
        String uuid = UUID.randomUUID().toString();
        Cookie cookie = new Cookie("VERIF_PHONE", uuid);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 15);
        response.addCookie(cookie);
        Objects.requireNonNull(cacheManager.getCache("VERIF_PHONE")).put(uuid, generateValue);
        Objects.requireNonNull(cacheManager.getCache("USERS_APP")).put(uuid, usersApp);
    }

    private String generateSixDigitNumber() {
        Random random = new Random();
        return String.valueOf(random.nextInt(900000) + 100000);
    }
}
