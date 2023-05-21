package com.example.auth.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.apache.tomcat.util.codec.binary.Base64;

@Data
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

}
