package com.apcsa.model;

import java.io.IOException;
import java.sql.ResultSet;

import com.apcsa.controller.Utils;
import com.apcsa.data.PowerSchool;
import com.apcsa.model.User;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

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

    public Student(ResultSet rs) throws SQLException {
        super(-1, "student", null, null, null);

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

    public String getName() {
        return lastName + ", " + firstName;
    }

    public int getGraduationYear() {
        return graduationYear;
    }

    public double getGPA() {
        return gpa;
    }

    public void setClassRank(int rank) {
        classRank = rank;
    }

    public int getClassRank() {
        return classRank;
    }

    public int getStudentId() {
        return studentId;
    }

    // APPLICATION THINGS
    /**
     * Not a menu method.
     * Okay, this whole method is actually irrelevant, but leave it because it functions and I will use it for admin, I wanna cry, but of happiness rn.
     *
     * @param studentId
     */
    public static String getCourse(int studentId, Scanner in) {
        ArrayList<String> courses = PowerSchool.getStudentCoursesBreakthrough(studentId);
        int numCourses = 0;
        if (courses.isEmpty()) {
            System.out.println("You take no courses.");
            return null;
        }
        for (int i = 1; i <= courses.size(); i++) {
            System.out.println("[" + i + "]" + " " + courses.get(i-1));
            numCourses = i;
        }
        System.out.print("\n");
        int selection;
        do {
            System.out.print("::: ");
            selection = Utils.getInt(in, -1);
        } while (selection < 0 || selection > numCourses);
        return courses.get(selection - 1);

        // System.out.println();
        // String courseNo = getCourse(((Student) user).getStudentId(), in);
        // System.out.println(courseNo);
        // int courseId = PowerSchool.getCourseIdFromCourseNo(courseNo);
        // System.out.println(courseId);
    }

    public static void viewCourseGrades(User user, Scanner in) {
        getCourse(((Student) user).getStudentId(), in);
    }

    public static void viewAssngGradesByCourse() {

    }
}