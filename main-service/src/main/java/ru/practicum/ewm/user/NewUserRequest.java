package ru.practicum.ewm.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@RequiredArgsConstructor
public class NewUserRequest {
    @Email(message = "Incorrect Email")
    @NotNull(message = "Email should not be null")
    private String email;

    @NotBlank(message = "Name should not be blank")
    @Size(min = 3, max = 256)
    private String name;
}
