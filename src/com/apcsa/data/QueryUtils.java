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

    public static String GET_TEACHERS_BY_DEPARTMENTS_SQL(int departmentId) {
        return "SELECT * FROM teachers t " +
        "LEFT JOIN departments d ON t.department_id = d.department_id " +
        "WHERE d.department_id = " + departmentId + " " +
        "ORDER BY t.last_name, t.first_name";
    }

    public static String selectStatement(String tables, String elements, String where, String order) { // Michael, it's so much easier to just write themmm
        String selStmt =
            (!(order.equals(null)) && !(where.equals(null))) ?
            "SELECT ? FROM ? " +
            "WHERE ?" +
            "ORDER BY ?" :
            (!(where.equals(null)) && (order.equals(null))) ?
            "SELECT ? FROM ?" +
            "WHERE ?":
            (where.equals(null) && !(order.equals(null))) ?
            "SELECT ? FROM ?" +
            "ORDER BY ?":
            (where.equals(null) && order.equals(null)) ?
            "SELECT ? FROM ?" :
            null;

            int replace = 1;
            for (int i = 0; i <= selStmt.length(); i++) {
                if (selStmt.charAt(i) == '?') {
                    selStmt = selStmt.substring(0, i - 1) +
                    ((replace == 1) ? elements :
                    (replace == 2) ? tables :
                    (replace == 3) ? where :
                    (replace == 4) ? order :
                    null) +
                    selStmt.substring(i);
                    replace++;
                }
            }



        return selStmt;
    }

    public static String updateStatement(String table, String elementUpdates, String where) {
        String updStmt = (!(where.equals(null))) ?
            "UPDATE ? " +
                "? " +
            "WHERE ?"
            :
            "UPDATE ? " +
                "? ";

        int replace = 1;
        for (int i = 0; i <= updStmt.length() - 1; i++) {
            if (updStmt.charAt(i) == '?') {
                updStmt = updStmt.substring(0, i - 1) +
                    ((replace == 1) ? table :
                    (replace == 2) ? elementUpdates :
                    (replace == 3) ? where :
                    null) +
                    updStmt.substring(i);
                    replace++;
            }
        }
        //String updStmt = (where.equals(null)) ? "UPDATE " + table + " SET " +

        return updStmt;
    }


    // Allows students to see their grades by course. TODO

    // public static final String GET_STUDENT_GRADES_COURSE_SQL =
    //     "SELECT c.course_no FROM "
}