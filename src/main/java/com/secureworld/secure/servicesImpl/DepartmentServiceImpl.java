package com.secureworld.secure.servicesImpl;

import com.secureworld.secure.Entity.Department;
import com.secureworld.secure.exception.DepartmentNotFoundException;
import com.secureworld.secure.repository.DepartmentRepository;
import com.secureworld.secure.services.DepartmentService;
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
public class DepartmentServiceImpl implements DepartmentService {

    private static final Logger logger = LoggerFactory.getLogger(DepartmentServiceImpl.class);

    private final DepartmentRepository departmentRepository;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    @Cacheable(cacheNames = "departments", sync = true)
    public List<Department> getAllDepartments() {
        logger.info("Fetching all departments");
        return departmentRepository.findAll();
    }

    @Override
    @Cacheable(cacheNames = "departmentById", sync = true)
    public Optional<Department> getDepartmentById(Long id) {
        logger.info("Fetching department by id: {}", id);
        return departmentRepository.findById(id);
    }

    @Override
    @Caching(
            put = @CachePut(cacheNames = "departmentById", key = "#result.id"),
            evict = @CacheEvict(cacheNames = "departments", allEntries = true)
    )
    public Department saveDepartment(Department department) {
        logger.info("Saving department: {}", department);
        return departmentRepository.save(department);
    }

    @Override
    @Caching(
            put = @CachePut(cacheNames = "departmentById", key = "#id"),
            evict = @CacheEvict(cacheNames = "departments", allEntries = true)
    )
    public Department updateDepartment(Long id, Department department) {
        logger.info("Updating department with id: {}", id);

        Department existingDepartment = departmentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Department not found for update with id: {}", id);
                    return new DepartmentNotFoundException(id);
                });

        existingDepartment.setName(department.getName());
        existingDepartment.setLocation(department.getLocation());

        Department updatedDepartment = departmentRepository.save(existingDepartment);
        logger.info("Department updated successfully: {}", updatedDepartment);
        return updatedDepartment;
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(cacheNames = "departmentById", key = "#id"),
                    @CacheEvict(cacheNames = "departments", allEntries = true)
            }
    )
    public void deleteDepartment(Long id) {
        logger.info("Deleting department with id: {}", id);

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Department not found for deletion with id: {}", id);
                    return new DepartmentNotFoundException(id);
                });

        departmentRepository.delete(department);
        logger.info("Department deleted successfully with id: {}", id);
    }
}
