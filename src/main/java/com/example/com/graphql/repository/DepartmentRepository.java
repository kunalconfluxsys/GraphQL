package com.example.com.graphql.repository;

import com.example.com.graphql.model.Department;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DepartmentRepository {

    private final JdbcTemplate jdbcTemplate;

    public DepartmentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Find all departments
    public List<Department> findAll() {
        return jdbcTemplate.query(
                "SELECT * FROM department",
                (rs, rowNum) -> new Department(
                        rs.getInt("id"),
                        rs.getString("name"),
                        null // Employees will be set later manually
                )
        );
    }

}
