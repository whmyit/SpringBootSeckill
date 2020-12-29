package com.dxhy.order.utils;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;


/**
 * @author Uknower-lch
 * @ClassName: DateUtils
 * @Description: 日期工具
 * @date 2015年11月3日 上午10:51:52
 */
public class DateUtilsLocal {
    
    public static final String DEFAULT_SDF = "yyyy-MM-dd HH:mm:ss";
    
    private static final String SDF_MSS = "yyyy年MM月dd日";
    
    private static final String MISS_DATA_FORMAT = "yyyyMMddHHmmss";
    
    private final static String SMF_YYYYMM = "yyyy-MM";
    
    private final static String YYYY_MM_DD = "yyyy-MM-dd";
    
    public final static String YYYYMMDD = "yyyyMMdd";
    
    public static String getYMDHMIS(Date date) {
        return DateFormatUtils.format(date, DEFAULT_SDF);
    }
    
    public static String getYMDHMSS(Date date) {
        return DateFormatUtils.format(date, SDF_MSS);
    }
    
    public static String format(Date date, String pattern) {
        if (date != null) {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            return df.format(date);
        } else {
            return null;
        }
    }

    public static String getDefaultFormatToString(Date date){
        return format(date, MISS_DATA_FORMAT);
    }

    public static Date getMissDataFormat(String date) throws ParseException {
        return DateUtils.parseDate(date, MISS_DATA_FORMAT);

    }

    /**
    * 获得某天最大时间 2017-10-15 23:59:59
    */
    public static Date getEndOfDay(Date date) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());
        LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
        return Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());
    }
    public static Date getDefaultDate(String date) throws ParseException {
        return DateUtils.parseDate(date, DEFAULT_SDF);
    }

    public static Date getDefaultDate_yyyy_MM_dd_HH_mm_ss(String date) {
        try {
            return DateUtils.parseDate(date, DEFAULT_SDF);
        } catch (ParseException e) {
            return null;
        }
    }

    public static long getNowTime() throws ParseException {
        Date date = DateUtils.parseDate(DateFormatUtils.format(new Date(), DEFAULT_SDF), DEFAULT_SDF);
        Date date2 = DateUtils.parseDate("1970-01-01 00:00:00", DEFAULT_SDF);
        return ((date.getTime() - date2.getTime()) / 1000);
    }

    /**
     * 计算10年后的long
     *
     * @throws ParseException
     */
    public static long getAfterTime() throws ParseException {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, 30);
        Date date = DateUtils.parseDate(DateFormatUtils.format(c.getTime(), DEFAULT_SDF), DEFAULT_SDF);
        Date date2 = DateUtils.parseDate("1970-01-01 00:00:00", DEFAULT_SDF);
        return ((date.getTime() - date2.getTime()) / 1000);
    }

    /**
     * 字符串转日期
     *
     * @param time
     * @return
     * @throws ParseException
     */
    public static Date parseYYYYMM(String time) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(SMF_YYYYMM);
        return simpleDateFormat.parse(time);
    }

    /**
     * 字符串转日期
     *
     * @param time
     * @return
     * @throws ParseException
     */
    public static Date parseYYYYMMDD(String time) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(YYYY_MM_DD);
        return simpleDateFormat.parse(time);
    }

    /**
     * 获取n个月前的年月时间，格式yyyy-MM
     * z 月份
     *
     * @return
     */
    public static String getZMonthAgeMore(int z) {
        String zMonthAge = getZMonthAge(z);
        return new StringBuffer().append(zMonthAge, 0, 4).append("-").append(zMonthAge, 4, 6).toString();
    }

    /**
     * 获取n个月前的年月时间，格式yyyyMM
     * z 月份
     *
     * @return
     */
    public static String getZMonthAge(int z) {
        String format = new SimpleDateFormat(SMF_YYYYMM).format(new Date());
        StringBuilder sbr = new StringBuilder();
        int month = Integer.parseInt(format.substring(5, 7));
        int year = Integer.parseInt(format.substring(0, 4));
        String parse = null;
        int m = month - z % 12;
        if (month > (z % 12)) {
            parse = sbr.append(year - z / 12).append(m < 10 ? ("0" + m) : m).toString();
        } else {
            parse = sbr.append(year - z / 12 - 1).append((m + 12) < 10 ? ("0" + (m + 12)) : (m + 12)).toString();
        }
        return parse;
    }


    /**
     * 获取X天前的0点时间
     *
     * @param i
     * @return
     */
    public static String getXDayAgo(int i) {
        Calendar calendar1 = Calendar.getInstance();
        SimpleDateFormat sdf1 = new SimpleDateFormat(YYYY_MM_DD);
        calendar1.add(Calendar.DATE, -i);
        return sdf1.format(calendar1.getTime());
    }

    /**
     * 获取N个月前一号零点，可以为负数
     *
     * @return
     */
    public static Date getFirstDayOfNMonthAgo(int n) {
        String format = new SimpleDateFormat(SMF_YYYYMM).format(new Date());
        StringBuilder sbr = new StringBuilder();
        int month = Integer.parseInt(format.substring(5, 7));
        int year = Integer.parseInt(format.substring(0, 4));
        Date parse = null;
        if (month > n) {
            try {
                parse = new SimpleDateFormat(SMF_YYYYMM).parse(sbr.append(year).append("-").append(month - n < 10 ? ("0" + (month - n)) : (month - n)).toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            try {
                parse = new SimpleDateFormat(SMF_YYYYMM).parse(sbr.append(year - 1).append("-").append(month - n + 12 < 10 ? ("0" + (month - n + 12)) : (month - n + 12)).toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return parse;
    }
    
    /**
     * 判断时间格式
     *
     * @param date
     * @return
     */
    
    public static String checkDate(String date) {
    
        date = date.trim();
        //yyyyMMddHHmmss
        String a1 = "[0-9]{4}[0-9]{2}[0-9]{2}[0-9]{2}[0-9]{2}[0-9]{2}";
        //yyyyMMdd
        String a2 = "[0-9]{4}[0-9]{2}[0-9]{2}";
        //yyyy-MM-dd HH:mm:ss
        String a3 = "[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}";
        //yyyy-MM-dd
        String a4 = "[0-9]{4}-[0-9]{2}-[0-9]{2}";
        //yyyy-MM-dd  HH:mm
        String a5 = "[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}";
    
        boolean datea1 = Pattern.compile(a1).matcher(date).matches();
    
        if (datea1) {
            return "yyyyMMddHHmmss";
        }
        boolean datea2 = Pattern.compile(a2).matcher(date).matches();
        if (datea2) {
            return "yyyyMMdd";
        }
        boolean datea3 = Pattern.compile(a3).matcher(date).matches();
        if (datea3) {
            return "yyyy-MM-dd HH:mm:ss";
        }
        
        boolean datea4 = Pattern.compile(a4).matcher(date).matches();
        if (datea4) {
            return "yyyy-MM-dd";
        }
        boolean datea5 = Pattern.compile(a5).matcher(date).matches();
        if (datea5) {
            return "yyyy-MM-dd HH:mm";
        }
        return "";
        
    }
    
}
