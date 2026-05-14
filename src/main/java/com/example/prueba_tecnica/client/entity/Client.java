package com.example.prueba_tecnica.client.entity;

import com.example.prueba_tecnica.account.entity.Account;
import com.example.prueba_tecnica.common.enums.IdentificationType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "clients",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_client_identification", columnNames = {"identification_type", "identification_number"}),
                @UniqueConstraint(name = "uk_client_email", columnNames = "email")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "identification_type", nullable = false, length = 30)
    private IdentificationType identificationType;

    @Column(name = "identification_number", nullable = false, length = 30)
    private String identificationNumber;

    @Column(nullable = false, length = 80)
    private String firstName;

    @Column(nullable = false, length = 80)
    private String lastName;

    @Column(nullable = false, length = 120)
    private String email;

    @Column(nullable = false)
    private LocalDate birthDate;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "client")
    private List<Account> accounts = new ArrayList<>();
}
