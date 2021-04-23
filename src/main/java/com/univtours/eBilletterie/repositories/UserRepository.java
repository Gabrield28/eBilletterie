package com.univtours.eBilletterie.repositories;

import com.univtours.eBilletterie.entities.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>{
	User findByUsername(String username);
}
