package com.apcsa.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import com.apcsa.controller.Utils;
import com.apcsa.data.PowerSchool;
import com.apcsa.model.User;

public class Administrator extends User {

    private int administratorId;
    private String firstName;
    private String lastName;
    private String jobTitle;

    public Administrator (User user, ResultSet rs) throws SQLException {
        super(user.getUserId(), user.getAccountType(), user.getUsername(), user.getPassword(), user.getLastLogin());

        this.administratorId = rs.getInt("administrator_id");
        this.firstName = rs.getString("first_name");
        this.lastName = rs.getString("last_name");
        this.jobTitle = rs.getString("job_title");
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    public String getName() {
        return firstName + " " + lastName;
    }

    // APPLICATION THINGS
    public static void viewFaculty() {
        ArrayList<Teacher> teachers = PowerSchool.getTeachers();

        if (teachers.isEmpty()) {
            System.out.println("\nNo teachers to display.\n");
        } else {
            System.out.println();
            int i = 1;
            for (Teacher teacher : teachers) {
                System.out.println(i++ + ". " + teacher.getName() + " / " + teacher.getDepartmentName());
            }
            System.out.println();
        }
    }

    /**
     * Retrieves the user's department selection.
     * Not a menu method.
     *
     * @return the selected department
     */

    private static int getDepartmentSelection(Scanner in) {
        int selection = -1;
        System.out.println("\nChoose a department.");
        while (selection < 1 || selection > 6) {
            System.out.println("\n[1] Computer Science.");
            System.out.println("[2] English.");
            System.out.println("[3] History.");
            System.out.println("[4] Mathematics.");
            System.out.println("[5] Physical Education.");
            System.out.println("[6] Science.");
            System.out.print("\n::: ");
            selection = Utils.getInt(in, -1);
        }
        return selection;
    }

    public static void viewFacultyByDept(Scanner in) {
        ArrayList<Teacher> teachers = PowerSchool.getTeachersByDepartment(getDepartmentSelection(in));;

        if (teachers.isEmpty()) {
            System.out.println("\nNo teachers to display.\n");
        } else {
            System.out.println();
            int i = 1;
            for (Teacher teacher : teachers) {
                System.out.println(i++ + ". " + teacher.getName() + " / " + teacher.getDepartmentName());
            }
            System.out.println();
        }
    }

    public static void viewStudentEnroll() {
        ArrayList<Student> students = PowerSchool.getStudents();;

        if (students.isEmpty()) {
            System.out.println("\nNo teachers to display.\n");
        } else {
            System.out.println();
            int i = 1;
            for (Student student : students) {
                System.out.println(i++ + ". " + student.getName() + " / " + student.getGraduationYear());
            }
            System.out.println();
        }
    }

    /**
     * Retrieves a user's grade selection.
     * Not a menu method.
     *
     * @return the selected grade
     */

    private static int getGradeSelection(Scanner in) {
        int selection = -1;
        System.out.println("\nChoose a grade level.");

        while (selection < 1 || selection > 4) {
            System.out.println("\n[1] Freshman.");
            System.out.println("[2] Sophomore.");
            System.out.println("[3] Junior.");
            System.out.println("[4] Senior.");
            System.out.print("\n::: ");

            selection = Utils.getInt(in, -1);
        }

        return selection + 8;   // +8 because you want a value between 9 and 12
    }

    public static void viewStudentEnrollByGrade() {

    }

    public static void viewStudentEnrollByCourse() {

    }
}