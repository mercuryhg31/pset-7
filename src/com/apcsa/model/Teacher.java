package com.apcsa.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import com.apcsa.controller.Application;
import com.apcsa.controller.Utils;
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
    public static void viewEnrollmentByCourse() {

    }

    /**
     * Prints all courses a teacher teaches and returns course selection
     *
     * @param user
     * @param in
     * @return course no of selected course
     */
    public static String getCourseSelection(User user, Scanner in) {
        System.out.println("Choose a course.\n");
        ArrayList<String> courses = PowerSchool.getTeacherCourses(user);
        int maxCourseNum = 0;
        for (int i = 1; i <= courses.size(); i++) {
            System.out.println("[" + i + "]" + " " + courses.get(i-1));
            maxCourseNum = i;
        }
        System.out.print("\n\n");
        int selection;
        do {
            System.out.print("::: ");
            selection = Utils.getInt(in, -1);
        } while (selection < 1 || selection > maxCourseNum);
        return courses.get(selection - 1);
    }

    public static void addAssignment(User user, Scanner in) {
        String courseNo = getCourseSelection(user, in);

        System.out.println("\nChoose a marking period or exam status.\n");
        System.out.println("[1] MP1 assignment.");
        System.out.println("[2] MP2 assignment.");
        System.out.println("[3] MP3 assignment.");
        System.out.println("[4] MP4 assignment.");
        System.out.println("[5] Midterm exam.");
        System.out.println("[6] Final exam.");
        System.out.print("\n\n");

        int mpSelection;
        do {
            System.out.print("::: ");
            mpSelection = Utils.getInt(in, -1);
        } while (mpSelection < 1 || mpSelection > 6);

        System.out.print("\nAssignment Title: ");
        String title = in.next();
        System.out.print("Point Value: ");
        int ptsPoss = Utils.getInt(in, 0);

        if (Utils.confirm("Are you sure you want to create this assignment? (y/n)")){
            
        }
    }

    public static void deleteAssignment() {

    }

    public static void enterGrade() {

    }
}