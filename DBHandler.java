import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pojos.User;

import com.mysql.jdbc.DatabaseMetaData;
import com.mysql.jdbc.ResultSetMetaData;

import common.Params;


/**
 * 
 * @author CPS
 * This class handles API requests and communicates with 
 * the Data base
 */
public class DBHandler {
	
	/**
	 * Data base instance
	 */
	private static DBHandler instance;
	
	/**
	 * connection to client instance 
	 */
	private Connection conn;

	
	/**
	 * 
	 * @param conn = conncetion
	 * 
	 */
	public DBHandler(Connection conn){
		this.conn = conn;
	}
	
	/**
	 * 
	 * @return instance of database - this is a signeltone
	 */
	//returns instance of data base 
	public static DBHandler getInstance(){ // implements the singleton design pattern
		if(instance == null){
			try
			{
	            Class.forName("com.mysql.jdbc.Driver").newInstance();
	        } catch (Exception ex) {/* handle the error*/}
			System.out.println("Connecting to db");

	        try
	        {
	        	Connection conn =
	        			DriverManager.getConnection("jdbc:mysql://softengproject.cspvcqknb3vj.eu-central-1.rds.amazonaws.com:3306/kea_schema","kea_admin","U_`jK<7JKhV%dBwW");

	            System.out.println("SQL connection succeed");

	            instance = new DBHandler(conn);

	        }catch(SQLException e){
	        	System.out.println(e.getMessage());
	        }
		}
        return instance;
	}
	

	
	// general methods **********************************************
		
