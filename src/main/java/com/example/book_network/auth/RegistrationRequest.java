package com.example.book_network.auth;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder

public class RegistrationRequest {

    @NotNull(message = "firstName is mandatory")
    @NotBlank(message = "firstName is mandatory")
    private String firstName;
    @NotNull(message = "lastName is mandatory")
    @NotBlank(message = "lastName is mandatory")
    private String lastName;
    @NotNull(message = "email is mandatory")
    @NotBlank(message = "email is mandatory")
    @Email(message = "Email is not formated")
    @Column(unique=true)
    private String email;

    @NotNull(message = "password is mandatory")
    @NotBlank(message = "password is mandatory")
    @Size(min = 8, message = "Password should be 8 characters long minimum")
    private String password;

}
