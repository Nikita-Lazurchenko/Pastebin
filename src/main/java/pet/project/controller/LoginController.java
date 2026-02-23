package pet.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import pet.project.database.entity.Role;
import pet.project.dto.UserDto;
import pet.project.service.UserService;

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
}
