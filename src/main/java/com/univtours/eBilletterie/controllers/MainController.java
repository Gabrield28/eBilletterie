package com.univtours.eBilletterie.controllers;

import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.univtours.eBilletterie.entities.User;
import com.univtours.eBilletterie.repositories.UserRepository;

@Controller
public class MainController {
	
	@Autowired
	UserRepository repo;
	
	@Autowired
	private PasswordEncoder encoder;

	@GetMapping("/")
	public String homepage(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model, Principal principal) {
		System.out.println("In controller method");
		Optional<User> u = repo.findByUsername("rahim2");
		if(!u.isPresent()) {
			User user = new User("rahim2@example.fr", "rahim2", encoder.encode("123456"));
			repo.save(user);
			u = repo.findByUsername("rahim2");
		}
//		System.out.println(principal.getName());
		model.addAttribute("users", repo.findAll());
		return "greeting";
	}

}
