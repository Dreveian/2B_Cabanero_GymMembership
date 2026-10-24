package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;

import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    private final UserDao userDao;
    private final Scanner sc;

    public AdminMenu(UserDao userdao, Scanner sc) {
        this.userDao = userdao;
        this.sc = sc;
    }

    public void display(User admin) {
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
                    manageUsers(admin);
                    break;
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

    // ================= MANAGE USERS =================

    private void manageUsers(User admin) {
        int choice;
        do {
            System.out.println("\n==========================================");
            System.out.println("        MANAGE USERS");
            System.out.println("==========================================");
            System.out.println("[1] View All Users");
            System.out.println("[2] Pending Approvals");
            System.out.println("[3] Change User Role");
            System.out.println("[4] Change User Status");
            System.out.println("[0] Back");
            System.out.println("------------------------------------------");
            System.out.print("Enter choice: ");

            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                choice = -1;
            }

            switch (choice) {
                case 1:
                    printUsers(userDao.getUsers(null));
                    break;
                case 2:
                    handlePending();
                    break;
                case 3:
                    handleChangeRole(admin);
                    break;
                case 4:
                    handleChangeStatus(admin);
                    break;
                case 0:
                    System.out.println();
                    break;
                default:
                    System.out.println("\nInvalid choice. Try again.");
            }
        } while (choice != 0);
    }

    private void printUsers(List<User> users) {
        if (users.isEmpty()) {
            System.out.println("\nNo users found.");
            return;
        }
        System.out.println();
        System.out.printf("%-5s %-15s %-22s %-12s %-10s %-20s%n",
                "ID", "USERNAME", "FULL NAME", "ROLE", "STATUS", "CREATED");
        System.out.println("-".repeat(88));
        for (User u : users) {
            System.out.printf("%-5d %-15s %-22s %-12s %-10s %-20s%n",
                    u.user_id(), u.username(), u.fullname(), u.role(), u.status(), u.created_at());
        }
    }

    private void handlePending() {
        List<User> pending = userDao.getUsers("PENDING");
        printUsers(pending);
        if (pending.isEmpty()) return;

        Integer id = askId("\nEnter User ID to review (blank to cancel): ");
        if (id == null) return;

        User target = userDao.findById(id);
        if (target == null || !"PENDING".equals(target.status())) {
            System.out.println("-> That user is not in the pending list.");
            return;
        }

        System.out.print("[1] Approve | [2] Reject | [0] Cancel: ");
        String c = sc.nextLine().trim();
        if (c.equals("1") && userDao.updateStatus(id, "APPROVED")) {
            System.out.println("-> " + target.username() + " approved.");
        } else if (c.equals("2") && userDao.updateStatus(id, "REJECTED")) {
            System.out.println("-> " + target.username() + " rejected.");
        } else {
            System.out.println("-> Cancelled.");
        }
    }

    private void handleChangeRole(User admin) {
        printUsers(userDao.getUsers(null));

        Integer id = askId("\nEnter User ID (blank to cancel): ");
        if (id == null) return;

        User target = userDao.findById(id);
        if (target == null) {
            System.out.println("-> User not found.");
            return;
        }
        if (target.user_id() == admin.user_id()) {
            System.out.println("-> You cannot change your own role.");
            return;
        }

        System.out.print("New role [1] ADMIN | [2] FRONT_DESK | [3] TRAINER | [0] Cancel: ");
        String c = sc.nextLine().trim();
        String role = c.equals("1") ? "ADMIN" : c.equals("2") ? "FRONT_DESK" : c.equals("3") ? "TRAINER" : null;

        if (role == null) {
            System.out.println("-> Cancelled.");
            return;
        }
        if (userDao.updateRole(id, role)) {
            System.out.println("-> " + target.username() + " is now " + role + ".");
        }
    }

    private void handleChangeStatus(User admin) {
        printUsers(userDao.getUsers(null));

        Integer id = askId("\nEnter User ID (blank to cancel): ");
        if (id == null) return;

        User target = userDao.findById(id);
        if (target == null) {
            System.out.println("-> User not found.");
            return;
        }
        if (target.user_id() == admin.user_id()) {
            System.out.println("-> You cannot change your own status.");
            return;
        }

        System.out.print("New status [1] APPROVED | [2] REJECTED | [3] PENDING | [0] Cancel: ");
        String c = sc.nextLine().trim();
        String status = c.equals("1") ? "APPROVED" : c.equals("2") ? "REJECTED" : c.equals("3") ? "PENDING" : null;

        if (status == null) {
            System.out.println("-> Cancelled.");
            return;
        }
        if (userDao.updateStatus(id, status)) {
            System.out.println("-> " + target.username() + " is now " + status + ".");
        }
    }

    private Integer askId(String prompt) {
        System.out.print(prompt);
        String input = sc.nextLine().trim();
        if (input.isEmpty()) return null;
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("-> Invalid ID.");
            return null;
        }
    }
}