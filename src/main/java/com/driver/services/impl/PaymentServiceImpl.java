package com.driver.services.impl;

import com.driver.model.Payment;
import com.driver.model.PaymentMode;
import com.driver.model.Reservation;
import com.driver.model.Spot;
import com.driver.repository.PaymentRepository;
import com.driver.repository.ReservationRepository;
import com.driver.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    ReservationRepository reservationRepository2;
    @Autowired
    PaymentRepository paymentRepository2;

    @Override
    public Payment pay(Integer reservationId, int amountSent, String mode) throws Exception {
        //getting the reservation by id
        Reservation reservation = reservationRepository2.findById(reservationId).get();
        //check the given payment mode is correct. if it's correct return that mode or return null.
        PaymentMode currentMode = Arrays.stream(PaymentMode.values())
                .filter(paymentMode -> mode.toUpperCase().equals(paymentMode.name())).findFirst().orElse(null);
        //if the current payment mode is null throw an exception
        if(currentMode == null) throw new Exception("Payment mode not detected");
        //generate a bill
        int bill = reservation.getNumberOfHours()*reservation.getSpot().getPricePerHour();
        //if the bill amount is grater than amount set throw exception
        if(amountSent < bill) throw new Exception("Insufficient Amount");

        //adding details to payment object to make payment
        Payment payment = new Payment();
        payment.setPaymentCompleted(true);
        payment.setPaymentMode(currentMode);
        payment.setReservation(reservation);
        return paymentRepository2.save(payment);
    }
}
