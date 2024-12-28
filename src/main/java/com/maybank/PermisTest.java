package com.maybank;

import java.util.*;
import java.io.*;

public class PermisTest {

    public static void main(String[] args) throws Exception {
        System.out.println(System.getProperty("user.home"));
        try
        {
            // home dir appended before file path
            Scanner scObj = new Scanner (new File(System.getProperty("user.home") + "/input.txt"));
            int no1 = scObj.nextInt();
            int no2 = scObj.nextInt();
            System.out.println("The two nos are : " + no1 + ", " + no2);
            // Write to a File. home dir appended before file path
            Formatter outObj = new Formatter(new File(System.getProperty("user.home") + "/output.txt"));
            int totalSum = no1 + no2;
            System.out.println("The total sum value is " + totalSum);
            outObj.format("%d", totalSum);
            outObj.close();
        }
        catch(Exception ee)
        {
            System.out.println("Error "+ee.toString());
        }
    }
}
