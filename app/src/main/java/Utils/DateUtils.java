package Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于生成阳历的
 *
 * @author Nonoas
 */
public class DateUtils {

    /*以 2019年1月1日 星期二 为标准*/
    final public int REF_YEAR_S = 1901;/*定义参考年份*/
    final public int REF_YEAR_E = 2100;/*定义年份范围*/

    /**
     * 判断是否为闰年
     *
     * @param year 传入的年份
     * @return 闰年返回true，平年返回false
     */
    private boolean isLeap(int year) {
        return ((year % 4 == 0) && (year % 100 != 0) || year % 400 == 0);
    }

    /**
     * 判断某年某月有多少天
     *
     * @param year  年
     * @param month 月
     * @return int天数
     */
    public int daysOfMonth(int year, int month) {
        switch (month) {
            /*大月*/
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            /*小月*/
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            /*二月*/
            case 2:
                if (isLeap(year)) return 29;
                else return 28;
            default:
                return 0;
        }
    }

    /**
     * 蔡勒公式,判断某年某月的一号是周几
     *
     * @param year  年
     * @param month 月
     * @return week(0, 6)
     */
    private int weekOfFirst(int year, int month) {
        int m = month;
        int d = 1;
        if (month <= 2) { /*对小于2的月份进行修正*/
            year--;
            m = month + 12;
        }
        int y = year % 100;
        int c = year / 100;//世纪数减1
        int w = (y + y / 4 + c / 4 - 2 * c + (13 * (m + 1) / 5) + d - 1) % 7;
        if (w < 0) /*修正计算结果是负数的情况*/
            w += 7;
        return w;
    }

    /**
     * 将某年某月的日期保存到数组
     *
     * @param year  年
     * @param month 月
     * @return 日期数组
     */
    public List<Integer> datePrint(int year, int month) {
        List<Integer> dateGrid = new ArrayList<>();
        int dayNum = 1;
        for (int i = 0; dayNum <= daysOfMonth(year, month); i++) {
            if (i < weekOfFirst(year, month)) {
                dateGrid.add(0);
            } else {
                dateGrid.add(dayNum++);
            }
        }
        return dateGrid;
    }
}