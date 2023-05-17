package com.example.auth.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.codec.binary.Base64;


public class DecodedToken {
    public String sub;
    public String name;
    public Boolean admin;
    public Long iat;
    public String exp;


    public DecodedToken(String sub, String name, Boolean admin, Long iat, String exp) {
        this.sub = sub;
        this.name = name;
        this.admin = admin;
        this.iat = iat;
        this.exp = exp;
    }

    public DecodedToken() {
    }

    public static DecodedToken getDecoded(String encodedToken) throws JsonProcessingException {

        String[] chunks = encodedToken.split("\\.");
        String payload = new String(Base64.decodeBase64(chunks[1]));

        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readValue(payload, DecodedToken.class);
    }

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public Long getIat() {
        return iat;
    }

    public void setIat(Long iat) {
        this.iat = iat;
    }

}
