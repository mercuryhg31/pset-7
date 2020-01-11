package com.apcsa.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.lang.Thread;

import javax.management.Query;

import java.util.ArrayList;
import com.apcsa.controller.Utils;
import com.apcsa.model.Administrator;
import com.apcsa.model.Student;
import com.apcsa.model.Teacher;
import com.apcsa.model.User;

public class PowerSchool {

    private final static String PROTOCOL = "jdbc:sqlite:";
    private final static String DATABASE_URL = "data/powerschool.db";

    /**
     * Initializes the database if needed (or if requested).
     *
     * @param force whether or not to force-reset the database
     * @throws Exception
     */

    public static void initialize(boolean force) {
        if (force) {
            reset();    // force reset
        } else {
            boolean required = false;

            // check if all tables have been created and loaded in database

            try (Connection conn = getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(QueryUtils.SETUP_SQL)) {

                while (rs.next()) {
                    if (rs.getInt("names") != 9) {
                        required = true;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // build database if needed

            if (required) {
                reset();
            }
        }
    }

    /**
     * Retrieves the User object associated with the requested login.
     *
     * @param username the username of the requested User
     * @param password the password of the requested User
     * @return the User object for valid logins; null for invalid logins
     */

    public static User login(String username, String password) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(QueryUtils.LOGIN_SQL)) {

            stmt.setString(1, username);
            stmt.setString(2, Utils.getHash(password));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Timestamp ts = new Timestamp(new Date().getTime());
                    int affected = PowerSchool.updateLastLogin(conn, username, ts);

                    if (affected != 1) {
                        System.err.println("Unable to update last login (affected rows: " + affected + ").");
                    }

                    return new User(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Retrieves all faculty members.
     *
     * @return a list of teachers
     */

    public static ArrayList<Teacher> getTeachers() {
        ArrayList<Teacher> teachers = new ArrayList<Teacher>();

        try (Connection conn = getConnection();
            Statement stmt = conn.createStatement()) {

            try (ResultSet rs = stmt.executeQuery(QueryUtils.GET_TEACHERS_SQL)) {
            // try (ResultSet rs = stmt.executeQuery("SELECT * FROM teachers")) {
                while (rs.next()) {
                    teachers.add(new Teacher(rs));
                    // System.out.println(rs.getInt("teacher_id"));
                    // System.out.println(rs.getInt("department_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return teachers;
    }

    public static ArrayList<Teacher> getTeachersByDepartment(int department) {
        ArrayList<Teacher> teachers = new ArrayList<Teacher>();

        try (Connection conn = getConnection();
            Statement stmt = conn.createStatement()) {

            try (ResultSet rs = stmt.executeQuery(QueryUtils.GET_TEACHERS_BY_DEPARTMENTS_SQL(department))) { // Michael, I've been doing it with a function because it some errors when I tried a prepared statement.
                while (rs.next()) {
                    teachers.add(new Teacher(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return teachers;
    }

    public static ArrayList<Student> getStudents() {
        ArrayList<Student> students = new ArrayList<Student>();

        try (Connection conn = getConnection();
            Statement stmt = conn.createStatement()) {

            try (ResultSet rs = stmt.executeQuery(QueryUtils.GET_STUDENTS_SQL)) {
                while (rs.next()) {
                    students.add(new Student(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }

    public static ArrayList<Student> getStudentsByGrade(int grade) {
        ArrayList<Student> students = new ArrayList<Student>();

        try (Connection conn = getConnection();
            Statement stmt = conn.createStatement()) {

            try (ResultSet rs = stmt.executeQuery(QueryUtils.GET_STUDENTS_BY_GRADE_SQL(grade))) {
                while (rs.next()) {
                    students.add(new Student(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }

    public static ArrayList<Student> getStudentsByCourse(String courseNo) {
        ArrayList<Student> students = new ArrayList<Student>();
        ArrayList<Integer> courseIds = new ArrayList<Integer>();
        ArrayList<Integer> studentIds = new ArrayList<Integer>();

        try (Connection conn = getConnection();
            Statement stmt = conn.createStatement()) {

            try (ResultSet rs = stmt.executeQuery(QueryUtils.GET_COURSES_BY_COURSENO_SQL(courseNo))) {
                while (rs.next()) {
                    courseIds.add(rs.getInt("course_id"));
                }

                for (int courseId : courseIds) {
                    try (Connection conn2 = getConnection();
                        Statement stmt2 = conn2.createStatement()) {

                        try (ResultSet rs2 = stmt2.executeQuery(QueryUtils.COURSE_GRADES_BY_COURSEID_SQL(courseId))) {
                            while (rs2.next()) {
                                studentIds.add(rs2.getInt("student_id"));
                            }

                            for (int studentId : studentIds) {
                                try (Connection conn3 = getConnection();
                                    Statement stmt3 = conn3.createStatement()) {

                                    try (ResultSet rs3 = stmt3.executeQuery(QueryUtils.GET_STUDENT_BY_STUDENT_ID_SQL(studentId))) {
                                        while (rs3.next()) {
                                            students.add(new Student(rs3));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }


    public static String getCourseNo(String courseNo) {
        String courseNoCheck = "";

        try (Connection conn = getConnection();
            Statement stmt = conn.createStatement()) {

            // System.out.println(QueryUtils.GET_COURSES_BY_COURSENO_SQL(courseNo));
            try (ResultSet rs = stmt.executeQuery(QueryUtils.GET_COURSES_BY_COURSENO_SQL(courseNo))) {
                if (rs.next()) {
                    courseNoCheck = rs.getString("course_no");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            courseNoCheck = null;
        }

        return courseNoCheck;
    }


    public static ArrayList<String> getGrades(int student_id) {
        ArrayList<String> grades = new ArrayList<String>();

        try (Connection conn = getConnection();
            Statement stmt = conn.createStatement()) {

            try (ResultSet rs = stmt.executeQuery(QueryUtils.GET_STUDENT_GRADES_SQL(student_id))) {
                while (rs.next()) {
                    grades.add(new String(String.valueOf(rs)));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return grades;
    }

    public static double getCourseGrade(int studentId, String title) {
        double grade = 0;
        try (Connection conn = getConnection()) {
            int courseId = 0;

            Statement stmt = conn.createStatement();
            try (ResultSet rs = stmt.executeQuery(QueryUtils.GET_COURSE_ID_BY_TITLE_SQL(title))) {
                if (rs.next()) {
                    courseId = rs.getInt("course_id");
                }
            }
            
            stmt = conn.createStatement();
            try (ResultSet rs = stmt.executeQuery(QueryUtils.GET_STUDENT_GRADES_SQL(studentId, courseId))) {
                if (rs.next()) {
                    grade = rs.getDouble("grade");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return grade;
    }

    public static String getCourseTitle(int courseId) {
        String title = new String();

        try (Connection conn = getConnection();
            Statement stmt = conn.createStatement()) {

            try (ResultSet rs = stmt.executeQuery(QueryUtils.GET_COURSES_SQL(courseId))) {
                if (rs.next()) {
                    title = rs.getString("title");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return title;
    }

    public static String getCourseNo(int courseId) {
        String courseNo = new String();

        try (Connection conn = getConnection();
            Statement stmt = conn.createStatement()) {

            try (ResultSet rs = stmt.executeQuery(QueryUtils.GET_COURSES_SQL(courseId))) {
                if (rs.next()) {
                    courseNo = rs.getString("course_no");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courseNo;
    }

    public static ArrayList<String> getTeacherCourses(User user) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            try (ResultSet rs = stmt.executeQuery(QueryUtils.GET_TEACHER_COURSES_SQL(((Teacher) user).getTeacherId()))) {
                ArrayList<String> courseNos = new ArrayList<String>();
                while (rs.next()) {
                    courseNos.add(rs.getString("course_no"));
                }
                return courseNos;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static ArrayList<String> getTeacherAssignments(User user, String course_no, int marking_period, int is_midterm, int is_final) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            try (ResultSet rs = stmt.executeQuery(QueryUtils.GET_TEACHER_ASSIGNMENTS_SQL(((Teacher) user).getTeacherId(), course_no, marking_period, is_midterm, is_final))) {
                ArrayList<String> assignments = new ArrayList<String>();
                while (rs.next()) {
                    assignments.add(rs.getString("title"));
                }
                return assignments;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static ArrayList<Student> getAssignmentStudents(String course_no) { // TODO
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            try (ResultSet rs = stmt.executeQuery(QueryUtils.GET_ASSIGNMENT_STUDENTS_SQL(course_no))) {
                ArrayList<Student> students = new ArrayList<Student>();
                while (rs.next()) {
                    students.add(new Student(rs));
                }
                return students;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static int getAssignmentPoints(String course_no, int assignment_id) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            try (ResultSet rs = stmt.executeQuery(QueryUtils.GET_ASSIGNMENT_POINTS(course_no, assignment_id))) {
                if (rs.next()) {
                    return rs.getInt("point_value");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static int enterGrade(int course_id, int assignment_id, int student_id, int points_earned, int points_possible) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             Statement stmt2 = conn.createStatement()) {
            
            try (ResultSet rs = stmt2.executeQuery(
                "SELECT * FROM assignment_grades WHERE course_id = " + course_id +
                " AND student_id = " + student_id + " AND assignment_id = " + assignment_id)) {
                if (rs.next()) {
                    System.out.println("\nA grade already exists for this assignment and student.\n");
                    return 0;
                }
            }

            if (stmt.executeUpdate(QueryUtils.ENTER_GRADE_SQL(course_id, assignment_id, student_id, points_earned, points_possible)) == 1) {
                updateGpaAndClassRank(student_id);
                updateCourseGrades(student_id, course_id);
                System.out.println("\nSuccessfully entered grade.\n");
                return 0;
            } else {
                return 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    } // QueryUtils.ENTER_GRADE_SQL(course_id, assignment_id, student_id, points_earned, points_possible))

    public static void updateCourseGrades(int student_id, int course_id) {
        String set = "";
        double mp1 = calculateMP(1, student_id, course_id);
        if (mp1 / mp1 == 1) set += ", mp1 = " + mp1;
        double mp2 = calculateMP(2, student_id, course_id);
        if (mp2 / mp2 == 1) set += ", mp2 = " + mp2;
        double mp3 = calculateMP(3, student_id, course_id);
        if (mp3 / mp3 == 1) set += ", mp3 = " + mp3;
        double mp4 = calculateMP(4, student_id, course_id);
        if (mp4 / mp4 == 1) set += ", mp4 = " + mp4;
        double midterm = calculateMP(5, student_id, course_id);
        if (midterm / midterm == 1) set += ", midterm_exam = " + midterm;
        double final_exam = calculateMP(6, student_id, course_id);
        if (final_exam / final_exam == 1) set += ", final_exam = " + final_exam;
        double grade = ((mp1 + mp2 + mp3 + mp4) * 0.2 + (midterm + final_exam) * 0.1);
        if (grade / grade == 1) set += ", grade = " + grade;

        // System.out.println(mp1);
        // System.out.println(mp2);
        // System.out.println(mp3);
        // System.out.println(mp4);
        // System.out.println(midterm);
        // System.out.println(final_exam);
        

        String updateCG =
        "UPDATE course_grades " +
            "SET " + set.substring(2, set.length()) +
        " WHERE student_id = " + student_id + " AND course_id = " + course_id;

        // System.o ut.println(updateCG);

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.executeUpdate(updateCG);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static double calculateMP(int mp, int student_id, int course_id) {
        double ptsPoss = 0;
        double ptsEarned = 0;
        int mpVal = 0; int midtermVal = 0; int finalVal = 0;
        if (mp >= 1 && mp <= 4) {
            mpVal = mp;
            midtermVal = 0;
            finalVal = 0;
        } else if (mp == 5) {
            mpVal = 0;
            midtermVal = 1;
            finalVal = 0;
        } else if (mp == 6) {
            mpVal = 0;
            midtermVal = 0;
            finalVal = 1;
        }
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            try (ResultSet rs = stmt.executeQuery("SELECT * FROM assignment_grades ag, assignments a WHERE ag.course_id = a.course_id AND ag.assignment_id = a.assignment_id AND ag.student_id = " + student_id + " AND ag.course_id = " + course_id + " AND a.marking_period = " + mpVal + " AND a.is_midterm = " + midtermVal + " AND a.is_final = " + finalVal)) { // TODO
                while (rs.next()) {
                    ptsPoss += rs.getInt("points_possible");
                    ptsEarned += rs.getInt("points_earned");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return (ptsEarned / ptsPoss);
    }

    public static ArrayList<Integer> getTeacherAssignmentPoints(User user, String course_no, int marking_period, int is_midterm, int is_final) { // TODO
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            try (ResultSet rs = stmt.executeQuery(QueryUtils.GET_TEACHER_ASSIGNMENTS_SQL(((Teacher) user).getTeacherId(), course_no, marking_period, is_midterm, is_final))) {
                ArrayList<Integer> assignmentsPts = new ArrayList<Integer>();
                while (rs.next()) {
                    assignmentsPts.add(rs.getInt("point_value"));
                }
                return assignmentsPts;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // public static ArrayList<Integer> getAssignment(User user, int course_id, int assignment_id) { // TODO
    //     try (Connection conn = getConnection();
    //          Statement stmt = conn.createStatement()) {

    //         try (ResultSet rs = stmt.executeQuery(QueryUtils.GET_ASSIGNMENT_SQL(((Teacher) user).getTeacherId(), course_id, assignment_id))) {
    //             ArrayList<Integer> assignmentsPts = new ArrayList<Integer>();
    //             while (rs.next()) {
    //                 assignmentsPts.add(rs.getInt("point_value"));
    //             }
    //             return assignmentsPts;
    //         }
    //     } catch (SQLException e) {
    //         e.printStackTrace();
    //     }

    //     return null;
    // }

    public static int createAssignment(int course_id, int marking_period, int is_midterm, int is_final, String title, int point_value) {
        int assignment_id = getNextAssignmentId(course_id);

        try (Connection conn = getConnection();
            Statement stmt = conn.createStatement()) {

            if (stmt.executeUpdate(QueryUtils.CREATE_ASSIGNMENT_SQL(course_id, assignment_id, marking_period, is_midterm, is_final, title, point_value)) == 1) {
                System.out.println("\nSuccessfully created assignment.\n");
                return 0;
            } else {
                return 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public static int deleteAssignment(int course_id, int assignment_id, String title) {
        try (Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            Statement stmt2 = conn.createStatement()) {

            if (stmt.executeUpdate(QueryUtils.DELETE_ASSIGNMENT_SQL(course_id, assignment_id)) == 1) {
                if (!getAssignmentsAssignmentGrades(course_id, assignment_id).isEmpty()) {
                    if (stmt2.executeUpdate(QueryUtils.DELETE_ASSIGNMENT_GRADES_SQL(course_id, assignment_id)) == 1) {
                        System.out.println("\nSuccessfully deleted " + title + ".\n");
                        return 0;
                    } else {
                        return 1;
                    }
                }
                System.out.println("\nSuccessfully deleted " + title + ".\n");
                return 0;
            } else {
                return 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * Getting the assignment_grades rows associated with an assignment
     * 
     * @param course_id
     * @param assignment_id
     * @return an arraylist of numbers which are the assng_id of whatever it finds in assng_grades associated with an assng, if empty, we no delete anything
     */
    public static ArrayList<Integer> getAssignmentsAssignmentGrades(int course_id, int assignment_id) {
        ArrayList<Integer> stuff = new ArrayList<Integer>();
        try (Connection conn = getConnection();
            Statement stmt = conn.createStatement()) {

            try (ResultSet rs = stmt.executeQuery("SELECT * FROM assignment_grades WHERE course_id = " + course_id + " AND assignment_id = " + assignment_id)) {
                if (rs.next()) {
                    stuff.add(rs.getInt("assignment_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stuff;
    }

    public static int getNextAssignmentId(int course_id) {
        int assignmentId = 1;
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_ASSIGNMENTS_SQL)) {

            stmt.setInt(1, course_id);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    assignmentId = rs.getInt("assignment_id") + 1;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return assignmentId;
    }

    public static int getAssignmentId(String course_no, String title) {
        int course_id = getCourseIdFromCourseNo(course_no);
        int assignmentId = 0;
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_ASSIGNMENT_ID_FOR_ALTER_SQL)) {

            stmt.setInt(1, course_id);
            stmt.setString(2, title);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    assignmentId = rs.getInt("assignment_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return assignmentId;
    }

    public static String getAssignmentName(String course_no, int assignment_id) {
        int course_id = getCourseIdFromCourseNo(course_no);
        String title = "";
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM assignments a, courses c " +
            "WHERE a.course_id = c.course_id " +
            "AND c.course_id = ? " +
            "AND a.assignment_id = ? " +
            "ORDER BY a.assignment_id")) {

            stmt.setInt(1, course_id);
            stmt.setInt(2, assignment_id);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    title = rs.getString("title");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return title;
    }

    public static int getCourseIdFromCourseNo(String courseNo) {
        try (Connection conn = getConnection();
            Statement stmt = conn.createStatement()) {

            try (ResultSet rs = stmt.executeQuery(QueryUtils.GET_COURSES_BY_COURSENO_SQL(courseNo))) {
                if (rs.next()) {
                    return rs.getInt("course_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // KEEP THIS, PLEASE, JUST DON'T DELETE IT, IT IS A MONUMENT TO THE BIGGEST, STUPIDEST BREAKTHROUGH OF MY LIFE
    public static ArrayList<String> getStudentCoursesBreakthrough(int student_id) {
        ArrayList<String> courses = new ArrayList<String>();
        try (Connection conn = getConnection();
            Statement stmt = conn.createStatement()) {

            try (ResultSet rs = stmt.executeQuery(QueryUtils.GET_STUDENT_COURSES_SQL(student_id))) {
                while (rs.next()) {
                    courses.add(rs.getString("title"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    public static ArrayList<String> getStudentCourses(int student_id) { // TODO for student
        ArrayList<String> courses = new ArrayList<String>();
        try (Connection conn = getConnection();
            Statement stmt = conn.createStatement()) {

            try (ResultSet rs = stmt.executeQuery(QueryUtils.GET_STUDENT_COURSES_SQL(student_id))) {
                while (rs.next()) {
                    courses.add(rs.getString("title"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    public static double getStudentCourseGrades(String title, int student_id) { // TODO for student or not - calculate grade
        double grade = 0;
        double numGrades = 0;
        try (Connection conn = getConnection();
            Statement stmt = conn.createStatement()) {

            try (ResultSet rs = stmt.executeQuery(QueryUtils.GET_STUDENT_COURSE_GRADES_SQL(title, student_id))) {
                while (rs.next()) {
                    grade += rs.getInt("grade");
                    numGrades++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return (grade / numGrades);
    }

    public static ArrayList<String> getStudentCoursesAndGrades(int student_id) { // TODO for student
        ArrayList<String> courses = new ArrayList<String>();
        try (Connection conn = getConnection();
            Statement stmt = conn.createStatement()) {

            try (ResultSet rs = stmt.executeQuery(QueryUtils.GET_STUDENT_COURSE_GRADE_SQL(student_id))) {
                while (rs.next()) {
                    courses.add(rs.getString("title") + " / " + rs.getInt("grade"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }



    /**
     * Returns the administrator account associated with the user.
     *
     * @param user the user
     * @return the administrator account if it exists
     */

    public static User getAdministrator(User user) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_ADMIN_SQL)) {

            stmt.setInt(1, user.getUserId());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Administrator(user, rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    /**
     * Returns the teacher account associated with the user.
     *
     * @param user the user
     * @return the teacher account if it exists
     */

    public static User getTeacher(User user) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_TEACHER_SQL)) {

            stmt.setInt(1, user.getUserId());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Teacher(user, rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    public static String getDepartmentName(int departmentId) {
        String departmentName = "";
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_DEPARTMENT_SQL)) {

            stmt.setInt(1, departmentId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    departmentName = rs.getString("title");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return departmentName;
    }

    /**
     * Returns the student account associated with the user.
     *
     * @param user the user
     * @return the student account if it exists
     */

    public static User getStudent(User user) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_STUDENT_SQL)) {

            stmt.setInt(1, user.getUserId());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Student(user, rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    // This should be run every time a grade is updated/changed (meaning when gpa is changed). Wait idea just set everyone using the getStudentRanks() (rename it setStudentsRanks() or whatever)
    // but then make a function that whenever they want to get the student rank just update the ranks (set function) and then getStudentsByGrade() where the grade
    // is equal to whatever the grade that was selected. TODO
    public static void setStudentRank(int grade) {

        ArrayList<Student> students = new ArrayList<Student>(getStudentsByGrade(grade));

        for (int i = 0; i < students.size(); i++) {
            Student comparing = students.get(i);
            int numOfBetterStudents = 0;

            if (comparing.getGPA() == -1.0) {
                students.get(i).setClassRank(0);
            } else {

                for (int j = 0; j < students.size(); j++) {
                    if (comparing.getGPA() < students.get(j).getGPA()) {
                        numOfBetterStudents++;
                    }
                }
                students.get(i).setClassRank(numOfBetterStudents+1);
            }
        }

        try {
            Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement("UPDATE students SET class_rank = ? WHERE student_id = ?");
            
            conn.setAutoCommit(false);
            for (Student student : students) {
                stmt.setInt(1, student.getClassRank());
                stmt.setInt(2, student.getStudentId());
                stmt.executeUpdate();
            }

            conn.commit();
        
        } catch (SQLException e) {
            e.printStackTrace();

            return;
        }        
        
    }

    public static ArrayList<Student> getStudentsByGradeWithUpdatedRank(int grade) {
        ArrayList <Student> students = new ArrayList<Student>();
        setStudentRank(grade);
        students = getStudentsByGrade(grade);
        
        return students;
    }

    public static void setStudentGpa(int studentId) {
        Student student = null;
        ArrayList<Double> courseGrades = new ArrayList<Double>();
        ArrayList<Integer> courseIds = new ArrayList<Integer>();
        ArrayList<Double> courseWeights = new ArrayList<Double>();
        try(Connection conn = getConnection()) {
            
            Statement stmt = conn.createStatement();
            try (ResultSet rs = stmt.executeQuery(QueryUtils.GET_STUDENT_BY_STUDENT_ID_SQL(studentId))) {
                if (rs.next()) {
                    student = new Student(rs);
                }
            }

            stmt = conn.createStatement();
            try (ResultSet rs = stmt.executeQuery(QueryUtils.GET_STUDENT_GRADES_ALL_SQL(studentId))) {
                while (rs.next()) {
                    courseGrades.add(rs.getDouble("grade"));
                    courseIds.add(rs.getInt("course_id"));
                }
            }

            stmt = conn.createStatement();
            for (int courseId : courseIds) {
                try (ResultSet rs = stmt.executeQuery(QueryUtils.GET_COURSES_SQL(courseId))) {
                    if (rs.next()) {
                        courseWeights.add(rs.getDouble("weight"));
                    }
                }
            }
            
            double totalWeight = 0;
            int numCourses = courseGrades.size();
            double calculatedGpa = -1.0;
            for (int i = 0; i < courseGrades.size(); i++) {
                totalWeight += courseWeights.get(i);
                calculatedGpa = courseGrades.get(i) * courseWeights.get(i);
            }

            if (courseGrades != null) {
                calculatedGpa = (((calculatedGpa / totalWeight) / numCourses) / 100) * 4.0;

            }

            student.setGPA(calculatedGpa);

            stmt = conn.createStatement();
            conn.setAutoCommit(false);
            stmt.executeUpdate("UPDATE students SET gpa = " + student.getGPA() + " WHERE student_id = " + student.getStudentId());
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateGpaAndClassRank(int studentId) {
        setStudentGpa(studentId);

        for (int i = 9; i < 13; i++) {
            setStudentRank(i);
        }
    }


    /**
     * Updates a changed password for a user.
     *
     * @param conn the current database connection
     * @param username the user's username
     * @param password the new password
     * @return the number of affected rows
     */

    public static int updatePassword(String username, String password) {

    	try (Connection conn = getConnection();
    		 PreparedStatement stmt = conn.prepareStatement(QueryUtils.UPDATE_PASSWORD_SQL)) {

            conn.setAutoCommit(false);
            stmt.setString(1, password);
            stmt.setString(2, username);

            if (stmt.executeUpdate() == 1) {
                conn.commit();

                return 1;
            } else {
                conn.rollback();

                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();

            return -1;
        }
    }

    /**
     * Establishes a connection to the database.
     *
     * @return a database Connection object
     * @throws SQLException
     */

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(PROTOCOL + DATABASE_URL);
    }

    /**
     * Updates the last login time for the user.
     *
     * @param conn the current database connection
     * @param username the user's username
     * @param ts the current timestamp
     * @return the number of affected rows
     */

    private static int updateLastLogin(Connection conn, String username, Timestamp ts) {
        try (PreparedStatement stmt = conn.prepareStatement(QueryUtils.UPDATE_LAST_LOGIN_SQL)) {

            conn.setAutoCommit(false);
            stmt.setString(1, ts.toString());
            stmt.setString(2, username);

            if (stmt.executeUpdate() == 1) {
                conn.commit();

                return 1;
            } else {
                conn.rollback();

                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();

            return -1;
        }
    }

    /**
     * Builds the database. Executes a SQL script from a configuration file to
     * create the tables, setup the primary and foreign keys, and load sample data.
     */

    private static void reset() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             BufferedReader br = new BufferedReader(new FileReader(new File("config/setup.sql")))) {

            String line;
            StringBuffer sql = new StringBuffer();

            // read the configuration file line-by-line to get SQL commands

            while ((line = br.readLine()) != null) {
                sql.append(line);
            }

            // execute SQL commands one-by-one

            for (String command : sql.toString().split(";")) {
                if (!command.strip().isEmpty()) {
                    stmt.executeUpdate(command);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error: Unable to load SQL configuration file.");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error: Unable to open and/or read SQL configuration file.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error: Unable to execute SQL script from configuration file.");
            e.printStackTrace();
        }
    }
}
