package com.univtours.eBilletterie.controllers;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.univtours.eBilletterie.entities.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController extends BaseController {

	@Autowired
	private PasswordEncoder encoder;

    @GetMapping("/admin/users")
    public String browse(Model model, Principal principal, RedirectAttributes redirectAttributes) {

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
        
        List<User> users = userRepo.findAll();

        model.addAttribute("users", users);

        return "user/browse";
    }

    @GetMapping("/admin/users/{user}")
    public String read(User user, Model model, Principal principal, RedirectAttributes redirectAttributes) {

        if (principal == null) {
            redirectAttributes.addFlashAttribute("error",
                    "Vous devez être un administrateur authentifié pour accéder à cette page.");
            return "redirect:/";
        }

        model = fillModel(model, "Détails d'un utilisateur - TicketMaster Admin", principal, "inAdmin");

        if ((Boolean) model.getAttribute("hasRoleAdmin") == false) {
            redirectAttributes.addFlashAttribute("error",
                    "Vous n'avez pas les autorisations nécessaires pour accéder à cette page.");
            return "redirect:/";
        }

        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "L'utilisateur n'existe pas.");
            return "redirect:/admin";
        }

        model.addAttribute("user", user);

        return "user/read";
    }

	@GetMapping("/register")
	public String showRegistrationForm(Model model, Principal principal, HttpServletRequest request) {
		User userToRegister = new User();

        if (principal != null) {
            try {
                request.logout();
            } catch (ServletException e) {
                System.out.println("Erreur lors de la déconnexion.");
                return "redirect:/";
            }
        }

		model = fillModel(model, "Inscription", null);
		model.addAttribute("user", userToRegister);

		return "register";
	}

	@PostMapping("/process_register")
	public String processRegister(User user, Model model, Principal principal, HttpServletRequest request,
			RedirectAttributes redirectAttributes) {
		if (!user.getLast_name().matches("^[a-zA-Z-']{2,}([ ]?[a-zA-Z-'])*$")) {
			redirectAttributes.addFlashAttribute("error", "Nom invalide.");
			return "redirect:/register";
		}
		if (!user.getFirst_name().matches("^[a-zA-Z-']{2,}([ ]?[a-zA-Z-'])*$")) {
			redirectAttributes.addFlashAttribute("error", "Prénom invalide.");
			return "redirect:/register";
		}
        if (user.getAge() < 0) {
            redirectAttributes.addFlashAttribute("error", "L'âge ne peut pas être négatif.");
            return "redirect:/register";
        }
		if (!user.getUsername().matches("^[a-zA-Z-'0-9_]{4,}$")) {
			redirectAttributes.addFlashAttribute("error", "Nom d'utilisateur invalide.");
			return "redirect:/register";
		}
		if (userRepo.findByUsername(user.getUsername()) != null) {
			redirectAttributes.addFlashAttribute("error", "Ce nom d'utilisateur est déjà pris.");
			return "redirect:/register";
		}
		if (!user.getEmail().matches("^[a-zA-Z][a-zA-Z0-9-_.]*[@][a-zA-Z][a-zA-Z0-9-_.]+[.][a-zA-Z]{2,}$")) {
			redirectAttributes.addFlashAttribute("error", "L'email ne peut pas être vide.");
			return "redirect:/register";
		}
		if (userRepo.findByEmail(user.getEmail()) != null) {
			redirectAttributes.addFlashAttribute("error", "Cet addresse email est déjà utilisée.");
			return "redirect:/register";
		}
		String rawPassword = user.getPassword();
		String rawPasswordConfirmation = request.getParameter("password_confirmation");
		if (!rawPassword.equals(rawPasswordConfirmation)) {
			redirectAttributes.addFlashAttribute("error", "Les mots de passe saisis doivent correspondre.");
			return "redirect:/register";
		}
		if (!rawPassword.matches("^[\\S0-9-_.@*+\\/]{6,}$")) {
			redirectAttributes.addFlashAttribute("error", "Mot de passe invalide.");
			return "redirect:/register";
		}
		String encodedPassword = encoder.encode(rawPassword);
		user.setPassword(encodedPassword);
		user.setRoles("ROLE_USER");

		// If no users exist in the database, make the newly created one an admin
		if (userRepo.findAll().isEmpty()) {
			user.addRole("ROLE_ADMIN");
		}

		user.setActive(true);
		userRepo.save(user);
		try {
			request.login(user.getUsername(), rawPassword);
		} catch (ServletException e) {
			System.out.println("Erreur lors de la connexion.");
			return "redirect:/";
		}

		return "redirect:/";
	}

	@GetMapping("/login")
	public String login(Model model, Principal principal) {
		model = fillModel(model, "Connexion", principal);

		return "login";
	}

	@GetMapping("/logout")
	public String logout(Model model, Principal principal) {
		model = fillModel(model, "Déconnexion", principal);

		return "logout";
	}

    @GetMapping("/admin/users/{user}/update")
    public String update(@PathVariable("user") User user, Model model, Principal principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            redirectAttributes.addFlashAttribute("error",
                    "Vous devez être un administrateur authentifié pour accéder à cette page.");
            return "redirect:/";
        }

        model = fillModel(model, "Modifier un utilisateur - TicketMaster Admin", principal, "inAdmin");

        if ((Boolean) model.getAttribute("hasRoleAdmin") == false) {
            redirectAttributes.addFlashAttribute("error",
                    "Vous n'avez pas les autorisations nécessaires pour accéder à cette page.");
            return "redirect:/";
        }

        model.addAttribute("user", user);

        return "user/update";
    }

    @PostMapping("/admin/users/process_update")
    public String updatePost(User user, Model model, Principal principal, RedirectAttributes redirectAttributes, HttpServletRequest request) {

        if (principal == null) {
            redirectAttributes.addFlashAttribute("error",
                    "Vous devez être un administrateur authentifié pour accéder à cette page.");
            return "redirect:/";
        }

        model = fillModel(model, "Modifier un utilisateur - TicketMaster Admin", principal, "inAdmin");

        if ((Boolean) model.getAttribute("hasRoleAdmin") == false) {
            redirectAttributes.addFlashAttribute("error",
                    "Vous n'avez pas les autorisations nécessaires pour accéder à cette page.");
            return "redirect:/";
        }

        Optional<User> modifiedUser = userRepo.findById(user.getId());

        if (modifiedUser.isPresent()) {

            User mUser = modifiedUser.get();

            if (!user.getLast_name().matches("^[a-zA-Z-']{2,}([ ]?[a-zA-Z-'])*$")) {
                redirectAttributes.addFlashAttribute("error", "Nom invalide.");
                return "redirect:/admin/users/" + user.getId() + "/update";
            }
            if (!user.getFirst_name().matches("^[a-zA-Z-']{2,}([ ]?[a-zA-Z-'])*$")) {
                redirectAttributes.addFlashAttribute("error", "Prénom invalide.");
                return "redirect:/admin/users/" + user.getId() + "/update";
            }
            if (user.getAge() < 0) {
                redirectAttributes.addFlashAttribute("error", "L'âge ne peut pas être négatif.");
                return "redirect:/admin/users/" + user.getId() + "/update";
            }
            if (request.getParameter("_roles").equals("ROLE_USER, ROLE_ADMIN")) {
                user.setRoles("ROLE_USER, ROLE_ADMIN");
            } else {
                user.setRoles("ROLE_USER");
            }
            if (request.getParameter("_blocked").equals("yes")) {
                user.setActive(false);
            } else {
                user.setActive(true);
            }

            mUser.setLast_name(user.getLast_name());
            mUser.setFirst_name(user.getFirst_name());
            mUser.setAge(user.getAge());
            mUser.setRoles(user.getRoles());
            mUser.setActive(user.isActive());

            userRepo.save(mUser);
        } else {
            redirectAttributes.addFlashAttribute("error", "L'utilisateur n'existe pas.");
            return "redirect:/admin";
        }

        return "redirect:/admin";
    }

    @GetMapping("/profile")
    public String profile(Model model, Principal principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            redirectAttributes.addFlashAttribute("error",
                    "Vous devez être authentifié pour accéder à cette page.");
            return "redirect:/";
        }

        model = fillModel(model, "Modifier mon profil - TicketMaster", principal);

        User user = userRepo.findByUsername(principal.getName());

        model.addAttribute("user", user);

        return "user/profile";
    }

    @PostMapping("/profile/update")
    public String profilePost(User user, Model model, Principal principal, RedirectAttributes redirectAttributes, HttpServletRequest request) {

        if (principal == null) {
            redirectAttributes.addFlashAttribute("error",
                    "Vous devez être authentifié pour accéder à cette page.");
            return "redirect:/";
        }

        model = fillModel(model, "Modifier mon profil - TicketMaster", principal);

        User modifiedUser = userRepo.findByUsername(principal.getName());

        if (modifiedUser != null) {

            User mUser = modifiedUser;

            if (!user.getLast_name().matches("^[a-zA-Z-']{2,}([ ]?[a-zA-Z-'])*$")) {
                redirectAttributes.addFlashAttribute("error", "Nom invalide.");
                return "redirect:/profile";
            }
            if (!user.getFirst_name().matches("^[a-zA-Z-']{2,}([ ]?[a-zA-Z-'])*$")) {
                redirectAttributes.addFlashAttribute("error", "Prénom invalide.");
                return "redirect:/profile";
            }
            if (user.getAge() < 0) {
                redirectAttributes.addFlashAttribute("error", "L'âge ne peut pas être négatif.");
                return "redirect:/profile";
            }

            mUser.setLast_name(user.getLast_name());
            mUser.setFirst_name(user.getFirst_name());
            mUser.setAge(user.getAge());

            userRepo.save(mUser);
        } else {
            redirectAttributes.addFlashAttribute("error", "L'utilisateur n'existe pas.");
            return "redirect:/";
        }

        redirectAttributes.addFlashAttribute("success", "Données modifiées avec succès.");
        return "redirect:/";
    }
}
