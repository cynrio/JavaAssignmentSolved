package com.maybank.resturantMenu;


import java.util.Scanner;

// Question 4
public class RestaurantMenu {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            MenuManager menuManager = new MenuManager();

            System.out.println("Enter menu items (name, category, price). Type 'done' at Name to finish:");
            while (true) {
                System.out.print("Name: ");
                String name = scanner.nextLine();
                if (name.equalsIgnoreCase("done")) break;

                System.out.print("Category: ");
                String category = scanner.nextLine();

                System.out.print("Price: ");
                double price = scanner.nextDouble();
                scanner.nextLine();

                menuManager.addMenuItem(new MenuItem(name, category, price));
            }

            int choice = 1;
            do {
                switch (choice) {
                    case 1 -> {
                        System.out.println("\nMenu List(Sorted by Category):");
                        menuManager.sortByCategory();
                        menuManager.displayMenu();
                    }
                    case 2 -> {
                        System.out.println("\nSorted by Price:");
                        menuManager.sortByPrice();
                        menuManager.displayMenu();
                    }
                    case 3 -> {
                        System.out.println("\nSorted by Name:");
                        menuManager.sortByName();
                        menuManager.displayMenu();
                    }
                }

                System.out.print("""
                        
                        Select a number:
                        \t1: Sorted by Category
                        \t2: Sorted by Price
                        \t3: Sorted by Name
                        \t0: Exit
                        """);
                choice = scanner.nextInt();
            } while (choice > 0 && choice < 4);
            scanner.close();
        } catch (Exception e){
            System.out.println("Invalid input provided by the user.");
        }
    }
}

