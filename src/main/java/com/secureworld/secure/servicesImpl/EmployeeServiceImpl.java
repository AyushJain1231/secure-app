package com.secureworld.secure.servicesImpl;

import com.secureworld.secure.exception.EmployeeNotFoundException;
import com.secureworld.secure.Entity.Employee;
import com.secureworld.secure.repository.EmployeeRepository;
import com.secureworld.secure.services.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    @Cacheable(cacheNames = "employees", sync = true)
    public List<Employee> getAllEmployees() {
        logger.info("Fetching all employees");
        return employeeRepository.findAll();
    }

    @Override
    @Cacheable(cacheNames = "employeeById", sync = true)
    public Optional<Employee> getEmployeeById(Long id) {
        logger.info("Fetching employee by id: {}", id);
        return employeeRepository.findById(id);
    }

    @Override
    @Caching(
            put = @CachePut(cacheNames = "employeeById", key = "#result.id"),
            evict = @CacheEvict(cacheNames = "employees", allEntries = true)
    )
    public Employee saveEmployee(Employee employee) {
        logger.info("Saving employee: {}", employee);
        return employeeRepository.save(employee);
    }

    @Override
    @Caching(
            put = @CachePut(cacheNames = "employeeById", key = "#id"),
            evict = @CacheEvict(cacheNames = "employees", allEntries = true)
    )
    public Employee updateEmployee(Long id, Employee employee) {
        logger.info("Updating employee with id: {}", id);

        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Employee not found for update with id: {}", id);
                    return new EmployeeNotFoundException(id);
                });

        existingEmployee.setName(employee.getName());
        existingEmployee.setSalary(employee.getSalary());
        existingEmployee.setDepartmentId(employee.getDepartmentId());

        Employee updatedEmployee = employeeRepository.save(existingEmployee);
        logger.info("Employee updated successfully: {}", updatedEmployee);
        return updatedEmployee;
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(cacheNames = "employeeById", key = "#id"),
                    @CacheEvict(cacheNames = "employees", allEntries = true)
            }
    )
    public void deleteEmployee(Long id) {
        logger.info("Deleting employee with id: {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Employee not found for deletion with id: {}", id);
                    return new EmployeeNotFoundException(id);
                });
        employeeRepository.delete(employee);
        logger.info("Employee deleted successfully with id: {}", id);
    }
}
