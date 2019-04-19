package com.abcapps;

public class A {

    static {
        System.out.println("STATIC");
    }

    private static A a;

    private A() {
        System.out.println("CONS");
    }

    public static A getInstance() {
        if (a == null) {
            System.out.println("getInstance()");
            a = new A();
        }
        return a;
    }

    public static void m1() {
        System.out.println("m1" + a);
    }

    public static void main(String[] args) {
        m1();
    }
}
