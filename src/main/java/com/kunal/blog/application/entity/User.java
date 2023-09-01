package com.kunal.blog.application.entity;

import jakarta.persistence.*;
import lombok.*;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;



@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

}
