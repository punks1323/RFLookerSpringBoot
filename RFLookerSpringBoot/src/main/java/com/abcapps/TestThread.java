package com.abcapps;

public class TestThread {

    static Runnable runnable = new Runnable() {
        @Override
        public void run() {

        }
    };

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            new Thread(runnable).start();
        }
    }
}
