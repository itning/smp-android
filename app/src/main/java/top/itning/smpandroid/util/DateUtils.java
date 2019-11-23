package top.itning.smpandroid.util;

import androidx.annotation.Nullable;

import java.util.Calendar;

/**
 * @author itning
 */
public class DateUtils {
    public static String helloTime(@Nullable String suffix) {
        if (suffix == null) {
            suffix = "";
        } else {
            suffix = "，" + suffix;
        }
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        if (timeOfDay < 6) {
            return "早上好" + suffix;
        } else if (timeOfDay < 12) {
            return "上午好" + suffix;
        } else if (timeOfDay < 13) {
            return "中午好" + suffix;
        } else if (timeOfDay < 18) {
            return "下午好" + suffix;
        } else {
            return "晚上好" + suffix;
        }
    }
}
