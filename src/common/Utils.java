package common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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

	
}
