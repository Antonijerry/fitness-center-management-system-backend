package com.fitnesscenter.payment.webhook;

import com.fitnesscenter.payment.client.PaystackProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
@RequiredArgsConstructor
public class PaystackWebhookSecurity {

    private static final String HMAC_ALGORITHM = "HmacSHA512";

    private final PaystackProperties properties;


    /**
     * Validates the x-paystack-signature header
     * against the raw webhook request body.
     *
     * Paystack uses HMAC-SHA512 with the Paystack
     * secret key.
     */
    public boolean isValid(
            String payload,
            String signature
    ) {

        if (payload == null
                || payload.isBlank()
                || signature == null
                || signature.isBlank()) {

            return false;
        }

        String secretKey =
                properties.getSecretKey();

        if (secretKey == null
                || secretKey.isBlank()) {

            return false;
        }

        try {

            Mac mac =
                    Mac.getInstance(
                            HMAC_ALGORITHM
                    );

            SecretKeySpec secretKeySpec =
                    new SecretKeySpec(
                            secretKey.getBytes(
                                    StandardCharsets.UTF_8
                            ),
                            HMAC_ALGORITHM
                    );

            mac.init(secretKeySpec);

            byte[] hash =
                    mac.doFinal(
                            payload.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            String expectedSignature =
                    bytesToHex(hash);

            return constantTimeEquals(
                    expectedSignature,
                    signature
            );

        } catch (Exception exception) {

            return false;
        }
    }


    /**
     * Converts the HMAC byte array into
     * the lowercase hexadecimal representation
     * used by the Paystack signature.
     */
    private String bytesToHex(
            byte[] bytes
    ) {

        StringBuilder result =
                new StringBuilder(
                        bytes.length * 2
                );

        for (byte b : bytes) {

            result.append(
                    String.format(
                            "%02x",
                            b
                    )
            );
        }

        return result.toString();
    }


    /**
     * Constant-time comparison prevents timing
     * attacks when comparing webhook signatures.
     */
    private boolean constantTimeEquals(
            String expected,
            String actual
    ) {

        if (expected == null
                || actual == null) {

            return false;
        }

        byte[] expectedBytes =
                expected.getBytes(
                        StandardCharsets.UTF_8
                );

        byte[] actualBytes =
                actual.getBytes(
                        StandardCharsets.UTF_8
                );

        return MessageDigest.isEqual(
                expectedBytes,
                actualBytes
        );
    }
}