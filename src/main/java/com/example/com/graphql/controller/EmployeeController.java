package com.example.com.graphql.controller;

import com.example.com.graphql.model.AddEmployeeInput;
import com.example.com.graphql.model.Department;
import com.example.com.graphql.model.Employee;
import com.example.com.graphql.model.UpdateSalaryInput;
import com.example.com.graphql.repository.DepartmentRepository;
import com.example.com.graphql.repository.EmployeeRepository;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class EmployeeController {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public EmployeeController(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository, JdbcTemplate jdbcTemplate) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/employeeByName")
    public List<Employee> employeeByName(String employeeName) {
        return this.employeeRepository.getEmployeeByName(employeeName);
    }

    @MutationMapping
    public Employee addEmployee(@Argument @Nullable AddEmployeeInput addEmployeeInput) {
        if (addEmployeeInput == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }

        // SQL query to insert a new employee
        String sql = "INSERT INTO employee (name, salary, department_id) VALUES (?, ?, ?)";

        // Create a key holder to capture the generated ID
        KeyHolder keyHolder = new GeneratedKeyHolder();

        // Use JdbcTemplate's update method to insert the data and capture the generated key (ID)
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[] {"id"});  // "id" is the name of the auto-generated column
            ps.setString(1, addEmployeeInput.getName());
            ps.setString(2, addEmployeeInput.getSalary());
            ps.setInt(3, addEmployeeInput.getDepartmentId());
            return ps;
        }, keyHolder);  // Pass the keyHolder to capture the generated key

        // Retrieve the generated ID from the KeyHolder
        Number employeeId = keyHolder.getKey();

        if (employeeId == null) {
            throw new IllegalStateException("Employee ID was not generated after saving.");
        }

        // Return the employee object with the generated ID
        return new Employee(employeeId.intValue(), addEmployeeInput.getName(), addEmployeeInput.getSalary(), addEmployeeInput.getDepartmentId());
    }

    @MutationMapping
    public Employee updateSalary(@Argument UpdateSalaryInput updateSalaryInput) {
        // Update the salary for the employee with the given ID
        employeeRepository.updateSalary(updateSalaryInput.getEmployeeId(), updateSalaryInput.getSalary());

        // Fetch the updated employee object and check if it's null
        Employee updatedEmployee = employeeRepository.findById(updateSalaryInput.getEmployeeId());
        if (updatedEmployee == null) {
            throw new IllegalArgumentException("Employee not found");
        }

        return updatedEmployee;
    }

    @QueryMapping
    public List<Department> allDepartment() {
        return this.departmentRepository.findAll();
    }

    @BatchMapping
    public List<Employee> employees(List<Department> departments) {
        for (Department department : departments) {
            List<Employee> employees = this.employeeRepository.getAllEmployeeByDepartmentId(department.getId());
            department.setEmployees(employees);  // Set the employees to the department
        }
        return departments.stream().flatMap(department -> department.getEmployees().stream()).collect(Collectors.toList());
    }


}
