package pet.project.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pet.project.database.entity.Paste;
import pet.project.database.entity.User;
import pet.project.database.repository.PasteRepository;
import pet.project.database.repository.UserRepository;
import pet.project.dto.PasteDto;
import pet.project.mapper.PasteCreateMapper;


@RequiredArgsConstructor
@Service
public class PasteService {
    private final PasteRepository pasteRepository;
    private final UserRepository userRepository;
    private final PasteCreateMapper pasteCreateMapper;

    @Transactional
    public Paste save(PasteDto pasteDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Paste paste = pasteCreateMapper.mapFrom(pasteDto);
        paste.setUser(user);

        return pasteRepository.save(paste);
    }
}
