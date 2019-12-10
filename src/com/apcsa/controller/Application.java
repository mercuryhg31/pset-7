package com.apcsa.controller;

import java.util.Scanner;
import com.apcsa.data.PowerSchool;
import com.apcsa.model.User;

public class Application {

    private Scanner in;
    private User activeUser;

    enum Menu {
        VIEW_GRADES,
        VIEW_GRADES_COURSE,
        CHANGE_PW,
        LOGOUT,
        VIEW_ENROLL_COURSE,
        ADD_ASSNG,
        DELETE_ASSNG,
        ENTER_GRADE,
        VIEW_FCLTY,
        VIEW_FCLTY_B_DEPT,
        VIEW_ST_ENROLL,
        VIEW_ST_ENROLL_GRADE,
        VIEW_ST_ENROLL_COURSE,
        RESET_PW, // unless this means change root password TODO check
        FACTORY_RESET,
        SHUTDOWN,
        INVALID;
    }

    /**
     * Creates an instance of the Application class, which is responsible for interacting
     * with the user via the command line interface.
     */

    public Application() {
        this.in = new Scanner(System.in);

        try {
            PowerSchool.initialize(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts the PowerSchool application.
     */

    public void startup() {
        System.out.println("PowerSchool -- now for students, teachers, and school administrators!");

        // continuously prompt for login credentials and attempt to login

        while (true) {
            System.out.print("\nUsername: ");
            String username = in.next();

            System.out.print("Password: ");
            String password = in.next();

            // if login is successful, update generic user to administrator, teacher, or student

            if (login(username, password)) {
                activeUser = activeUser.isAdministrator()
                    ? PowerSchool.getAdministrator(activeUser) : activeUser.isTeacher()
                    ? PowerSchool.getTeacher(activeUser) : activeUser.isStudent()
                    ? PowerSchool.getStudent(activeUser) : activeUser.isRoot()
                    ? activeUser : null;

                if (isFirstLogin() && !activeUser.isRoot()) {
                    System.out.print("Please set your password: ");
                    activeUser.setPassword(in.next());
                }

                if (activeUser.isStudent()) {
                    studentMenu();
                } else if (activeUser.isTeacher()) {
                    teacherMenu();
                } else if (activeUser.isAdministrator()) {
                    adminMenu();
                } else if (activeUser.isRoot()) {
                    rootMenu();
                }

            } else {
                System.out.println("\nInvalid username and/or password.");
            }
        }
    }

    public void studentMenu() {
        System.out.println("[1] View course grades.");
        System.out.println("[2] View assignment grades by course.");
        System.out.println("[3] Change password.");
        System.out.println("[4] Logout.");
    }

    public void teacherMenu() {
        System.out.println("[1] View enrollment by course.");
        System.out.println("[2] Add assignment.");
        System.out.println("[3] Delete assignment.");
        System.out.println("[4] Enter grade.");
        System.out.println("[5] Change password.");
        System.out.println("[6] Logout.");
    }

    public void adminMenu() {
        // System.out.println("\nHello, again, NAME!\n");
        System.out.println("[1] View faculty.");
        System.out.println("[2] View faculty by department.");
        System.out.println("[3] View student enrollment.");
        System.out.println("[4] View student enrollment by grade.");
        System.out.println("[5] View student enrollment by course.");
        System.out.println("[6] Change password.");
        System.out.println("[7] Logout.\n");
        System.out.println("\n::: ");
    }

    public void rootMenu() {
        System.out.println("\nHello, again, Root!\n");
        System.out.println("[1] Reset user password.");
        System.out.println("[2] Factory reset database.");
        System.out.println("[3] Logout.");
        System.out.println("[4] Shutdown.\n");
        System.out.println("\n::: ");
    }

    public Menu returnSelection(int n) {
        if (activeUser.isStudent()) {
            switch (n) {
                case 1:
                    return Menu.VIEW_GRADES;
                    break;
                case 2:
                    return Menu.VIEW_GRADES_COURSE;
                    break;
                case 3:
                    return Menu.CHANGE_PW;
                case 4:
                    return Menu.LOGOUT;
                default:
                    return Menu.INVALID;
            }
        } else if (activeUser.isTeacher()) {
            // TODO teacher selection cases
        } else if (activeUser.isAdministrator()) {
            // TODO admin selection cases
        } else if (activeUser.isRoot()) {
            switch (n) {
                case 1:
                    return Menu.RESET_PW;
                    break;
                case 2:
                    return Menu.FACTORY_RESET;
                    break;
                case 3:
                    return Menu.LOGOUT;
                    break;
                case 4:
                    return Menu.SHUTDOWN;
                    break;
                default:
                    return Menu.INVALID;
            }
        }
    }

    /**
     * Logs in with the provided credentials.
     *
     * @param username the username for the requested account
     * @param password the password for the requested account
     * @return true if the credentials were valid; false otherwise
     */

    public boolean login(String username, String password) {
        activeUser = PowerSchool.login(username, password);

        return activeUser != null;
    }

    /**
     * Determines whether or not the user has logged in before.
     *
     * @return true if the user has never logged in; false otherwise
     */

    public boolean isFirstLogin() {
        return activeUser.getLastLogin().equals("0000-00-00 00:00:00.000");
    }

    /////// MAIN METHOD ///////////////////////////////////////////////////////////////////

    /*
     * Starts the PowerSchool application.
     *
     * @param args unused command line argument list
     */

    public static void main(String[] args) {
        Application app = new Application();

        app.startup();
    }
}