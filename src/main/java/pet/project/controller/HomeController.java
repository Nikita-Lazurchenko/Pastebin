package pet.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pet.project.database.entity.*;
import pet.project.dto.PasteCreateDto;
import pet.project.dto.PasteViewDto;
import pet.project.service.PasteService;
import pet.project.service.UserService;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final PasteService pasteService;
    private final UserService userService;
    private final static int PAGE_NUMBER = 0;
    private final static int PAGE_SIZE = 5;

    @GetMapping("/")
    public String home(Model model, Principal principal) {
        model.addAttribute("pasteCreation", new PasteCreateDto());
        model.addAttribute("category", DisplayEnum.getDisplayNames(Category.class));
        model.addAttribute("expiration", Expiration.getDescriptions());
        model.addAttribute("access",  DisplayEnum.getDisplayNames(Access.class));
        model.addAttribute("passwordProtect", DisplayEnum.getDisplayNames(PasswordProtect.class));

        Long id = userService.getUserIdByUsername(principal.getName());
        getAuthorAndUserPastes(model,id);

        return "home";
    }

    @PostMapping("/")
    public String savePaste(@ModelAttribute("pasteCreation") PasteCreateDto pasteCreateDto,
                            RedirectAttributes redirectAttributes,
                            Principal principal) {
        Long id = userService.getUserIdByUsername(principal.getName());

        Paste paste = pasteService.save(pasteCreateDto,id);

        redirectAttributes.addFlashAttribute("paste", paste);
        redirectAttributes.addAttribute("text",pasteCreateDto.getPaste());

        return "redirect:/"+paste.getPasteLink();
    }

    @GetMapping("/{hash}")
    public String showPaste(@PathVariable("hash") String hash,
                            @ModelAttribute("paste") Paste paste,
                            @ModelAttribute("text") String text,
                            Model model){
        PasteViewDto pasteViewDto = pasteService.findByHash(hash);

        model.addAttribute("title", pasteViewDto.getTitle());
        model.addAttribute("views", pasteViewDto.getViews());
        model.addAttribute("createdAtRelative", pasteViewDto.getCreatedAtRelative());
        model.addAttribute("text", pasteViewDto.getPaste());
        model.addAttribute("category", pasteViewDto.getCategory());
        model.addAttribute("tags", pasteViewDto.getTag());
        model.addAttribute("expiration",pasteViewDto.getExpiration());

        Long userId = pasteViewDto.getUserId();
        getAuthorAndUserPastes(model, userId);

        return "show-paste";
    }

    private void getAuthorAndUserPastes(Model model, Long userId) {
        List<PasteViewDto> authorPasteList = pasteService.getAuthorPastes(userId, PAGE_NUMBER, PAGE_SIZE);
        model.addAttribute("PublicAuthorPastes", authorPasteList);

        List<PasteViewDto> publicPasteList = pasteService.getPublicPastes(PAGE_NUMBER, PAGE_SIZE);
        model.addAttribute("PublicPastes", publicPasteList);
    }

}
