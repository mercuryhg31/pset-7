package com.apcsa.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

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
        this.departmentName = PowerSchool.getDepartmentName(this.departmentId);
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

    public int getTeacherId() {
        return teacherId;
    }

    // APPLICATION THINGS

    /**
     * Prints all courses a teacher teaches and returns course selection
     * Not a menu method.
     *
     * @param user
     * @param in
     * @return course no of selected course
     */
    public static String getCourseSelection(Scanner in, User user) {
        System.out.println("\nChoose a course.\n");
        ArrayList<String> courses = PowerSchool.getTeacherCourses(user);
        int numCourses = 0;
        if (courses.isEmpty()) {
            System.out.println("You teach no courses.");
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
    }

    public static void viewEnrollmentByCourse(User user, Scanner in) {
        String courseNo = "";

        try {
            courseNo = getCourseSelection(in, user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayList<Student> students = PowerSchool.getStudentsByCourse(courseNo);

        if (students.isEmpty()) {
            System.out.println("\nNo students to display.\n");
        } else {
            System.out.println();
            int i = 1;
            for (Student student : students) {
                System.out.println(i++ + ". " + student.getName() + " / " + " NEEDS TO BE PROGRAMMED!!!"); // TODO ALSO NEED TO PROGRAM
            }
            System.out.println();
        }
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

    public static void addAssignment(User user, Scanner in) {
        String courseNo = getCourseSelection(in, user);

        if (courseNo.isEmpty()) {
            System.out.println("\nYou don't teach any courses.\n");
        } else {
            int mpSelection = getMPSelection(in);

            System.out.print("\nAssignment Title: ");
            String title = in.nextLine();
            System.out.print("Point Value: ");
            int point_value = Utils.getInt(in, 0);
            while (point_value < 1 || point_value > 100) {
                System.out.println("\nPoint value must be between 1 and 100.\n");
                System.out.print("Point Value: ");
                point_value = Utils.getInt(in, 0);
            }

            int course_id = PowerSchool.getCourseIdFromCourseNo(courseNo);
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

            if (Utils.confirm("Are you sure you want to create this assignment? (y/n) ", in)){
                if (PowerSchool.createAssignment(course_id, marking_period, is_midterm, is_final, title, point_value) == 1) {
                    System.out.println("\nThere was an error.\n");
                }
            }
        }
    }

    public static void deleteAssignment(User user, Scanner in) {
        String courseNo = getCourseSelection(in, user);

        if (courseNo.isEmpty()) {
            System.out.println("\nYou don't teach any courses.\n");
        } else {
            int mpSelection = getMPSelection(in);
            int course_id = PowerSchool.getCourseIdFromCourseNo(courseNo);
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
            int assignment_id = getAssignment(user, in, courseNo, marking_period, is_midterm, is_final);
            if (assignment_id == 0) {
                System.out.println();
            } else {
                String title = PowerSchool.getAssignmentName(courseNo, assignment_id);

                if (Utils.confirm("Are you sure you want to delete this assignment? (y/n) ", in)){
                    if (PowerSchool.deleteAssignment(course_id, assignment_id, title) == 1) {
                        System.out.println("\nThere was an error.\n");
                    }
                }
            }
        }
    }

    public static int getAssignment(User user, Scanner in, String course_no, int marking_period, int is_midterm, int is_final) {
        ArrayList<String> assignments = PowerSchool.getTeacherAssignments(user, course_no, marking_period, is_midterm, is_final);
        ArrayList<Integer> points = PowerSchool.getTeacherAssignmentPoints(user, course_no, marking_period, is_midterm, is_final);
        int count = 0;

        if (assignments.isEmpty()) {
            System.out.println("You have no assignments here.");
            return 0;
        }

        System.out.println("Choose an assignment.\n");
        for (int i = 1; i <= assignments.size(); i++) {
            System.out.println("[" + i + "]" + " " + assignments.get(i-1) + " (" + points.get(i-1) + " pts)");
            count++;
        }
        System.out.print("\n");

        int selection = 0;
        do {
            System.out.print("::: ");
            selection = Utils.getInt(in, 0);
        } while (selection < 0 || selection > count);

        return PowerSchool.getAssignmentId(course_no, assignments.get(selection-1));
    }

    public static void enterGrade(User user, Scanner in) {
        String course_no = getCourseSelection(in, user);

        if (course_no.isEmpty()) {
            System.out.println("\nYou don't teach any courses.\n");
        } else {
            int mpSelection = getMPSelection(in);
            int course_id = PowerSchool.getCourseIdFromCourseNo(course_no);
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
            int assignment_id = getAssignment(user, in, course_no, marking_period, is_midterm, is_final);
            String title = PowerSchool.getAssignmentName(course_no, assignment_id);

            Student student = getStudent(in, course_no);

            System.out.println("\nAssignment: " + title + " (" + PowerSchool.getAssignmentPoints(course_no, assignment_id) + " pts)");
            System.out.println("Student: " + student.getName());
            System.out.println("Current Grade: " + "NEED TO PROGRAM"); // TODO NEED TO PROGRAM
            System.out.print("\n");

            int newGrade;
            do {
                System.out.print("New Grade: ");
                newGrade = Utils.getInt(in, -1);
            } while (newGrade < 0 || newGrade > PowerSchool.getAssignmentPoints(course_no, assignment_id));

            if (Utils.confirm("\nAre you sure you want to enter this grade? (y/n) ", in)){
                if (PowerSchool.enterGrade(course_id, assignment_id, student.getStudentId(), newGrade, PowerSchool.getAssignmentPoints(course_no, assignment_id)) == 1) {
                    System.out.println("\nThere was an error.\n");
                }
            }
        }
    }

    public static Student getStudent(Scanner in, String course_no) {
        System.out.println("\nChoose a student.\n");
        ArrayList<Student> students = PowerSchool.getAssignmentStudents(course_no);

        for (int i = 1; i <= students.size(); i++) {
            System.out.println("[" + i + "] " + students.get(i-1).getName());
        }
        System.out.print("\n");

        int selection;
        do {
            System.out.print("::: ");
            selection = Utils.getInt(in, 0);
        } while (selection < 1 || selection > students.size());

        return students.get(selection-1);
    }
}