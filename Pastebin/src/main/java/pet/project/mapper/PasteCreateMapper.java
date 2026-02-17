package pet.project.mapper;

import org.springframework.stereotype.Component;
import pet.project.database.entity.Access;
import pet.project.database.entity.Category;
import pet.project.database.entity.Expiration;
import pet.project.database.entity.Paste;
import pet.project.dto.PasteDto;


import java.time.LocalDateTime;
import java.util.Set;

@Component
public class PasteCreateMapper implements Mapper<Paste, PasteDto> {

    @Override
    public Paste mapFrom(PasteDto pasteDto) {
        return Paste.builder()
                .category(Category.valueOf(pasteDto.getCategory().toUpperCase()))
                .tags(Set.of(pasteDto.getTag().split(" ")))
                .createdAt(LocalDateTime.now())
                .deletedAt(Expiration.fromDescription(pasteDto.getExpiration()).getExpirationDate())
                .access(Access.valueOf(pasteDto.getAccess().toUpperCase()))
                .password(pasteDto.getPassword())
                .title(pasteDto.getTitle())
                .build();
    }

}
