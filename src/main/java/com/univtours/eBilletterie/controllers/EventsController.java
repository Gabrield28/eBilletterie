package com.univtours.eBilletterie.controllers;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.univtours.eBilletterie.entities.Event;
import com.univtours.eBilletterie.repositories.EventRepository;
import com.univtours.eBilletterie.services.FileUploadService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class EventsController extends BaseController {

    private static String UPLOAD_DIR = "src/main/resources/static/images/uploaded/events/";

    @Autowired
    EventRepository eventRepo;

    @GetMapping("/admin/events")
    public String browse(Model model, Principal principal, RedirectAttributes redirectAttributes) {

        if (principal == null) {
            redirectAttributes.addFlashAttribute("error",
                    "Vous devez être un administrateur authentifié pour accéder à cette page.");
            return "redirect:/";
        }

        model = fillModel(model, "Tous les événements - TicketMaster Admin", principal, "inAdmin");

        if ((Boolean) model.getAttribute("hasRoleAdmin") == false) {
            redirectAttributes.addFlashAttribute("error",
                    "Vous n'avez pas les autorisations nécessaires pour accéder à cette page.");
            return "redirect:/";
        }
        
        List<Event> events = eventRepo.findAll();

        model.addAttribute("events", events);

        return "event/browse";
    }

    @GetMapping("/admin/events/{event}")
    public String read(Event event, Model model, Principal principal, RedirectAttributes redirectAttributes) {

        if (principal == null) {
            redirectAttributes.addFlashAttribute("error",
                    "Vous devez être un administrateur authentifié pour accéder à cette page.");
            return "redirect:/";
        }

        model = fillModel(model, "Détails d'un événement - TicketMaster Admin", principal, "inAdmin");

        if ((Boolean) model.getAttribute("hasRoleAdmin") == false) {
            redirectAttributes.addFlashAttribute("error",
                    "Vous n'avez pas les autorisations nécessaires pour accéder à cette page.");
            return "redirect:/";
        }

        if (event == null) {
            redirectAttributes.addFlashAttribute("error", "L'événement n'existe pas.");
            return "redirect:/admin";
        }

        model.addAttribute("event", event);

        return "event/read";
    }

    @GetMapping("/admin/events/create")
    public String create(Model model, Principal principal, RedirectAttributes redirectAttributes) {

        if (principal == null) {
            redirectAttributes.addFlashAttribute("error",
                    "Vous devez être un administrateur authentifié pour accéder à cette page.");
            return "redirect:/";
        }

        model = fillModel(model, "Ajouter un événement - TicketMaster Admin", principal, "inAdmin");

        if ((Boolean) model.getAttribute("hasRoleAdmin") == false) {
            redirectAttributes.addFlashAttribute("error",
                    "Vous n'avez pas les autorisations nécessaires pour accéder à cette page.");
            return "redirect:/";
        }

        Event eventToCreate = new Event();
        eventToCreate.setCapacity(500);
        eventToCreate.setMinimum_age_allowed(18);
        model.addAttribute("event", eventToCreate);

        return "event/create";
    }

    @PostMapping("/admin/events/create")
    public String createPost(Event event, @RequestParam("_image") MultipartFile multipartFile, Model model,
            Principal principal, RedirectAttributes redirectAttributes) {

        if (principal == null) {
            redirectAttributes.addFlashAttribute("error",
                    "Vous devez être un administrateur authentifié pour accéder à cette page.");
            return "redirect:/";
        }

        model = fillModel(model, "Ajouter un événement - TicketMaster Admin", principal, "inAdmin");

        if ((Boolean) model.getAttribute("hasRoleAdmin") == false) {
            redirectAttributes.addFlashAttribute("error",
                    "Vous n'avez pas les autorisations nécessaires pour accéder à cette page.");
            return "redirect:/";
        }

        if (!event.getTitle().matches("^[a-zA-Z-'0-9]{2,}([ ]?[a-zA-Z-'0-9])*$")) {
            redirectAttributes.addFlashAttribute("error", "Titre invalide.");
            return "redirect:/admin/events/create";
        }

        if (!event.getArtist().matches("^[a-zA-Z-'0-9]{2,}([ ]?[a-zA-Z-'0-9])*$")) {
            redirectAttributes.addFlashAttribute("error", "Artiste/Groupe invalide.");
            return "redirect:/admin/events/create";
        }

        if (!event.getAddress().matches("^[a-zA-Z0-9-_',. ]{2,}([ ]?[a-zA-Z-'0-9,._])*$")) {
            redirectAttributes.addFlashAttribute("error", "Addresse invalide.");
            return "redirect:/admin/events/create";
        }

        if (!event.getPostal_code().matches("^[0-9]{5}$")) {
            redirectAttributes.addFlashAttribute("error", "Code postale invalide.");
            return "redirect:/admin/events/create";
        }

        if (!event.getCity().matches("^[a-zA-Z-'0-9,._]{2,}([ ]?[a-zA-Z-'0-9,._])*$")) {
            redirectAttributes.addFlashAttribute("error", "Ville invalide.");
            return "redirect:/admin/events/create";
        }

        if (event.getCapacity() <= 0) {
            redirectAttributes.addFlashAttribute("error", "La capacité ne peut pas être négative.");
            return "redirect:/admin/events/create";
        }

        if (event.getMinimum_age_allowed() < 0) {
            redirectAttributes.addFlashAttribute("error", "L'âge minimum ne peut pas être négatif.");
            return "redirect:/admin/events/create";
        }

        if (multipartFile.getSize() == 0) {
            redirectAttributes.addFlashAttribute("error", "L'image ne peut pas être vide.");
            return "redirect:/admin/events/create";
        }

        event.setActive(true);

        event = eventRepo.save(event);

        if (multipartFile.getSize() != 0) {

            String fileName = event.getId() + " - " + StringUtils.cleanPath(multipartFile.getOriginalFilename());
            event.setImage(fileName);

            String uploadDir = UPLOAD_DIR;
            try {
                FileUploadService.saveFile(uploadDir, fileName, multipartFile);
            } catch (IOException e) {
                eventRepo.deleteById(event.getId());
                redirectAttributes.addFlashAttribute("error", e.getMessage());
                return "redirect:/admin/events/create";
            }

        }

        eventRepo.save(event);

        return "redirect:/admin";
    }

    @GetMapping("/admin/events/{event}/delete")
    public String delete(@PathVariable("event") Event event, Model model, Principal principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            redirectAttributes.addFlashAttribute("error",
                    "Vous devez être un administrateur authentifié pour accéder à cette page.");
            return "redirect:/";
        }

        model = fillModel(model, "Supprimer un événement - TicketMaster Admin", principal, "inAdmin");

        if ((Boolean) model.getAttribute("hasRoleAdmin") == false) {
            redirectAttributes.addFlashAttribute("error",
                    "Vous n'avez pas les autorisations nécessaires pour accéder à cette page.");
            return "redirect:/";
        }

        eventRepo.delete(event);

        return "redirect:/admin";
    }

    @GetMapping("/admin/events/{event}/update")
    public String update(@PathVariable("event") Event event, Model model, Principal principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            redirectAttributes.addFlashAttribute("error",
                    "Vous devez être un administrateur authentifié pour accéder à cette page.");
            return "redirect:/";
        }

        model = fillModel(model, "Modifier un événement - TicketMaster Admin", principal, "inAdmin");

        if ((Boolean) model.getAttribute("hasRoleAdmin") == false) {
            redirectAttributes.addFlashAttribute("error",
                    "Vous n'avez pas les autorisations nécessaires pour accéder à cette page.");
            return "redirect:/";
        }

        model.addAttribute("event", event);

        return "event/update";
    }

    @PostMapping("/admin/events/process_update")
    public String updatePost(Event event, Model model, Principal principal, RedirectAttributes redirectAttributes, HttpServletRequest request) {

        if (principal == null) {
            redirectAttributes.addFlashAttribute("error",
                    "Vous devez être un administrateur authentifié pour accéder à cette page.");
            return "redirect:/";
        }

        model = fillModel(model, "Modifier un événement - TicketMaster Admin", principal, "inAdmin");

        if ((Boolean) model.getAttribute("hasRoleAdmin") == false) {
            redirectAttributes.addFlashAttribute("error",
                    "Vous n'avez pas les autorisations nécessaires pour accéder à cette page.");
            return "redirect:/";
        }

        Optional<Event> modifiedEvent = eventRepo.findById(event.getId());

        if (modifiedEvent.isPresent()) {

            if (!event.getTitle().matches("^[a-zA-Z-'0-9]{2,}([ ]?[a-zA-Z-'0-9])*$")) {
                redirectAttributes.addFlashAttribute("error", "Titre invalide.");
                return "redirect:/admin/events/" + event.getId() + "/update";
            }

            if (!event.getArtist().matches("^[a-zA-Z-'0-9]{2,}([ ]?[a-zA-Z-'0-9])*$")) {
                redirectAttributes.addFlashAttribute("error", "Artiste/Groupe invalide.");
                return "redirect:/admin/events/create";
            }

            if (!event.getAddress().matches("^[a-zA-Z0-9-_',. ]{2,}([ ]?[a-zA-Z-'0-9,._])*$")) {
                redirectAttributes.addFlashAttribute("error", "Addresse invalide.");
                return "redirect:/admin/events/" + event.getId() + "/update";
            }

            if (!event.getPostal_code().matches("^[0-9]{5}$")) {
                redirectAttributes.addFlashAttribute("error", "Code postale invalide.");
                return "redirect:/admin/events/" + event.getId() + "/update";
            }

            if (!event.getCity().matches("^[a-zA-Z-'0-9,._]{2,}([ ]?[a-zA-Z-'0-9,._])*$")) {
                redirectAttributes.addFlashAttribute("error", "Ville invalide.");
                return "redirect:/admin/events/" + event.getId() + "/update";
            }

            if (event.getCapacity() <= 0) {
                redirectAttributes.addFlashAttribute("error", "La capacité ne peut pas être négative.");
                return "redirect:/admin/events/" + event.getId() + "/update";
            }

            if (event.getMinimum_age_allowed() < 0) {
                redirectAttributes.addFlashAttribute("error", "L'âge minimum ne peut pas être négatif.");
                return "redirect:/admin/events/" + event.getId() + "/update";
            }

            if (request.getParameter("_active").equals("yes")) {
                event.setActive(true);
            } else {
                event.setActive(false);
            }


            if (event.getImage() == null) {
                event.setImage(modifiedEvent.get().getImage());
            }

            eventRepo.save(event);
        } else {
            redirectAttributes.addFlashAttribute("error", "L'événement n'existe pas.");
            return "redirect:/admin";
        }

        return "redirect:/admin";
    }
}
