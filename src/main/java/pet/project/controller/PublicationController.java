package pet.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private final UserService userService;

    @GetMapping("/public-pastes/{page-number}")
    public String publicPastes(Model model, @PathVariable("page-number") int pageNumber) {
        List<PasteViewDto> pastePage = pasteService.getPublicPastes(pageNumber, PAGE_SIZE);

        model.addAttribute("PublicPastes", pastePage);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("hasNext", pastePage.size() > PAGE_SIZE);

        return "show-public-pastes";
    }

    @GetMapping("/author-pastes/{page-number}")
    public String authorPastes(Model model, Principal principal,
                               @PathVariable("page-number") int pageNumber) {
        Long id = userService.getUserIdByUsername(principal.getName());
        List<PasteViewDto> authorPasteList = pasteService.getAuthorPastes(id,pageNumber,PAGE_SIZE);
        model.addAttribute("PublicAuthorPastes", authorPasteList);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("hasNext", authorPasteList.size() > PAGE_SIZE);

        return "show-author-pastes";
    }
}
