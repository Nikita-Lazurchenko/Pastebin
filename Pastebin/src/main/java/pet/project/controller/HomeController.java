package pet.project.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import pet.project.database.entity.Expiration;
import pet.project.dto.PasteDto;
import pet.project.database.entity.Access;
import pet.project.database.entity.Category;
import pet.project.database.entity.PasswordProtect;
import pet.project.mapper.PasteCreateMapper;
import pet.project.service.PasteService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final PasteCreateMapper pasteCreateMapper;
    private final PasteService pasteService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("pasteCreation", new PasteDto());

        List<String> category  = Arrays.stream(Category.values()).map(e -> e.getDisplayName()).collect(Collectors.toList());
        model.addAttribute("Category", category);

        List<String> expiration = Arrays.stream(Expiration.values()).map(e -> e.getDescription()).collect(Collectors.toList());
        model.addAttribute("Expiration", expiration);

        List<String> access  = Arrays.stream(Access.values()).map(e -> e.getDisplayName()).collect(Collectors.toList());
        model.addAttribute("Access", access);

        List<String> hasPasswordOrNo = Arrays.stream(PasswordProtect.values()).map(e -> e.getDisplayName()).collect(Collectors.toList());
        model.addAttribute("PasswordProtect", hasPasswordOrNo);

        return "home";
    }

    @PostMapping("/")
    public String savePaste(@ModelAttribute("pasteCreation") PasteDto pasteDto){

        pasteService.save(pasteDto,1L);

        return "home";
    }
}
