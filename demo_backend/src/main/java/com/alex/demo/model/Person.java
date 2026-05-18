package com.alex.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
@Table(name = "person")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "password", nullable = false)
    private String password;

    private Integer age;

    @Column(name = "email", nullable = false, unique = true)
    private String email;
/// /////////////////////////////////////////////////////////////////

    @Column(name = "role")
    private String role = "CUSTOMER";

    //un om are mai multe comenzi
    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
    @JsonIgnore
    private java.util.List<Order> orders;
}
