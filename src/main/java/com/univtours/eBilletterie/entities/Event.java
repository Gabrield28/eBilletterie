package com.univtours.eBilletterie.entities;


import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

@Entity
public class Event {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = true)
    private String artist;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String postal_code;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private Boolean active;

    @Column(nullable = true)
    private int capacity;

    @Column(nullable = true)
    private int minimum_age_allowed;

    @Column(nullable = true)
    private String image;

    @Column(nullable = false)
    private Integer type;

    @Column(nullable = false)
    private LocalDateTime datetime;

    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            mappedBy = "event")
    private Set<Rate> rates = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostal_code() {
        return postal_code;
    }

    public void setPostal_code(String postal_code) {
        this.postal_code = postal_code;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

	public String showAddress() {
		if (city == null) {
			return "Addresse non définie";
		}
		if(address == null && postal_code == null && city != null) {
			return this.city;
		}
		if(address == null && postal_code != null && city != null) {
			return this.postal_code + " " + this.city;
		}
		return this.address + ", " + this.postal_code + " " + this.city;
	}

    public Boolean isActive() {
        return active;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String showActive() {
        if (active) {
            return "Oui";
        } else {
            return "Non";            
        }
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getMinimum_age_allowed() {
        return minimum_age_allowed;
    }

    public void setMinimum_age_allowed(int minimum_age_allowed) {
        this.minimum_age_allowed = minimum_age_allowed;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Transient
    public String getPhotosImagePath() {
        if (image == null || id == null) return null;
         
        return "/images/uploaded/events/" + image;
    }

    public Integer getType() {
        return type;
    }

    public String showType() {
        switch (type) {
            case 1:
                return "Concert";
            case 2:
                return "Sports";
            case 3:
                return "Arts et théâtre";
            default:
                return "Type non défini";
        }
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public String showDate() {
        String pattern = "yyyy-MM-dd";
        return datetime.format(DateTimeFormatter.ofPattern(pattern));
    }

    public String showTime() {
        String pattern = "HH:mm";
        return datetime.format(DateTimeFormatter.ofPattern(pattern));
    }

    public String showDateTime() {
        String pattern = "dd/MM/yyyy à HH:mm";
        return datetime.format(DateTimeFormatter.ofPattern(pattern));
    }

    public void setDatetime(LocalDateTime datetime) {
        this.datetime = datetime;
    }

    public Set<Rate> getRates() {
        return rates;
    }

    public void setRates(Set<Rate> rates) {
        this.rates = rates;
    }

    public Event() {
        type = 0;
    }
}
