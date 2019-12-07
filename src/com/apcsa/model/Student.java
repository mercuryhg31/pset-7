package com.apcsa.model;

import java.io.IOException;

import com.apcsa.model.User;

public class Student extends User {

    private int studentId;
    private int classRank;
    private int gradeLevel;
    private int graduationYear;
    private double gpa;
    private String firstName;
    private String lastName;
    
    public Student(User user, ResultSet rs) {
        this.studentId = rs.getInt("user_id");
        this.studentId = user.getUserId(); // ?
    }
}