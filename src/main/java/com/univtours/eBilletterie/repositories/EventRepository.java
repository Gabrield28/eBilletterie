package com.univtours.eBilletterie.repositories;

import com.univtours.eBilletterie.entities.Event;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
    //
}
