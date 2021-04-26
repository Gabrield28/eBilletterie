package com.univtours.eBilletterie.repositories;

import java.util.List;
import java.util.Optional;

import com.univtours.eBilletterie.entities.Rate;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RateRepository extends JpaRepository<Rate, Long> {
    List<Rate> findByEventId(Long eventId);
    Optional<Rate> findByIdAndEventId(Long id, Long eventId);
}
