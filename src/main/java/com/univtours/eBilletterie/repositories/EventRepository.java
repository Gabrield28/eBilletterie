package com.univtours.eBilletterie.repositories;

import java.util.List;

import com.univtours.eBilletterie.entities.Event;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByActive(Boolean active);
}
