package com.univtours.eBilletterie.controllers;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.univtours.eBilletterie.entities.Booking;
import com.univtours.eBilletterie.entities.Event;
import com.univtours.eBilletterie.entities.Ticket;
import com.univtours.eBilletterie.entities.User;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class BookingController extends BaseController {

    @PostMapping("/events/{event}/buy")
    public String name(Event event, Model model, Principal principal, HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            redirectAttributes.addFlashAttribute("error", "Vous devez être authentifié pour accéder à cette page.");
            return "redirect:/events/" + event.getId();
        }

        if (event.isActive() == false) {
            redirectAttributes.addFlashAttribute("error", "Cet événement est annulé.");
            return "redirect:/events/" + event.getId();
        }

        model = fillModel(model, "Réserver un billet pour " + event.getTitle() + " - TicketMaster", principal);

        model = getRatesForUser(model, event, principal);

        Boolean ageNotAllowed = (Boolean) model.getAttribute("ageNotAllowed");
        if (ageNotAllowed != null) {
            redirectAttributes.addFlashAttribute("error", "Vous n'avez pas l'âge minimum pour réserver cet événement.");
            return "redirect:/events/" + event.getId();
        }

        Boolean price = (Boolean) model.getAttribute("price");
        if (price == null) {
            redirectAttributes.addFlashAttribute("error",
                    "Il n'y a pas de places disponibles pour cet événement pour le moment.");
            return "redirect:/events/" + event.getId();
        }

        User currentUser = userRepo.findByUsername(principal.getName());
        List<Booking> bList = bookingRepo.findByUserIdAndEventId(currentUser.getId(), event.getId());
        if (bList.size() > 0) {
            for (Booking booking : bList) {
                if (booking.isActive() == false) {
                    continue;
                }
                if (booking.getExpiryDateTime().compareTo(LocalDateTime.now()) <= 0) {
                    booking.setActive(false);
                    bookingRepo.save(booking);
                } else {
                    redirectAttributes.addFlashAttribute("error", "Vous avez déjà une réservation pour cet événement.");
                    return "redirect:/events/" + event.getId();
                }
            }
        }

        Booking booking = new Booking();
        booking.setEvent(event);
        booking.setUser(currentUser);
        Integer ticketClass = null;
        Double priceAtReservation = null;
        String firstClass = request.getParameter("firstClass");
        if (firstClass != null && model.getAttribute("firstClassPrice") != null && firstClass.equals("true")) {
            ticketClass = 1;
            priceAtReservation = (Double) model.getAttribute("firstClassPrice");
        }
        String secondClass = request.getParameter("secondClass");
        if (secondClass != null && model.getAttribute("secondClassPrice") != null && secondClass.equals("true")) {
            ticketClass = 2;
            priceAtReservation = (Double) model.getAttribute("secondClassPrice");
        }
        String thirdClass = request.getParameter("thirdClass");
        if (thirdClass != null && model.getAttribute("thirdClassPrice") != null && thirdClass.equals("true")) {
            ticketClass = 3;
            priceAtReservation = (Double) model.getAttribute("thirdClassPrice");
        }
        booking.setTicketCkass(ticketClass);
        booking.setPriceAtReservation(priceAtReservation);
        booking.setCreatedAt(LocalDateTime.now());
        LocalDateTime dateTime = LocalDateTime.now().plusWeeks(1);
        booking.setExpiryDateTime(dateTime);
        booking.setActive(true);
        booking.setConfirmed(false);
        bookingRepo.save(booking);

        redirectAttributes.addFlashAttribute("success",
                "Billet réservé avec succès. Pensez à confirmer la réservation dans une semaine ou elle expirera.");
        return "redirect:/bookings/";
    }

    @GetMapping("/bookings")
    public String browse(Model model, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            redirectAttributes.addFlashAttribute("error", "Vous devez être authentifié pour accéder à cette page.");
            return "redirect:/";
        }

        User cUser = userRepo.findByUsername(principal.getName());
        
        List<Booking> bList = bookingRepo.findByUserIdAndActive(cUser.getId(), true);
        deactiveExpiredEvents(bList);
        bList = bookingRepo.findByUserIdAndActive(cUser.getId(), true);
        
        model.addAttribute("bookings", bList);
        
        if (bList.size() == 0) {
            model.addAttribute("emptyList", true); 
        }

        model = fillModel(model, "Mes réservations - TicketMaster", principal);
        
        return "booking/browse";
    }

    private void deactiveExpiredEvents(List<Booking> bList) {
        if (bList.size() > 0) {
            for (Booking booking : bList) {
                if (booking.isActive() == false) {
                    continue;
                }
                if (booking.getExpiryDateTime().compareTo(LocalDateTime.now()) <= 0) {
                    booking.setActive(false);
                    bookingRepo.save(booking);
                }
            }
        }
    }

    @GetMapping("/bookings/{booking}/cancel")
    public String cancel(Booking booking, Model model, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            redirectAttributes.addFlashAttribute("error", "Vous devez être authentifié pour accéder à cette page.");
            return "redirect:/";
        }
        model = fillModel(model, "Annuler une réservation - TicketMaster", principal);

        User cUser = userRepo.findByUsername(principal.getName());
        if (booking.getUser().getId() != cUser.getId()) {
            redirectAttributes.addFlashAttribute("error", "Vous n'êtes pas le propriétaire de cette réservation.");
            return "redirect:/bookings";
        }
        
        booking.setActive(false);
        bookingRepo.save(booking);

        return "redirect:/bookings";
    }

    @GetMapping("/bookings/confirm")
    public String confirmGET(Model model, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            redirectAttributes.addFlashAttribute("error", "Vous devez être authentifié pour accéder à cette page.");
            return "redirect:/";
        }
        
        User cUser = userRepo.findByUsername(principal.getName());
        
        List<Booking> bList = bookingRepo.findByUserIdAndActive(cUser.getId(), true);
        deactiveExpiredEvents(bList);
        bList = bookingRepo.findByUserIdAndActive(cUser.getId(), true);
        model.addAttribute("bookings", bList);        
        if (bList.size() > 0) {
            model.addAttribute("nonZeroListSize", true);
        }

        Long total = (long) 0;
        for (Booking booking : bList) {
            Double addedValue = booking.getPriceAtReservation() * 100;
            total = total + addedValue.longValue();
        }
        Double totalWithDecimals = (double) total / 100;
        model.addAttribute("totalPrice", totalWithDecimals);

        model = fillModel(model, "Confirmer mes réservations - TicketMaster", principal);

        return "booking/confirm";
    }

    @PostMapping("/bookings/confirm")
    public String confirmPOST(Model model, Principal principal, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        if (principal == null) {
            redirectAttributes.addFlashAttribute("error", "Vous devez être authentifié pour accéder à cette page.");
            return "redirect:/";
        }

        String name = request.getParameter("name");
        if (name == null || !name.matches("^[a-zA-Z-']{2,}([ ]?[a-zA-Z-'])*$")) {
            redirectAttributes.addFlashAttribute("error", "Nom invalide.");
            return "redirect:/bookings/confirm";
        }
        
        String cardNumber = request.getParameter("cardNumber");
        cardNumber = cardNumber.replaceAll("\\s", "");
        if (cardNumber == null || !cardNumber.matches("^[0-9]{16}$")) {
            redirectAttributes.addFlashAttribute("error", "Numéro de carte invalide.");
            return "redirect:/bookings/confirm";
        }
        
        String cvc = request.getParameter("cvc");
        if (cvc == null || !cvc.matches("^[0-9]{3}$")) {
            redirectAttributes.addFlashAttribute("error", "CVC invalide.");
            return "redirect:/bookings/confirm";
        }
        
        String month = request.getParameter("month");
        if (month == null || !month.matches("^[0-9]{2}$")) {
            redirectAttributes.addFlashAttribute("error", "Mois invalide.");
            return "redirect:/bookings/confirm";
        }
        
        String year = request.getParameter("year");
        if (year == null || !year.matches("^20[0-9]{2}$")) {
            redirectAttributes.addFlashAttribute("error", "Année invalide.");
            return "redirect:/bookings/confirm";
        }

        User cUser = userRepo.findByUsername(principal.getName());

        List<Booking> bList = bookingRepo.findByUserIdAndActive(cUser.getId(), true);
        deactiveExpiredEvents(bList);
        bList = bookingRepo.findByUserIdAndActive(cUser.getId(), true);
        Long total = (long) 0;
        for (Booking booking : bList) {
            Double addedValue = booking.getPriceAtReservation() * 100;
            total = total + addedValue.longValue();
        }
        Double totalWithDecimals = (double) total / 100;

        // This is where the actual payment system would intervene if it existed
        System.out.println("L'utilisateur " + cUser.getUsername() + " a été débité " + totalWithDecimals + "€" );

        for (Booking booking : bList) {
            booking.setConfirmed(true);
            booking.setConfirmedAt(LocalDateTime.now());
            booking.setActive(false);
            bookingRepo.save(booking);

            Ticket ticket = new Ticket();
            ticket.setUser(cUser);
            ticket.setEvent(booking.getEvent());
            ticket.setCreatedAt(LocalDateTime.now());
            ticket.setTicketCkass(booking.getTicketCkass());
            ticket.setActive(true);
            ticketRepo.save(ticket);
        }

        redirectAttributes.addFlashAttribute("success", "Réservation(s) confirmée(s).");
        return "redirect:/bookings";
    }
}
