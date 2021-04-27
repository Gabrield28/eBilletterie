package com.univtours.eBilletterie.controllers;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.univtours.eBilletterie.entities.Booking;
import com.univtours.eBilletterie.entities.Event;
import com.univtours.eBilletterie.entities.Rate;
import com.univtours.eBilletterie.entities.Ticket;
import com.univtours.eBilletterie.entities.User;
import com.univtours.eBilletterie.repositories.BookingRepository;
import com.univtours.eBilletterie.repositories.TicketRepository;
import com.univtours.eBilletterie.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
public class BaseController {

	@Autowired
	UserRepository userRepo;
    
    @Autowired
    BookingRepository bookingRepo;
    
    @Autowired
    TicketRepository ticketRepo;
    
    protected Model fillModel(Model model, String title, Principal principal) {
        return fillModel(model, title, principal, "");
    }
    
    protected Model fillModel(Model model, String title, Principal principal, String context) {
        if (principal != null) {
            User user = userRepo.findByUsername(principal.getName());
			model.addAttribute("principal", principal);
			model.addAttribute("isLoggedIn", true);
			model.addAttribute("user", user);
			model.addAttribute("hasRoleAdmin", user.hasRole("ROLE_ADMIN"));
            List<Booking> bookingList = bookingRepo.findByUserIdAndActive(user.getId(), true);
            if (bookingList.size() > 0) {
                model.addAttribute("bookingCount", bookingList.size());
            }
            List<Ticket> ticketList = ticketRepo.findByUserId(user.getId());
            if (ticketList.size() > 0) {
                Integer count = 0;
                for (Ticket ticket : ticketList) {
                    if (ticket.getEvent().getDatetime().isAfter(LocalDateTime.now())) {
                        count++;
                    }
                }
                if (count > 0) {
                    model.addAttribute("ticketCount", count);
                }
            }
		}
        model.addAttribute("title", title);
        if (context.equals("inAdmin")) {
            model.addAttribute("inAdmin", true);
        }
        return model;
    }

    protected Model getRatesForUser(Model model, Event event, Principal principal) {
        if (principal != null) {

            Set<Rate> rates = event.getRates();

            User user = userRepo.findByUsername(principal.getName());

            if (user.getAge() < event.getMinimum_age_allowed()) {
                model.addAttribute("ageNotAllowed", true);
            } else {

                Integer age = (int) Double.POSITIVE_INFINITY;

                Double firstClassPrice = (double) 0;
                Double secondClassPrice = (double) 0;
                Double thirdClassPrice = (double) 0;

                for (Rate rate : rates) {
                    if (rate.getMaxAge() <= age && user.getAge() <= rate.getMaxAge()) {
                        age = rate.getMaxAge();
                        switch (rate.getTicket_class()) {
                        case 1:
                            firstClassPrice = rate.getPrice();
                            break;
                        case 2:
                            secondClassPrice = rate.getPrice();
                            break;
                        case 3:
                            thirdClassPrice = rate.getPrice();
                            break;
                        default:
                            break;
                        }
                    }
                }

                if (!firstClassPrice.equals((double) 0) || !secondClassPrice.equals((double) 0)
                        || !thirdClassPrice.equals((double) 0)) {
                    model.addAttribute("price", true); 
                }
                if (!firstClassPrice.equals((double) 0)) {
                    model.addAttribute("firstClassPrice", firstClassPrice);
                }
                if (!secondClassPrice.equals((double) 0)) {
                    model.addAttribute("secondClassPrice", secondClassPrice);
                }
                if (!thirdClassPrice.equals((double) 0)) {
                    model.addAttribute("thirdClassPrice", thirdClassPrice);
                }
            }

        }
        return model;
    }
}
