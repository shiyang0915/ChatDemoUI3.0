package com.hyphenate.fluter.domain;

import android.os.Handler;
import android.os.Message;

public class Son extends Father {
    public static final String str = "";

    public static void main(String args[]) {
        Father father = new Son();
        ((Son) father).test03();
    }


    public static void test01() {

    }


    public void test03() {
        try {
            int a = 6 / 0;
            return;
        } catch (Exception e) {
            System.out.println("catch");
            return;
        } finally {
            System.out.print("finally");
        }
    }


    public static class Shiyang {
        public static int test01;
        public String string;

        public static String test02() {
            return "";
        }

        public String test024() {
            return "";
        }

    }

    static String hahahahahah = "";


    private class MyHandler extends Handler {
        public String test;

        public void haha() {
            hahahahahah = "";
        }


    }

}
