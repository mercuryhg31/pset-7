package com.apcsa.data;

import com.apcsa.model.User;

public class QueryUtils {

    /////// QUERY CONSTANTS ///////////////////////////////////////////////////////////////

    /*
     * Determines if the default tables were correctly loaded.
     */

    public static final String SETUP_SQL =
        "SELECT COUNT(name) AS names FROM sqlite_master " +
            "WHERE type = 'table' " +
        "AND name NOT LIKE 'sqlite_%'";

    /*
     * Updates the last login timestamp each time a user logs into the system.
     * I'm pretty sure this is miscommented.
     */

    public static final String LOGIN_SQL =
        "SELECT * FROM users " +
            "WHERE username = ?" +
        "AND auth = ?";

    /*
     * Updates the last login timestamp each time a user logs into the system.
     */

    public static final String UPDATE_LAST_LOGIN_SQL =
        "UPDATE users " +
            "SET last_login = ? " +
        "WHERE username = ?";

    /*
     * Retrieves an administrator associated with a user account.
     */

    public static final String GET_ADMIN_SQL =
        "SELECT * FROM administrators " +
            "WHERE user_id = ?";

    /*
     * Retrieves a teacher associated with a user account.
     */

    public static final String GET_TEACHER_SQL =
        "SELECT * FROM teachers " +
            "WHERE user_id = ?";

    /*
     * Retrieves a student associated with a user account.
     */

    public static final String GET_STUDENT_SQL =
        "SELECT * FROM students " +
            "WHERE user_id = ?";

    /*
     * Retrieves a user associated with a user account.
     */

    public static final String GET_USER_SQL =
        "SELECT * FROM users " +
            "WHERE username = ?";

    public static final String GET_DEPARTMENT_SQL = 
        "SELECT * FROM departments " + 
            "WHERE department_id = ?";
    /*
     * Sets password for user associated with a username.
     */
    public static final String UPDATE_PASSWORD_SQL =
        "UPDATE users " +
            "SET auth = ? " +
        "WHERE username = ?";

    public static final String GET_TEACHERS_SQL =
        "SELECT * FROM teachers, departments " +
        "WHERE teachers.department_id = departments.department_id " +
        "ORDER BY last_name, first_name";
        
    

    // public static final String GET_TEACHERS_BY_DEPARTMENTS_SQL =
    //     "SELECT * FROM teachers t, departments d " +
    //     "WHERE t.department_id = d.department_id " +
    //     "AND d.department_id = ? " +
    //     "ORDER BY t.last_name, t.first_name";

    // public static final String GET_TEACHERS_BY_DEPARTMENTS_SQL =
    //     "SELECT * FROM teachers t " +
    //     "LEFT JOIN departments d ON t.department_id = d.department_id " +
    //     "WHERE d.department_id = ? " +
    //     "ORDER BY t.last_name, t.first_name";

    public static /*final, lol, why does this work*/ String GET_TEACHERS_BY_DEPARTMENTS_SQL(int department_id) {
        return "SELECT * FROM teachers t " +
        "LEFT JOIN departments d ON t.department_id = d.department_id " +
        "WHERE d.department_id = " + String.valueOf(department_id) + " " +
        "ORDER BY t.last_name, t.first_name";
    }

    public static final String GET_STUDENTS_SQL =
        "SELECT * FROM students s, users u " +
        "WHERE s.user_id = u.user_id " +
        "ORDER BY s.last_name, s.first_name";
    
    public static String GET_STUDENTS_BY_GRADE_SQL(int grade) {
        return "SELECT * FROM students " +
        "WHERE grade_level = " + String.valueOf(grade) + " " +
        "ORDER BY student_id";//"ORDER BY last_name, first_name";
    }

    public static String GET_STUDENTS_BY_GRADE_ORDERED_BY_GPA_SQL(int grade) {
        return "SELECT * FROM students " + 
        "WHERE grade_level = " + String.valueOf(grade) + " " +
        "ORDER BY gpa";
    }

    // public static String GET_STUDENTS_BY_COURSE_SQL(String course) {
    //     // return "SELECT * FROM students s, course_grades cg, courses c " +
    //     // "WHERE s.student_id = cg.student_id AND cg.course_id = c.course_id " +
    //     // "AND c.course_no = " + course + " " +
    //     // "ORDER BY s.last_name, s.first_name";
    //     return "SELECT * FROM students s " +
    //     "LEFT JOIN course_grades cg ON s.student_id = cg.student_id AND " +
    //     "LEFT JOIN courses c ON cg.course_id = c.course_id " +
    //     "WHERE c.course_no = " + course + " " +
    //     "ORDER BY s.last_name, s.first_name";
    // }

