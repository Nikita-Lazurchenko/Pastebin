package pet.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pet.project.database.entity.Paste;
import pet.project.database.entity.User;
import pet.project.database.repository.UserRepository;
import pet.project.dto.PasteViewDto;
import pet.project.service.PasteService;
import pet.project.service.UserService;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class PublicationController {
    private final static int PAGE_SIZE = 10;
    private final PasteService pasteService;
    private final UserRepository userRepository;

    @GetMapping("/public-pastes/{page-number}")
    public String publicPastes(Model model, @PathVariable("page-number") int pageNumber) {
        List<PasteViewDto> pastePage = pasteService.getPublicPastes(pageNumber, PAGE_SIZE);
        System.out.println(pastePage);

        model.addAttribute("PublicPastes", pastePage);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("hasNext", pastePage.size() > PAGE_SIZE);

        return "show-public-pastes";
    }

    @GetMapping("/author-pastes/{page-number}")
    public String authorPastes(Model model, Principal principal,
                               @PathVariable("page-number") int pageNumber) {
        User user = userRepository.loadUserByUsername(principal.getName()).orElseThrow();
        List<PasteViewDto> authorPasteList = pasteService.getAuthorPastes(user.getId(),pageNumber,PAGE_SIZE);
        model.addAttribute("PublicAuthorPastes", authorPasteList);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("hasNext", authorPasteList.size() > PAGE_SIZE);

        return "show-author-pastes";
    }
}
