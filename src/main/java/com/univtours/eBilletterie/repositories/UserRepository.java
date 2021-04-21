package com.univtours.eBilletterie.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.univtours.eBilletterie.entities.User;

public interface UserRepository extends JpaRepository<User, Long>{

}
