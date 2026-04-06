package pet.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pet.project.dto.ChangeUserPasswordDto;
import pet.project.dto.UserCreateDto;
import pet.project.service.UserService;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class LoginController {
    private final UserService userService;

    @GetMapping("/login")
    public String login(Model model){
        model.addAttribute("user", new UserCreateDto());

        return "login";
    }

    @GetMapping("/login/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password";
    }

    @GetMapping("/login/ott")
    public String ottLoginPage(@RequestParam(value = "token", required = false) String token, Model model) {
        model.addAttribute("token", token);
        return "ott-login";
    }

    @GetMapping("/login/change-password")
    public String changePasswordPage(Model model){
        model.addAttribute("user", new ChangeUserPasswordDto());

        return "change-password";
    }

    @PostMapping("/login/change-password")
    public String changePassword(@ModelAttribute("user") ChangeUserPasswordDto changeUserPasswordDto, Principal principal){
        changeUserPasswordDto.setUsername(principal.getName());

        String username = changeUserPasswordDto.getUsername();
        String newPassword = changeUserPasswordDto.getPassword();

        userService.updateUserPassword(username, newPassword);

        return "redirect:/login";
    }
}
