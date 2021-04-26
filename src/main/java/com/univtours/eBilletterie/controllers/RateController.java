package com.univtours.eBilletterie.controllers;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.univtours.eBilletterie.entities.Event;
import com.univtours.eBilletterie.entities.Rate;
import com.univtours.eBilletterie.repositories.EventRepository;
import com.univtours.eBilletterie.repositories.RateRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RateController extends BaseController {

    @Autowired
    EventRepository eventRepo;

    @Autowired
    RateRepository rateRepo;

    @GetMapping("/admin/events/{event}/rates/add")
    public String browse(Event event, Model model, Principal principal, RedirectAttributes redirectAttributes) {

        if (principal == null) {
            redirectAttributes.addFlashAttribute("error",
                    "Vous devez être un administrateur authentifié pour accéder à cette page.");
            return "redirect:/";
        }

        model = fillModel(model, "Ajouter un tarif - TicketMaster Admin", principal, "inAdmin");

        if ((Boolean) model.getAttribute("hasRoleAdmin") == false) {
            redirectAttributes.addFlashAttribute("error",
                    "Vous n'avez pas les autorisations nécessaires pour accéder à cette page.");
            return "redirect:/";
        }

        Rate rate = new Rate();
        rate.setEvent(event);

        model.addAttribute("rate", rate);

        return "rate/create";
    }

    @PostMapping("/admin/events/{event}/rates/add")
    public String createPost(Event event, Rate rate, Model model, Principal principal,
            RedirectAttributes redirectAttributes, HttpServletRequest request) {

        if (principal == null) {
            redirectAttributes.addFlashAttribute("error",
                    "Vous devez être un administrateur authentifié pour accéder à cette page.");
            return "redirect:/";
        }

        model = fillModel(model, "Ajouter un tarif - TicketMaster Admin", principal, "inAdmin");

        if ((Boolean) model.getAttribute("hasRoleAdmin") == false) {
            redirectAttributes.addFlashAttribute("error",
                    "Vous n'avez pas les autorisations nécessaires pour accéder à cette page.");
            return "redirect:/";
        }

        if (event.getId() != rate.getEvent().getId()) {
            redirectAttributes.addFlashAttribute("error", "Tentative de modification illégale détectée.");
            return "redirect:/admin/events/" + event.getId() + "/rates/add";
        }

        Integer[] acceptableClasses = { 1, 2, 3 };
        if (!Arrays.asList(acceptableClasses).contains(rate.getTicket_class())) {
            redirectAttributes.addFlashAttribute("error", "Classe invalide.");
            return "redirect:/admin/events/" + event.getId() + "/rates/add";
        }

        if (rate.getPrice() < 0) {
            redirectAttributes.addFlashAttribute("error", "Prix invalide.");
            return "redirect:/admin/events/" + event.getId() + "/rates/add";
        }

        if (rate.getMaxAge() < 0) {
            redirectAttributes.addFlashAttribute("error", "Age maximum invalide.");
            return "redirect:/admin/events/" + event.getId() + "/rates/add";
        }

        List<Rate> rates = rateRepo.findByEventId(event.getId());

        for (Rate rate2 : rates) {
            if (rate2.getTicket_class() == rate.getTicket_class() && rate2.getMaxAge() == rate.getMaxAge()) {
                redirectAttributes.addFlashAttribute("error", "Conflit d'age maximum.");
                return "redirect:/admin/events/" + event.getId() + "/rates/add";
            }
        }

        rateRepo.save(rate);

        return "redirect:/admin/events/" + event.getId();
    }

    @GetMapping("admin/events/{event}/rates/{rate}/update")
    public String update(@PathVariable("event") Event event, @PathVariable("rate") Rate rate, Model model,
            Principal principal, RedirectAttributes redirectAttributes) {

        if (principal == null) {
            redirectAttributes.addFlashAttribute("error",
                    "Vous devez être un administrateur authentifié pour accéder à cette page.");
            return "redirect:/";
        }

        model = fillModel(model, "Modifier un tarif - TicketMaster Admin", principal, "inAdmin");

        if ((Boolean) model.getAttribute("hasRoleAdmin") == false) {
            redirectAttributes.addFlashAttribute("error",
                    "Vous n'avez pas les autorisations nécessaires pour accéder à cette page.");
            return "redirect:/";
        }

        if (rate.getEvent().getId() != event.getId()) {
            redirectAttributes.addFlashAttribute("error", "Ce tarif n'appartient pas à cet événement.");
            return "redirect:/admin/events/{event}";
        }

        model.addAttribute("rate", rate);

        return "rate/update";
    }

    @PostMapping("admin/events/{event}/rates/{rateId}/update")
    public String updatePost(@PathVariable("event") Event event, @PathVariable("rateId") Long rateId, Rate rate,
            Model model, Principal principal, RedirectAttributes redirectAttributes) {

        if (principal == null) {
            redirectAttributes.addFlashAttribute("error",
                    "Vous devez être un administrateur authentifié pour accéder à cette page.");
            return "redirect:/";
        }

        model = fillModel(model, "Modifier un tarif - TicketMaster Admin", principal, "inAdmin");

        if ((Boolean) model.getAttribute("hasRoleAdmin") == false) {
            redirectAttributes.addFlashAttribute("error",
                    "Vous n'avez pas les autorisations nécessaires pour accéder à cette page.");
            return "redirect:/";
        }

        Integer[] acceptableClasses = { 1, 2, 3 };
        if (!Arrays.asList(acceptableClasses).contains(rate.getTicket_class())) {
            redirectAttributes.addFlashAttribute("error", "Classe invalide.");
            return "redirect:/admin/events/" + event.getId() + "/rates/" + rateId + "/update";
        }

        if (rate.getPrice() < 0) {
            redirectAttributes.addFlashAttribute("error", "Prix invalide.");
            return "redirect:/admin/events/" + event.getId() + "/rates/" + rateId + "/update";
        }

        if (rate.getMaxAge() < 0) {
            redirectAttributes.addFlashAttribute("error", "Age maximum invalide.");
            return "redirect:/admin/events/" + event.getId() + "/rates/" + rateId + "/update";
        }

        List<Rate> rates = rateRepo.findByEventId(event.getId());

        for (Rate rate2 : rates) {
            if (rate2.getId() != rate.getId() && rate2.getTicket_class() == rate.getTicket_class()
                    && rate2.getMaxAge() == rate.getMaxAge()) {
                redirectAttributes.addFlashAttribute("error", "Conflit d'age maximum.");
                return "redirect:/admin/events/" + event.getId() + "/rates/" + rateId + "/update";
            }
        }

        rateRepo.save(rate);

        return "redirect:/admin/events/" + event.getId();
    }
}
