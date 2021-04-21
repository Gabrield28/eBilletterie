package com.univtours.eBilletterie;

import org.springframework.beans.factory.annotation.Autowired;
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

	@GetMapping("/")
	public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
		User u = new User();
		u.setEmail("azerty@akzae.fr");
		model.addAttribute("name", name);
		return "greeting";
	}

}
