// Copyright 2018 OmegaTrace Inc. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License

package utils;

import java.util.Calendar;
import java.util.Date;

public class TimeUtility {

    public static String getTwelveHourCurrentTime() {

        String timeString;

        Calendar time = Calendar.getInstance();
        int month = time.get(Calendar.MONTH); // create variables
        int year = time.get(Calendar.YEAR);
        int DOM = time.get(Calendar.DAY_OF_MONTH);
        int DOW = time.get(Calendar.DAY_OF_WEEK);
        int hour = time.get(Calendar.HOUR);
        int hourOfDay = time.get(Calendar.HOUR_OF_DAY);
        int minute = time.get(Calendar.MINUTE);
        int second = time.get(Calendar.SECOND);
        long setMill = 1234567898765L;
        String ampm = "";
        String dayofw = "";
        int am_pm = time.get(Calendar.AM_PM);

//		timeString = month + "/" + year + ", " + hour + ":" + minute + "." + second + " ";
        timeString = hour + ":" + minute + "." + second + " ";
        switch (am_pm) {
            case Calendar.AM:
                timeString = timeString.concat("AM");
                break;
            default:
                timeString = timeString.concat("PM");
                break;
        }

        System.out.println(month + "/" + year + ", " + hourOfDay + ":" + minute + "." + second + " " + (am_pm == Calendar.AM ? "AM" : "PM"));
        return timeString;
    }

    public static String getTwelveHourTime(Date date) {

        String timeString;

        Calendar time = Calendar.getInstance();
        int month = time.get(Calendar.MONTH); // create variables
        int year = time.get(Calendar.YEAR);
        int DOM = time.get(Calendar.DAY_OF_MONTH);
        int DOW = time.get(Calendar.DAY_OF_WEEK);
        int hour = time.get(Calendar.HOUR);
        int hourOfDay = time.get(Calendar.HOUR_OF_DAY);
        int minute = time.get(Calendar.MINUTE);
        int second = time.get(Calendar.SECOND);
        long setMill = 1234567898765L;
        String ampm = "";
        String dayofw = "";
        int am_pm = time.get(Calendar.AM_PM);

//		timeString = month + "/" + year + ", " + hour + ":" + minute + "." + second + " ";
        timeString = hour + ":" + minute + "." + second + " ";
        switch (am_pm) {
            case Calendar.AM:
                timeString = timeString.concat("AM");
                break;
            default:
                timeString = timeString.concat("PM");
                break;
        }

        System.out.println(month + "/" + year + ", " + hourOfDay + ":" + minute + "." + second + " " + (am_pm == Calendar.AM ? "AM" : "PM"));
        return timeString;
    }

}
