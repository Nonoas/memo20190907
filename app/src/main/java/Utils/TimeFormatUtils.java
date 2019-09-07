package Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 自定义的工具类：用来进行日期的格式转换
 * @author  Nonoas
 * @version 1.0
 */
public class TimeFormatUtils {
    /**
     *将传进来的时间转换为24小时制
     * @param str 用户传进来的某一时间格式的字符串
     * @return 转为24小时制的字符串
     */
    public static String to24Hour(String str){
        SimpleDateFormat format12=new SimpleDateFormat("ahh:mm",Locale.getDefault());
        SimpleDateFormat format24=new SimpleDateFormat("HH:mm",Locale.getDefault());
        Date d= new Date();
        try {
            d = format12.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return format24.format(d);
    }

    /**
     * 用于将文本框中的时间转为存进数据库的格式
     * @param str 文本框显示的时间：yyyy年MM月dd日ahh:mm
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String datetimeToDB(String str){
        SimpleDateFormat format12=new SimpleDateFormat("yyyy年MM月dd日 ahh:mm",Locale.getDefault());//声明读取的时间格式
        SimpleDateFormat format24=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());//声明读取的时间格式
        Date date=new Date();
        try {
            date=format12.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return format24.format(date);//返回用于储存到数据库的日期格式
    }

    public static String DBToDatetime(String str){
        SimpleDateFormat format24=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());//声明读取的时间格式
        SimpleDateFormat format12=new SimpleDateFormat("yyyy年M月d日 ahh:mm",Locale.getDefault());//声明读取的时间格式
        Date date=new Date();
        try {
            date=format24.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  format12.format(date);
    }

    /**
     * 从数据库读取时间部分
     * @param str yyyy-MM-dd HH:mm:ss
     * @return ahh:mm
     */
    public static String DBToTime12(String str){
        SimpleDateFormat format24=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());//声明读取的时间格式
        SimpleDateFormat format12=new SimpleDateFormat("ahh:mm",Locale.getDefault());//声明读取的时间格式
        Date date=new Date();
        try {
            date=format24.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  format12.format(date);
    }

    /**
     * 将文本框上的日期分解
     * @param str yyyy年MM月dd日 ahh:mm
     * @return int[]：0.yyyy, 1.MM 2.dd 3.HH 4.mm
     */
    public static int[] showDateTimeDivide24(String str){
        int[] a=new int[5];
        SimpleDateFormat formatShow=new SimpleDateFormat("yyyy年MM月dd日 ahh:mm",Locale.getDefault());
        Date date=new Date();
        try {
            date=formatShow.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal=Calendar.getInstance();
        cal.setTime(date);
        a[0]=cal.get(Calendar.YEAR);
        a[1]=cal.get(Calendar.MONTH)+1;
        a[2]=cal.get(Calendar.DAY_OF_MONTH);
        a[3]=cal.get(Calendar.HOUR_OF_DAY);
        a[4]=cal.get(Calendar.MINUTE);
        return a;
    }
    /*public static void main(String[] args){
        String s=TimeFormatUtils.DBToTime12("2019-08-03 23:04:00");
        System.out.println(s);
    }*/
}
