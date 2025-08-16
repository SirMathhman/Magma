package com.example;

public class App {
    public static String greet(String name) {
        if (name == null || name.isEmpty()) return "Hello, World!";
        return "Hello, " + name + "!";
    }

    public static void main(String[] args) {
        System.out.println(greet(args.length > 0 ? args[0] : null));
    }
}
