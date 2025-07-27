package com.expensemanagement.service;

import com.expensemanagement.bean.SignupForm;
import com.expensemanagement.entities.Customer;
import com.expensemanagement.repositories.CustomerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomerService {

    private final CustomerRepo customerRepo;
    private final PasswordEncoder passwordEncoder;

    public void registerUser(SignupForm form) {
        Customer customer = new Customer();
        customer.setUserId(form.getUsername());
        customer.setFirstName(form.getFirstName());
        customer.setLastName(form.getLastName());
        customer.setRole("user");
        customer.setPassword(passwordEncoder.encode(form.getPassword()));

        customerRepo.save(customer);
    }

    public boolean isUserPresentWithUsername(String userId) {
        return customerRepo.existsByUserId(userId);
    }

    public Customer loadCustomerByUsername(String username) {
        return customerRepo.findByUserId(username).orElseThrow();
    }

    public void saveCustomer(Customer customer) {
        customerRepo.save(customer);
    }
}
