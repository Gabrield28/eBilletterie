package com.univtours.eBilletterie.controllers;

import java.security.Principal;
import java.util.List;

import com.univtours.eBilletterie.entities.Event;
import com.univtours.eBilletterie.repositories.EventRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController extends BaseController {

	@Autowired
	EventRepository eventRepo;

	@GetMapping("/")
	public String index(Model model, Principal principal) {
		model = fillModel(model, "TicketMaster", principal);

		List<Event> events = eventRepo.findAll();

		model.addAttribute("events", events);

		return "index";
	}
}
