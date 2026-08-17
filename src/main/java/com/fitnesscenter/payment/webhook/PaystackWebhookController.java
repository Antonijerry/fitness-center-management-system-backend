package com.fitnesscenter.payment.webhook;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitnesscenter.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments/webhook")
@RequiredArgsConstructor
public class PaystackWebhookController {

    private final PaymentService paymentService;

    private final PaystackWebhookSecurity webhookSecurity;

    private final ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<Void> handleWebhook(

            @RequestHeader(
                    value = "x-paystack-signature",
                    required = false
            )
            String signature,

            @RequestBody
            String payload
    ) {

        /*
         * Step 1:
         * Validate Paystack webhook signature.
         */
        if (!webhookSecurity.isValid(
                payload,
                signature
        )) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        /*
         * Step 2:
         * Deserialize the webhook payload.
         */
        final PaystackWebhookPayload webhook;

        try {

            webhook =
                    objectMapper.readValue(
                            payload,
                            PaystackWebhookPayload.class
                    );

        } catch (Exception exception) {

            /*
             * Invalid JSON/payload.
             */
            return ResponseEntity
                    .badRequest()
                    .build();
        }

        /*
         * Step 3:
         * Ignore events that are not relevant
         * to our payment flow.
         */
        if (!"charge.success".equalsIgnoreCase(
                webhook.event()
        )) {

            return ResponseEntity
                    .ok()
                    .build();
        }

        /*
         * Step 4:
         * Validate webhook data.
         */
        if (webhook.data() == null
                || webhook.data().reference() == null
                || webhook.data().reference().isBlank()) {

            return ResponseEntity
                    .badRequest()
                    .build();
        }

        /*
         * Step 5:
         * Do NOT trust the webhook alone.
         *
         * The payment service will independently
         * verify the transaction with Paystack.
         */
        try {

            paymentService.verifyPayment(
                    webhook.data().reference()
            );

            /*
             * Paystack has received and processed
             * our webhook successfully.
             */
            return ResponseEntity
                    .ok()
                    .build();

        } catch (Exception exception) {

            /*
             * A temporary processing problem should
             * not be treated as an invalid webhook.
             *
             * Returning 500 allows Paystack to retry
             * the webhook.
             */
            return ResponseEntity
                    .status(
                            HttpStatus.INTERNAL_SERVER_ERROR
                    )
                    .build();
        }
    }
}