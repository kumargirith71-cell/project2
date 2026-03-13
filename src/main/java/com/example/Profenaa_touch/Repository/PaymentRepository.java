package com.example.Profenaa_touch.Repository;

import com.example.Profenaa_touch.entity.Payment;
import com.example.Profenaa_touch.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByRazorpayOrderId(String orderId);
    List<Payment> findByUserAndStatus(User user, String status);
}