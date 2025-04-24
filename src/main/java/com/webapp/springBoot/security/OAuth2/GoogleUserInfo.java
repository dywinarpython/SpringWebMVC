package com.webapp.springBoot.security.OAuth2;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class GoogleUserInfo {
    private String email;

    private Boolean email_verified;

    @JsonProperty("given_name")
    private String name;

    @JsonProperty("family_name")
    private String surname;

    public GoogleUserInfo checkFiledNull(){
        if(Objects.equals(email, "null")){
            throw new RuntimeException("Почта не может быть null");
        }else if(Objects.equals(name, "null")){
            this.setName(null);
        } else if(Objects.equals(surname, "null")){
            this.setName(null);
        }
        return this;
    }
    public List<String> getNullFiled(){
        List<String> list = new ArrayList<>();
        if (name == null){
            list.add("name");
        }
        if (surname == null){
            list.add("surname");
        }
        return list;
    }
}
