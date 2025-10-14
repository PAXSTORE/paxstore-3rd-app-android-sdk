package com.pax.market.android.app.sdk.util;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.util.Date;

import javax.crypto.SecretKey;

/**
 * Generate a JWT with HS512 signature using Nimbus, compatible with JJWT 0.9.0's secret handling (i.e., the secret is a Base64 string)
 */
public final class NimbusJwtHelper {

    private NimbusJwtHelper() {
    }


    /**
     * Generate HS512 JWT
     *
     * @param subject   JWT's subject (such as application ID or user ID)
     * @param expDate   expiration time (millisecond timestamp)
     * @param secretKey Symmetric Key (SecretKeySpec, algorithm HmacSHA512)
     * @return serialized JWT string
     * @throws JOSEException thrown when signature error occurs
     */
    public static String generateHs512Token(String subject, Date expDate, SecretKey secretKey) throws JOSEException {
        if (subject == null || subject.isEmpty()) {
            throw new IllegalArgumentException("subject should not be null");
        }
        if (secretKey == null) {
            throw new IllegalArgumentException("secretKey should not be null");
        }

        // 1. Claims
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(subject)
                .expirationTime(expDate)
                .build();

        // 2. Header
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        SignedJWT signedJWT = new SignedJWT(header, claims);

        // 3. sign with SecretKey
        signedJWT.sign(new MACSigner(secretKey.getEncoded()));

        // 4. Return Compact serialization
        return signedJWT.serialize();
    }
}