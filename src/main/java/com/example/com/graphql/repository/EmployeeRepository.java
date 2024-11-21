package com.example.com.graphql.repository;

import com.example.com.graphql.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public class EmployeeRepository {

    private final JdbcTemplate jdbcTemplate;

    public EmployeeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Save a new employee
    public int save(Employee employee) {
        String sql = "INSERT INTO employee (name, salary, department_id) VALUES (?, ?, ?)";
        return jdbcTemplate.update(sql, employee.getName(), employee.getSalary(), employee.getDepartmentId());
    }

    // Find employees by name (using PreparedStatementCreator to avoid deprecated query())
    public List<Employee> getEmployeeByName(String name) {
        String sql = "SELECT * FROM employee WHERE name = ?";

        // Using PreparedStatementCreator for custom query execution
        return jdbcTemplate.query(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, name); // setting the parameter dynamically
                return ps;
            }
        }, new RowMapper<Employee>() {
            @Override
            public Employee mapRow(java.sql.ResultSet rs, int rowNum) throws SQLException {
                return new Employee(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("salary"),
                        rs.getInt("department_id")
                );
            }
        });
    }

    public Employee findById(Integer id) {
        String sql = "SELECT * FROM employee WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, new RowMapper<Employee>() {
                @Override
                public Employee mapRow(java.sql.ResultSet rs, int rowNum) throws SQLException {
                    return new Employee(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("salary"),
                            rs.getInt("department_id")
                    );
                }
            });
        } catch (EmptyResultDataAccessException e) {
            // Return null or handle the case when no employee is found
            return null;
        }
    }


    // Update employee salary (No deprecated methods)
    public int updateSalary(Integer employeeId, String salary) {
        String sql = "UPDATE employee SET salary = ? WHERE id = ?";
        return jdbcTemplate.update(sql, salary, employeeId);
    }

    // Get all employees in a department (using PreparedStatementCreator)
    public List<Employee> getAllEmployeeByDepartmentId(Integer departmentId) {
        String sql = "SELECT * FROM employee WHERE department_id = ?";

        return jdbcTemplate.query(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, departmentId); // setting the parameter dynamically
                return ps;
            }
        }, new RowMapper<Employee>() {
            @Override
            public Employee mapRow(java.sql.ResultSet rs, int rowNum) throws SQLException {
                return new Employee(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("salary"),
                        rs.getInt("department_id")
                );
            }
        });
    }


}
