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

        this.studentId = rs.getInt("student_id");
        this.classRank = rs.getInt("class_rank");
        this.gradeLevel = rs.getInt("grade_level");
        this.graduationYear = rs.getInt("graduation");
        this.gpa = rs.getDouble("gpa");
        this.firstName = rs.getString("first_name");
        this.lastName = rs.getString("last_name");
    }

    public Student(ResultSet rs) throws SQLException {
        super(-1, "student", null, null, null);

        this.studentId = rs.getInt("student_id");
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

    public void setGPA(double newGpa) {
        gpa = newGpa;
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
     * Nevermind, it is now relevant, god bless.
     *
     * @param studentId
     * @param in
     * @return course no of selected course
     */
    public static String getCourse(int studentId, Scanner in) {
        ArrayList<String> courses = PowerSchool.getStudentCoursesBreakthrough(studentId);
        int numCourses = 0;
        if (courses.isEmpty()) {
            System.out.println("You take no courses.");
            return null;
        }
        for (int i = 1; i <= courses.size(); i++) {
            double grade = PowerSchool.getCourseGrade(studentId, courses.get(i));
            String gradePrint = "";
            // TODO
            // if (grade == null) { 
            //     gradePrint = "--";
            // }
            System.out.println(i + "." + " " + courses.get(i-1) + " / " + );
            numCourses = i;
        }
        System.out.print("\n");
        int selection;
        do {
            System.out.print("::: ");
            selection = Utils.getInt(in, -1);
        } while (selection < 0 || selection > numCourses);
        return courses.get(selection - 1);
    }

    /**
     * Gets marking period (or midterm or final) selection
     * Not a menu method.
     *
     * @param in
     * @return numbers 1-6 for mp or exams
     */
    public static int getMPSelection(Scanner in) {
        System.out.println("\nChoose a marking period or exam status.\n");
        System.out.println("[1] MP1 assignment.");
        System.out.println("[2] MP2 assignment.");
        System.out.println("[3] MP3 assignment.");
        System.out.println("[4] MP4 assignment.");
        System.out.println("[5] Midterm exam.");
        System.out.println("[6] Final exam.");
        System.out.print("\n");

        int mpSelection;
        do {
            System.out.print("::: ");
            mpSelection = Utils.getInt(in, -1);
        } while (mpSelection < 1 || mpSelection > 6);

        return mpSelection;
    }

    public static void viewCourseGrades(User user, Scanner in) {
        System.out.println();
        ArrayList<String> courses = PowerSchool.getStudentCoursesAndGrades(((Student) user).getStudentId());
        for (int i = 1; i <= courses.size(); i++) {
            System.out.println(i + ". " + courses.get(i-1));
        }
        System.out.println();
    }

    public static void viewAssngGradesByCourse(User user, Scanner in) {
        System.out.println();
        String courseNo = getCourse(((Student) user).getStudentId(), in);
        int courseId = PowerSchool.getCourseIdFromCourseNo(courseNo);
        int mpSelection = getMPSelection(in);
        int marking_period = -1; int is_midterm = -1; int is_final = -1;
        switch (mpSelection) {
            case 1:
                marking_period = mpSelection;
                is_midterm = 0;
                is_final = 0;
                break;
            case 2:
                marking_period = mpSelection;
                is_midterm = 0;
                is_final = 0;
                break;
            case 3:
                marking_period = mpSelection;
                is_midterm = 0;
                is_final = 0;
                break;
            case 4:
                marking_period = mpSelection;
                is_midterm = 0;
                is_final = 0;
                break;
            case 5:
                marking_period = 0;
                is_midterm = 1;
                is_final = 0;
                break;
            case 6:
                marking_period = 0;
                is_midterm = 0;
                is_final = 1;
                break;
        }

        ArrayList<String> assignments = PowerSchool.getStudentAssignments(((Student) user).getStudentId(), courseId);
        if (assignments.isEmpty()) {
            System.out.println("\nThere are no assignments to display.\n");
        } else {
            for (int i = 1; i <= assignments.size(); i++) {
                System.out.println(i + ". " + assignments.get(i-1));
            }
            System.out.println();
        }
    }
}