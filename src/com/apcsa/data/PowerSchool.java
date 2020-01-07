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

            try (ResultSet rs = stmt.executeQuery("SELECT * FROM students")) {
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
                                studentIds.add(rs.getInt("student_id"));
                            }
            
                            for (int studentId : studentIds) {
                                try (Connection conn3 = getConnection();
                                    Statement stmt3 = conn3.createStatement()) {
            
                                    try (ResultSet rs3 = stmt3.executeQuery(QueryUtils.GET_STUDENT_BY_STUDENT_ID_SQL(studentId))) {
                                        while (rs3.next()) {
                                            students.add(new Student(rs));
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

            System.out.println(QueryUtils.GET_COURSES_BY_COURSENO_SQL(courseNo));
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

    // public static User getUser(String username) {
    //     try (Connection conn = getConnection();
    //          PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_USER_SQL)) {

    //         stmt.setString(1, username);

    //         try (ResultSet rs = stmt.executeQuery()) {
    //             if (rs.next()) {
    //                 return new User(rs);
    //             }
    //         }
    //     } catch (SQLException e) {
    //         e.printStackTrace();
    //     }

    //     return null;
    // }

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
