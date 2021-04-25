package com.univtours.eBilletterie.controllers;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController extends BaseController {

	@GetMapping("/")
	public String index(Model model, Principal principal) {
		model = fillModel(model, "TicketMaster", principal);

		return "index";
	}
}
