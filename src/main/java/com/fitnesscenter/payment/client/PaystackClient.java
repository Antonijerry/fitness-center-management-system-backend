
        package com.fitnesscenter.payment.client;

import com.fitnesscenter.common.exception.PaymentException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
@RequiredArgsConstructor
public class PaystackClient {

    private final PaystackProperties properties;

    private RestClient restClient;


    /**
     * Initialize the Paystack REST client after
     * Spring has injected the configuration.
     */
    @jakarta.annotation.PostConstruct
    private void initializeClient() {

        restClient =
                RestClient.builder()
                        .baseUrl(
                                properties.getBaseUrl()
                        )
                        .defaultHeader(
                                HttpHeaders.AUTHORIZATION,
                                "Bearer "
                                        + properties
                                        .getSecretKey()
                        )
                        .defaultHeader(
                                HttpHeaders.CONTENT_TYPE,
                                MediaType.APPLICATION_JSON_VALUE
                        )
                        .defaultHeader(
                                HttpHeaders.ACCEPT,
                                MediaType.APPLICATION_JSON_VALUE
                        )
                        .build();
    }


    /**
     * Initialize a Paystack transaction.
     */
    public PaystackInitializeResponse
    initializeTransaction(
            PaystackInitializeRequest request
    ) {

        try {

            return restClient
                    .post()
                    .uri(
                            "/transaction/initialize"
                    )
                    .body(request)
                    .retrieve()
                    .body(
                            PaystackInitializeResponse.class
                    );

        } catch (RestClientException exception) {

            throw new PaymentException(
                    "Unable to initialize Paystack payment",
                    exception
            );
        }
    }


    /**
     * Verify a Paystack transaction.
     */
    public PaystackVerifyResponse
    verifyTransaction(
            String reference
    ) {

        if (reference == null
                || reference.isBlank()) {

            throw new IllegalArgumentException(
                    "Payment reference cannot be blank"
            );
        }

        try {

            return restClient
                    .get()
                    .uri(
                            "/transaction/verify/{reference}",
                            reference
                    )
                    .retrieve()
                    .body(
                            PaystackVerifyResponse.class
                    );

        } catch (RestClientException exception) {

            throw new PaymentException(
                    "Unable to verify Paystack payment",
                    exception
            );
        }
    }
}
