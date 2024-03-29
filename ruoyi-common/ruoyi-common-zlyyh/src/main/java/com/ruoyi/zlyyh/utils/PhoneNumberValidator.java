package com.ruoyi.zlyyh.utils;

import java.util.regex.Pattern;

public class PhoneNumberValidator {
    private static final String PHONE_NUMBER_PATTERN = "^((13[0-9])|(14[0-9])|(15([0-9]))|(16[0-9])|(17[0-9])|(18[0-9])|(19[0-9]))\\d{8}$";

    public static boolean isValid(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() != 11) {
            return false;
        }
        return Pattern.matches(PHONE_NUMBER_PATTERN, phoneNumber);
    }
}
