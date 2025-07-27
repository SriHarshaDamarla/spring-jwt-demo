package com.expensemanagement.bean;

import com.expensemanagement.entities.Customer;
import lombok.Data;

import java.util.List;

@Data
public class LoaderBean {
    private List<Customer> customers;
}
