package org.openea.eap.extj.util;


import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONArray;
import org.openea.eap.framework.common.util.date.DateUtils;
import org.quartz.CronExpression;
import org.quartz.TriggerUtils;
import org.quartz.impl.triggers.CronTriggerImpl;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DateUtil extends DateUtils {

    public static Date getDayBegin() {
        Calendar cal = Calendar.getInstance();
        cal.set(11, 0);
        cal.set(12, 0);
        cal.set(13, 0);
        cal.set(14, 0);
        return cal.getTime();
    }

    public static String getNow() {
        LocalDateTime ldt1 = LocalDateTime.now();
        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String temp = dtf1.format(ldt1);
        return temp;
    }

    public static String getNow(String zone) {
        LocalDateTime ldt1 = LocalDateTime.now(ZoneId.of(zone));
        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String temp = dtf1.format(ldt1);
        return temp;
    }

    public static String getmmNow() {
        LocalDateTime ldt1 = LocalDateTime.now();
        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String temp = dtf1.format(ldt1);
        return temp;
    }

    public static Date getNowDate() {
        Date date = new Date();
        return date;
    }

    public static Date getDayEnd() {
        Calendar cal = Calendar.getInstance();
        cal.set(11, 23);
        cal.set(12, 59);
        cal.set(13, 59);
        return cal.getTime();
    }

    public static Date getBeginDayOfYesterday() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getDayBegin());
        cal.add(5, -1);
        return cal.getTime();
    }

    public static Date getEndDayOfYesterday() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getDayEnd());
        cal.add(5, -1);
        return cal.getTime();
    }

    public static Date getBeginDayOfTomorrow() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getDayBegin());
        cal.add(5, 1);
        return cal.getTime();
    }

    public static Date getEndDayOfTomorrow() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getDayEnd());
        cal.add(5, 1);
        return cal.getTime();
    }

    public static Timestamp getDayStartTime(Date d) {
        Calendar calendar = Calendar.getInstance();
        if (null != d) {
            calendar.setTime(d);
        }

        calendar.set(calendar.get(1), calendar.get(2), calendar.get(5), 0, 0, 0);
        calendar.set(14, 0);
        return new Timestamp(calendar.getTimeInMillis());
    }

    public static Timestamp getDayEndTime(Date d) {
        Calendar calendar = Calendar.getInstance();
        if (null != d) {
            calendar.setTime(d);
        }

        calendar.set(calendar.get(1), calendar.get(2), calendar.get(5), 23, 59, 59);
        calendar.set(14, 999);
        return new Timestamp(calendar.getTimeInMillis());
    }

    public static Date getBeginDayOfWeek() {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayOfWeek = cal.get(7);
        if (dayOfWeek == 1) {
            dayOfWeek += 7;
        }

        cal.add(5, 2 - dayOfWeek);
        return getDayStartTime(cal.getTime());
    }

    public static Date getEndDayOfWeek() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getBeginDayOfWeek());
        cal.add(7, 6);
        Date weekEndSta = cal.getTime();
        return getDayEndTime(weekEndSta);
    }

    public static Date getBeginDayOfLastWeek() {
        Date date = new Date();
        if (date == null) {
            return null;
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int dayofweek = cal.get(7);
            if (dayofweek == 1) {
                dayofweek += 7;
            }

            cal.add(5, 2 - dayofweek - 7);
            return getDayStartTime(cal.getTime());
        }
    }

    public static Date getEndDayOfLastWeek() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getBeginDayOfLastWeek());
        cal.add(7, 6);
        Date weekEndSta = cal.getTime();
        return getDayEndTime(weekEndSta);
    }

    public static Integer getNowYear() {
        Date date = new Date();
        GregorianCalendar gc = (GregorianCalendar)Calendar.getInstance();
        gc.setTime(date);
        return gc.get(1);
    }

    public static int getNowMonth() {
        Date date = new Date();
        GregorianCalendar gc = (GregorianCalendar)Calendar.getInstance();
        gc.setTime(date);
        return gc.get(2) + 1;
    }

    public static Date getBeginDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getNowYear(), getNowMonth() - 1, 1);
        return getDayStartTime(calendar.getTime());
    }

    public static Date getEndDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getNowYear(), getNowMonth() - 1, 1);
        int day = calendar.getActualMaximum(5);
        calendar.set(getNowYear(), getNowMonth() - 1, day);
        return getDayEndTime(calendar.getTime());
    }

    public static Date getBeginDayOfLastMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getNowYear(), getNowMonth() - 2, 1);
        return getDayStartTime(calendar.getTime());
    }

    public static Date getEndDayOfLastMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getNowYear(), getNowMonth() - 2, 1);
        int day = calendar.getActualMaximum(5);
        calendar.set(getNowYear(), getNowMonth() - 2, day);
        return getDayEndTime(calendar.getTime());
    }

    public static Date getBeginDayOfYear() {
        Calendar cal = Calendar.getInstance();
        cal.set(1, getNowYear());
        cal.set(2, 0);
        cal.set(5, 1);
        return getDayStartTime(cal.getTime());
    }

    public static Date getEndDayOfYear() {
        Calendar cal = Calendar.getInstance();
        cal.set(1, getNowYear());
        cal.set(2, 11);
        cal.set(5, 31);
        return getDayEndTime(cal.getTime());
    }

    public static int getDiffDays(Date beginDate, Date endDate) {
        if (beginDate != null && endDate != null) {
            long diff = (endDate.getTime() - beginDate.getTime()) / 86400000L;
            int days = (int)diff;
            return days;
        } else {
            throw new IllegalArgumentException("getDiffDays param is null!");
        }
    }

    public static long dateDiff(Date beginDate, Date endDate) {
        long date1ms = beginDate.getTime();
        long date2ms = endDate.getTime();
        return date2ms - date1ms;
    }

    public static Date max(Date beginDate, Date endDate) {
        if (beginDate == null) {
            return endDate;
        } else if (endDate == null) {
            return beginDate;
        } else {
            return beginDate.after(endDate) ? beginDate : endDate;
        }
    }

    public static Date min(Date beginDate, Date endDate) {
        if (beginDate == null) {
            return endDate;
        } else if (endDate == null) {
            return beginDate;
        } else {
            return beginDate.after(endDate) ? endDate : beginDate;
        }
    }

    public static Date getFirstSeasonDate(Date date) {
        int[] season = new int[]{1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int sean = season[cal.get(2)];
        cal.set(2, sean * 3 - 3);
        return cal.getTime();
    }

    public static Date getNextDay(Date date, int i) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.set(5, cal.get(5) + i);
        return cal.getTime();
    }

    public static Date getFrontDay(Date date, int i) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.set(5, cal.get(5) - i);
        return cal.getTime();
    }

    public static List getTimeList(int beginYear, int beginMonth, int k) {
        List list = new ArrayList();
        Calendar begincal = new GregorianCalendar(beginYear, beginMonth, 1);
        int max = begincal.getActualMaximum(5);

        for(int i = 1; i < max; i += k) {
            list.add(begincal.getTime());
            begincal.add(5, k);
        }

        begincal = new GregorianCalendar(beginYear, beginMonth, max);
        list.add(begincal.getTime());
        return list;
    }

    public static List getTimeList(int beginYear, int beginMonth, int endYear, int endMonth, int k) {
        List list = new ArrayList();
        if (beginYear == endYear) {
            for(int j = beginMonth; j <= endMonth; ++j) {
                list.add(getTimeList(beginYear, j, k));
            }
        } else {
            for(int j = beginMonth; j < 12; ++j) {
                list.add(getTimeList(beginYear, j, k));
            }

            for(int j = beginYear + 1; j < endYear; ++j) {
                for(int i= 0; i < 12; ++i) {
                    list.add(getTimeList(j, i, k));
                }
            }

            for(int j = 0; j <= endMonth; ++j) {
                list.add(getTimeList(endYear, j, k));
            }
        }

        return list;
    }

    public static boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {
        if (nowTime.getTime() != startTime.getTime() && nowTime.getTime() != endTime.getTime()) {
            Calendar date = Calendar.getInstance();
            date.setTime(nowTime);
            Calendar begin = Calendar.getInstance();
            begin.setTime(startTime);
            Calendar end = Calendar.getInstance();
            end.setTime(endTime);
            return date.after(begin) && date.before(end);
        } else {
            return true;
        }
    }

    public static String dateNow(String pattern) {
        Date date = new Date();
        if (pattern == null && "".equals(pattern)) {
            return null;
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat(pattern);
            String dateString = formatter.format(date);
            return dateString;
        }
    }

    public static String dateToString(Date date, String pattern) {
        if (StringUtil.isEmpty(pattern) && date == null) {
            return null;
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat(pattern);
            String dateString = formatter.format(date);
            return dateString;
        }
    }

    public static String dateFormat(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(date);
        return dateString;
    }

    public static String daFormat(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(date);
        return dateString;
    }

    public static String daFormat(Long date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date time = new Date(date);
        String dateString = formatter.format(time);
        return dateString;
    }

    public static String daFormatYmd(Long date) {
        DateTimeFormatter ftf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dateString = ftf.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(date), ZoneId.of("+8")));
        return dateString;
    }

    public static String daFormatHHMMSSAddEight(Long date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date time = dateAddHours(new Date(date), 8);
        String dateString = formatter.format(time);
        return dateString;
    }

    public static String daFormatHHMMSS(Long date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date time = new Date(date);
        String dateString = formatter.format(time);
        return dateString;
    }

    public static Date stringToDate(String str) {
        Date date = null;
        if (StringUtil.isEmpty(str)) {
            return date;
        } else {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                date = formatter.parse(str);
                return date;
            } catch (ParseException var3) {
                System.out.println(var3.getMessage());
                return date;
            }
        }
    }

    public static Date stringToDates(String str) {
        Date date = null;

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            date = formatter.parse(str);
            return date;
        } catch (ParseException var3) {
            return date;
        }
    }

    public static String cstFormat(String str) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
            Date date = formatter.parse(str);
            return dateFormat(date);
        } catch (Exception var3) {
            return "";
        }
    }

    public static Date longToDate(long str) throws ParseException {
        return new Date(str * 1000L);
    }

    public static boolean isEffectiveDate(Date startDate, Date endDate) {
        if (startDate != null && endDate != null) {
            long currentTime = System.currentTimeMillis();
            return currentTime >= startDate.getTime() && currentTime <= endDate.getTime();
        } else {
            return false;
        }
    }

    public static String getTwoDay(String secondString, String firstString) {
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
        long day = 0L;

        try {
            Date secondTime = myFormatter.parse(secondString);
            Date firstTime = myFormatter.parse(firstString);
            day = (secondTime.getTime() - firstTime.getTime()) / 86400000L;
        } catch (Exception var7) {
            return "";
        }

        return day + "";
    }

    public static String getPreTime(String stringTime, String minute) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String mydate1 = "";

        try {
            Date date1 = format.parse(stringTime);
            long time = date1.getTime() / 1000L + (long)(Integer.parseInt(minute) * 60);
            date1.setTime(time * 1000L);
            mydate1 = format.format(date1);
            return mydate1;
        } catch (Exception var7) {
            return "";
        }
    }

    public static long getTime(Date data) {
        return data != null ? data.getTime() / 1000L : 0L;
    }

    public static Date strToDate(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    public static String getNextDay(String nowdate, String delay) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String mdate = "";
            Date d = strToDate(nowdate);
            long myTime = d.getTime() / 1000L + (long)(Integer.parseInt(delay) * 24 * 60 * 60);
            d.setTime(myTime * 1000L);
            mdate = format.format(d);
            return mdate;
        } catch (Exception var7) {
            return "";
        }
    }

    public static boolean isLeapYear(String ddate) {
        Date d = strToDate(ddate);
        GregorianCalendar gc = (GregorianCalendar)Calendar.getInstance();
        gc.setTime(d);
        int year = gc.get(1);
        if (year % 400 == 0) {
            return true;
        } else if (year % 4 == 0) {
            return year % 100 != 0;
        } else {
            return false;
        }
    }

    public static String geteDate(String str) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(str, pos);
        String j = strtodate.toString();
        String[] k = j.split(" ");
        return k[2] + k[1].toUpperCase() + k[5].substring(2, 4);
    }

    public static boolean isSameWeekDates(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        int subYear = cal1.get(1) - cal2.get(1);
        if (0 == subYear) {
            if (cal1.get(3) == cal2.get(3)) {
                return true;
            }
        } else if (1 == subYear && 11 == cal2.get(2)) {
            if (cal1.get(3) == cal2.get(3)) {
                return true;
            }
        } else if (-1 == subYear && 11 == cal1.get(2) && cal1.get(3) == cal2.get(3)) {
            return true;
        }

        return false;
    }

    public static String getSeqWeek() {
        Calendar c = Calendar.getInstance(Locale.CHINA);
        String week = Integer.toString(c.get(3));
        if (week.length() == 1) {
            week = "0" + week;
        }

        String year = Integer.toString(c.get(1));
        return year + "年第" + week + "周";
    }

    public static String getWeek(String sdate, String num) {
        Date dd = strToDate(sdate);
        Calendar c = Calendar.getInstance();
        c.setTime(dd);
        if ("1".equals(num)) {
            c.set(7, 2);
        } else if ("2".equals(num)) {
            c.set(7, 3);
        } else if ("3".equals(num)) {
            c.set(7, 4);
        } else if ("4".equals(num)) {
            c.set(7, 5);
        } else if ("5".equals(num)) {
            c.set(7, 6);
        } else if ("6".equals(num)) {
            c.set(7, 7);
        } else if ("0".equals(num)) {
            c.set(7, 1);
        }

        return (new SimpleDateFormat("yyyy-MM-dd")).format(c.getTime());
    }

    public static String getWeek(String sdate) {
        Date date = strToDate(sdate);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return (new SimpleDateFormat("EEEE")).format(c.getTime());
    }

    public static String getWeekStr(String sdate) {
        String str = "";
        str = getWeek(sdate);
        if ("1".equals(str)) {
            str = "星期日";
        } else if ("2".equals(str)) {
            str = "星期一";
        } else if ("3".equals(str)) {
            str = "星期二";
        } else if ("4".equals(str)) {
            str = "星期三";
        } else if ("5".equals(str)) {
            str = "星期四";
        } else if ("6".equals(str)) {
            str = "星期五";
        } else if ("7".equals(str)) {
            str = "星期六";
        }

        return str;
    }

    public static String getWeekStr(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        String str = c.get(7) + "";
        if ("1".equals(str)) {
            str = "SUN";
        } else if ("2".equals(str)) {
            str = "MON";
        } else if ("3".equals(str)) {
            str = "TUE";
        } else if ("4".equals(str)) {
            str = "WED";
        } else if ("5".equals(str)) {
            str = "THU";
        } else if ("6".equals(str)) {
            str = "FRI";
        } else if ("7".equals(str)) {
            str = "SAT";
        }

        return str;
    }

    public static long getDays(String date1, String date2) {
        if (date1 != null && !"".equals(date1)) {
            if (date2 != null && !"".equals(date2)) {
                SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
                Date date = null;
                Date mydate = null;

                try {
                    date = myFormatter.parse(date1);
                    mydate = myFormatter.parse(date2);
                } catch (Exception var7) {
                    throw new RuntimeException(var7);
                }

                long day = (date.getTime() - mydate.getTime()) / 86400000L;
                return day;
            } else {
                return 0L;
            }
        } else {
            return 0L;
        }
    }

    public static String getNowMonth(String sdate) {
        sdate = sdate.substring(0, 8) + "01";
        Date date = strToDate(sdate);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int u = c.get(7);
        String newday = getNextDay(sdate, 1 - u + "");
        return newday;
    }

    public static String getUserDate(String sformat) {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(sformat);
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    public static String getDateString(Date date, String sformat) {
        Date currentTime = null;
        if (date == null) {
            currentTime = new Date();
        } else {
            currentTime = date;
        }

        SimpleDateFormat formatter = new SimpleDateFormat(sformat);
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    public static String getRandom(int i) {
        Random jjj = new Random();
        if (i == 0) {
            return "";
        } else {
            String jj = "";

            for(int k = 0; k < i; ++k) {
                jj = jj + jjj.nextInt(9);
            }

            return jj;
        }
    }

    public static LocalDateTime dateToLocalDateTime(Date date) {
        if (date != null) {
            Instant instant = date.toInstant();
            ZoneId zoneId = ZoneId.systemDefault();
            LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
            return localDateTime;
        } else {
            return null;
        }
    }

    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        if (localDateTime != null) {
            ZoneId zoneId = ZoneId.systemDefault();
            ZonedDateTime zdt = localDateTime.atZone(zoneId);
            Date date = Date.from(zdt.toInstant());
            return date;
        } else {
            return null;
        }
    }

    public static Date dateAddHours(Date startDate, int hours) {
        if (startDate == null) {
            startDate = new Date();
        }

        Calendar c = Calendar.getInstance();
        c.setTime(startDate);
        c.set(10, c.get(10) + hours);
        return c.getTime();
    }

    public static Date dateAddMinutes(Date startDate, int minutes) {
        if (startDate == null) {
            startDate = new Date();
        }

        Calendar c = Calendar.getInstance();
        c.setTime(startDate);
        c.set(12, c.get(12) + minutes);
        return c.getTime();
    }

    public static Date dateAddSeconds(Date startDate, int seconds) {
        if (startDate == null) {
            startDate = new Date();
        }

        Calendar c = Calendar.getInstance();
        c.setTime(startDate);
        c.set(13, c.get(13) + seconds);
        return c.getTime();
    }

    public static Date dateAddYears(Date startDate, int years) {
        if (startDate == null) {
            startDate = new Date();
        }

        Calendar c = Calendar.getInstance();
        c.setTime(startDate);
        c.set(1, c.get(1) + years);
        return c.getTime();
    }

    public static Date dateAddMonths(Date startDate, int months) {
        if (startDate == null) {
            startDate = new Date();
        }

        Calendar c = Calendar.getInstance();
        c.setTime(startDate);
        c.set(2, c.get(2) + months);
        return c.getTime();
    }

    public static Date dateAddDays(Date startDate, int days) {
        if (startDate == null) {
            startDate = new Date();
        }

        Calendar c = Calendar.getInstance();
        c.setTime(startDate);
        c.set(5, c.get(5) + days);
        return c.getTime();
    }

    public static LocalDateTime localDateAddHours(LocalDateTime localDateTime, int hours) {
        if (localDateTime == null) {
            localDateTime = LocalDateTime.now();
        }

        return localDateTime.plusHours((long)hours);
    }

    public static LocalDateTime localDateAddMinutes(LocalDateTime localDateTime, int minutes) {
        if (localDateTime == null) {
            localDateTime = LocalDateTime.now();
        }

        return localDateTime.plusMinutes((long)minutes);
    }

    public static LocalDateTime localDateAddSeconds(LocalDateTime localDateTime, int seconds) {
        if (localDateTime == null) {
            localDateTime = LocalDateTime.now();
        }

        return localDateTime.plusSeconds((long)seconds);
    }

    public static LocalDateTime localDateAddYears(LocalDateTime localDateTime, int years) {
        if (localDateTime == null) {
            localDateTime = LocalDateTime.now();
        }

        return localDateTime.plusYears((long)years);
    }

    public static LocalDateTime localDateMonths(LocalDateTime localDateTime, int months) {
        if (localDateTime == null) {
            localDateTime = LocalDateTime.now();
        }

        return localDateTime.plusMonths((long)months);
    }

    public static LocalDateTime localDateAddDays(LocalDateTime localDateTime, int days) {
        if (localDateTime == null) {
            localDateTime = LocalDateTime.now();
        }

        return localDateTime.plusDays((long)days);
    }

    public static int dateCompare(Date myDate, Date compareDate) {
        Calendar myCal = Calendar.getInstance();
        Calendar compareCal = Calendar.getInstance();
        myCal.setTime(myDate);
        compareCal.setTime(compareDate);
        return myCal.compareTo(compareCal);
    }

    public static Date dateMin(Date date, Date compareDate) {
        if (date == null) {
            return compareDate;
        } else if (compareDate == null) {
            return date;
        } else if (1 == dateCompare(date, compareDate)) {
            return compareDate;
        } else {
            return -1 == dateCompare(date, compareDate) ? date : date;
        }
    }

    public static Date dateMax(Date date, Date compareDate) {
        if (date == null) {
            return compareDate;
        } else if (compareDate == null) {
            return date;
        } else if (1 == dateCompare(date, compareDate)) {
            return date;
        } else {
            return -1 == dateCompare(date, compareDate) ? compareDate : date;
        }
    }

    public static int getLastDayOfMonth(Date startDate, int month) {
        if (startDate == null) {
            startDate = new Date();
        }

        Calendar c = Calendar.getInstance();
        c.setTime(startDate);
        c.set(c.get(1), month, 1);
        c.add(5, -1);
        int day = c.get(5);
        return day;
    }

    public static List<Date> getAllDays(Date startTime, Date endTime) {
        List<Date> listDay = new ArrayList();
        new Date();

        for(Date dtDay = startTime; dtDay.compareTo(endTime) <= 0; dtDay = dateAddDays(dtDay, 1)) {
            listDay.add(dtDay);
        }

        return listDay;
    }

    public static List<String> getRecentExecTime(String cron, int numTimes) {
        List<String> list = new ArrayList();

        try {
            CronTriggerImpl cronTriggerImpl = new CronTriggerImpl();
            cronTriggerImpl.setCronExpression(cron);
            List<Date> dates = TriggerUtils.computeFireTimes(cronTriggerImpl, (org.quartz.Calendar)null, numTimes);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Iterator var6 = dates.iterator();

            while(var6.hasNext()) {
                Date date = (Date)var6.next();
                list.add(dateFormat.format(date));
            }
        } catch (ParseException var8) {
            var8.getMessage();
        }

        return list;
    }

    public static String getNextCron(String cron, Date dates) {
        String crons = null;
        if (dates == null) {
            dates = new Date();
        }

        if (StringUtil.isEmpty(cron)) {
            return crons;
        } else {
            CronExpression cronExpression = null;

            try {
                cronExpression = new CronExpression(cron);
            } catch (ParseException var5) {
                System.out.println(var5.getMessage());
                return crons;
            }

            Date date = cronExpression.getNextValidTimeAfter(dates);
            if (date != null) {
                crons = dateFormat(date);
            }

            return crons;
        }
    }

    public static Date getNextCronDate(String cron, Date dates) {
        Date crons = new Date();
        if (dates == null) {
            dates = new Date();
        }

        if (StringUtil.isEmpty(cron)) {
            return crons;
        } else {
            CronExpression cronExpression = null;

            try {
                cronExpression = new CronExpression(cron);
            } catch (ParseException var5) {
                System.out.println(var5.getMessage());
                return crons;
            }

            Date date = cronExpression.getNextValidTimeAfter(dates);
            return date;
        }
    }

    public static void getNextDate(int num, String repetition, Date dates, Date repeatTime, List<Date> dataList) {
        if (dates == null) {
            dates = new Date();
        }

        if (repeatTime == null) {
            repeatTime = new Date();
        }

        int result = 0;
        List<String> repetitionList = new ArrayList() {
            {
                this.add("2");
                this.add("3");
                this.add("4");
                this.add("5");
            }
        };
        if (ObjectUtil.isNotEmpty(repetition) && repetitionList.contains(repetition)) {
            switch (repetition) {
                case "2":
                    DateTime day = cn.hutool.core.date.DateUtil.offsetDay(dates, num);
                    if (day.getTime() <= repeatTime.getTime()) {
                        dataList.add(day);
                        ++num;
                        ++result;
                    }
                    break;
                case "3":
                    DateTime week = cn.hutool.core.date.DateUtil.offsetDay(dates, num * 7);
                    if (week.getTime() <= repeatTime.getTime()) {
                        dataList.add(week);
                        ++num;
                        ++result;
                    }
                    break;
                case "4":
                    DateTime month = cn.hutool.core.date.DateUtil.offsetMonth(dates, num);
                    if (month.getTime() <= repeatTime.getTime()) {
                        dataList.add(month);
                        ++num;
                        ++result;
                    }
                    break;
                case "5":
                    DateTime year = cn.hutool.core.date.DateUtil.offsetMonth(dates, num * 12);
                    if (year.getTime() <= repeatTime.getTime()) {
                        dataList.add(year);
                        ++num;
                        ++result;
                    }
            }

            if (result > 0) {
                getNextDate(num, repetition, dates, repeatTime, dataList);
            }
        } else {
            dataList.add(dates);
        }

    }

    public static void getNextDate(Date dates, String repetition, Date repeatTime, List<Date> dataList) {
        if (dates == null) {
            dates = new Date();
        }

        if (repeatTime == null) {
            repeatTime = new Date();
        }

        List<String> repetitionList = new ArrayList() {
            {
                this.add("2");
                this.add("3");
                this.add("4");
                this.add("5");
            }
        };
        if (repeatTime.getTime() >= dates.getTime() && ObjectUtil.isNotEmpty(repetition) && repetitionList.contains(repetition)) {
            dataList.add(dates);
            Date date = dates;
            switch (repetition) {
                case "2":
                    date = cn.hutool.core.date.DateUtil.offsetDay(dates, 1);
                    break;
                case "3":
                    date = cn.hutool.core.date.DateUtil.offsetDay(dates, 7);
                    break;
                case "4":
                    date = cn.hutool.core.date.DateUtil.offsetMonth(dates, 1);
                    break;
                case "5":
                    date = cn.hutool.core.date.DateUtil.offsetMonth(dates, 12);
            }

            getNextDate((Date)date, repetition, repeatTime, dataList);
        } else if (!repetitionList.contains(repetition)) {
            dataList.add(dates);
        }

    }

    public static String getDateToCron(Date date) {
        String cron = null;
        String dateFormat = "ss mm HH dd MM ?";
        if (date == null) {
            cron = dateNow(dateFormat);
        } else {
            cron = dateToString(date, dateFormat);
        }

        return cron;
    }

    public static boolean timeCalendar(Date nowTime, Date dayTimeStart, Date dayTimeEnd) {
        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);
        Calendar timeStart = Calendar.getInstance();
        timeStart.setTime(dayTimeStart);
        Calendar timeEnd = Calendar.getInstance();
        timeEnd.setTime(dayTimeEnd);
        if (!date.equals(timeStart) && !date.equals(timeEnd)) {
            return date.after(timeStart) && date.before(timeEnd);
        } else {
            return true;
        }
    }

    public static boolean isValidDate(String str) {
        boolean convertSuccess = true;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (str.length() == 10) {
            format = new SimpleDateFormat("yyyy-MM-dd");
        }

        try {
            format.setLenient(false);
            format.parse(str);
        } catch (ParseException var4) {
            convertSuccess = false;
        }

        return convertSuccess;
    }

    public static boolean isOverlap(Date leftStartDate, Date leftEndDate, Date rightStartDate, Date rightEndDate) {
        return leftStartDate.getTime() >= rightStartDate.getTime() && leftStartDate.getTime() < rightEndDate.getTime() || leftStartDate.getTime() > rightStartDate.getTime() && leftStartDate.getTime() <= rightEndDate.getTime() || rightStartDate.getTime() >= leftStartDate.getTime() && rightStartDate.getTime() < leftEndDate.getTime() || rightStartDate.getTime() > leftStartDate.getTime() && rightStartDate.getTime() <= leftEndDate.getTime();
    }

    public static JSONArray addCon(JSONArray jsonArray, String type, String format) {
        if ("timeRange".equals(type)) {
            String value1 = jsonArray.get(0).toString();
            String value2 = jsonArray.get(1).toString();
            jsonArray.clear();
            jsonArray.add(value1 + "至");
            jsonArray.add(value2);
        }

        if ("dateRange".equals(type)) {
            DateTimeFormatter ftfDateRange = DateTimeFormatter.ofPattern(format);
            long date1 = Long.parseLong(String.valueOf(jsonArray.get(0)));
            long date2 = Long.parseLong(String.valueOf(jsonArray.get(1)));
            String value1 = ftfDateRange.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(date1), ZoneId.systemDefault()));
            String value2 = ftfDateRange.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(date2), ZoneId.systemDefault()));
            jsonArray.clear();
            jsonArray.add(value1 + "至");
            jsonArray.add(value2);
        }

        return jsonArray;
    }

    public static Long getMillis2() {
        return Instant.now().toEpochMilli();
    }

    public static Long localDateTime2Millis(LocalDateTime localDateTime) {
        return localDateTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
    }

    public static Long localDate2Millis(LocalDate localDate) {
        return LocalDateTime.of(localDate, LocalTime.MIN).toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
    }

    public static Long clock2Millis(Clock clock) {
        return clock.millis();
    }

    public static Long zoneDateTime2Millis(ZonedDateTime zonedDateTime) {
        return zonedDateTime.toLocalDateTime().toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
    }

    public static Long string2MillisWithJdk8(String dateStr, String formatStr) {
        return LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern(formatStr)).toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
    }

    public static String getZonedDateTimeToString(ZonedDateTime zonedDateTime) {
        ZonedDateTime zoneDateTime1 = zonedDateTime.plusHours(11L);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return zoneDateTime1.format(formatter);
    }

}
