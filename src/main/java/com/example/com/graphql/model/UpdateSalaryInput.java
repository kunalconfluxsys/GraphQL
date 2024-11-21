package com.example.com.graphql.model;

public class UpdateSalaryInput {

    private Integer employeeId;
    private String salary;

    public Integer getEmployeeId() {
        return employeeId;
    }

    public String getSalary() {
        return salary;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public UpdateSalaryInput(Integer employeeId, String salary) {
        this.employeeId = employeeId;
        this.salary = salary;
    }
}
