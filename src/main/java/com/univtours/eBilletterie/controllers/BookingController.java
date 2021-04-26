package com.univtours.eBilletterie.controllers;

import com.univtours.eBilletterie.entities.Event;
import com.univtours.eBilletterie.repositories.BookingRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BookingController extends BaseController {
    @Autowired
    BookingRepository bookingRepo;

    @GetMapping("/events/{event}/buy") 
    public String name(Event event, Model model) {
        model.addAttribute("event", event);
        return "event/read";
    }
}
