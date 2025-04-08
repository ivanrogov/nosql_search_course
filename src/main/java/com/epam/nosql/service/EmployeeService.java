package com.epam.nosql.service;

import com.epam.nosql.model.Employee;
import com.epam.nosql.model.Response;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public interface EmployeeService {
    List<Employee> findAll();

    Employee getById(String id);

    Response create(String id, Employee employee);

    Response delete(String id);

    List<Employee> searchByField(String fieldName, String fieldValue);

    JsonNode aggregate(String aggField, String metricType, String metricField);
}
