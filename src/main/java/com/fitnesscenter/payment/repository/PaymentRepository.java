package com.fitnesscenter.payment.repository;

import com.fitnesscenter.payment.entity.Payment;
import com.fitnesscenter.payment.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
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

    /*
     * Calculate total revenue for a payment status.
     *
     * COALESCE ensures that the result is zero
     * instead of null when there are no matching
     * payments.
     */
    @Query("""
            SELECT COALESCE(SUM(p.amount), 0)
            FROM Payment p
            WHERE p.status = :status
            """)
    BigDecimal sumAmountByStatus(
            PaymentStatus status
    );
}