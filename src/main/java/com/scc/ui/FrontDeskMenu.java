package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;

import java.util.Scanner;

public class FrontDeskMenu {
    private final UserDao userDao;
    private final Scanner sc;

    public FrontDeskMenu(UserDao userDao, Scanner sc) {
        this.userDao = userDao;
        this.sc = sc;
    }

    public void display(User user) {
        int choice;
        do {
            System.out.println("==========================================");
            System.out.println("        GYM MEMBERSHIP SYSTEM");
            System.out.println("        FRONT DESK MENU");
            System.out.println("==========================================");
            System.out.println("[1] Manage Members");
            System.out.println("[2] Payments & Renewals");
            System.out.println("[3] Attendance");
            System.out.println("[0] Logout");
            System.out.println("------------------------------------------");
            System.out.print("Enter choice: ");

            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                choice = -1;
            }

            switch (choice) {
                case 1, 2, 3 -> System.out.println("\nComing soon...\n");
                case 0 -> System.out.println("\nLogging out... Goodbye!");
                default -> System.out.println("\nInvalid choice. Try again.\n");
            }
        } while (choice != 0);
    }
}