package com.maybank.resturantMenu;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
