package com.univtours.eBilletterie.controllers;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.univtours.eBilletterie.entities.Event;
import com.univtours.eBilletterie.entities.Ticket;
import com.univtours.eBilletterie.entities.User;
import com.univtours.eBilletterie.repositories.EventRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminController extends BaseController {

    @Autowired
    EventRepository eventRepo;

    @GetMapping("/admin")
    public String adminIndex(Model model, Principal principal, RedirectAttributes redirectAttributes) {

        if (principal == null) {
            redirectAttributes.addFlashAttribute("error",
                    "Vous devez être un administrateur authentifié pour accéder à cette page.");
            return "redirect:/";
        }

        model = fillModel(model, "TicketMaster Admin", principal, "inAdmin");

        if ((Boolean) model.getAttribute("hasRoleAdmin") == false) {
            redirectAttributes.addFlashAttribute("error",
                    "Vous n'avez pas les autorisations nécessaires pour accéder à cette page.");
            return "redirect:/";
        }

        int LIMIT = 5;

        List<User> users = userRepo.findAll();
        // If number of users is too great, limit that number to LIMIT
        if (users.size() > LIMIT) {
            users = users.subList(users.size() - LIMIT, users.size());
        }
        model.addAttribute("users", users);

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

        model = fillModelWithStats(model);

        return "admin";
    }

    private Model fillModelWithStats(Model model) {
        List<Ticket> tList = ticketRepo.findByActive(true);
        Integer yearlyConfirmedBookingsCount = 0;
        Integer monthlyConfirmedBookingsCount = 0;
        Integer weeklyConfirmedBookingsCount = 0;
        Integer dailyConfirmedBookingsCount = 0;
        LocalDateTime now = LocalDateTime.now();
        for (Ticket ticket : tList) {
            LocalDateTime eventDateTime = ticket.getCreatedAt();
            if (eventDateTime.isAfter(now.minusYears(1))) {
                yearlyConfirmedBookingsCount++;
            }
            if (eventDateTime.isAfter(now.minusMonths(1))) {
                monthlyConfirmedBookingsCount++;
            }
            if (eventDateTime.isAfter(now.minusWeeks(1))) {
                weeklyConfirmedBookingsCount++;
            }
            if (eventDateTime.isAfter(now.minusDays(1))) {
                dailyConfirmedBookingsCount++;
            }
        }
        model.addAttribute("confirmedBookingsCount", tList.size());
        model.addAttribute("yearlyConfirmedBookingsCount", yearlyConfirmedBookingsCount);
        model.addAttribute("monthlyConfirmedBookingsCount", monthlyConfirmedBookingsCount);
        model.addAttribute("weeklyConfirmedBookingsCount", weeklyConfirmedBookingsCount);
        model.addAttribute("dailyConfirmedBookingsCount", dailyConfirmedBookingsCount);

        List<Event> eList = eventRepo.findByActive(true);
        Integer yearlyEventsCount = 0;
        Integer monthlyEventsCount = 0;
        Integer weeklyEventsCount = 0;
        Integer dailyEventsCount = 0;
        for (Event event : eList) {
            LocalDateTime eventDateTime = event.getDatetime();
            if (eventDateTime.isAfter(now.minusYears(1)) && eventDateTime.isBefore(now)) {
                yearlyEventsCount++;
            }
            if (eventDateTime.isAfter(now.minusMonths(1)) && eventDateTime.isBefore(now)) {
                monthlyEventsCount++;
            }
            if (eventDateTime.isAfter(now.minusWeeks(1)) && eventDateTime.isBefore(now)) {
                weeklyEventsCount++;
            }
            if (eventDateTime.isAfter(now.minusDays(1)) && eventDateTime.isBefore(now)) {
                dailyEventsCount++;
            }
        }
        model.addAttribute("eventsCount", eList.size());
        model.addAttribute("yearlyEventsCount", yearlyEventsCount);
        model.addAttribute("monthlyEventsCount", monthlyEventsCount);
        model.addAttribute("weeklyEventsCount", weeklyEventsCount);
        model.addAttribute("dailyEventsCount", dailyEventsCount);

        return model;
    }
}
