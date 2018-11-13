package com.android.sagot.go4lunch.Utils;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Toolbox containing various functions used in the application
 */
public class Toolbox {

    /**
     *   Function that returns the current day
     *
     *   in  :
     *   out :  0 = Sunday
     *          1 = Monday
     *          2 = Tuesday
     *          3 = Wednesday
     *          4 = Thursday
     *          5 = Friday
     *          6 = Saturday
     */
    public static int getCurrentDay() {

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        return calendar.get(calendar.DAY_OF_WEEK);
    }
}
