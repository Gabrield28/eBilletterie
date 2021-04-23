package com.univtours.eBilletterie.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.univtours.eBilletterie.entities.User;

public interface UserRepository extends JpaRepository<User, Long>{
	Optional<User> findByUsername(String username);
}
