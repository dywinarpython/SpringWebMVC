package com.webapp.springBoot.util;

import com.webapp.springBoot.DTO.Users.UserRequestDTO;
import com.webapp.springBoot.cache.CacheSaveVerifyRecord;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
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

    public void sendConfirmationCode(String toNumber, UserRequestDTO userRequestDTO, HttpServletResponse response) throws Exception {
        String generateValue = generateSixDigitNumber();
        String text = STR."Ваш код подтверждения: \{generateValue}";
//        try {
//            Message.creator(
//                    new PhoneNumber(STR."+7\{toNumber}"),
//                    new PhoneNumber(fromNumber),
//                    text
//            ).create();
//        }catch (Exception e){
//            log.error("Ошика отправки кода на номер телефона {}", e.getMessage());
//            throw new Exception("На данный момент подтверждение по номеру телефона не доступно");
//        }
        System.out.println(text);
        String uuid = UUID.randomUUID().toString();
        Cookie cookie = new Cookie("VERIFY_PHONE", uuid);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 15);
        response.addCookie(cookie);
        Objects.requireNonNull(cacheManager.getCache("VERIFY_PHONE")).put(uuid, new CacheSaveVerifyRecord(userRequestDTO, generateValue));
    }

    private String generateSixDigitNumber() {
        Random random = new Random();
        return String.valueOf(random.nextInt(900000) + 100000);
    }


}
