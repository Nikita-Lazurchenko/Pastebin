package pet.project.mapper;

import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.stereotype.Component;
import pet.project.database.entity.Paste;
import pet.project.dto.PasteViewDto;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class PasteViewMapper implements Mapper<PasteViewDto, Paste>{
    private final PrettyTime prettyTime = new PrettyTime(new Locale("ru"));
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public PasteViewDto mapFrom(Paste paste) {
        return PasteViewDto.builder()
                .category(paste.getCategory().getDisplayName())
                .tag(String.join(", ",paste.getTags()))
                .expiration(paste.getExpiration().getDescription())
                .createdAtRelative(prettyTime.format(paste.getCreatedAt()))
                .title(paste.getTitle())
                .build();
    }
}
