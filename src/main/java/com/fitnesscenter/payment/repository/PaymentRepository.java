package com.fitnesscenter.payment.repository;

import com.fitnesscenter.payment.entity.Payment;
import com.fitnesscenter.payment.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository
        extends JpaRepository<Payment, Long> {

    Optional<Payment> findByReference(
            String reference
    );

    Optional<Payment> findByGatewayReference(
            String gatewayReference
    );

    boolean existsByMembershipIdAndStatus(
            Long membershipId,
            PaymentStatus status
    );
}