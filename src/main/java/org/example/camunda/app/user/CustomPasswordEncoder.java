package org.example.camunda.app.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
public class CustomPasswordEncoder implements PasswordEncoder {

    @Value("${spring.application.password-salt}")
    private String salt;

    @Override
    public String encode(CharSequence rawPassword) {
        return calculatePasswordHash(rawPassword.toString());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        String calculated = calculatePasswordHash(rawPassword.toString());
        if (calculated == null || encodedPassword == null) {
            return false;
        }
        return calculated.equals(encodedPassword);
    }

    private String calculatePasswordHash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] passwordBytes = (String.format("%s%s", password, salt)).getBytes(StandardCharsets.UTF_8);
            byte[] result = md.digest(passwordBytes);
            return new String(Hex.encode(result)).toUpperCase();
            //return DigestUtils.md5Hex(String.format("%s%s", password, salt)).toUpperCase();
        } catch (Exception e) {
            return null;
        }
    }

}
