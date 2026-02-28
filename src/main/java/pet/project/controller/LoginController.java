package pet.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pet.project.dto.UserDto;
import pet.project.service.UserService;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class LoginController {
    private final UserService userService;

    @GetMapping("/login")
    public String login(Model model){
        model.addAttribute("user", new UserDto());

        return "login";
    }

    @PostMapping("/login")
    public String validateUser(@ModelAttribute("user") UserDto userDto){
        System.out.println(userDto);

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
        model.addAttribute("user", new UserDto());

        return "change-password";
    }

    @PostMapping("/login/change-password")
    public String changePassword(@ModelAttribute("user") UserDto userDto, Principal principal){
        userDto.setUsername(principal.getName());

        System.out.println("UserDto"+userDto);
        System.out.println(userService.updateUserPassword(userDto));

        return "redirect:/login";
    }
}
