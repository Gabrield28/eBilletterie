package com.univtours.eBilletterie.repositories;

import com.univtours.eBilletterie.entities.Booking;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    // 
}
