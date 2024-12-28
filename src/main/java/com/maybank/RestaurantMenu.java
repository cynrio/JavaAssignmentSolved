package com.maybank;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class RestaurantMenu {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        MenuManager menuManager = new MenuManager();

        System.out.println("Enter menu items (name, category, price). Type 'done' to finish:");
        while (true) {
            System.out.print("Name: ");
            String name = scanner.nextLine();
            if (name.equalsIgnoreCase("done")) break;

            System.out.print("Category: ");
            String category = scanner.nextLine();

            System.out.print("Price: ");
            double price = scanner.nextDouble();
            scanner.nextLine(); // Consume the newline

            menuManager.addMenuItem(new MenuItem(name, category, price));
        }

        System.out.println("\nMenu List:");
        menuManager.displayMenu();

        System.out.println("\nSorted by Category:");
        menuManager.sortByCategory();
        menuManager.displayMenu();

        System.out.println("\nSorted by Price:");
        menuManager.sortByPrice();
        menuManager.displayMenu();

        System.out.println("\nSorted by Name:");
        menuManager.sortByName();
        menuManager.displayMenu();

        scanner.close();
    }


}

class MenuItem {
    private String name;
    private String category;
    private double price;

    public MenuItem(String name, String category, double price) {
        this.name = name;
        this.category = category;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - $%.2f", name, category, price);
    }
}

class MenuManager {
    private List<MenuItem> menuList = new ArrayList<>();

    public void addMenuItem(MenuItem item) {
        menuList.add(item);
    }

    public void displayMenu() {
        for (MenuItem item : menuList) {
            System.out.println(item);
        }
    }

    public void sortByCategory() {
        menuList.sort(Comparator.comparing(MenuItem::getCategory).thenComparing(MenuItem::getName));
    }

    public void sortByPrice() {
        menuList.sort(Comparator.comparingDouble(MenuItem::getPrice));
    }

    public void sortByName() {
        menuList.sort(Comparator.comparing(MenuItem::getName));
    }
}