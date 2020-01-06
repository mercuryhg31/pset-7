package com.apcsa.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import com.apcsa.data.PowerSchool;
import com.apcsa.model.User;

public class Teacher extends User {

    private int teacherId;
    private int departmentId;
    private String firstName;
    private String lastName;
    private String departmentName;

    public Teacher(User user, ResultSet rs) throws SQLException {
        super(user.getUserId(), user.getAccountType(), user.getUsername(), user.getPassword(), user.getLastLogin());

        this.teacherId = rs.getInt("teacher_id");
        this.departmentId = rs.getInt("department_id");
        this.firstName = rs.getString("first_name");
        this.lastName = rs.getString("last_name");
        this.departmentName = rs.getString("title");
    }

    public Teacher(ResultSet rs) throws SQLException {
        super(-1, "teacher", null, null, null);

        this.teacherId = rs.getInt("teacher_id");
        this.departmentId = rs.getInt("department_id");
        this.firstName = rs.getString("first_name");
        this.lastName = rs.getString("last_name");
        this.departmentName = rs.getString("title");
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    public String getName() {
        return lastName + ", " + firstName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    // APPLICATION THINGS
    public static void viewEnrollmentByCourse(Scanner in) {
        ArrayList<Student> students = PowerSchool.getStudentsByCourse(courseNo);
    }

    public static void addAssignment() {

    }

    public static void deleteAssignment() {

    }

    public static void enterGrade() {

    }
}