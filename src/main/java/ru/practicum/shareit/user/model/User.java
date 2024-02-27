package ru.practicum.shareit.user.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

/**
 * TODO Sprint add-controllers.
 */
@Data
@NoArgsConstructor
public class User {
    private Long id;
    @NotEmpty
    private String name;
    @Email
    private String email;
}
