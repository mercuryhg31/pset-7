package com.apcsa.controller;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.Scanner;
import java.util.InputMismatchException;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Utils {

    /**
     * Returns an MD5 hash of the user's plaintext password.
     *
     * @param plaintext the password
     * @return an MD5 hash of the password
     */

    public static String getHash(String plaintext) {
        StringBuilder pwd = new StringBuilder();

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            md.update(plaintext.getBytes());
            byte[] digest = md.digest(plaintext.getBytes());

            for (int i = 0; i < digest.length; i++) {
                pwd.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return pwd.toString();
    }

    public static boolean confirm(String message, Scanner in) {
        String confirmation;
        do {
            System.out.print(message);
            confirmation = in.next().toLowerCase();
            if (confirmation.equals("y")) {
                return true;
            }
            System.out.println(confirmation);
            System.out.println(confirmation.equals("y"));
        } while (!confirmation.equals("y") && !confirmation.equals("n"));
        return false;
    }

    /**
     * Safely reads an integer from the user.
     * 
     * @param in the Scanner
     * @param invalid an invalid (but type-safe) default
     * @return the value entered by the user or the invalid default
     */
        
    public static int getInt(Scanner in, int invalid) {
        try {
            return in.nextInt();                // try to read and return user-provided value
        } catch (InputMismatchException e) {            
            return invalid;                     // return default in the even of an type mismatch
        } finally {
            in.nextLine();                      // always consume the dangling newline character
        }
    }

    /**
     * Rounds a number to a set number of decimal places.
     * 
     * @param value the value to round
     * @param places the number of decimal places
     * @return the rounded value
     */
        
    private static double round(double value, int places) {
        return new BigDecimal(Double.toString(value))
            .setScale(places, RoundingMode.HALF_UP)
            .doubleValue();
    }
}
