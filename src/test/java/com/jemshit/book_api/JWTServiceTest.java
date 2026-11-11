package com.jemshit.book_api;

import com.jemshit.book_api.service.JWTService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Encoders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import io.jsonwebtoken.security.SignatureException;

import static org.junit.jupiter.api.Assertions.*;

public class JWTServiceTest {

    private JWTService jwtService;

    @BeforeEach
    void setUp(){
        jwtService = new JWTService();
        String testKey = Encoders.BASE64.encode(Jwts.SIG.HS256.key().build().getEncoded());
        ReflectionTestUtils.setField(jwtService, "secretKey", testKey);
        ReflectionTestUtils.setField(jwtService, "expirationMs", -3600000L);
    }

    @Test
    void validToken_ReturnsUsername(){
        String token = jwtService.generateToken("Enesh");

        assertEquals("Enesh", jwtService.extractUserName(token));
    }


    @Test
    void tamperedToken_isRejected(){

        String token = jwtService.generateToken("Enesh");
        String[] parts = token.split("\\.");
        String signature = parts[2];
        char swapped = signature.charAt(0) == 'A' ? 'B' : 'A';
        String tamperedToken = parts[0] + "." + parts[1] + "." + swapped + signature.substring(1);

        assertThrows(SignatureException.class, () -> jwtService.extractUserName(tamperedToken));

    }

    @Test
    void expiredToken_isRejected(){
        String token = jwtService.generateToken("Enesh");

        assertThrows(ExpiredJwtException.class, () -> jwtService.extractUserName(token));
    }
}
