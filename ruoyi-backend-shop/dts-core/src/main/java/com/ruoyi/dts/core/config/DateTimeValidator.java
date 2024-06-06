package com.ruoyi.dts.core.config;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author  suichj
 */
public class DateTimeValidator {

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String TIME_PATTERN = "HH:mm:ss";

    public static boolean isValidDate(String date) {
        return isValidFormat(DATE_PATTERN, date);
    }

    public static boolean isValidTime(String time) {
        return isValidFormat(TIME_PATTERN, time);
    }

    private static boolean isValidFormat(String pattern, String value) {
        boolean isValid = false;
        Pattern regexPattern = Pattern.compile(pattern);
        Matcher matcher = regexPattern.matcher(value);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static void main(String[] args) {
        boolean isDateValid = DateTimeValidator.isValidDate("2021-12-31"); // true
        System.out.println(isDateValid);
        boolean isTimeValid = DateTimeValidator.isValidTime("23:59:59"); // true


        String yearMonthday = "2020022";
        System.out.println("yearMonthday: " + isLegalDate(yearMonthday));
    }

    /**
     * 根据时间 和时间格式 校验是否正确
     *
     * @param sDate 校验的日期
     * @return
     */
    public static boolean isLegalDate(String sDate) {
        String format = "yyyy-MM-dd";
        DateFormat formatter = new SimpleDateFormat(format);
        try {
            Date date = formatter.parse(sDate);
            return sDate.equals(formatter.format(date));
        } catch (Exception e) {
            return false;
        }
    }
}