    public static String GET_STUDENT_BY_STUDENTID_ORDERED_BY_GPA_SQL(int studentId) {
        return "SELECT * FROM students " + 
        "WHERE student_id = " + String.valueOf(studentId) + " " + 
        "ORDER BY gpa";
    }

    public static String UPDATE_CLASS_RANK_SQL(int studentId, int classRank) {
        return 
        "UPDATE students " + 
            "SET class_rank = " + String.valueOf(classRank) + " " +
        "WHERE student_id = " + String.valueOf(studentId);

    }

    public static String GET_STUDENT_GRADES_ALL_SQL(int studentId) {
        return "SELECT * FROM course_grades " + 
        "WHERE student_id = " + String.valueOf(studentId) + " " + 
        "ORDER BY course_id";
    }

    public static String GET_STUDENT_GRADES_SQL(int studentId) {
        return "SELECT grade FROM course_grades " +
        "WHERE student_id = " + String.valueOf(studentId) + " " +
        "ORDER BY course_id";
    } 

    // Gives grade of a course that a student is taking given the student id and course id
    public static String GET_STUDENT_GRADES_SQL(int studentId, int courseId) {
        return "SELECT grade FROM course_grades " +
        "WHERE student_id = " + String.valueOf(studentId) + " AND WHERE course_id = " + String.valueOf(courseId);

    }

    // // Gives grades from all courses that a student is taking given their student id // TODO do we need this?
    // public static String GET_STUDENT_COURSES_SQL(int studentId) {
    //     return "SELECT course_id FROM course_grades " + 
    //     "WHERE student_id = " + String.valueOf(studentId) + " " +
    //     "ORDER BY course_id";
    // }

    public static String GET_STUDENT_BY_STUDENT_ID_SQL(int studentId) {
        return "SELECT * FROM students " + 
        "WHERE student_id = " + String.valueOf(studentId) + " " +
        "ORDER BY last_name, first_name";
    }

    public static String GET_COURSE_ID_BY_TITLE_SQL(String title) {
        return "SELECT course_id FROM courses WHERE title = \"" + title + "\"";
    }

    // // Gives the title of a course given the course id (ex something like AP Computer Science Principles)
    // public static String GET_COURSE_TITLE_SQL(int courseId) {
    //     return "SELECT title FROM courses " + 
    //     "WHERE course_id = " + String.valueOf(courseId);
    // }

    // // Gives the course no. of a course given the course id (ex something like CS1000)
    // public static String GET_COURSE_NO_SQL(int courseId) {
    //     return "SELECT course_no FROM courses " + 
    //     "WHERE course_id = " + String.valueOf(courseId);
    // }

    // public static String GET_STUDENT_ID_BY_COURSE(String courseNo) {
    //     return "SELECT "
    // }

    public static String COURSE_GRADES_BY_COURSEID_SQL(int courseId) {
        return "SELECT * FROM course_grades " + 
        "WHERE course_id = " + String.valueOf(courseId);
    }

    public static String GET_COURSES_BY_COURSENO_SQL(String courseNo) {
        return "SELECT * FROM courses " + 
        "WHERE course_no = \"" + courseNo + "\"";
    }

    public static String GET_COURSES_SQL(int courseId) {
        return "SELECT * FROM courses " + 
        "WHERE course_id = " + String.valueOf(courseId);
    }


    // Allows students to see their grades by course. TODO

    // public static final String GET_STUDENT_GRADES_COURSE_SQL =
    //     "SELECT c.course_no FROM "
    // public static final String GET_TEACHER_COURSES_SQL =
    //     "SELECT * FROM courses c, teachers t " +
    //     "WHERE c.teacher_id = t.teacher_id " +
    //     "AND t.teacher_id = ? " +
    //     "ORDER BY title";

    public static String GET_TEACHER_COURSES_SQL(int teacher_id) {
        return "SELECT * FROM courses c, teachers t " +
        "WHERE c.teacher_id = t.teacher_id " +
        "AND t.teacher_id = " + teacher_id + " " +
        "ORDER BY title";
    }

    public static String GET_TEACHER_ASSIGNMENTS_SQL(int teacher_id, String course_no, int marking_period, int is_midterm, int is_final) {
        return "SELECT * FROM assignments a, courses c, teachers t " +
        "WHERE c.teacher_id = t.teacher_id AND c.course_id = a.course_id " +
        "AND t.teacher_id = " + teacher_id + " " +
        "AND a.course_id = " + PowerSchool.getCourseIdFromCourseNo(course_no) + " " +
        "AND a.marking_period = " + marking_period + " " +
        "AND a.is_midterm = " + is_midterm + " " +
        "AND a.is_final = " + is_final + " " +
        "ORDER BY a.assignment_id";
    }

