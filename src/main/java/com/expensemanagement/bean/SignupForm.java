package com.expensemanagement.bean;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupForm {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    @Size(min=4)
    private String username;

    @NotBlank
    @Size(min=6, message = "Password must have a size of atleast 6 characters")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[!@#$&*])(?=.*[0-9]).+$",
            message = "Password must contain atleast one uppercase letter, " +
                    "one lowercase letter, one numeric digit and one special symbol from (!@#$&*)"
    )
    private String password;
}
