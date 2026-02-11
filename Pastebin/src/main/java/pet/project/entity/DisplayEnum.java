package pet.project.entity;

public interface DisplayEnum {
    String name();

    default String getDisplayName() {
        if (name() == null || name().isEmpty()) return "";
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}
