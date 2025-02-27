package com.mycompany.firstflatlaf;

public class ThemeManager {

    private static boolean _isDarkMode = false;

    public static boolean isDarkMode() {
        return _isDarkMode;
    }

    public static void setDarkMode(boolean isDarkMode) {
        _isDarkMode = isDarkMode;
    }
}
