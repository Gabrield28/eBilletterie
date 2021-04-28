package com.univtours.eBilletterie.entities;

import java.time.LocalDate;
import java.time.Period;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class User {

	@Id
	@GeneratedValue
	private Long id;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false, unique = true)
	private String username;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String roles;

	@Column(nullable = false)
	private boolean active;

	@Column(nullable = false)
	private String first_name;

	@Column(nullable = false)
	private String last_name;

	@Column(nullable = false)
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate birthday;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	public void addRole(String role) {
		this.roles += ", " + role;
	}

	public Boolean hasRole(String role) {
		String[] roles = this.roles.split(", ");
		for (String r : roles) {
			if (r.equals(role)) {
				return true;
			}
		}
		return false;
	}

	public Boolean isAdmin() {
		return hasRole("ROLE_ADMIN");
	}

	public String showDominantRole() {
		if (this.hasRole("ROLE_ADMIN"))
			return "Administrateur";
		else
			return "Utilisateur";
	}

	public boolean isActive() {
		return active;
	}

	public String showIsBlocked() {
		if (active) {
			return "Non";
		} else {
			return "Oui";
		}
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public LocalDate getBirthday() {
		return birthday;
	}

	public void setBirthday(LocalDate birthday) {
		this.birthday = birthday;
	}

	public int getAge() {
		return Period.between(birthday, LocalDate.now()).getYears();
	}

	public User() {
		//
	}

	public String getFullName() {
		return this.first_name + " " + this.last_name;
	}
}
