package com.univtours.eBilletterie.entities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
public class Booking {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private User user;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiryDateTime;

    @Column(nullable = false)
    private Integer ticketCkass;

    @Column(nullable = false)
    private Double priceAtReservation;

    @Column(nullable = false)
    private Boolean active;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean confirmed;

    @Column(nullable = true)
    private LocalDateTime confirmedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String showCreatedAtDate() {
        String pattern = "yyyy-MM-dd";
        return createdAt.format(DateTimeFormatter.ofPattern(pattern));
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiryDateTime() {
        return expiryDateTime;
    }

    public String showExpiryDate() {
        String pattern = "yyyy-MM-dd";
        return expiryDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    public void setExpiryDateTime(LocalDateTime expiryDateTime) {
        this.expiryDateTime = expiryDateTime;
    }

    public Integer getTicketCkass() {
        return ticketCkass;
    }

    public String showTicketClass() {
        switch (ticketCkass) {
        case 1:
            return "1ère Classe";
        case 2:
            return "2ème Classe";
        case 3:
            return "3ème Classe";
        default:
            return null;
        }
    }

    public void setTicketCkass(Integer ticketCkass) {
        this.ticketCkass = ticketCkass;
    }

    public Double getPriceAtReservation() {
        return priceAtReservation;
    }

    public void setPriceAtReservation(Double priceAtReservation) {
        this.priceAtReservation = priceAtReservation;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }

    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(LocalDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public Booking() {
        //
    }

}
