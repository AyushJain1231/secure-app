package com.secureworld.secure.services;

import com.secureworld.secure.Entity.Department;

import java.util.List;
import java.util.Optional;

public interface DepartmentService {

    List<Department> getAllDepartments();

    Optional<Department> getDepartmentById(Long id);

    Department saveDepartment(Department department);

    Department updateDepartment(Long id, Department department);

    void deleteDepartment(Long id);
}
