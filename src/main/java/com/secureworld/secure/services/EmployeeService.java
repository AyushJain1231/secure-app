package com.secureworld.secure.services;

import com.secureworld.secure.Entity.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeService {

    List<Employee> getAllEmployees();

    Optional<Employee> getEmployeeById(Long id);

    Employee saveEmployee(Employee employee);

    Employee updateEmployee(Long id, Employee employee);

    void deleteEmployee(Long id);
}
