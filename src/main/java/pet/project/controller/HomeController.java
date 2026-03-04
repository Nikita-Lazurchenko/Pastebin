package pet.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pet.project.database.entity.*;
import pet.project.database.repository.UserRepository;
import pet.project.dto.PasteCreateDto;
import pet.project.dto.PasteViewDto;
import pet.project.mapper.PasteViewMapper;
import pet.project.service.PasteService;
import pet.project.service.UserService;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final PasteService pasteService;
    private final UserRepository userRepository;
    private final PasteViewMapper pasteViewMapper;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("pasteCreation", new PasteCreateDto());

        List<String> category  = Arrays.stream(Category.values()).map(DisplayEnum::getDisplayName).collect(Collectors.toList());
        model.addAttribute("category", category);

        List<String> expiration = Arrays.stream(Expiration.values()).map(Expiration::getDescription).collect(Collectors.toList());
        model.addAttribute("expiration", expiration);

        List<String> access  = Arrays.stream(Access.values()).map(DisplayEnum::getDisplayName).collect(Collectors.toList());
        model.addAttribute("access", access);

        List<String> hasPasswordOrNo = Arrays.stream(PasswordProtect.values()).map(DisplayEnum::getDisplayName).collect(Collectors.toList());
        model.addAttribute("passwordProtect", hasPasswordOrNo);

        return "home";
    }

    @PostMapping("/")
    public String savePaste(@ModelAttribute("pasteCreation") PasteCreateDto pasteCreateDto,
                            RedirectAttributes redirectAttributes,
                            Principal principal) {
        System.out.println("Prin"+principal.getName());

        User user = userRepository.loadUserByUsername(principal.getName()).orElseThrow();

        System.out.println("User"+user);

        Paste paste = pasteService.save(pasteCreateDto,user.getId());

        System.out.println(paste.getPasteLink());
        System.out.println(paste.getGoogleFileId());

        redirectAttributes.addFlashAttribute("paste", paste);
        redirectAttributes.addAttribute("text",pasteCreateDto.getPaste());

        return "redirect:/"+paste.getPasteLink();
    }

    @GetMapping("/favicon.ico")
    @ResponseBody
    void returnNoFavicon() {
       //Убрать метод когда подключишь Spring Security
    }

    @GetMapping("/{hash}")
    public String showPaste(@PathVariable("hash") String hash,
                            @ModelAttribute("paste") Paste paste,
                            @ModelAttribute("text") String text,
                            Model model){
        PasteViewDto pasteViewDto;

        if(paste.getPasteLink() == null){
            pasteViewDto = pasteService.findByHash(hash);
        }else{
            pasteViewDto = pasteViewMapper.mapFrom(paste);
            pasteViewDto.setPaste(text);
        }

        model.addAttribute("title", pasteViewDto.getTitle());
        model.addAttribute("createdAtRelative", pasteViewDto.getCreatedAtRelative());
        model.addAttribute("text", pasteViewDto.getPaste());
        model.addAttribute("category", pasteViewDto.getCategory());
        model.addAttribute("tags", pasteViewDto.getTag());
        model.addAttribute("expiration",pasteViewDto.getExpiration());

        return "show-paste";
    }
}
