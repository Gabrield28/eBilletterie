package com.univtours.eBilletterie.repositories;

import java.util.List;

import com.univtours.eBilletterie.entities.Ticket;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByUserId(Long userId);
    
    List<Ticket> findByActive(Boolean active);
    
    List<Ticket> findByUserIdAndActive(Long userId, Boolean active);

    List<Ticket> findByEventId(Long eventId);

    List<Ticket> findByUserIdAndEventId(Long userId, Long eventId);
}
