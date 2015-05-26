package com.example.facefortest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utils {
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
	public  static String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}
	public static Date addDate(Date date){
		Calendar rightNow = Calendar.getInstance();
		rightNow.setTime(date);
	    rightNow.add(Calendar.DAY_OF_YEAR,1);//日期加1天
        String reStr = sdf.format(rightNow.getTime());
        Date dt1=null;
		try {
			dt1 = sdf.parse(reStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return dt1;
	}

}
