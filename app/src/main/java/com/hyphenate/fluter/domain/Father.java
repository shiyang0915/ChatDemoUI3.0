package com.hyphenate.fluter.domain;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

public class Father implements Parcelable {


    protected Father(Parcel in) {

    }

    protected Father() {

    }

    public static final Creator<Father> CREATOR = new Creator<Father>() {
        @Override
        public Father createFromParcel(Parcel in) {
            return new Father(in);
        }

        @Override
        public Father[] newArray(int size) {
            return new Father[size];
        }
    };

    public static void test01() {
        System.out.println("father的静态方法");
    }

    public final void test02() {
        System.out.println("father的普通方法");
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }


}
