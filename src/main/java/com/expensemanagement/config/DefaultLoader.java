package com.expensemanagement.config;

import com.expensemanagement.entities.Customer;
import com.expensemanagement.repositories.CustomerRepo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Component
@Scope("singleton")
@RequiredArgsConstructor
public class DefaultLoader {
    private final CustomerRepo customerRepo;

    @PostConstruct
    public void defaultUsers() {
        File file = new File(System.getProperty("user.home") + "/users.json");
        if(file.exists()) {
            try(FileReader reader = new FileReader(file)) {
                ObjectMapper mapper = new ObjectMapper();
                List<Customer> customers = mapper.readValue(reader, new TypeReference<>() {});
                customers.forEach(customer -> {
                    customer.setId(null);
                    customerRepo.save(customer);
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @PreDestroy
    public void destroy() throws Exception {
        System.out.println("Saving Customers!");
        List<Customer> customers = customerRepo.findAll();
        File file = new File(System.getProperty("user.home") + "/users.json");
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(new ObjectMapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(customers));
            fw.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
