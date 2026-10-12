package com.scc;

import java.util.Scanner;

public class Main {
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args){

        System.out.println("\n==========================");
        System.out.println("Gym Member Ship System");
        System.out.println("==============================")
        System.out.println("1. LogIn: ");
        System.out.println("2. Register: ");
        System.out.println("3. Exit: ");
        System.out.println("Choose an option: ");
        String  choice = sc.nextInt();

        switch (choice){
            case "1":
                break;
            case "2":
                System.out.println("Enter new username: ");
                String user = sc.nextLine();
                System.out.println("Enter Password: ");
                String pass = sc.nextLine();



               break;
        }

    }
}
