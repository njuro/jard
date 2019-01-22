package com.github.njuro.jboard.models;

import com.github.njuro.jboard.models.enums.BanStatus;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "bans")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Ban {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ip;

    @Enumerated(EnumType.STRING)
    private BanStatus status;

    @OneToOne(targetEntity = Post.class)
    private Post post;

    @ManyToOne(targetEntity = User.class, optional = false)
    private User bannedBy;

    private String reason;

    @ManyToOne(targetEntity = User.class)
    private User unbannedBy;

    @Size(max = 1000)
    private String unbanReason;

    private LocalDateTime start;

    private LocalDateTime end;

}
