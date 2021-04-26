package com.univtours.eBilletterie.entities;

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
public class Rate {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Event event;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = true)
    private Integer maxAge;

    @Column(nullable = false)
    private Integer ticket_class;

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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
    }

    public Integer getTicket_class() {
        return ticket_class;
    }

    public String showTicketClass() {
        switch (ticket_class) {
            case 1:
                return "1ère Classe";
            case 2:
                return "2ème Classe";
            case 3:
                return "3ème Classe";
            default:
                return "Classe non définie";
        }
    }

    public void setTicket_class(Integer ticket_class) {
        this.ticket_class = ticket_class;
    }

    public Rate() {
        //
    }

}