    public static String GET_ASSIGNMENT_STUDENTS_SQL(String course_no) {
        return "SELECT * FROM students s, course_grades cg, courses c " +
        "WHERE s.student_id = cg.student_id AND cg.course_id = c.course_id " +
        "AND c.course_id = " + PowerSchool.getCourseIdFromCourseNo(course_no)
        ;
    }

    public static String GET_ASSIGNMENT_POINTS(String course_no, int assignment_id) {
        return "SELECT * FROM assignments " +
        "WHERE course_id = " + PowerSchool.getCourseIdFromCourseNo(course_no) + " " +
        "AND assignment_id = " + assignment_id;
    }

    public static String GET_STUDENT_COURSE_GRADE_SQL(int student_id) { // for student, because if i don't start commenting, i will loose what small amount of sanity i have regained through my 3 hours of sleep
        return "SELECT * FROM courses c, students s, course_grades cg " +
        "WHERE c.course_id = cg.course_id AND cg.student_id = s.student_id " +
        "AND cg.student_id = " + student_id;
    }

    // public static String GET_ASSIGNMENT_SQL(int teacher_id, int course_id, int assignment_id) {
    //     return "SELECT * FROM assignments a " +
    //     "WHERE course_id = " + course_id + " " +
    //     "AND assignment_id = " + assignment_id;
    // }

    public static String DELETE_ASSIGNMENT_SQL(int course_id, int assignment_id) {
        return "DELETE FROM assignments " +
        "WHERE course_id = " + course_id + " " +
        "AND assignment_id = " + assignment_id;
    }

    public static String DELETE_ASSIGNMENT_GRADES_SQL(int course_id, int assignment_id) {
        return "DELETE FROM assignment_grades " +
        "WHERE course_id = " + course_id + " " +
        "AND assignment_id = " + assignment_id;
    }

    public static String ENTER_GRADE_SQL(int course_id, int assignment_id, int student_id, int points_earned, int points_possible) {
        return "INSERT INTO assignment_grades " +
        "(course_id, assignment_id, student_id, points_earned, points_possible, is_graded) " +
        "VALUES (" + course_id + ", " + assignment_id + ", " + student_id + ", " +
                    points_earned + ", " + points_possible + ", " + 1 + ")";
    }
    
    /**
     * Creates an assignment.
     * 
     * @return
     */
    public static String CREATE_ASSIGNMENT_SQL(
        int course_id, int assignment_id, int marking_period,
        int is_midterm, int is_final, String title, int point_value) {

        return "INSERT INTO assignments " +
        "(course_id, assignment_id, marking_period, is_midterm, is_final, title, point_value) " +
        "VALUES (" + course_id + ", " + assignment_id + ", " + marking_period + ", " +
                    is_midterm + ", " + is_final + ", \"" + title + "\", " + point_value + ")";
    }

    public static final String GET_ASSIGNMENTS_SQL =
        "SELECT * FROM assignments a, courses c " +
        "WHERE a.course_id = c.course_id " +
        "AND c.course_id = ? " +
        "ORDER BY a.assignment_id";

    public static final String GET_ASSIGNMENT_ID_FOR_ALTER_SQL =
        "SELECT * FROM assignments a, courses c " +
        "WHERE a.course_id = c.course_id " +
        "AND c.course_id = ? " +
        "AND a.title = ? " +
        "ORDER BY a.assignment_id";
    
    // public static String GET_TEACHER_FROM_USER_SQL(User user) {
    //     return "SELECT * FROM teachers t, users u " +
    //     "WHERE t.user_id = u.user_id " +
    //     "AND u.user_id = " + user.getUserId();
    // }

    // public static String GET_STUDENT_FROM_USER_SQL(User user) {
    //     return "SELECT * FROM students s, users u " +
    //     "WHERE s.user_id = u.user_id " +
    //     "AND u.user_id = " + user.getUserId();
    // }

    public static String GET_STUDENT_COURSES_SQL(int student_id) { // THIS. RIGHT HERE. THIS IS HOLY. PRAY TO IT.
        return "SELECT * FROM courses c, students s, course_grades cg " +
        "WHERE s.student_id = cg.student_id AND c.course_id = cg.course_id " +
        "AND s.student_id = " + student_id;
    }

    public static String GET_STUDENT_COURSE_GRADES_SQL(String title, int student_id) {
        return "SELECT * FROM courses c, course_grades cg " +
        "WHERE c.course_id = cg.course_id " +
        "AND c.title = " + title + " " +
        "AND cg.student_id = " + student_id;
    }
}