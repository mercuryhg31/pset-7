package com.apcsa.model;

import java.io.IOException;
import java.sql.ResultSet;
import com.apcsa.model.User;
import java.sql.SQLException;

public class Student extends User {

    private int studentId;
    private int classRank;
    private int gradeLevel;
    private int graduationYear;
    private double gpa;
    private String firstName;
    private String lastName;
    
    public Student(User user, ResultSet rs) throws SQLException {
        super(user.getUserId(), user.getAccountType(), user.getUsername(), user.getPassword(), user.getLastLogin());

        this.studentId = rs.getInt("user_id");
        this.classRank = rs.getInt("class_rank");
        this.gradeLevel = rs.getInt("grade_level");
        this.graduationYear = rs.getInt("graduation");
        this.gpa = rs.getDouble("gpa");
        this.firstName = rs.getString("first_name");
        this.lastName = rs.getString("last_name");
    }

    @Override
    public String getFirstName() {
        return firstName;
    }
}