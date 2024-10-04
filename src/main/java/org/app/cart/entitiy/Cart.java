package org.app.cart.entitiy;


import jakarta.persistence.*;
import lombok.*;
import org.app.cart.enums.CartStatus;

import java.time.LocalDateTime;


@Entity
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Table(name = "cart")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private CartStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        lastUpdated = LocalDateTime.now();
        expiresAt = LocalDateTime.now().plusHours(2);
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }

}
