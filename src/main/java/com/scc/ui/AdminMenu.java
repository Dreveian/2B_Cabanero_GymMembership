package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;

import java.util.Scanner;

public class AdminMenu {
    private final UserDao userDao;
    private  final Scanner sc;

    public AdminMenu(UserDao userdao, Scanner sc){
        this.userDao = userdao;
        this.sc = sc;
    }

    public void display(User admin){
        int choice;
        do {
            System.out.println("==========================================");
            System.out.println("        GYM MEMBERSHIP SYSTEM");
            System.out.println("        ADMIN MENU");
            System.out.println("==========================================");
            System.out.println("[1] Manage Users");
            System.out.println("[2] Manage Membership Plans");
            System.out.println("[3] Manage Members");
            System.out.println("[4] Payments & Renewals");
            System.out.println("[5] Attendance");
            System.out.println("[6] Reports");
            System.out.println("[7] Audit Logs");
            System.out.println("[0] Logout");
            System.out.println("------------------------------------------");
            System.out.print("Enter choice: ");

            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                choice = -1;
            }

            switch (choice) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                    System.out.println("\nComing soon...\n");
                    break;
                case 0:
                    System.out.println("\nLogging out... Goodbye!");
                    break;
                default:
                    System.out.println("\nInvalid choice. Try again.\n");
            }
        } while (choice != 0);
    }
}
