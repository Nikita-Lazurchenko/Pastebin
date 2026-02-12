package pet.project.database.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum Expiration {
    NEVER("Never", now -> now.plusYears(100)),
    TEN_MINUTES("10 Minutes", now -> now.plusMinutes(10)),
    ONE_HOUR("1 Hour", now -> now.plusHours(1)),
    DAY("1 Day", now -> now.plusDays(1)),
    WEEK("1 Week / 7 Days", now -> now.plusWeeks(1)),
    MONTH("1 Month", now -> now.plusMonths(1)),
    YEAR("1 Year", now -> now.plusYears(1)),
    TEN_YEAR("10 Years", now -> now.plusYears(10));

    private final String description;
    private final UnaryOperator<LocalDateTime> expirationStrategy;

    private static final Map<String, Expiration> DESCRIPTION_MAP = Arrays.stream(values())
            .collect(Collectors.toMap(Expiration::getDescription, e -> e));

    public static Expiration fromDescription(String description) {
        return DESCRIPTION_MAP.getOrDefault(description, NEVER);
    }

    public LocalDateTime getExpirationDate() {
        return this.expirationStrategy.apply(LocalDateTime.now());
    }
}
