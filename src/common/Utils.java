package common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Utils {

	/**
	 * Convert a date string to time in millis
	 * @param date: a date string in the format dd-MM-yyyy
	 * @return
	 */
	public static long dateToMillis(String dateStr){
		DateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
		Date date;
		try {
			date = format.parse(dateStr);
			return date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Convert date and time strings to time in millis
	 * @param date: a date string in the format dd-MM-yyyy
	 * @return
	 */
	public static long dateAndTimeToMillis(String dateStr, String timeStr) {
		DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ENGLISH);
		Date date;
		try {
			date = format.parse(dateStr+" " + timeStr);
			return date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public static long timeToMillis(String timeStr) {
		String[] tokens = timeStr.split(":");
		String hours = tokens[0];
		String minutes = tokens[1];
		int millisInMinute = 60*1000;
		return Integer.valueOf(hours) * millisInMinute * 60 + Integer.valueOf(minutes)*millisInMinute;
	}


	public static long todayTimeToMillis(String timeStr) {
		String[] tokens = timeStr.split(":");
		String hours = tokens[0];
		String minutes = tokens[1];
		//DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		date.setHours(Integer.valueOf(hours));
		date.setMinutes(Integer.valueOf(minutes));
		date.setSeconds(0);

		System.out.println(date);
		return date.getTime();
	}
	
	
	
//	public static void main(String args[]) {
//		System.out.println(todayTimeToMillis("14:15"));
//	}

	public static boolean isInLastMonth(long subscriptionStartUnixtime) {
		System.out.println("current time in millis:" + System.currentTimeMillis());
		long diff = System.currentTimeMillis() - subscriptionStartUnixtime;
		System.out.println("diff:" + diff);
		long millisInMonth = 1000l * 60l * 60l * 24l * 28l;
		System.out.println("millis in month" + millisInMonth);
		return diff < millisInMonth;
	}

	public static boolean isCurrentHourBetween(String h1HHMM, String h2HHMM) {
		Date currentDate = new Date();
		String currentHHMM = String.valueOf(currentDate.getHours()) + ":" + String.valueOf(currentDate.getMinutes());
		return timeToMillis(h1HHMM) <= timeToMillis(currentHHMM) && timeToMillis(currentHHMM) < timeToMillis(h2HHMM);
	}

	public static boolean isCurrentTimeAfter(String unixTime) {
		return System.currentTimeMillis() > Long.valueOf(unixTime);
	}

	public static boolean isCurrentlyWeekend() {
		Date currentDate = new Date();
		Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        return calendar.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY && calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY;
	}

	public static double getHoursDiff(String unixTime) {
		long diff = -(System.currentTimeMillis() - Long.valueOf(unixTime));
		return diff / 1000.0 / 60.0 / 60.0;
	}

	
}
