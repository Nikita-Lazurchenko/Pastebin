package pet.project.database.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@ToString(exclude = {"user"})
@EqualsAndHashCode(of="pasteLink")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Component
@Entity
@Table(schema = "pastebin", name = "pastes")
public class Paste implements BaseEntity<Long>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "paste_link", nullable = false, unique = true)
    private String pasteLink;

    @Enumerated(EnumType.STRING)
    private Category category;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "tags", columnDefinition = "text[]")
    private Set<String> tags;

    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    @Enumerated(EnumType.STRING)
    private Access access;

    private String password;

    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public void setUser(User user){
        this.user = user;
        user.getPastes().add(this);
    }
}
