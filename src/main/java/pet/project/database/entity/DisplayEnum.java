package pet.project.database.entity;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public interface DisplayEnum {
    String name();

    default String getDisplayName() {
        if (name() == null || name().isEmpty()) return "";
        return name().charAt(0) + name().substring(1).toLowerCase();
    }

    static <T extends Enum<T> & DisplayEnum> List<String> getDisplayNames(Class<T> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .map(DisplayEnum::getDisplayName)
                .collect(Collectors.toList());
    }
}
