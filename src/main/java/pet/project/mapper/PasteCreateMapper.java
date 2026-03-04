package pet.project.mapper;

import org.springframework.stereotype.Component;
import pet.project.database.entity.Access;
import pet.project.database.entity.Category;
import pet.project.database.entity.Expiration;
import pet.project.database.entity.Paste;
import pet.project.dto.PasteCreateDto;


import java.time.LocalDateTime;
import java.util.Set;

@Component
public class PasteCreateMapper implements Mapper<Paste, PasteCreateDto> {

    @Override
    public Paste mapFrom(PasteCreateDto pasteCreateDto) {
        return Paste.builder()
                .category(Category.valueOf(pasteCreateDto.getCategory().toUpperCase()))
                .tags(Set.of(pasteCreateDto.getTag().split(" ")))
                .expiration(Expiration.fromDescription(pasteCreateDto.getExpiration()))
                .createdAt(LocalDateTime.now())
                .deletedAt(Expiration.fromDescription(pasteCreateDto.getExpiration()).getExpirationDate())
                .access(Access.valueOf(pasteCreateDto.getAccess().toUpperCase()))
                .password(pasteCreateDto.getPassword())
                .title(pasteCreateDto.getTitle())
                .build();
    }

}
