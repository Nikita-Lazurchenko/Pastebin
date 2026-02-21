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
public class RegistrationController {
    private final UserService userService;

    @GetMapping("/registration")
    public String fillingRegistrationData(Model model){
        model.addAttribute("user", new UserDto());

        return "registration";
    }

    @PostMapping("/registration")
    public String saveRegistrationData(@ModelAttribute("user") UserDto userDto){
        userDto.setRole(String.valueOf(Role.USER));

        userService.save(userDto);

        return "redirect:/";
    }

}
