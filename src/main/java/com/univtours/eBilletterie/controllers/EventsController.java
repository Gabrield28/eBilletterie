package com.univtours.eBilletterie.controllers;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.univtours.eBilletterie.entities.Event;
import com.univtours.eBilletterie.entities.Rate;
import com.univtours.eBilletterie.repositories.EventRepository;
import com.univtours.eBilletterie.repositories.RateRepository;
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

    @Autowired
    RateRepository rateRepo;

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

        List<Rate> rates = rateRepo.findByEventId(event.getId());

        model.addAttribute("rates", rates);

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
            Principal principal, RedirectAttributes redirectAttributes, HttpServletRequest request) {

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

        if (!request.getParameter("_type").matches("^[123]$")) {
            redirectAttributes.addFlashAttribute("error", "Type invalide.");
            return "redirect:/admin/events/create";
        }

        if (!request.getParameter("_date").matches("^20[2-3][0-9]-[0-1][0-9]-[0-3][0-9]$")) {
            redirectAttributes.addFlashAttribute("error", "Date invalide.");
            return "redirect:/admin/events/create";
        }

        if (!request.getParameter("_time").matches("^[0-2][0-9]:[0-5][0-9]$")) {
            redirectAttributes.addFlashAttribute("error", "Heure invalide.");
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

        event.setType(Integer.parseInt(request.getParameter("_type")));

        String datetime = request.getParameter("_date") + " " + request.getParameter("_time");
        String pattern = "yyyy-MM-dd HH:mm";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime localDateTime = LocalDateTime.parse(datetime, formatter);
        event.setDatetime(localDateTime);

        event = eventRepo.save(event);

        if (multipartFile.getSize() != 0) {

            String fileName = event.getId() + " - " + StringUtils.cleanPath(multipartFile.getOriginalFilename());
            fileName = fileName.replaceAll("\\s", "");
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
    public String updatePost(Event event, @RequestParam("_image") MultipartFile multipartFile, Model model,
            Principal principal, RedirectAttributes redirectAttributes, HttpServletRequest request) {

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
                return "redirect:/admin/events/" + event.getId() + "/update";
            }

            if (!request.getParameter("_type").matches("^[123]$")) {
                redirectAttributes.addFlashAttribute("error", "Type invalide.");
                return "redirect:/admin/events/" + event.getId() + "/update";
            }

            if (!request.getParameter("_date").matches("^20[2-3][0-9]-[0-1][0-9]-[0-3][0-9]$")) {
                redirectAttributes.addFlashAttribute("error", "Date invalide.");
                return "redirect:/admin/events/" + event.getId() + "/update";
            }

            if (!request.getParameter("_time").matches("^[0-2][0-9]:[0-5][0-9]$")) {
                redirectAttributes.addFlashAttribute("error", "Heure invalide.");
                return "redirect:/admin/events/" + event.getId() + "/update";
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

            event.setType(Integer.parseInt(request.getParameter("_type")));

            String datetime = request.getParameter("_date") + " " + request.getParameter("_time");
            String pattern = "yyyy-MM-dd HH:mm";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            LocalDateTime localDateTime = LocalDateTime.parse(datetime, formatter);
            event.setDatetime(localDateTime);

            if (multipartFile.getSize() != 0) {

                String fileName = event.getId() + " - " + StringUtils.cleanPath(multipartFile.getOriginalFilename());
                fileName = fileName.replaceAll("\\s", "");
                event.setImage(fileName);

                String uploadDir = UPLOAD_DIR;
                try {
                    FileUploadService.saveFile(uploadDir, fileName, multipartFile);
                } catch (IOException e) {
                    eventRepo.deleteById(event.getId());
                    redirectAttributes.addFlashAttribute("error", e.getMessage());
                    return "redirect:/admin/events/create";
                }

            } else {
                event.setImage(modifiedEvent.get().getImage());
            }

            eventRepo.save(event);
        } else {
            redirectAttributes.addFlashAttribute("error", "L'événement n'existe pas.");
            return "redirect:/admin";
        }

        return "redirect:/admin";
    }

    @GetMapping("/events")
    public String browsePublic(Model model, Principal principal, HttpServletRequest request, RedirectAttributes redirectAttributes) {

        model = fillModel(model, "Evénements - TicketMaster", principal);

        List<Event> events = eventRepo.findAll();

        String typeFilter = request.getParameter("typeFilter");
        if (typeFilter != null) {
            switch (typeFilter) {
            case "concerts":
                events = filterByType(events, 1);
                model.addAttribute("filterByConcerts", true);
                break;
            case "sports":
                events = filterByType(events, 2);
                model.addAttribute("filterBySports", true);
                break;
            case "arts":
                events = filterByType(events, 3);
                model.addAttribute("filterByArts", true);
                break;
            default:
                typeFilter = "all";
                model.addAttribute("filterByAll", true);
                break;
            }
        } else {
            model.addAttribute("filterByAll", true);
        }

        String qType = request.getParameter("qType");
        String q = request.getParameter("q");
        if (qType != null && q != null) {
            if (!q.isBlank()) {
                switch (qType) {
                    case "date":
                        model.addAttribute("searchByDate", true);
                        if (!q.matches("[0-3][0-9]-[0-1][0-9]-20[0-9][0-9]")) {
                            redirectAttributes.addFlashAttribute("error", "Date invalide.");
                            return "redirect:/events";                            
                        }
                        events = filterByDate(events, q);
                        break;
                    case "locale":
                        model.addAttribute("searchByLocale", true);
                        events = filterByLocale(events, q);
                        break;
                    default:
                        model.addAttribute("searchByTitle", true);
                        events = filterByTitle(events, q);
                        break;
                }
                model.addAttribute("q", q);
            }
        }

        model.addAttribute("events", events);

        return "event/browse-public";
    }

    private List<Event> filterByType(List<Event> events, Integer targetType) {
        ListIterator<Event> iterator = events.listIterator();
        while (iterator.hasNext()) {
            Event event = iterator.next();
            Integer type = event.getType();
            if (!type.equals(targetType)) {
                iterator.remove();
            }
        } 
        return events;
    }

    private List<Event> filterByTitle(List<Event> events, String targetTitle) {
        ListIterator<Event> iterator = events.listIterator();
        while (iterator.hasNext()) {
            Event event = iterator.next();
            Boolean flag1 = event.getTitle().toLowerCase().contains(targetTitle.toLowerCase());
            Boolean flag2 = targetTitle.toLowerCase().contains(event.getTitle().toLowerCase());
            if (flag1 == false && flag2 == false) {
                iterator.remove();
            }
        } 
        return events;
    }

    private List<Event> filterByDate(List<Event> events, String targetDate) {
        String pattern = "dd-MM-yyyy";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDate targetLocalDate = LocalDate.parse(targetDate, formatter);
        Integer targetYear = targetLocalDate.getYear();
        Integer targetMonth = targetLocalDate.getMonthValue();
        Integer targetDay = targetLocalDate.getDayOfMonth();

        ListIterator<Event> iterator = events.listIterator();
        while (iterator.hasNext()) {
            Event event = iterator.next();

            LocalDateTime date = event.getDatetime();
            Integer year = date.getYear();
            Integer month = date.getMonthValue();
            Integer day = date.getDayOfMonth();

            Boolean flag1 = (year.equals(targetYear));
            Boolean flag2 = (month.equals(targetMonth));
            Boolean flag3 = (day.equals(targetDay));
            if (flag1 == false || flag2 == false || flag3 == false) {
                iterator.remove();
            }
        }
        return events;
    }

    private List<Event> filterByLocale(List<Event> events, String targetLocale) {
        ListIterator<Event> iterator = events.listIterator();
        while (iterator.hasNext()) {
            Event event = iterator.next();
            Boolean flag1 = event.getCity().toLowerCase().contains(targetLocale.toLowerCase());
            Boolean flag2 = targetLocale.toLowerCase().contains(event.getCity().toLowerCase());
            if (flag1 == false && flag2 == false) {
                iterator.remove();
            }
        } 
        return events;
    }

    @GetMapping("/events/{event}")
    public String indexPublic(Event event, Model model, Principal principal) {

        model = fillModel(model, event.getTitle() + " - TicketMaster", principal);

        model = getRatesForUser(model, event, principal);

        return "event/read-public";
    }
}
