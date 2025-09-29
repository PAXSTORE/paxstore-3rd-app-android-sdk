package com.pax.android.demoapp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static String getCurrentDateString() {
        // 1. 获取当前时间戳（毫秒）
        long currentTimestamp = System.currentTimeMillis();

        // 2. 创建格式化器，指定格式为 "yyyy/MM/dd"
        //    注意：yyyy-四位年份，MM-两位月份，dd-两位日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());

        // 3. 将时间戳格式化为字符串
        return sdf.format(new Date(currentTimestamp));
    }
}
