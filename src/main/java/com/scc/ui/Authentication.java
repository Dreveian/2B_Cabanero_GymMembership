package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;

import java.util.Scanner;

public class Authentication {
    private final UserDao userDao;
    private final Scanner sc;

    public Authentication(UserDao userDao, Scanner sc) {
        this.userDao = userDao;
        this.sc = sc;
    }

    public void start() {
        boolean running = true;

        while (running) {
            System.out.println("\n==========================");
            System.out.println(" GYM MEMBERSHIP SYSTEM ");
            System.out.println("==========================");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> handleLogin();
                case "2" -> handleRegister();
                case "3" -> {
                    System.out.println("Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid option! Please choose 1, 2, or 3.");
            }
        }
    }

    private void handleRegister() {
        String fullname, username, password, role;

        while (true) {
            System.out.print("Full name: ");
            fullname = sc.nextLine().trim();
            if (!fullname.isEmpty()) break;
            System.out.println("Full name cannot be empty. Try again.");
        }

        while (true) {
            System.out.print("Username (4-20 letters/numbers): ");
            username = sc.nextLine().trim();
            if (username.matches("[A-Za-z0-9_]{4,20}")) break;
            System.out.println("Invalid username. Use 4-20 letters, numbers, or underscore only.");
        }

        while (true) {
            System.out.print("Password (at least 6 characters): ");
            password = sc.nextLine();
            if (password.length() >= 6) break;
            System.out.println("Password is too short. Try again.");
        }

        while (true) {
            System.out.print("Select Role [1] ADMIN | [2] FRONT_DESK | [3] TRAINER: ");
            String roleInput = sc.nextLine().trim();
            if (roleInput.equals("1")) { role = "ADMIN"; break; }
            else if (roleInput.equals("2")) { role = "FRONT_DESK"; break; }
            else if (roleInput.equals("3")) { role = "TRAINER"; break; }
            System.out.println("Invalid choice. Please enter 1, 2, or 3.");
        }

        userDao.registerUser(fullname, username, password, role);
    }

    private void handleLogin() {
        System.out.print("Enter Username: ");
        String username = sc.nextLine().trim();

        System.out.print("Enter Password: ");
        String password = sc.nextLine();

        User user = userDao.login(username, password);

        if (user != null) {
            System.out.println("\n -> Welcome " + user.fullname() + " | ROLE: " + user.role());
            routeByRole(user);
        }
    }

    private void routeByRole(User user) {
        switch (user.role()) {
            case "ADMIN" -> new AdminMenu(userDao, sc).display(user);
            case "FRONT_DESK" -> new FrontDeskMenu(userDao, sc).display(user);
            case "TRAINER" -> new TrainerMenu(userDao, sc).display(user);
            default -> System.out.println("Account role not found!");
        }
    }
}