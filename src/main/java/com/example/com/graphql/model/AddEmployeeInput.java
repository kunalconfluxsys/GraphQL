package com.example.com.graphql.model;

public class AddEmployeeInput {

    private String name;
    private String salary;
    private Integer departmentId;

    public String getName() {
        return name;
    }

    public String getSalary() {
        return salary;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public AddEmployeeInput(String name, String salary, Integer departmentId) {
        this.name = name;
        this.salary = salary;
        this.departmentId = departmentId;
    }
}
