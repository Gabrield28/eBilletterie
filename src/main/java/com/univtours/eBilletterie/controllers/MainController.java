package com.univtours.eBilletterie.controllers;

import java.security.Principal;

import com.univtours.eBilletterie.entities.User;
import com.univtours.eBilletterie.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MainController {

	@Autowired
	UserRepository userRepo;

	@Autowired
	private PasswordEncoder encoder;

	@GetMapping("/")
	public String index(Model model, Principal principal) {
		if (principal != null)
			model.addAttribute("principal", principal);
		model.addAttribute("title", "TicketMaster");
		
		return "index";
	}

	@GetMapping("/register")
	public String showRegistrationForm(Model model, Principal principal) {
		User userToRegister = new User();

		if (principal != null)
			model.addAttribute("principal", principal);
		model.addAttribute("title", "Inscription");
		model.addAttribute("user", userToRegister);

		return "register";
	}

	@PostMapping("/process_register")
	public String processRegister(User user, Model model, Principal principal) {
		String encodedPassword = encoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
		user.setRoles("ROLE_USER");
		user.setActive(true);
		userRepo.save(user);

		if (principal != null)
			model.addAttribute("principal", principal);
		model.addAttribute("title", "Success");

		return "redirect:/";
	}
}
