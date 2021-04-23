package com.univtours.eBilletterie.security;

import com.univtours.eBilletterie.entities.User;
import com.univtours.eBilletterie.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {
	
	@Autowired
	UserRepository repo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = repo.findByUsername(username);

		if(user == null) {
			throw new UsernameNotFoundException("Not found: " + username);
		}
		
		return new MyUserDetails(user);
	}

}
