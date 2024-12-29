package com.maybank;

// Question 1
public class ArrArgs {
    public static void main(String[] args) {
        // args validation if they are present of not
        if (args.length == 0) {
            System.out.println("No arguments provided to the program.");
            return;
        }
        // replaced do-while with for loop because iterations are limited.
        // No need to check ArrayIndexOutOfBoundsException
        for (int k = 0; k < args.length; k++) {
            System.out.println("Value of input argument " + k + " is: " + args[k]);
        }
    }
}