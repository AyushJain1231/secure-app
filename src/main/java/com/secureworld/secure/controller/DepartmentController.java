package com.secureworld.secure.controller;

import com.secureworld.secure.Entity.Department;
import com.secureworld.secure.services.DepartmentService;
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
@RequestMapping("/api/departments")
public class DepartmentController {

    private static final Logger logger = LoggerFactory.getLogger(DepartmentController.class);

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public List<Department> getAllDepartments() {
        logger.info("GET /api/departments called");
        return departmentService.getAllDepartments();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Department> getDepartmentById(@PathVariable Long id) {
        logger.info("GET /api/departments/{} called", id);
        return departmentService.getDepartmentById(id)
                .map(department -> {
                    logger.info("Department found for id {}: {}", id, department);
                    return ResponseEntity.ok(department);
                })
                .orElseGet(() -> {
                    logger.warn("Department not found for id {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    public ResponseEntity<Department> createDepartment(@RequestBody Department department) {
        logger.info("POST /api/departments called with payload: {}", department);
        Department createdDepartment = departmentService.saveDepartment(department);
        logger.info("Department created successfully: {}", createdDepartment);
        return new ResponseEntity<>(createdDepartment, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Department> updateDepartment(@PathVariable Long id, @RequestBody Department department) {
        logger.info("PUT /api/departments/{} called with payload: {}", id, department);
        Department updatedDepartment = departmentService.updateDepartment(id, department);
        logger.info("Department updated successfully for id {}: {}", id, updatedDepartment);
        return ResponseEntity.ok(updatedDepartment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        logger.info("DELETE /api/departments/{} called", id);
        departmentService.deleteDepartment(id);
        logger.info("Department deleted successfully for id {}", id);
        return ResponseEntity.noContent().build();
    }
}
