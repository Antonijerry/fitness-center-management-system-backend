package com.fitnesscenter.payment.client;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "paystack") //register this at the springboot main class
public class PaystackProperties {

    private String baseUrl;

    private String secretKey;

    private String publicKey;

    private String callbackUrl;
}