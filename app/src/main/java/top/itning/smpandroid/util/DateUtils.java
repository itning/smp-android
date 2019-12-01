package top.itning.smpandroid.util;

import androidx.annotation.Nullable;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * @author itning
 */
public class DateUtils {
    /**
     * 北京时区
     */
    public static final ZoneId ZONE_ID = ZoneId.of("Asia/Shanghai");
    /**
     * 时间日期格式化
     */
    public static final DateTimeFormatter YYYYMMDDHHMMSS_DATE_TIME_FORMATTER_1 = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss");
    public static final DateTimeFormatter YYYYMMDDHHMM_DATE_TIME_FORMATTER_2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public static final DateTimeFormatter YYYYMMDDHHMM_DATE_TIME_FORMATTER_3 = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
    public static final DateTimeFormatter MMDDHHMME_DATE_TIME_FORMATTER_4 = DateTimeFormatter.ofPattern("MM月dd日 HH:mm E");
    public static final DateTimeFormatter YYYYMMDD_DATE_TIME_FORMATTER_5 = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
    public static final DateTimeFormatter YYYYMMDD_DATE_TIME_FORMATTER_6 = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    public static final DateTimeFormatter YYYYMMDD_DATE_TIME_FORMATTER_7 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter YYYYMMDDHHMMSS_DATE_TIME_FORMATTER_8 = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");

    /**
     * 问候信息
     *
     * @param suffix 后置
     * @return 问候字符串
     */
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

    /**
     * 时间格式化
     *
     * @param date              Date
     * @param dateTimeFormatter DateTimeFormatter
     * @return 格式化的时间字符串
     */
    public static String format(Date date, DateTimeFormatter dateTimeFormatter) {
        return LocalDateTime.ofInstant(date.toInstant(), ZONE_ID).format(dateTimeFormatter);
    }
}
