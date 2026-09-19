package com.secureworld.secure.controller;

import com.secureworld.secure.Entity.Employee;
import com.secureworld.secure.services.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public List<Employee> getAllEmployees() {
        logger.info("GET /api/employees called");
        return employeeService.getAllEmployees();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        logger.info("GET /api/employees/{} called", id);
        return employeeService.getEmployeeById(id)
                .map(employee -> {
                    logger.info("Employee found for id {}: {}", id, employee);
                    return ResponseEntity.ok(employee);
                })
                .orElseGet(() -> {
                    logger.warn("Employee not found for id {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        logger.info("POST /api/employees called with payload: {}", employee);
        Employee createdEmployee = employeeService.saveEmployee(employee);
        logger.info("Employee created successfully: {}", createdEmployee);
        return new ResponseEntity<>(createdEmployee, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        logger.info("PUT /api/employees/{} called with payload: {}", id, employee);
        Employee updatedEmployee = employeeService.updateEmployee(id, employee);
        logger.info("Employee updated successfully for id {}: {}", id, updatedEmployee);
        return ResponseEntity.ok(updatedEmployee);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        logger.info("DELETE /api/employees/{} called", id);
        employeeService.deleteEmployee(id);
        logger.info("Employee deleted successfully for id {}", id);
        return ResponseEntity.noContent().build();
    }
}
