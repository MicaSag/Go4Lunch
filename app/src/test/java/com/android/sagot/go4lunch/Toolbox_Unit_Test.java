package com.android.sagot.go4lunch;

import android.location.Location;

import com.android.sagot.go4lunch.Utils.Toolbox;
import com.android.sagot.go4lunch.api.UserHelper;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 *      Unit test of the Toolbox Class Utilities
 */
public class Toolbox_Unit_Test {

    @Test
    public void formatTime_Unit_Test() {

        assertEquals("https://maps.googleapis.com/maps/api" +
                        "/place/photo?maxwidth=800&" +
                        "photoreference=ZE5edd7e68f724z&key=rffcrfr577fdxzx24d2",
                Toolbox.formatPlacePhotoUrl("800", "ZE5edd7e68f724z",
                        "rffcrfr577fdxzx24d2"));
    }

    @Test
    public void dateReformatSSAAMMJJ_Unit_Test() {
        String originDate = "2018-05-08T12:37:10-04:00";         //Original date to reformat
        assertEquals("20180508", Toolbox.dateReformatSSAAMMJJ(originDate));
    }

    @Test
    public void dateReformat_Unit_Test() {
        String originDate = "20180508";         //Original date to reformat
        assertEquals("08/05/18", Toolbox.dateReformat(originDate));
    }
}