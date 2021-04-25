package com.univtours.eBilletterie.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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

    public Event() {
        //
    }
}
