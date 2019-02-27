package com.abcapps;

public class A {
    private static A a;

    private A() {
    }

    public static A getInstance() {
        if (a == null)
            a = new A();
        return a;
    }
}
