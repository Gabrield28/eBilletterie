package com.univtours.eBilletterie.controllers;

import java.security.Principal;
import java.util.List;

import com.univtours.eBilletterie.entities.Ticket;
import com.univtours.eBilletterie.entities.User;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class TicketController extends BaseController {
    @GetMapping("/tickets")
    public String browse(Model model, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            redirectAttributes.addFlashAttribute("error", "Vous devez être authentifié pour accéder à cette page.");
            return "redirect:/";
        }

        User cUser = userRepo.findByUsername(principal.getName());

        List<Ticket> tList = ticketRepo.findByUserIdAndActive(cUser.getId(), true);

        for (Ticket ticket : tList) {
            if (ticket.getEvent().isActive() == false) {
                ticket.setActive(false);
                ticketRepo.save(ticket);
            }
        }

        tList = ticketRepo.findByUserIdAndActive(cUser.getId(), true);

        model.addAttribute("tickets", tList);

        if (tList.size() == 0) {
            model.addAttribute("emptyList", true);
        }

        model = fillModel(model, "Mes billets - TicketMaster", principal);

        return "ticket/browse";
    }
}
