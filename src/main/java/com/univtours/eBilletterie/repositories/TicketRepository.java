package com.univtours.eBilletterie.repositories;

import java.util.List;

import com.univtours.eBilletterie.entities.Ticket;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByUserId(Long userId);
    List<Ticket> findByEventId(Long eventId);
}