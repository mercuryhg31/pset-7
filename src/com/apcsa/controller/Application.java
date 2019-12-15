package com.apcsa.controller;

import java.util.Scanner;
import com.apcsa.data.PowerSchool;
import com.apcsa.model.User;

// java -cp "./src;./lib/sqlite-jdbc-3.28.0.jar" com/apcsa/controller/Application

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
        VIEW_FCLTY_DEPT,
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
                    activeUser.setPassword(Utils.getHash(in.next()));
                }

                System.out.println("\nHello, again, " + activeUser.getFirstName() + "!\n");
                if (activeUser.isStudent()) {
                    studentMenu();
                    switch (returnSelection(in.nextInt())) {
                        case VIEW_GRADES:
                            // do view grades things
                            break;
                        case VIEW_GRADES_COURSE:
                            //
                            break;
                        case CHANGE_PW:
                            changePassword();
                            break;
                        case LOGOUT:
                            //
                            break;
                        default:
                            System.out.println("\nInvalid selection.\n");
                    }
                } else if (activeUser.isTeacher()) {
                    teacherMenu();
                    switch (returnSelection(in.nextInt())) {
                        case VIEW_ENROLL_COURSE:
                            //
                            break;
                        case ADD_ASSNG:
                            //
                            break;
                        case DELETE_ASSNG:
                            //
                            break;
                        case ENTER_GRADE:
                            //
                            break;
                        case CHANGE_PW:
                            changePassword();
                            break;
                        case LOGOUT:
                            //
                            break;
                        default:
                            System.out.println("\nInvalid selection.\n");
                    }
                } else if (activeUser.isAdministrator()) {
                    adminMenu();
                    switch (returnSelection(in.nextInt())) {
                        case VIEW_FCLTY:
                            //
                            break;
                        case VIEW_FCLTY_DEPT:
                            //
                            break;
                        case VIEW_ST_ENROLL:
                            //
                            break;
                        case VIEW_ST_ENROLL_GRADE:
                            //
                            break;
                        case VIEW_ST_ENROLL_COURSE:
                            //
                            break;
                        case CHANGE_PW:
                            changePassword();
                            break;
                        case LOGOUT:
                            //
                            break;
                        default:
                            System.out.println("\nInvalid selection.\n");
                    }
                } else if (activeUser.isRoot()) {
                    rootMenu();
                    switch (returnSelection(in.nextInt())) {
                        case RESET_PW:
                            resetPassword();
                            break;
                        case FACTORY_RESET:
                            factoryReset();
                            break;
                        case LOGOUT:
                            logout();
                            break;
                        case SHUTDOWN:
                            shutdown();
                            break;
                        default:
                            System.out.println("\nInvalid selection.\n");
                    }
                }

            } else {
                System.out.println("\nInvalid username and/or password.");
            }
        }
    }

    public boolean confirm(String message) {
        return true;
        // String confirm;
        // do {
        //     System.out.print(message);
        //     confirm = in.next().toLowerCase();
        //     if (confirm == "y") {
        //         return true;
        //     }
        //     System.out.println(confirm);
        //     System.out.println(confirm == "y");
        // } while (confirm != "y" && confirm != "n"); // TODO this won't turn to yes when it does answer y/n??
        // return false;
    }

    public void logout() {
        if (confirm("Are you sure? y/n ")) {
            activeUser = null;
        }
    }

    public void changePassword() {
        System.out.println("\nEnter current password: ");
        String currentPW = Utils.getHash(in.next());
        System.out.println("Enter new password: ");
        String newPW = Utils.getHash(in.next());;

        if (currentPW != activeUser.getPassword()) {
            System.out.println("\nInvalid current password.");
        } else {
            activeUser.setPassword(newPW);
        }
    }

    /**
     * For root only - maybe this shouldn't be in Application, but User?
     */
    public void resetPassword() { // TODO wip
        System.out.println("\nUsername: ");
        String username = in.next(); // make this a User instead of string
        // User user;

        try {
            // user = 
        }

        if (confirm("Are you sure you want to reset the password for USERNAME? (y/n) ")) {
            // activeUser = User();
            // activeUser.setPassword(Utils.getHash(activeUser.getUsername()));
        }
    }

    public factoryReset() {
        if (confirm("\nAre you sure you want to reset all settings and data? (y/n)")) {
            PowerSchool.initialize(true);
            System.out.println("\nSuccessfully reset database.\n");
        }
    }

    public void shutdown(Exception e) {
        if (in != null) {
            in.close();
        }
        System.out.println("Encountered unrecoverable error. Shutting down...\n");
        System.out.println(e.getMessage());
        System.exit(0);
    }

    public void shutdown() {
        if (confirm("Are you sure? (y/n) ")) {
            if (in != null) {
                in.close();
            }
            System.out.println("\nGoodbye!");
            System.exit(0);
        }
    }

    public void studentMenu() {
        System.out.println("[1] View course grades.");
        System.out.println("[2] View assignment grades by course.");
        System.out.println("[3] Change password.");
        System.out.println("[4] Logout.");
        System.out.print("\n::: ");
    }

    public void teacherMenu() {
        System.out.println("[1] View enrollment by course.");
        System.out.println("[2] Add assignment.");
        System.out.println("[3] Delete assignment.");
        System.out.println("[4] Enter grade.");
        System.out.println("[5] Change password.");
        System.out.println("[6] Logout.");
        System.out.print("\n::: ");
    }

    public void adminMenu() {
        System.out.println("[1] View faculty.");
        System.out.println("[2] View faculty by department.");
        System.out.println("[3] View student enrollment.");
        System.out.println("[4] View student enrollment by grade.");
        System.out.println("[5] View student enrollment by course.");
        System.out.println("[6] Change password.");
        System.out.println("[7] Logout.");
        System.out.print("\n::: ");
    }

    public void rootMenu() {
        System.out.println("[1] Reset user password.");
        System.out.println("[2] Factory reset database.");
        System.out.println("[3] Logout.");
        System.out.println("[4] Shutdown.");
        System.out.print("\n::: ");
    }

    public Menu returnSelection(int n) {
        if (activeUser.isStudent()) {
            switch (n) {
                case 1:
                    return Menu.VIEW_GRADES;
                case 2:
                    return Menu.VIEW_GRADES_COURSE;
                case 3:
                    return Menu.CHANGE_PW;
                case 4:
                    return Menu.LOGOUT;
                default:
                    return Menu.INVALID;
            }
        } else if (activeUser.isTeacher()) {
            switch (n) {
                case 1:
                    return Menu.VIEW_ST_ENROLL_COURSE;
                case 2:
                    return Menu.ADD_ASSNG;
                case 3:
                    return Menu.DELETE_ASSNG;
                case 4:
                    return Menu.ENTER_GRADE;
                case 5:
                    return Menu.CHANGE_PW;
                case 6:
                    return Menu.LOGOUT;
                default:
                    return Menu.INVALID;
            }
        } else if (activeUser.isAdministrator()) {
            switch (n) {
                case 1:
                    return Menu.VIEW_FCLTY;
                case 2:
                    return Menu.VIEW_FCLTY_DEPT;
                case 3:
                    return Menu.VIEW_ST_ENROLL;
                case 4:
                    return Menu.VIEW_ST_ENROLL_GRADE;
                case 5:
                    return Menu.VIEW_ENROLL_COURSE;
                case 6:
                    return Menu.CHANGE_PW;
                case 7:
                    return Menu.LOGOUT;
                default:
                    return Menu.INVALID;
            }
        } else if (activeUser.isRoot()) {
            switch (n) {
                case 1:
                    return Menu.RESET_PW;
                case 2:
                    return Menu.FACTORY_RESET;
                case 3:
                    return Menu.LOGOUT;
                case 4:
                    return Menu.SHUTDOWN;
                default:
                    return Menu.INVALID;
            }
        } else {
            return Menu.INVALID;
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