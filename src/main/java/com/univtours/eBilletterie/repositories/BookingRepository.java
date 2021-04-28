package com.univtours.eBilletterie.repositories;

import java.util.List;

import com.univtours.eBilletterie.entities.Booking;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);

    List<Booking> findByEventId(Long eventId);

    List<Booking> findByUserIdAndActive(Long userId, Boolean active);

    List<Booking> findByUserIdAndEventId(Long userId, Long eventId);

    List<Booking> findByConfirmed(Boolean confirmed);
}
