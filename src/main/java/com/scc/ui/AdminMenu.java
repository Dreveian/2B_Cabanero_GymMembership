package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;

import java.util.List;
import java.util.Objects;
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
                    filterStatus();
                    break;
                case 2:
                    handlePending("PENDING");
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

    private void tableDisplay(List<User> users, String title) {
        System.out.println("\n====================================== " + title + " ======================================");
        if (users.isEmpty()) {
            System.out.println("\nNo users found.");
            return;
        }

        System.out.println("\n+------+--------------------+--------------------+--------------+----------+---------------------+");
        System.out.printf("| %-4s | %-18s | %-18s | %-12s | %-8s | %-19s |\n", "ID", "USERNAME", "FULL NAME", "ROLE", "STATUS", "DATE CREATED");
        System.out.println("+------+--------------------+--------------------+--------------+----------+---------------------+");
        for (User u : users) {
            System.out.printf("| %-4s | %-18s | %-18s | %-12s | %-8s | %-19s |\n", u.user_id(), u.username(), u.fullname(), u.role(), u.status(), u.created_at());
        }
        System.out.println("+------+--------------------+--------------------+--------------+----------+---------------------+");
    }

    private void filterStatus() {
        tableDisplay(userDao.getUsers(null), "ALL USERS");
        while (true) {
            String input = readString("Filter ([1] APPROVED | [2] PENDING | [3] REJECTED | [0] Back): ");

            if (input.equals("0")) {
                return;
            }

            String status = input.equals("1") ? "APPROVED" : input.equals("2") ? "PENDING" : input.equals("3") ? "REJECTED" : null;

            if (status != null) {
                tableDisplay(userDao.getUsers(status), status);
            } else {
                System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void handlePending(String pending) {
        List<User> user = userDao.getUsers(pending);

        tableDisplay(user, "PENDING USER APPROVALS");

        if (user.isEmpty()) return;

        Integer id = askId("\nEnter User ID to review (blank to cancel): ");
        if (id == null) return;

        User target = userDao.findById(id);
        if (target == null || !"PENDING".equals(target.status())) {
            System.out.println("-> That user is not in the pending list.");
            return;
        }

        String c = readString("[1] Approve | [2] Reject | [0] Cancel: ");

        switch (c) {
            case "1":
                if (userDao.updateStatus(id, "APPROVED")) {
                    System.out.println("-> " + target.username() + " approved.");
                } else {
                    System.out.println("-> [ERROR] Failed to approve.");
                }
                break;
            case "2":
                if (userDao.updateStatus(id, "REJECTED")) {
                    System.out.println("-> " + target.username() + " rejected.");
                } else {
                    System.out.println("-> [ERROR] Failed to reject.");
                }
                break;
            case "0":
                System.out.println("-> Cancelled.");
                break;
            default:
                System.out.println("-> Invalid choice.");
        }
    }

    private void handleChangeRole(User admin) {
        tableDisplay(userDao.getUsers(null), "UPDATE USER ROLE");

        Integer id = askId("\nEnter User ID (blank or 0 to cancel): ");
        if (id == null || id == 0) return;

        User target = userDao.findById(id);
        if (target == null) {
            System.out.println("-> User not found.");
            return;
        }
        if (Objects.equals(target.user_id(), admin.user_id())) {
            System.out.println("-> You cannot change your own role.");
            return;
        }

        String role = readValidRole();

        if (role == null) {
            System.out.println("-> Cancelled.");
            return;
        }
        if (userDao.updateRole(id, role)) {
            System.out.println("-> " + target.username() + " is now " + role + ".");
        } else {
            System.out.println("-> [ERROR] Failed to update role.");
        }
    }

    private String readValidRole() {
        while (true) {
            String c = readString("New role [1] ADMIN | [2] FRONT_DESK | [3] TRAINER | [0] Cancel: ");
            if (c.equals("0")) return null;

            String role = c.equals("1") ? "ADMIN" : c.equals("2") ? "FRONT_DESK" : c.equals("3") ? "TRAINER" : null;
            if (role != null) return role;

            System.out.println(" -> [ERROR] Invalid Role");
        }
    }

    private void handleChangeStatus(User admin) {
        tableDisplay(userDao.getUsers(null), "UPDATE USER STATUS");

        Integer id = askId("\nEnter User ID (blank or 0 to cancel): ");
        if (id == null || id == 0) return;

        User target = userDao.findById(id);
        if (target == null) {
            System.out.println("-> User not found.");
            return;
        }
        if (Objects.equals(target.user_id(), admin.user_id())) {
            System.out.println("-> You cannot change your own status.");
            return;
        }

        String status = readValidStatus();

        if (status == null) {
            System.out.println("-> Cancelled.");
            return;
        }
        if (userDao.updateStatus(id, status)) {
            System.out.println("-> " + target.username() + " is now " + status + ".");
        } else {
            System.out.println("-> [ERROR] Failed to update status.");
        }
    }

    private String readValidStatus() {
        while (true) {
            String c = readString("New status [1] APPROVED | [2] PENDING | [3] REJECTED | [0] Cancel: ");
            if (c.equals("0")) return null;

            String status = c.equals("1") ? "APPROVED" : c.equals("2") ? "PENDING" : c.equals("3") ? "REJECTED" : null;

            if (status != null) return status;

            System.out.println(" -> [ERROR] Invalid Status. Choose 1, 2, 3, or 0 to cancel.");
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

    private String readString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("It must not be empty.");
        }
    }

}