	/**
	 * 
	 * @param parkingID the parking ID
	 * @return return the parking name
	 */
	public String getParkingNameByID(int parkingID) {
		try {
			PreparedStatement select = conn.prepareStatement("SELECT name FROM ParkingFacility WHERE id=?");
			select.setInt(1, parkingID);
	
			ResultSet uprs = select.executeQuery();
			//System.out.println("success");
			if(uprs.next()){
				return uprs.getString("name");
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	
	//methods for cron job*******************************************8
	
	//sue user for a price, add it to the amount it needs to pay
	public void sueUser(int userID,double suePrice) {

			
		try { //success
	
			PreparedStatement requestPriceChanege = conn.prepareStatement("UPDATE Users SET money= money+ ? WHERE id = ?;");
			requestPriceChanege.setInt(1, userID);
			requestPriceChanege.setDouble(2, suePrice);
			requestPriceChanege.executeUpdate();

	
			
		} catch(SQLException e) { //failure
		
		}
		
	}
	
	
	
	//Methods for reports ********************************************
	
	
	
	
	//return activity data report for main manager
	//facID, startDate(unix), number of days back 
	//return:
	//mean/probabilityDist/standard deviation per week:
	//	num orders
	//	num canceled
	//  total hours per week
	
	public String returnActivityDataReport(Params data){
		
		Params resData = Params.getEmptyInstance();
		try {
			JSONArray returnData = new JSONArray();
			long endDate = Long.parseLong(data.getParam("startDate"));
			long startDate = endDate - (long)1000l*60l*60l*24l;
						
			System.out.println("End date : " + endDate);
			
			int counter = Integer.parseInt(data.getParam("numDays"));
			
			//run on all reports - per week
			while (counter > 0) {
				int numOrders = 0;
				int numCanceld = 0;
				double numHoursDisabled = 0;
				
							
				
				//add num hours disabled
				PreparedStatement getDataDisabled = conn.prepareStatement("SELECT * FROM DisabledTimes WHERE  facID = ?");
				getDataDisabled.setInt(1, Integer.parseInt(data.getParam("facID")));
				ResultSet getDataDisabledTimes = getDataDisabled.executeQuery();
				long startOfWeek = endDate - (long)7l*1000l*60l*60l*24l;
				long endOfWeekDate = endDate;
				//add data
				while (getDataDisabledTimes.next()) {
						long currStartTime = Long.parseLong(getDataDisabledTimes.getString("startTime"));
						long currEndTime = Long.parseLong(getDataDisabledTimes.getString("endTime"));
						
						//if current time contains it
						double addHours = 0;
						if (startOfWeek <= currStartTime && currEndTime <= endOfWeekDate) {
							addHours = (currEndTime - currStartTime)/1000/60/60;
							
							System.out.println("1");
						} else if (currStartTime <= startOfWeek &&  endOfWeekDate<= currEndTime) { //if times are in range
							addHours = (endOfWeekDate - startOfWeek)/1000/60/60;
							
							System.out.println("2");
						} else if(startOfWeek <= currStartTime && endOfWeekDate <=currEndTime && endOfWeekDate >=  currStartTime) { //if started before but ended in the middle
							addHours = (endOfWeekDate - currStartTime)/1000/60/60;

							System.out.println("3");
						}else if(currStartTime <= startOfWeek && endOfWeekDate <=currEndTime && startOfWeek <= currEndTime) { //if started after but ended  after
							addHours = (currEndTime - startOfWeek)/1000/60/60;

							System.out.println("4");
						}
						
						System.out.println("Else");
				
						numHoursDisabled += addHours;
						
				}
				
				
				
				//run on every day of week
				for (int j=0; j < 7;j++) {
					
					//add num orders and num canceld
					PreparedStatement getData = conn.prepareStatement("SELECT cancelOrders,orderByType FROM dailyStats WHERE date >= ? AND date < ? AND facID = ?");
					getData.setLong(1, startDate);
					getData.setLong(2, endDate);
					getData.setInt(3, Integer.parseInt(data.getParam("facID")));
					ResultSet result = getData.executeQuery();
					
					//add data
					while (result.next()) {
							numOrders += Integer.parseInt(result.getString("orderByType"));
							numCanceld += Integer.parseInt(result.getString("cancelOrders"));
					}
									
					//one day before
					startDate -= 1000l*60l*60l*24l;
					endDate -= 1000l*60l*60l*24l;
				}
				
				JSONObject currentDataObj = new JSONObject();
				try {
					currentDataObj.put("orders", String.valueOf(numOrders));
					currentDataObj.put("cancel", String.valueOf(numCanceld));
					currentDataObj.put("hoursDisabled",String.valueOf(numHoursDisabled));

					returnData.put(currentDataObj);
				
				} catch (JSONException e) {e.printStackTrace();}
				counter -= 7;
			}
			
			
				
			String parkingName = this.getParkingNameByID(Integer.parseInt(data.getParam("facID")));
			resData.addParam("name", parkingName);
			resData.addParam("data", returnData.toString());
			resData.addParam("status", "OK");
			
			return resData.toString();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//if failed
		resData.addParam("status", "BAD");
		return resData.toString(); 
	}
	
	
	
	public long getStartOfDayInMillis() {
	    Date d = new Date();
	    d.setHours(0);
	    d.setMinutes(0);
	    return d.getTime();
	}
	
	//return the data for costumer complaints report
	public String returnProbLotsReport(Params data){
		//get data from last quarter
		
		Params resData = Params.getEmptyInstance();
		try {
			JSONArray returnData = new JSONArray();
			long currentDate = this.getStartOfDayInMillis();
			long endOfDate = currentDate + (long)1000l*60l*60l*24l;
			long lastQuarter = this.getStartOfDayInMillis() - (long)(1000l*60l*60l*24l*30l*4l);

			//run on all reports
			while (currentDate > lastQuarter) {

				PreparedStatement requestQuarterReports = conn.prepareStatement("SELECT numLotsDisabled FROM dailyStats WHERE date >= ? AND date < ? AND facID = ?");
				requestQuarterReports.setLong(1, currentDate);
				requestQuarterReports.setLong(2, endOfDate);
				requestQuarterReports.setInt(3, Integer.parseInt(data.getParam("facID")));
				ResultSet result = requestQuarterReports.executeQuery();
				
				
				int hasData = 0;
				//go over all data
				while (result.next()) {
					returnData.put(result.getInt("numLotsDisabled"));
					hasData = 1;
				}	
				
				//add no complaints
				if (hasData == 0)
					returnData.put(hasData);
				
				//one day before
				currentDate -= 1000l*60l*60l*24l;
				endOfDate -= 1000l*60l*60l*24l;
			}
			
			
				
			String parkingName = this.getParkingNameByID(Integer.parseInt(data.getParam("facID")));
			resData.addParam("name", parkingName);
			resData.addParam("data", returnData.toString());
			resData.addParam("status", "OK");
			
			return resData.toString();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//if failed
		resData.addParam("status", "BAD");
		return resData.toString();
	}
	
	//return the data for costumer complaints report
	public String returnCostumerComplaintsReport(Params data){
		//get data from last quarter
		
		Params resData = Params.getEmptyInstance();
		try {
			JSONArray returnData = new JSONArray();
			long currentDate = this.getStartOfDayInMillis();
			long endOfDate = currentDate + (long)1000l*60l*60l*24l;
			long lastQuarter = this.getStartOfDayInMillis() - (long)(1000l*60l*60l*24l*30l*4l);

			//run on all reports
			while (currentDate > lastQuarter) {
				JSONArray currDayData = new JSONArray();

				PreparedStatement requestQuarterReports = conn.prepareStatement("SELECT * FROM complaints WHERE dateTime >= ? AND dateTime < ? AND facID = ?");
				requestQuarterReports.setLong(1, currentDate);
				requestQuarterReports.setLong(2, endOfDate);
				requestQuarterReports.setInt(3, Integer.parseInt(data.getParam("facID")));
				ResultSet result = requestQuarterReports.executeQuery();
				
				
				//go over all data
				while (result.next()) {
					int amount = -1;
					//set num refund
					if (result.getInt("moneyRec") >0) {
						amount = result.getInt("moneyRec");
					}
					currDayData.put(amount);
					
				}	
				
				//one day before
				currentDate -= 1000l*60l*60l*24l;
				endOfDate -= 1000l*60l*60l*24l;
				
				returnData.put(currDayData);
			}
			
			
			String parkingName = this.getParkingNameByID(Integer.parseInt(data.getParam("facID")));

			resData.addParam("name", parkingName);
			resData.addParam("data", returnData.toString());
			resData.addParam("status", "OK");
			
			return resData.toString();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//if failed
		resData.addParam("status", "BAD");
		return resData.toString();
	}
	
	
	//return the data for orders report
	public String returnOrdersReport(Params data){
		//get data from last quarter
		
		JSONArray returnData = new JSONArray();

		try {
			long currentDate = this.getStartOfDayInMillis();
			long endOfDate = currentDate + (long)1000l*60l*60l*24l;
			long lastQuarter = this.getStartOfDayInMillis() - (long)(1000l*60l*60l*24l*30l*4l);

			//run on all reports
			while (currentDate > lastQuarter) {
				PreparedStatement requestQuarterReports = conn.prepareStatement("SELECT * FROM dailyStats WHERE date >= ? AND date < ? AND facID = ?");
				requestQuarterReports.setLong(1, currentDate);
				requestQuarterReports.setLong(2, endOfDate);
				requestQuarterReports.setInt(3, Integer.parseInt(data.getParam("facID")));
				ResultSet result = requestQuarterReports.executeQuery();
				
				
				//go over all data
				int isStats = 0;
				while (result.next()) {
					JSONObject current = new JSONObject();
					current.put("orderByType", result.getString("orderByType"));
					current.put("cancelOrders", result.getString("cancelOrders"));
					current.put("lateForParking", result.getString("lateForParking"));
					current.put("date", currentDate);
					returnData.put(current);
					
					isStats = 1;
				}		
				
				if (isStats == 0) {
					JSONObject current = new JSONObject();
					current.put("orderByType", "0");
					current.put("cancelOrders", "0");
					current.put("lateForParking", "0");
					current.put("date", currentDate);
					returnData.put(current);
				}
				
				//one day before
				currentDate -= 1000l*60l*60l*24l;
				endOfDate -= 1000l*60l*60l*24l;
				
				
			}
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (JSONException e) {
			// TODO Auto-generated catch block
		}
		
		Params resData = Params.getEmptyInstance();
		String parkingName = this.getParkingNameByID(Integer.parseInt(data.getParam("facID")));
		resData.addParam("name", parkingName);
		resData.addParam("data", returnData.toString());
		resData.addParam("status", "OK");
		
		System.out.println("Returned data : " +resData.toString());
		
		return resData.toString();
		
	}
		
	
	
	//Methods for Worker *********************************************
	
	public String changeIndexWithVal(String changeData,int index, String string) {	
		
		try {
			JSONArray oldData = new JSONArray(changeData);
			JSONArray newData = new JSONArray();
			
			//change price value
			for (int i=0; i < oldData.length(); i++){
				System.out.println("Test ");
			    int itemArr = (int) oldData.get(i);
			    if(i == index){
			    	newData.put(Integer.parseInt(string));
			    } else 
			    	newData.put(itemArr);
			}
			
			return newData.toString();
			
		} catch(Exception e) {
			System.out.println("error" + e.getMessage());
			
			return changeData;
		}
	
	}
	
	
	//request all parkings
	//returns in format [{name, id, price,priceChangeRequest}]
	public String requestParkings(Params data){
					
		Params resData = Params.getEmptyInstance();
	    JSONArray returnData = new JSONArray();

		try {
			PreparedStatement requestPriceRequests = conn.prepareStatement("SELECT name,id FROM ParkingFacility");
			ResultSet result = requestPriceRequests.executeQuery();

			while (result.next()) {
				//if default parking
				if (result.getString("name").equals("Default"))
					continue;
				
		    	JSONObject current = new JSONObject();
				current.put("name", result.getString("name"));
				current.put("facID", result.getString("id"));
				returnData.put(current);
				
			}				
			resData.addParam("array", returnData.toString());
			resData.addParam("status", "ok");
			
			return resData.toString();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//if failed
		resData.addParam("res", "-1");
		return resData.toString();
	}
	
	//requestChangePrices
	//returns in format [{name, location, price,priceChangeRequest}]
		public String requestChangePrices(Params data){
						
			Params resData = Params.getEmptyInstance();
		    JSONArray returnData = new JSONArray();

			try {
				PreparedStatement requestPriceRequests = conn.prepareStatement("SELECT name,location,price,priceChangeRequest,id FROM ParkingFacility");
				ResultSet result = requestPriceRequests.executeQuery();

				while (result.next()) {
					JSONArray oldData = new JSONArray(result.getString("priceChangeRequest"));
					JSONArray oldPricesData = new JSONArray(result.getString("price"));

					//change price value
					for (int i=0; i < oldData.length(); i++){
					    int itemArr = (int) oldData.get(i);
					    if(itemArr != -1) {
					    	 JSONObject current = new JSONObject();
								current.put("name", result.getString("name"));
								current.put("location", result.getString("location"));
								current.put("price", oldPricesData.get(i));
								current.put("priceChangeRequest", itemArr);
								current.put("facID", result.getString("id"));
								current.put("type", i);

								returnData.put(current);
					    }
					}
					
				}				
				resData.addParam("array", returnData.toString());
				resData.addParam("status", "ok");
				
				System.out.println("Request : " + resData.toString());

				
				return resData.toString();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			//if failed
			resData.addParam("res", "-1");
			return resData.toString();
		}
	
	
	//perform login for employee
	public String emplyeLogin(Params data){
		String username = data.getParam("UserName");
		String password = data.getParam("Password");
				
		Params resData = Params.getEmptyInstance();
		
		try {
			PreparedStatement login = conn.prepareStatement("SELECT type,facID,id FROM Employee WHERE username=? AND password=? ;");
			login.setString(1, username);
			login.setString(2, password);
			
			ResultSet loginResult = login.executeQuery();
			
			while (loginResult.next()) {
				resData.addParam("type", loginResult.getString("type"));
				resData.addParam("facID", loginResult.getString("facID"));
				resData.addParam("userID", loginResult.getString("id"));
				
				return resData.toString();
			}
			
			System.out.println("success");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	// if login failed
		resData.addParam("type", "-1");
		resData.addParam("facID", "None");
		return resData.toString();
	}
	
	
	//return current prices/price change requests of parking facility
	public Params returnPricesOfParking(String facID) {
			Params resData = Params.getEmptyInstance();
		
		try {
			PreparedStatement query = conn.prepareStatement("SELECT price,priceChangeRequest FROM ParkingFacility WHERE id = ? ;");
			query.setInt(1, Integer.parseInt(facID));
			
			ResultSet dataRes = query.executeQuery();
			
			while (dataRes.next()) {
				resData.addParam("price", dataRes.getString("price"));
				resData.addParam("priceChangeRequest", dataRes.getString("priceChangeRequest"));
				
				return resData;
			}
			
			System.out.println("success");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	// if login failed
		resData.addParam("status", "-1");
		return resData;
	}
	
	//perform request for price change
	//facID, userID , price
	public String requestChangeOfPrice(Params data) {
		String facID = data.getParam("facID");
		String price = data.getParam("price");
		String type = data.getParam("type");
			
		try { //success
		System.out.println("Params : " + data.toString());
			
		Params resData = Params.getEmptyInstance();
		Params currentPriceData = this.returnPricesOfParking(facID);
		
			System.out.println("Data before : " + currentPriceData.getParam("priceChangeRequest").toString());
			String newData = this.changeIndexWithVal(currentPriceData.getParam("priceChangeRequest").toString(),Integer.parseInt(type),price.toString());
			System.out.println("Data after : " + newData);

			PreparedStatement requestPriceChanege = conn.prepareStatement("UPDATE ParkingFacility SET priceChangeRequest= ? WHERE id = ?;");
			requestPriceChanege.setString(1, newData.toString());
			requestPriceChanege.setInt(2, Integer.parseInt(facID));
			requestPriceChanege.executeUpdate();

			resData.addParam("res", "1");
			return resData.toString();
			
		} catch(SQLException e) { //failure
			Params resData = Params.getEmptyInstance();

			resData.addParam("res", "0");
			return resData.toString();
		}
		
	}
	
	//update price of facility
	//facID, approve,updatedPrice,type
	public String finishChangePrice(Params data) {
		String facID = data.getParam("facID");
		String approve = data.getParam("approve");
		String updatedPrice = data.getParam("updatedPrice");
		String type = data.getParam("type");
		Params currentPriceData = this.returnPricesOfParking(facID);

		System.out.println("Finished change price : "+data.toString());
		
		Params resData = Params.getEmptyInstance();
		
		try { //success
			//if approved request
			if (approve.equals("1")) {
				String newData = this.changeIndexWithVal(currentPriceData.getParam("price").toString(),Integer.parseInt(type),updatedPrice.toString());
				
				PreparedStatement changePrice = conn.prepareStatement("UPDATE ParkingFacility SET price= ? WHERE id = ? ;");
				changePrice.setString(1, newData.toString());
				changePrice.setInt(2, Integer.parseInt(facID));
				changePrice.executeUpdate();
			}
			
			//remove change request
			String newData = this.changeIndexWithVal(currentPriceData.getParam("priceChangeRequest").toString(),Integer.parseInt(type),"-1");
			
			PreparedStatement changeToNoRequsts = conn.prepareStatement("UPDATE ParkingFacility SET priceChangeRequest= ? WHERE id = ? ;");
			changeToNoRequsts.setString(1, newData.toString());
			changeToNoRequsts.setInt(2, Integer.parseInt(facID));
			changeToNoRequsts.executeUpdate();
			
			System.out.println("Finished updating price ");

			resData.addParam("res", "1");
			return resData.toString();
			
		} catch(SQLException e) { //failure
			resData.addParam("res", "0");
			return resData.toString();
		}
		
	}
	
	//Methods for parking full status **************************
	
	//changeParking status handled
	public String setParkingFullStatus(Params data) {

		System.out.println("Changing park status is full");
		
		Params resData = Params.getEmptyInstance();
		
		try { //success
			PreparedStatement updateStatus = conn.prepareStatement("UPDATE ParkingFacility SET isFull= ? WHERE id = ? ;");
			updateStatus.setInt(1, Integer.parseInt(data.getParam("isFull")));
			updateStatus.setInt(2, Integer.parseInt(data.getParam("facID")));
			updateStatus.executeUpdate();
			
			resData.addParam("status", "OK");
			return resData.toString();
			
		} catch(SQLException e) { //failure
			resData.addParam("status", "BAD");
			return resData.toString();
		}
		
	}
	
	//changeParking status handled
		public String setParkingDisabledStatus(Params data) {

			System.out.println("Changing park status disabled");
			
			Params resData = Params.getEmptyInstance();
			long unixTime = System.currentTimeMillis();

			try { //success
				
				//support num of hours disabled
				
				//if set to disabled
				if (Integer.parseInt(data.getParam("isDisabled")) == 1) {
					//set start unix time
					
					PreparedStatement updateStatus = conn.prepareStatement("UPDATE ParkingFacility SET lastDisChangeDate= ? WHERE id = ? ;");
					updateStatus.setLong(1, unixTime);
					updateStatus.setInt(2, Integer.parseInt(data.getParam("facID")));
					updateStatus.executeUpdate();
				} else {  // set how many hours past
					try {
						PreparedStatement query = conn.prepareStatement("SELECT lastDisChangeDate FROM ParkingFacility WHERE id = ? ;");
						query.setInt(1, Integer.parseInt(data.getParam("facID")));
						
						ResultSet dataRes = query.executeQuery();
						
						while (dataRes.next()) {
							long lastDisChangeDate = Long.parseLong(dataRes.getString("lastDisChangeDate"));
							long timePassed = System.currentTimeMillis() ;
							
							//add disabled time
							PreparedStatement addDis = conn.prepareStatement("INSERT INTO DisabledTimes(facID,startTime,endTime) VALUES (?,?,?) ");
							addDis.setInt(1,Integer.valueOf(data.getParam("facID")));
							addDis.setLong(2,Long.valueOf(lastDisChangeDate));
							addDis.setLong(3,Long.valueOf(timePassed));
							addDis.executeUpdate();
						
							//update facility
							PreparedStatement updateStatusDate = conn.prepareStatement("UPDATE ParkingFacility SET lastDisChangeDate=-1 WHERE id = ? ;");
							updateStatusDate.setInt(1, Integer.parseInt(data.getParam("facID")));
							updateStatusDate.executeUpdate();
										
						}
						
					} catch (SQLException e) {}
										
				}

				
				//support is currently disabled
				PreparedStatement updateStatus = conn.prepareStatement("UPDATE ParkingFacility SET isDisabled= ? WHERE id = ? ;");
				updateStatus.setInt(1, Integer.parseInt(data.getParam("isDisabled")));
				updateStatus.setInt(2, Integer.parseInt(data.getParam("facID")));
				updateStatus.executeUpdate();
								
				
				
				resData.addParam("status", "OK");
				return resData.toString();
				
			} catch(SQLException e) { //failure
				resData.addParam("status", "BAD");
				return resData.toString();
			}
			
		}

	//returns facility parking data
	public String requestParkingStatusData(Params data){

		Params resData = Params.getEmptyInstance();
		
		try {
			PreparedStatement reqData = conn.prepareStatement("SELECT isFull,isDisabled,name FROM ParkingFacility WHERE id =?;");
			reqData.setInt(1,Integer.parseInt( data.getParam("facID")));
			
			ResultSet results = reqData.executeQuery();
			
			while (results.next()) {
				resData.addParam("name", results.getString("name"));
				resData.addParam("isFull", results.getString("isFull"));
				resData.addParam("isDisabled", results.getString("isDisabled"));
				resData.addParam("status", "OK");
				
				return resData.toString();
			}
			
			System.out.println("success");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	// if login failed
		resData.addParam("name", "-1");
		resData.addParam("isFull", "-1");
		resData.addParam("isDisabled", "-1");
		resData.addParam("status", "BAD");
		return resData.toString();
	}
	
	
	
	
	//Methods for client *********************************************
	//performs payment from client to systen
	public String performPayment(Params data) {
			
		Params resData = Params.getEmptyInstance();
		double amount = Double.parseDouble(data.getParam("amount"));
		
		try { 	
			PreparedStatement payAmount = conn.prepareStatement("UPDATE Users SET money = money + ? WHERE userID = ? ");
			payAmount.setDouble(1,amount);
			payAmount.setString(2,String.valueOf(data.getParam("userID")));
			payAmount.executeUpdate();
			
			System.out.println("Payed");

			resData.addParam("status", "OK");
			return resData.toString();
			
		} catch(SQLException e) { //failure
			resData.addParam("status", "BAD");
			return resData.toString();
		}
		
		
		//**********************************
		//pass amount and pay to system
		//ENTER VISA SYSTEM FOR PAYMENT 
		//**********************************
		
	}
	
	
	
	//returns random parking facility
	public String getRandomParking(String excludeName) {
		
		ArrayList<String> allParkingNames = new ArrayList<String>();
		//check if full
		PreparedStatement reqData;
		try {
			reqData = conn.prepareStatement("SELECT name FROM ParkingFacility WHERE isFull = 0");
			ResultSet results = reqData.executeQuery();	
			//get result
			while (results.next()) {
				//if current 
				if (excludeName.equals(results.getString("name")) || excludeName.equals("Default"))
					continue;
				
				allParkingNames.add(results.getString("name"));
			}
			
		    Random random = new Random();
			int randIndex = random.nextInt(allParkingNames.size());
			
			return allParkingNames.get(randIndex);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return "";
	}
	
			
	//return fac id by name 
	public int returnFacIDByName(String name) {
		PreparedStatement reqData;
		try {
			reqData = conn.prepareStatement("SELECT id FROM ParkingFacility WHERE name =?;");
			reqData.setString(1,name);
			
			ResultSet results = reqData.executeQuery();
			
			while (results.next()) {
				return results.getInt("id");
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	
	//add complaint by the users
	public String addUserComplaint(Params data) {
		int facID = this.returnFacIDByName(data.getParam("facName"));
	
		System.out.println("Adding User Complaint");
		
		Params resData = Params.getEmptyInstance();
		
		try { 
			long unixTime = System.currentTimeMillis();
	
			PreparedStatement changeToNoRequsts = conn.prepareStatement("INSERT INTO complaints(userID,text,dateTime,facID) VALUES (?,?,?,?) ");
			changeToNoRequsts.setString(1,data.getParam("ID"));
			changeToNoRequsts.setString(2,data.getParam("text").toString());
			changeToNoRequsts.setLong(3,Long.valueOf(unixTime));
			changeToNoRequsts.setInt(4,facID);
			changeToNoRequsts.executeUpdate();
			
			System.out.println("Added user complaint ");

			resData.addParam("status", "OK");
			return resData.toString();
			
		} catch(SQLException e) { //failure
			resData.addParam("status", "BAD");
			return resData.toString();
		}
	}
	
	
	//return all user complaints not handled
	public String returnUsersComplaints(Params data) {
		
		System.out.println("Data : " + data.toString());
		
		Params resData = Params.getEmptyInstance();
	    JSONArray returnData = new JSONArray();

		try {
			PreparedStatement complaintsReq = conn.prepareStatement("SELECT * FROM complaints WHERE isHandled=0 AND facID = ?;");		
			complaintsReq.setInt(1, Integer.parseInt(data.getParam("facID")));
			ResultSet result = complaintsReq.executeQuery();

			while (result.next()) {

				JSONObject current = new JSONObject();
				current.put("id", result.getString("id"));
				current.put("userID", result.getString("userID"));
				current.put("text",result.getString("text"));
				current.put("dateTime", result.getString("dateTIme"));
				
				returnData.put(current);
			    
			}
				
							
			resData.addParam("array", returnData.toString());
			resData.addParam("status", "ok");
			
			System.out.println("Request : " + resData.toString());

			
			return resData.toString();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//if failed
		resData.addParam("array", "");
		resData.addParam("res", "-1");
		return resData.toString();
	}
			
	
	//returns the user id for multiple cars
	public String returnGenericUserID(int id) {
		try {
	
			PreparedStatement reqData = conn.prepareStatement("SELECT userID FROM Users WHERE id = ?");
			reqData.setInt(1, id);
			ResultSet results = reqData.executeQuery();	
			//get result
			while (results.next()) {
				return results.getString("userID");	
			}
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "-1";
	}
	
	//handle complaint of a user
	public String handleComplaint(Params data) {
		System.out.println("handling user complaints");
		
		Params resData = Params.getEmptyInstance();
		
		try { //success
			
			//updated handled
			PreparedStatement updateStatus = conn.prepareStatement("UPDATE complaints SET isHandled= 1 WHERE id = ? ;");
			updateStatus.setInt(1, Integer.parseInt(data.getParam("complaintID")));
			updateStatus.executeUpdate();
		
			//if only to ignore it		
			if (data.getParam("shouldResolve").equals("0")) { 				
				resData.addParam("status", "OK");
				return resData.toString();
			}
			
			//if not to ignore - send money to user
			//update money sent
			PreparedStatement updateStatus1 = conn.prepareStatement("UPDATE complaints SET moneyRec= ? WHERE id = ? ;");
			updateStatus1.setInt(1, Integer.parseInt(data.getParam("money")));
			updateStatus1.setInt(2, Integer.parseInt(data.getParam("complaintID")));
			updateStatus1.executeUpdate();
			
			String userFullID = data.getParam("userID");
			//update handled
			PreparedStatement updateUserMoney = conn.prepareStatement("UPDATE Users SET money= money - ? WHERE userID = ? ;");
			updateUserMoney.setInt(1, Integer.parseInt(data.getParam("money")));
			updateUserMoney.setString(2, userFullID);
			updateUserMoney.executeUpdate();
			
			resData.addParam("status", "OK");
			return resData.toString();
			
		} catch(SQLException e) { //failure
			resData.addParam("status", "BAD");
			return resData.toString();
		}
		
	}

	
	public void printAllTables(){
		try{
			  java.sql.DatabaseMetaData md = conn.getMetaData();
			  ResultSet rs = md.getTables(null, null, "%", null);
			  while (rs.next()) {
			    System.out.println(rs.getString(3));
			  }
		}catch(SQLException e){
			System.out.println(e.getMessage());
		}
	}

	public boolean addRow(String name, int balance){
		Statement stmt;
		try {
			//stmt = conn.createStatement();
			//stmt.executeUpdate("INSERT INTO test_table VALUES();");
			if(getBalanceOf(name) != -1){
				return false;
			}
			PreparedStatement updatePrice = conn.prepareStatement("INSERT INTO test_table(name, balance) VALUES(?,?);");
			updatePrice.setString(1, name);
			updatePrice.setInt(2, balance);
			updatePrice.executeUpdate();
			System.out.println("success");
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public void updateRow(String name, int balance){
		Statement stmt;
		try {
			//stmt = conn.createStatement();
			//stmt.executeUpdate("INSERT INTO test_table VALUES();");
			PreparedStatement updatePrice = conn.prepareStatement("UPDATE test_table SET balance=? WHERE name=?");
			updatePrice.setString(2, name);
			updatePrice.setInt(1, balance);
			updatePrice.executeUpdate();
			System.out.println("success");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public int[] getVehicleParkingSpot(String vehicleID, String parkingLot) {
		JSONObject data = getParkingLotJsonData(parkingLot);
		try {
			JSONArray parkingData = data.getJSONArray("parkingData");
			for(int index = 0; index < parkingData.length(); index++) {
				JSONObject spotJson = parkingData.getJSONObject(index);
				int i = spotJson.getInt("i");
				int j = spotJson.getInt("j");
				int k = spotJson.getInt("k");
				String carID = spotJson.getString("carID");
				if(carID.equals(vehicleID)) {
					return new int[] {i, j, k};
				}
				
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new int[] {-1,-1,-1};
	}
	
	public String canBeInParking(Params data) {
        System.out.println("canBeInParking : " + data.toString());
       
        Params resData = Params.getEmptyInstance();
        JSONArray returnData = new JSONArray();
 
        try {
            //check if full
            PreparedStatement reqData = conn.prepareStatement("SELECT id,isFull FROM ParkingFacility WHERE name =?;");
            reqData.setString(1,data.getParam("facName"));
            ResultSet results = reqData.executeQuery();
           
            //get result
            while (results.next()) {
                //if not full
                if (results.getString("isFull").equals("0")) {
                    resData.addParam("status", "OK");
                    resData.addParam("isFull", "0");
                    resData.addParam("alternative", "-1");
                    return resData.toString();
                //if full
                } else {
                    resData.addParam("status", "OK");
                    resData.addParam("isFull", "1");
                    resData.addParam("alternative",DBHandler.getInstance().getRandomParking(data.getParam("facName")) );
                    return resData.toString();
                }
       
               
            }
               
 
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
 
        //if failed
        resData.addParam("status", "BAD");
        resData.addParam("isFull", "-1");
        resData.addParam("alternative", "-1");
        return resData.toString();
    }

	public List<String> getAllVehiclesOfUser(String userID) {
		List<String> vehicleIDs = new ArrayList<String>();
		try {
			PreparedStatement select = conn.prepareStatement("SELECT vehicleID FROM Vehicles WHERE userID=?");
			select.setString(1, userID);
	
			ResultSet uprs = select.executeQuery();
			System.out.println("success");
			//TODO: support multiple vehicles
			while(uprs.next()){
				vehicleIDs.add(uprs.getString("vehicleID"));
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return vehicleIDs;
	}
	
	public int getBalanceOf(String name){
		try {
			PreparedStatement selectName = conn.prepareStatement("SELECT balance FROM test_table WHERE name=?;");
			selectName.setString(1, name);
			ResultSet uprs = selectName.executeQuery();

			while(uprs.next()){
				return uprs.getInt("balance");
			}
			System.out.println("success");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	public String getAll() {
		try {
			PreparedStatement selectName = conn.prepareStatement("SELECT name, balance FROM test_table WHERE 1;");

			ResultSet uprs = selectName.executeQuery();
			String buff = "";
			while(uprs.next()){
				buff += uprs.getString("name") + "," + String.valueOf((uprs.getInt("balance"))) + "\n";
			}
			System.out.println("success");
			return buff;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";

	}

	public boolean isInTable(String table, String colName, String value) {
		try {
			PreparedStatement selectName = conn.prepareStatement("SELECT * FROM " + table + " WHERE "+colName+"=?;");
			selectName.setString(1, value);
			//PreparedStatement selectName = conn.prepareStatement("SELECT * FROM Users WHERE userID=?;");
			//selectName.setString(1, "123");
			ResultSet uprs = selectName.executeQuery();
			System.out.println("success");
			if(uprs.next()){
				return true;
			}

			return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public String getParkingLotNameByID(int parkingLotID) {
		try {
			PreparedStatement select = conn.prepareStatement("SELECT name FROM ParkingFacility WHERE id=?");
			select.setInt(1, parkingLotID);
	
			ResultSet uprs = select.executeQuery();
			System.out.println("success");
			if(uprs.next()){
				return uprs.getString("name");
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public void addUser(String userID, String vehicleID, String email, String type) {
		PreparedStatement update;
		try {
			update = conn.prepareStatement("INSERT INTO Users(userID, vehicleID, email, type) VALUES(?,?,?,?)");
			update.setString(1, userID);
			update.setString(2, vehicleID);
			update.setString(3, email);
			update.setString(4, type);
			update.executeUpdate();
			System.out.println("success");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void addVehicle(String userID, String vehicleID, String parkingLot, long startTime, long endTime, boolean isInParking) {
		PreparedStatement update;
		try {
			update = conn.prepareStatement("INSERT INTO Vehicles(userID, vehicleID, parkingLot, startTime, endTime, isInParking) VALUES(?,?,?,?,?,?)");
			update.setString(1, userID);
			update.setString(2, vehicleID);
			update.setString(3, parkingLot);
			System.out.println("startTime" + startTime);
			System.out.println("endTime" + endTime);
			update.setLong(4, startTime);
			update.setLong(5, endTime);
			update.setString(6, String.valueOf(isInParking));
			update.executeUpdate();
			System.out.println("success");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int getParkingLotWidthOld(String parkingLotName){
		try {
			PreparedStatement select = conn.prepareStatement("SELECT width FROM ParkingLots WHERE name=?");
			select.setString(1, parkingLotName);

			ResultSet uprs = select.executeQuery();
			System.out.println("success");
			if(uprs.next()){
				return uprs.getInt("width");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	
	public int getParkingLotWidth(String parkingLotName){
		try {
			PreparedStatement select = conn.prepareStatement("SELECT dimension FROM ParkingFacility WHERE name=?");
			select.setString(1, parkingLotName);

			ResultSet uprs = select.executeQuery();
			System.out.println("success");
			if(uprs.next()){
				return uprs.getInt("dimension");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	public JSONArray getParkingLotJsonDataOld(String parkingLotName){
		try {
			PreparedStatement select = conn.prepareStatement("SELECT data FROM ParkingLots WHERE name=?");
			select.setString(1, parkingLotName);

			ResultSet uprs = select.executeQuery();
			System.out.println("success");
			if(uprs.next()){
				System.out.println("getParkingLotJsonData resp:" + uprs.getString("data"));
				return new JSONArray(uprs.getString("data"));
			}
			System.out.println("getParkingLotJsonData got rempty result set");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("exceotion at getParkingLotJsonData");
		return null;
	}

	public JSONObject getParkingLotJsonData(String parkingLotName){
		try {
			PreparedStatement select = conn.prepareStatement("SELECT data FROM ParkingFacility WHERE name=?");
			select.setString(1, parkingLotName);

			ResultSet uprs = select.executeQuery();
			System.out.println("success");
			if(uprs.next()){
				System.out.println("getParkingLotJsonData resp:" + uprs.getString("data"));
				return new JSONObject(uprs.getString("data"));
			}
			System.out.println("getParkingLotJsonData got rempty result set");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("exceotion at getParkingLotJsonData");
		return null;
	}
	
	public void addParkingLot(String name, String data, int width){
		PreparedStatement update;
		try {
			update = conn.prepareStatement("INSERT INTO ParkingLots(name, data, width) VALUES(?,?,?)");
			update.setString(1, name);
			update.setString(2, data);
			update.setInt(3, width);

			update.executeUpdate();
			System.out.println("success");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updateParkingLotDataOld(String name, String data){
		System.out.println("updateParkingLotData with:" + data);
		PreparedStatement update;
		try {
			update = conn.prepareStatement("UPDATE ParkingLots SET data=? WHERE name=?");
			update.setString(1, data);
			update.setString(2, name);

			update.executeUpdate();
			System.out.println("success");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updateParkingLotData(String name, String data){
		System.out.println("updateParkingLotData with:" + data);
		PreparedStatement update;
		try {
			update = conn.prepareStatement("UPDATE ParkingFacility SET data=? WHERE name=?");
			update.setString(1, data);
			update.setString(2, name);

			update.executeUpdate();
			System.out.println("success");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int getIndexIDOfUser(String userID) {
		try {
			PreparedStatement select = conn.prepareStatement("SELECT ID FROM Users WHERE userID=?");
			select.setString(1, userID);
	
			ResultSet uprs = select.executeQuery();
			System.out.println("success");
			if(uprs.next()){
				return uprs.getInt("id");
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public void updateUserType(String userID, String typeStr) {
		PreparedStatement update;
		try {
			update = conn.prepareStatement("UPDATE Users SET type=? WHERE userID=?");
			update.setString(1, typeStr);
			update.setString(2, userID);

			update.executeUpdate();
			System.out.println("success");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getUserVehicleID(String userID) {
		try {
			PreparedStatement select = conn.prepareStatement("SELECT vehicleID FROM Users WHERE userID=?");
			select.setString(1, userID);
	
			ResultSet uprs = select.executeQuery();
			System.out.println("success");
			//TODO: support multiple vehicles
			if(uprs.next()){
				return uprs.getString("vehicleID");
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return "";
		
	}

	public String getUserIdFromVehicleID(String vehicleID) {
		try {
			PreparedStatement select = conn.prepareStatement("SELECT userID FROM Vehicles WHERE vehicleID=?");
			select.setString(1, vehicleID);
	
			ResultSet uprs = select.executeQuery();
			System.out.println("success");
			if(uprs.next()){
				return uprs.getString("userID");
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return "";
	}

	public String getUserSubscriptionTypeStr(String userID) {
		try {
			PreparedStatement select = conn.prepareStatement("SELECT type FROM Users WHERE userID=?");
			select.setString(1, userID);
	
			ResultSet uprs = select.executeQuery();
			System.out.println("success");
			if(uprs.next()){
				return uprs.getString("type");
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return "";
	}

	public void updateVehicleStartLeaveTimes(String vehicleID, String startTimeUnix, String leaveTimeUnix) {
		PreparedStatement update;
		try {
			update = conn.prepareStatement("UPDATE Vehicles SET startTime=?, endTime=? WHERE vehicleID=?");
			update.setString(1, startTimeUnix);
			update.setString(2, leaveTimeUnix);
			update.setString(3, vehicleID);

			update.executeUpdate();
			System.out.println("success");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updateVehicleIsParking(String vehicleID, String val) {
		PreparedStatement update;
		try {
			update = conn.prepareStatement("UPDATE Vehicles SET isInParking=? WHERE vehicleID=?");
			update.setString(1, val);
			update.setString(2, vehicleID);

			update.executeUpdate();
			System.out.println("success");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void addToUserMoney(String userID, double amount) {
		PreparedStatement update;
		try {
			update = conn.prepareStatement("UPDATE Users SET money=money+? WHERE userID=?");
			update.setDouble(1, amount);
			update.setString(2, userID);
			update.executeUpdate();
			System.out.println("success");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<Integer> getPrices(String parkingLot) {
		List<Integer> prices = new ArrayList<Integer>();
		try {
			PreparedStatement select = conn.prepareStatement("SELECT price FROM ParkingFacility WHERE name=?");
			select.setString(1, parkingLot);
	
			ResultSet uprs = select.executeQuery();
			System.out.println("success");
			if(uprs.next()){
				String priceListStr = uprs.getString("price");
				JSONArray priceList = new JSONArray(priceListStr);
				for(int i = 0; i < priceList.length(); i++) {
					prices.add(priceList.getInt(i));
				}
			}
		}catch(SQLException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return prices;
	}

	public long getVehicleStartParkTime(String vehicleID) {
		try {
			PreparedStatement select = conn.prepareStatement("SELECT startTime FROM Vehicles WHERE vehicleID=?");
			select.setString(1, vehicleID);
	
			ResultSet uprs = select.executeQuery();
			System.out.println("success");
			if(uprs.next()){
				String timeStr = uprs.getString("startTime");
				return Long.valueOf(timeStr);
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return 0l;
	}

	public String getUserIDByVehicleID(String vehicleID) {
		try {
			PreparedStatement select = conn.prepareStatement("SELECT userID FROM Vehicles WHERE vehicleID=?");
			select.setString(1, vehicleID);
	
			ResultSet uprs = select.executeQuery();
			System.out.println("success");
			if(uprs.next()){
				return uprs.getString("userID");
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return "";
	}

	public String getVehicleParkingLot(String vehicleID) {
		try {
			PreparedStatement select = conn.prepareStatement("SELECT parkingLot FROM Vehicles WHERE vehicleID=?");
			select.setString(1, vehicleID);
	
			ResultSet uprs = select.executeQuery();
			System.out.println("success");
			if(uprs.next()){
				return uprs.getString("parkingLot");
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return "";
	}

	public void removeUser(String userID) {
		PreparedStatement update;
		try {
			update = conn.prepareStatement("DELETE FROM Users WHERE userID=?");
			update.setString(1, userID);
			update.executeUpdate();
			System.out.println("success");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void removeVehicle(String vehicleID) {
		PreparedStatement update;
		try {
			update = conn.prepareStatement("DELETE FROM Vehicles WHERE vehicleID=?");
			update.setString(1, vehicleID);
			update.executeUpdate();
			System.out.println("success");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<User> getAllUsers() {
		ArrayList<User> users = new ArrayList<User>();
		try {
			PreparedStatement select = conn.prepareStatement("SELECT * FROM Users WHERE 1");
	
			ResultSet uprs = select.executeQuery();
			System.out.println("success");
			while(uprs.next()){
				users.add(new User(uprs.getString("userID"), uprs.getString("vehicleID"), uprs.getString("email"), uprs.getString("type"), uprs.getDouble("money")));
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return users;
	}

	public List<String> getAllParkingLotNames() {
		List<String> parkingLots = new ArrayList<String>();
		try {
			PreparedStatement select = conn.prepareStatement("SELECT name FROM ParkingFacility WHERE 1");
	
			ResultSet uprs = select.executeQuery();
			System.out.println("success");
			while(uprs.next()){
				parkingLots.add(uprs.getString("name"));
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return parkingLots;
	}

	public boolean getIsVehicleInParking(String vehicleID) {
		try {
			PreparedStatement select = conn.prepareStatement("SELECT isInParking FROM Vehicles WHERE vehicleID=?");
			select.setString(1, vehicleID);
	
			ResultSet uprs = select.executeQuery();
			System.out.println("success");
			if(uprs.next()){
				return uprs.getString("isInParking").equals("true");
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public int getParkingLotIDByName(String parkingLotName) {
		try {
			PreparedStatement select = conn.prepareStatement("SELECT id FROM ParkingFacility WHERE name=?");
			select.setString(1, parkingLotName);
	
			ResultSet uprs = select.executeQuery();
			System.out.println("success");
			if(uprs.next()){
				return uprs.getInt("id");
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void initStatsIfDoesntExists(int parkingLotID, long todayUnixTime) {
		try {
			PreparedStatement select = conn.prepareStatement("SELECT id FROM dailyStats WHERE date=? AND facID=?");
			select.setString(1, String.valueOf(todayUnixTime));
			select.setString(2, String.valueOf(parkingLotID));
			ResultSet uprs = select.executeQuery();
			System.out.println("success");
			boolean exists = false;
			if(uprs.next()){
				exists = true;
			}
			System.out.println("exists in stats table:" + exists);
			if(!exists) {
				//insert to stats table if doesn't exists
				PreparedStatement update;
				try {
					update = conn.prepareStatement("INSERT INTO dailyStats(date, facID) VALUES(?,?)");
					update.setString(1, String.valueOf(todayUnixTime));
					update.setString(2, String.valueOf(parkingLotID));
					

					update.executeUpdate();
					System.out.println("success-inserted");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
	}

	public void addToDailyStats(int parkingLotID, long todayUnixTime, int lateDelta, int cancelDelta, int arrivedDelta, int numDisabledDelta) {
		try {
			PreparedStatement updateStats = conn.prepareStatement("UPDATE dailyStats SET lateForParking=lateForParking+?, cancelOrders=cancelOrders+?, orderByType=orderByType+?, numLotsDisabled=numLotsDisabled+?  WHERE facID=? AND date=?");
			
			updateStats.setInt(1, lateDelta);
			updateStats.setInt(2, cancelDelta);
			updateStats.setInt(3, arrivedDelta);
			updateStats.setInt(4, numDisabledDelta);
			updateStats.setInt(5, parkingLotID);
			updateStats.setString(6, String.valueOf(todayUnixTime));
			updateStats.executeUpdate();
			System.out.println("success");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
}


