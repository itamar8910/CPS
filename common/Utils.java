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

import com.sun.javafx.image.impl.ByteIndexed.Getter;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
* <h> Class Utils aggregates many kinds of utilities reltated to time and data manipulations </h>
*/


public class Utils {

	/**
	 * Converts a date string to time in millis
	 * @param date: a date string in the format dd-MM-yyyy
	 * @return long date in millis
	 * @see DataFormat
	 * @see DataFormat.getTime()
	 */
	public static long dateToMillis(String dateStr){
		if(!isDateValid(dateStr)) {
			return -1l;
		}
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
	 * Converts date and time strings to time in millis
	 * @param dateStr: a date string in the format dd-MM-yyyy
	 * @param timeStr: a timeStr of format hh:mm:ss
	 * @return long Time in millis
	 */
	public static long dateAndTimeToMillis(String dateStr, String timeStr) {
		if(!isDateValid(dateStr) || todayTimeToMillis(timeStr) == -1l) {
			return -1l;
		}
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
	
	 /**
	 * Converts time string to time in millis
	 * @param timeStr: a timeStr of foramt hh:mm:ss
	 * @return long Time in millis
	 */
	public static long timeToMillis(String timeStr) {
		String[] tokens = timeStr.split(":");
		String hours = tokens[0];
		String minutes = tokens[1];
		int millisInMinute = 60*1000;
		return Integer.valueOf(hours) * millisInMinute * 60 + Integer.valueOf(minutes)*millisInMinute;
	}

	 /**
	 * Converts today time to millis
	 * @param timeStr: a timeStr of foramt hh:mm:ss
	 * @return long Time in millis
	 */
	public static long todayTimeToMillis(String timeStr) {
		String[] tokens = timeStr.split(":");
		String hours = tokens[0];
		String minutes = tokens[1];
		if(Integer.valueOf(hours) < 0 || Integer.valueOf(hours) > 24 || Integer.valueOf(minutes) < 0 || Integer.valueOf(minutes) > 60) {
			return -1l;
		}
		//DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		date.setHours(Integer.valueOf(hours));
		date.setMinutes(Integer.valueOf(minutes));
		date.setSeconds(0);

		System.out.println(date);
		return date.getTime();
	}
	
	/**
	* Given data string of format dd-MM-yyyy this function checks weather it is valid
	* @param data dataStr of format dd-MM-yyyy
	* @return boolan True if data is valid, False otherwise
	*/
	public static boolean isDateValid(String date) {
		String DATE_FORMAT = "dd-MM-yyyy";
        try {
            DateFormat df = new SimpleDateFormat(DATE_FORMAT);
            df.setLenient(false);
            df.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
	}
	
	
	public static void main(String args[]) {
		//System.out.println(timeToMillis("18:88"));
		System.out.println(dateAndTimeToMillis("01-11-2018", "18:28"));
	}

	/**
	* @param subscriptionStartUnixtime subscription start in unix time format
	* @return boolean True if given time is in last month, False otherwise
	*/
	public static boolean isInLastMonth(long subscriptionStartUnixtime) {
		System.out.println("current time in millis:" + System.currentTimeMillis());
		long diff = System.currentTimeMillis() - subscriptionStartUnixtime;
		System.out.println("diff:" + diff);
		long millisInMonth = 1000l * 60l * 60l * 24l * 28l;
		System.out.println("millis in month" + millisInMonth);
		return diff < millisInMonth;
	}

	/**
	* check weather current Hour is between 2 given hours
	* @param h1HHMM hour1, lower boundry, format: HH:MM
	* @param h2HHMM hour2, upper boundry, format: HH:MM
	* @return boolean True if current hour is between hour1 and hour2 (params)
	*/
	public static boolean isCurrentHourBetween(String h1HHMM, String h2HHMM) {
		Date currentDate = new Date();
		String currentHHMM = String.valueOf(currentDate.getHours()) + ":" + String.valueOf(currentDate.getMinutes());
		return timeToMillis(h1HHMM) <= timeToMillis(currentHHMM) && timeToMillis(currentHHMM) < timeToMillis(h2HHMM);
	}

	/**
	* check weather current time is after given time
	* @param unixTime Time in unix format
	* @return boolean True if current time is after given time(input param)
	*/
	public static boolean isCurrentTimeAfter(String unixTime) {
		return System.currentTimeMillis() > Long.valueOf(unixTime);
	}

	/**
	* check weather current time is somewhere in the weekend
	* @return boolean True if current time is weekend
	*/
	public static boolean isCurrentlyWeekend() {
		Date currentDate = new Date();
		Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        return calendar.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY && calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY;
	}

	/**
	* @param unixTime time in unix format
	* @return double hours difference between given time and current time
	*/
	public static double getHoursDiff(String unixTime) {
		long diff = -(System.currentTimeMillis() - Long.valueOf(unixTime));
		return diff / 1000.0 / 60.0 / 60.0;
	}

	/**
	* Check weather a parkin lot with the name ParkingLotName is full or not --> returns boolean + pop a dialog to UI
	* @param parkingLotName Parking lot name
	* @param mainStage User UI mainStage
	* @return boolean True if parking lot with parkingLotName is full, False otherwise
	*/
	public static boolean getIsFull(String parkingLotName, Stage mainStage) {
		Params params = Params.getEmptyInstance().addParam("action", "isParkingLotFull").addParam("name", parkingLotName);
		final boolean[] res = new boolean[1];
		TalkToServer.getInstance().sendAndWait(params.toString(), msg -> {
			Params resp = new Params(msg);
			System.out.println("clientIsFull got resp:" + resp);
			if(resp.getParam("isFull").equals("yes")) {
				res[0] = true;
				Platform.runLater(new Runnable() {
  	  		      @Override public void run() {
  	  	    		 final Stage dialog = new Stage();
  	  	             dialog.initModality(Modality.APPLICATION_MODAL);
  	  	             dialog.initOwner(mainStage);
  	  	             VBox dialogVbox = new VBox(20);
  	  	             dialogVbox.getChildren().add(new Text("Parking lot is full, please go to:" + resp.getParam("alternative")));
  	  	             Scene dialogScene = new Scene(dialogVbox, 300, 200);
  	  	             dialog.setScene(dialogScene);
  	  	             dialog.show();
  	  	             System.out.println("showed dialog");
  	  		      }
	  		    	});
			}else {
				res[0] = false;
			}
		});
		return res[0];
	}
	
	/**
	*
	* @param subscriptionStartUnix start time of subscription in unix format
	* @return int how many days ago the subscription started
	*/
	public static int getNumDaysAgo(long subscriptionStartUnix) {
		long diffUnix = System.currentTimeMillis() - subscriptionStartUnix;
		return (int)(diffUnix / 1000.0 / 60.0 / 60.0 / 24.0);
	}

	/**
	*
	* @param vehicleStartParkTime in unix format
	* @return String Hour format corresponding to vehicleStartParkTime param in unix format
	*/
	public static String unixTimeToHour(long vehicleStartParkTime) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(vehicleStartParkTime);
		return c.get(Calendar.HOUR) + ":" + c.get(Calendar.MINUTE);
	}

	public static boolean isEmailValid(String email) {
		return email.contains("@") && email.contains(".");
	}
	
}