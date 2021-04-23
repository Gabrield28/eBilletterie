package com.univtours.eBilletterie.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.springframework.lang.NonNull;

@Entity
public class User {
	
//	@Autowired
//	PasswordEncoder encoder;
	
	@Id @GeneratedValue
	private Long id;
	@NonNull
	private String email;
	@NonNull
	private String username;
	@NonNull
	private String password;
	private String roles;
	private boolean active;
	private int age;
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
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public User(Long id, String email, String username, String password, String roles, boolean active, int age) {
		super();
		this.id = id;
		this.email = email;
		this.username = username;
		this.password = password;
		this.roles = roles;
		this.active = active;
		this.age = age;
	}
	public User(String email, String username, String password, String roles, boolean active, int age) {
		super();
		this.email = email;
		this.username = username;
		this.password = password;
		this.roles = roles;
		this.active = active;
		this.age = age;
	}
	public User(String email, String username, String password) {
		super();
		this.email = email;
		this.username = username;
		this.password = password;
		this.roles = "ROLE_USER";
		this.active = true;
	}
	public User() {}
}
