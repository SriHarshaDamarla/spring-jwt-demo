package com.expensemanagement.controller;

import com.expensemanagement.bean.SignupForm;
import com.expensemanagement.bean.StatusResponse;
import com.expensemanagement.bean.UserDataDto;
import com.expensemanagement.entities.Customer;
import com.expensemanagement.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

import static com.expensemanagement.constants.AppConstants.LOGIN_URL;
import static com.expensemanagement.constants.AppConstants.SIGNUP_URL;

@RestController
@RequiredArgsConstructor
public class UserDataController {

    private final CustomerService customerService;

    @GetMapping("/my-data")
    public ResponseEntity<UserDataDto> getUserData(Authentication authentication) {
        String username = authentication.getName();
        Customer customer = customerService.loadCustomerByUsername(username);
        UserDataDto userDataDto = new UserDataDto();
        userDataDto.setUsername(customer.getUserId());
        userDataDto.setFirstName(customer.getFirstName());
        userDataDto.setLastName(customer.getLastName());
        return ResponseEntity
                .status(HttpStatus.OK.value())
                .body(userDataDto);
    }

    @PostMapping(LOGIN_URL)
    public ResponseEntity<StatusResponse> login(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity
                .ok(StatusResponse.of(HttpStatus.OK.value(), "User " + username + " logged in successfully!"));
    }

    @PostMapping(SIGNUP_URL)
    public ResponseEntity<StatusResponse> register(
            @Valid
            @RequestBody SignupForm form,
            BindingResult br
    ) {
        if(customerService.isUserPresentWithUsername(form.getUsername())) {
            br.addError(new FieldError(
                            "signupForm",
                            "username",
                            String.format("User %s already exists", form.getUsername())
                    )
            );
        }
        if(br.hasErrors()) {
           String errorMessage = br.getFieldErrors().stream()
                    .map(fieldError ->
                            fieldError.getField() +": " + fieldError.getDefaultMessage())
                    .collect(Collectors.joining(",\n"));
           return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE.value())
                   .body(StatusResponse.of(HttpStatus.NOT_ACCEPTABLE.value(),errorMessage));
        }
        customerService.registerUser(form);
        return ResponseEntity.ok(StatusResponse.of(HttpStatus.OK.value(), "User registered successfully"));
    }
}
