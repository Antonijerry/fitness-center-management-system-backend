package com.fitnesscenter.user.entity;

import com.fitnesscenter.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseEntity {


    @Column(
            name = "first_name",
            nullable = false,
            length = 100
    )
    private String firstName;


    @Column(
            name = "last_name",
            nullable = false,
            length = 100
    )
    private String lastName;


    @Column(
            nullable = false,
            unique = true,
            length = 255
    )
    private String email;


    @Column(
            length = 30
    )
    private String phone;


    @Column(
            nullable = false,
            length = 255
    )
    private String password;


    @Column(
            nullable = false
    )
    private boolean enabled = true;


    @Column(
            name = "account_non_locked",
            nullable = false
    )
    private boolean accountNonLocked = true;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(
                    name = "user_id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id"
            )
    )
    private Set<Role> roles = new HashSet<>();
}