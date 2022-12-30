package com.example.creditapp;

import java.time.LocalDate;

public class DataUtils {
    public static boolean isEmailValid(String s){
        if (isEmptyOrNull(s)) {
            return false;
        }
        return s.matches("[\\w.]{2,50}@[\\w.]{2,20}");
    }

    public static boolean isOlderThan(LocalDate date, int age){
        LocalDate dateOfMature = date.plusYears(age);
        return dateOfMature.isAfter(LocalDate.now()) || dateOfMature.isEqual(LocalDate.now());
    }

    public static boolean checkLengthBetween(String s, int from, int to){
        if (isEmptyOrNull(s)) {
            return false;
        }
        return s.length()>=from && s.length()<=to;
    }

    public static boolean checkLengthEquals(String s, int length){
        if (isEmptyOrNull(s)) {
            return false;
        }
        return s.length()==length;
    }

    public static boolean isEmptyOrNull(String s){
        return s==null || s.isEmpty();
    }
}
