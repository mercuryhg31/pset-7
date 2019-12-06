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
        this.studentId = user.getUserId();
        if (super.isStudent() == false) {
            //cleanup thingies
        } else {
            //do the things you want to do here lmao
            
        }

    }
}