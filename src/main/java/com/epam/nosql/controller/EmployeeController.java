package com.epam.nosql.controller;

import com.epam.nosql.model.Response;
import com.epam.nosql.model.Employee;
import com.epam.nosql.service.impl.EmployeeService;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
@Tag(name = "Employees", description = "Employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public List<Employee> getAllEmployees() {
        return employeeService.findAll();
    }

    @GetMapping("/{id}")
    public Employee getEmployeeById(@PathVariable String id) {
        return employeeService.getById(id);
    }

    @PostMapping("/{id}")
    public Response createEmployee(@PathVariable String id, @RequestBody Employee employee) {
        return employeeService.create(id, employee);
    }

    @DeleteMapping("/{id}")
    public Response deleteEmployee(@PathVariable String id) {
        return employeeService.delete(id);
    }

    @GetMapping("/search")
    public List<Employee> searchEmployees(@RequestParam String field,
                                          @RequestParam String value) {
        return employeeService.searchByField(field, value);
    }

    @GetMapping("/aggregate")
    public JsonNode performAggregation(@RequestParam String aggField,
                                       @RequestParam String metricType,
                                       @RequestParam String metricField) {
        return employeeService.aggregate(aggField, metricType, metricField);
    }
}
