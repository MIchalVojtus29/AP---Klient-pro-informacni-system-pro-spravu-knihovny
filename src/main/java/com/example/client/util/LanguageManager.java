package com.example.client.util;

import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageManager {
    private static Locale currentLocale = new Locale("cs", "CZ");
    private static ResourceBundle bundle = loadBundle();

    private static ResourceBundle loadBundle() {
        return ResourceBundle.getBundle("bundle.lang", currentLocale);
    }

    public static ResourceBundle getBundle() {
        return bundle;
    }

    public static void setLocale(Locale locale) {
        currentLocale = locale;
        bundle = loadBundle();
    }

    public static Locale getCurrentLocale() {
        return currentLocale;
    }
}