package com.univtours.eBilletterie.controllers;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

		int LIMIT = 8;

		List<Event> eventsInDB = eventRepo.findAll();

        List<Event> events = new ArrayList<Event>();

        for (Event event : eventsInDB) {
            if (event.getDatetime().isAfter(LocalDateTime.now())) {
                events.add(event);
            }
        }
		// If number of events is too great, limit that number to LIMIT
		if (events.size() > LIMIT) {
			events = events.subList(events.size() - LIMIT, events.size());
		}
		model.addAttribute("events", events);

		return "index";
	}
}
