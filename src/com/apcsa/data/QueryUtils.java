package com.apcsa.data;

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

    public static final String GET_DEPARTMENTS_SQL =
        "SELECT * FROM teachers as t, departments as d " +
        "WHERE t.department_id = d.department_id " +
        "AND d.title = ? " +
        "ORDER BY title";
}