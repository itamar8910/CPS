import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mysql.jdbc.DatabaseMetaData;

import common.Params;
import common.User;


/*
* <h> Main class for communication and management of DataBase </h>
* This class implements the singleton design pattern.
* This class is responsible for all communication with DB and his management.
* This is the only class the communicate (requests and responses) with SQL DB.
* Each update, insertion or query of DataBase is going through this class.

* @author  ~~ Etgar ~~ team, Software Engineering course 2018
* @version 1.0
* @since   10.1.18
*/


public class ItamarDB {

	private static ItamarDB instance;

	private Connection conn;

	public ItamarDB(Connection conn){
		this.conn = conn;
	}
	/**
	* Implementation of the singleton design pattern
	* @return ItamarDB Instance this class
	*/
	public static ItamarDB getInstance(){ // implements the singleton design pattern
		if(instance == null){
			try
			{
	            Class.forName("com.mysql.jdbc.Driver").newInstance();
	        } catch (Exception ex) {/* handle the error*/}
			System.out.println("Connecting to db");

	        try
	        {
	        	//plz don't pwn
	        	Connection conn =
	        			DriverManager.getConnection("jdbc:mysql://softengproject.cspvcqknb3vj.eu-central-1.rds.amazonaws.com:3306/kea_schema","kea_admin","U_`jK<7JKhV%dBwW");

	            System.out.println("SQL connection succeed");

	            instance = new ItamarDB(conn);

	        }catch(SQLException e){
	        	System.out.println(e.getMessage());
	        }
		}
        return instance;
	}
	/**
	* Prints all tables
	*/
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
	/**
	* Adds a new row to DB given name, balance
	* @param name username
	* @param balance Balance of user in account
	*/
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
			
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	/**
	* Updates row given name, balance according to given balance
	* @param name Username
	* @param balance New balance to update in user's account
	*/
	public void updateRow(String name, int balance){
		Statement stmt;
		try {
			//stmt = conn.createStatement();
			//stmt.executeUpdate("INSERT INTO test_table VALUES();");
			PreparedStatement updatePrice = conn.prepareStatement("UPDATE test_table SET balance=? WHERE name=?");
			updatePrice.setString(2, name);
			updatePrice.setInt(1, balance);
			updatePrice.executeUpdate();
			

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	* Returns balance of given user
	* @param name Username
	*
	* @return int Balance of user with given name
	*/
	public int getBalanceOf(String name){
		try {
			PreparedStatement selectName = conn.prepareStatement("SELECT balance FROM test_table WHERE name=?;");
			selectName.setString(1, name);
			ResultSet uprs = selectName.executeQuery();

			while(uprs.next()){
				return uprs.getInt("balance");
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	
	/**
	* @return String All names and their balances from table
	*/
	public String getAll() {
		try {
			PreparedStatement selectName = conn.prepareStatement("SELECT name, balance FROM test_table WHERE 1;");

			ResultSet uprs = selectName.executeQuery();
			String buff = "";
			while(uprs.next()){
				buff += uprs.getString("name") + "," + String.valueOf((uprs.getInt("balance"))) + "\n";
			}
			
			return buff;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";

	}
	/**
	* Check if colName with given value is in a specific table
	*
	* @param table Table to search in
	* @param colName Column name
	* @param value Target value to check if col's value equals to
	* @return boolean Indicates weather value of colName is equal to given value param
	*/
	public boolean isInTable(String table, String colName, String value) {
		try {
			PreparedStatement selectName = conn.prepareStatement("SELECT * FROM " + table + " WHERE "+colName+"=?;");
			selectName.setString(1, value);
			//PreparedStatement selectName = conn.prepareStatement("SELECT * FROM Users WHERE userID=?;");
			//selectName.setString(1, "123");
			ResultSet uprs = selectName.executeQuery();
			
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
	
	/**
	* Adds user to DB
	* @param userID UserID number
	* @param vehicleID User's vehicleID number
	* @param email User's email
	* @param type User type
	*/
	public void addUser(String userID, String vehicleID, String email, String type) {
		PreparedStatement update;
		try {
			update = conn.prepareStatement("INSERT INTO Users(userID, vehicleID, email, type) VALUES(?,?,?,?)");
			update.setString(1, userID);
			update.setString(2, vehicleID);
			update.setString(3, email);
			update.setString(4, type);
			update.executeUpdate();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/**
	* Adds vehicle to DB
	* @param userID UserID number
	* @param vehicleID User's vehicleID number
	* @param parkingLot parkingLot where the vehicle parks
	* @param startTime Parking of vehicle start time
	* @param endTime Parking of vehicle end time
	* @param isInParking Is vehicle in parking
	*/
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
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	* @param ParkingLotName Parking lot name
	* @return int Width of parkingLog
	*/
	public int getParkingLotWidthOld(String parkingLotName){
		try {
			PreparedStatement select = conn.prepareStatement("SELECT width FROM ParkingLots WHERE name=?");
			select.setString(1, parkingLotName);

			ResultSet uprs = select.executeQuery();
			
			if(uprs.next()){
				return uprs.getInt("width");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	/**
	* @param ParkingLotName Parking lot name
	* @return int Width of parkingLog
	*/
	public int getParkingLotWidth(String parkingLotName){
		try {
			PreparedStatement select = conn.prepareStatement("SELECT dimension FROM ParkingFacility WHERE name=?");
			select.setString(1, parkingLotName);

			ResultSet uprs = select.executeQuery();
			
			if(uprs.next()){
				return uprs.getInt("dimension");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	/**
	* @param ParkingLotName Parking lot name
	* @return JSONArray Data about parking lot with given name
	*/
	public JSONArray getParkingLotJsonDataOld(String parkingLotName){
		try {
			PreparedStatement select = conn.prepareStatement("SELECT data FROM ParkingLots WHERE name=?");
			select.setString(1, parkingLotName);

			ResultSet uprs = select.executeQuery();
			
			if(uprs.next()){
				//System.out.println("getParkingLotJsonData resp:" + uprs.getString("data"));
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

	/**
	* @param ParkingLotName Parking lot name
	* @return JSONArray Data about parking lot with given name
	*/
	public JSONObject getParkingLotJsonData(String parkingLotName){
		try {
			PreparedStatement select = conn.prepareStatement("SELECT data FROM ParkingFacility WHERE name=?");
			select.setString(1, parkingLotName);

			ResultSet uprs = select.executeQuery();
			
			if(uprs.next()){
				//System.out.println("getParkingLotJsonData resp:" + uprs.getString("data"));
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
	
	/**
	* @param name Parking lot's name
	* @param data Parking lot's data
	* @param width Width of parking lot
	*/
	public void addParkingLot(String name, String data, int width){
		PreparedStatement update;
		try {
			update = conn.prepareStatement("INSERT INTO ParkingLots(name, data, width) VALUES(?,?,?)");
			update.setString(1, name);
			update.setString(2, data);
			update.setInt(3, width);

			update.executeUpdate();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	* Updates data of given parking lot.
	* @param name Parking lot's name
	* @param data Parking lot's data
	*/
	public void updateParkingLotDataOld(String name, String data){
		System.out.println("updateParkingLotData with:" + data);
		PreparedStatement update;
		try {
			update = conn.prepareStatement("UPDATE ParkingLots SET data=? WHERE name=?");
			update.setString(1, data);
			update.setString(2, name);

			update.executeUpdate();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	* Updates data of given parking lot.
	* @param name Parking lot's name
	* @param data Parking lot's data
	*/
	public void updateParkingLotData(String name, String data){
		System.out.println("updateParkingLotData with:" + data);
		PreparedStatement update;
		try {
			update = conn.prepareStatement("UPDATE ParkingFacility SET data=? WHERE name=?");
			update.setString(1, data);
			update.setString(2, name);

			update.executeUpdate();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	* @param userID UserID
	* @return index of user with userID
	*/
	public int getIndexIDOfUser(String userID) {
		try {
			PreparedStatement select = conn.prepareStatement("SELECT ID FROM Users WHERE userID=?");
			select.setString(1, userID);
	
			ResultSet uprs = select.executeQuery();
			
			if(uprs.next()){
				return uprs.getInt("id");
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

		
	/**
	* Updates user type
	* @param userID UserID
	* @param typeStr Type to update to
	*/
	public void updateUserType(String userID, String typeStr) {
		PreparedStatement update;
		try {
			update = conn.prepareStatement("UPDATE Users SET type=? WHERE userID=?");
			update.setString(1, typeStr);
			update.setString(2, userID);

			update.executeUpdate();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	* @param userID UserID
	* @return VehicleID of vehicle of user with UserID
	*/
	public String getUserVehicleID(String userID) {
		try {
			PreparedStatement select = conn.prepareStatement("SELECT vehicleID FROM Users WHERE userID=?");
			select.setString(1, userID);
	
			ResultSet uprs = select.executeQuery();
			
			//TODO: support multiple vehicles
			if(uprs.next()){
				return uprs.getString("vehicleID");
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return "";
		
	}

	/**
	* @param vehicleID VehicleID
	* @return UserID of user owning vehicle with vehicleID
	*/
	public String getUserIdFromVehicleID(String vehicleID) {
		try {
			PreparedStatement select = conn.prepareStatement("SELECT userID FROM Vehicles WHERE vehicleID=?");
			select.setString(1, vehicleID);
	
			ResultSet uprs = select.executeQuery();
			
			if(uprs.next()){
				return uprs.getString("userID");
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	* @param userID UserID
	* @return User subscription type
	*/
	public String getUserSubscriptionTypeStr(String userID) {
		try {
			PreparedStatement select = conn.prepareStatement("SELECT type FROM Users WHERE userID=?");
			select.setString(1, userID);
	
			ResultSet uprs = select.executeQuery();
			
			if(uprs.next()){
				return uprs.getString("type");
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	* Updates vehicle start and leave time
	* @param vehicleID VehicleID
	* @param startTimeUnix StartTimeUnix
	* @param leaveTimeUnix LeaveTimeUnix
	*/
	public void updateVehicleStartLeaveTimes(String vehicleID, String startTimeUnix, String leaveTimeUnix) {
		PreparedStatement update;
		try {
			update = conn.prepareStatement("UPDATE Vehicles SET startTime=?, endTime=? WHERE vehicleID=?");
			update.setString(1, startTimeUnix);
			update.setString(2, leaveTimeUnix);
			update.setString(3, vehicleID);

			update.executeUpdate();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	* Updates status of isParking (doest the vehicle parks or not)
	* @param vehicleID VehicleID
	* @param val Value to update to 
	*/
	public void updateVehicleIsParking(String vehicleID, String val) {
		PreparedStatement update;
		try {
			update = conn.prepareStatement("UPDATE Vehicles SET isInParking=? WHERE vehicleID=?");
			update.setString(1, val);
			update.setString(2, vehicleID);

			update.executeUpdate();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	* Adds Money amount to user's account
	* @param vehicleID VehicleID
	* @param amount Amount of money to add to user's account
	*/
	public void addToUserMoney(String userID, double amount) {
		PreparedStatement update;
		try {
			update = conn.prepareStatement("UPDATE Users SET money=money+? WHERE userID=?");
			update.setDouble(1, amount);
			update.setString(2, userID);
			update.executeUpdate();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	* @param parkingLot Parking lot name
	* @return List<Integer> prices in given parking lot
	*/
	public List<Integer> getPrices(String parkingLot) {
		List<Integer> prices = new ArrayList<Integer>();
		try {
			PreparedStatement select = conn.prepareStatement("SELECT price FROM ParkingFacility WHERE name=?");
			select.setString(1, parkingLot);
	
			ResultSet uprs = select.executeQuery();
			
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

	
	/**
	* @param vehicleID VehicleID
	* @return long Parking start time of given vehicle
	*/
	public long getVehicleStartParkTime(String vehicleID) {
		try {
			PreparedStatement select = conn.prepareStatement("SELECT startTime FROM Vehicles WHERE vehicleID=?");
			select.setString(1, vehicleID);
	
			ResultSet uprs = select.executeQuery();
			
			if(uprs.next()){
				String timeStr = uprs.getString("startTime");
				return Long.valueOf(timeStr);
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return 0l;
	}

	/**
	* @param vehicleID VehicleID
	* @return String UserID corresponding to vehicleID
	*/
	public String getUserIDByVehicleID(String vehicleID) {
		try {
			PreparedStatement select = conn.prepareStatement("SELECT userID FROM Vehicles WHERE vehicleID=?");
			select.setString(1, vehicleID);
	
			ResultSet uprs = select.executeQuery();
			
			if(uprs.next()){
				return uprs.getString("userID");
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	* @param vehicleID VehicleID
	* @return String Parking lot where the vehicle with vehicleID parks at
	*/
	public String getVehicleParkingLot(String vehicleID) {
		try {
			PreparedStatement select = conn.prepareStatement("SELECT parkingLot FROM Vehicles WHERE vehicleID=?");
			select.setString(1, vehicleID);
	
			ResultSet uprs = select.executeQuery();
			
			if(uprs.next()){
				return uprs.getString("parkingLot");
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	* Removes user with userID from DB
	* @param userID UserID
	*/
	public void removeUser(String userID) {
		PreparedStatement update;
		try {
			update = conn.prepareStatement("DELETE FROM Users WHERE userID=?");
			update.setString(1, userID);
			update.executeUpdate();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	* Removes vehicle with vehicleID from DB
	* @param vehicleID VehicleID
	*/
	public void removeVehicle(String vehicleID) {
		PreparedStatement update;
		try {
			update = conn.prepareStatement("DELETE FROM Vehicles WHERE vehicleID=?");
			update.setString(1, vehicleID);
			update.executeUpdate();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	* @return List<User> list of all users in DB
	* @see User
	*/
	public List<User> getAllUsers() {
		ArrayList<User> users = new ArrayList<User>();
		try {
			PreparedStatement select = conn.prepareStatement("SELECT * FROM Users WHERE 1");
	
			ResultSet uprs = select.executeQuery();
			
			while(uprs.next()){
				users.add(new User(uprs.getString("userID"), uprs.getString("vehicleID"), uprs.getString("email"), uprs.getString("type"), uprs.getDouble("money")));
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return users;
	}

	/**
	* @return List<String> list of all parking lots names in DB
	*/
	public List<String> getAllParkingLotNames() {
		List<String> parkingLots = new ArrayList<String>();
		try {
			PreparedStatement select = conn.prepareStatement("SELECT name FROM ParkingFacility WHERE 1");
	
			ResultSet uprs = select.executeQuery();
			
			while(uprs.next()){
				parkingLots.add(uprs.getString("name"));
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return parkingLots;
	}

	/**
	* @param vehicleID VehicleID 
	* @return boolean Indicating weather vehicle is in parking or not
	*/
	public boolean getIsVehicleInParking(String vehicleID) {
		try {
			PreparedStatement select = conn.prepareStatement("SELECT isInParking FROM Vehicles WHERE vehicleID=?");
			select.setString(1, vehicleID);
	
			ResultSet uprs = select.executeQuery();
			
			if(uprs.next()){
				return uprs.getString("isInParking").equals("true");
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	
	/**
	* @param parkingLotName Parking lot name 
	* @return int This is the parking lot ID corresponding to the given parking lot name
	*/
	public int getParkingLotIDByName(String parkingLotName) {
		try {
			PreparedStatement select = conn.prepareStatement("SELECT id FROM ParkingFacility WHERE name=?");
			select.setString(1, parkingLotName);
	
			ResultSet uprs = select.executeQuery();
			
			if(uprs.next()){
				return uprs.getInt("id");
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	* @param parkingLotID Parking lot ID 
	* @return String This is the parking lot name corresponding to the given parking lot ID
	*/
	public String getParkingLotNameByID(int parkingLotID) {
		try {
			PreparedStatement select = conn.prepareStatement("SELECT name FROM ParkingFacility WHERE id=?");
			select.setInt(1, parkingLotID);
	
			ResultSet uprs = select.executeQuery();
			
			if(uprs.next()){
				return uprs.getString("name");
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	* Initiates stats in given parking lot
	* @param parkingLotID Parking lot ID 
	* @param todayUnixTime time of today in unix time format
	*/
	public void initStatsIfDoesntExists(int parkingLotID, long todayUnixTime) {
		try {
			PreparedStatement select = conn.prepareStatement("SELECT id FROM dailyStats WHERE date=? AND facID=?");
			select.setString(1, String.valueOf(todayUnixTime));
			select.setString(2, String.valueOf(parkingLotID));
			ResultSet uprs = select.executeQuery();
			
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
	
	/**
	 * 
	 * @param data 
	 * @see Params
	 * @return String Structure of return: isFull, status, alternative.
	 * If parking lot written in data param is not full --> returns isFull = 0, status = OK, alternative = -1
	 * If parking lot written in data param is full --> returns isFull = 1, status = OK,
	 * alternative = some other random parking facility which is not full
	 */
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
                    resData.addParam("alternative",getRandomParking(data.getParam("facName")) );
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
	
	/**
	 * 
	 * @param excludeName Name of parking lot to exclude from random choice
	 * @return String Name of parking lot which is not full (picked randomly from all parking facilities which are not full)
	 */
	  public String getRandomParking(String excludeName) {
	       
	        ArrayList<String> allParkingNames = new ArrayList<String>();
	        //check if full
	        PreparedStatement reqData;
	        try {
	            reqData = conn.prepareStatement("SELECT name FROM ParkingFacility WHERE isFull=0");
	            ResultSet results = reqData.executeQuery();
	            //get result
	            while (results.next()) {
	                //if current
	                if (excludeName == results.getString("name"))
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
	
	/**
	* Adds daily stats to DB, filled by given params
	* @param parkingLotID Parking lot ID
	* @param todayUnixTime Time of today in unix time format
	* @param lateDelta Late delta
	* @param cancelDelta Cancel delta
	* @param arrivedDelta arrived delta
	* @param numDisabledDelta number disabled delta
	*/

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
			

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	* @param vehicleID VehicleID
	* @param parkingLot Parking lot to search in (should be where vehicle is parked)
	* @return int[] Vehicle's parking spot (of vehicle with given vehicleID)
	*/
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

	/**
	* @param userID UserID of user
	* @return List<String> List of all vehicles of user with given userID
	*/
	public List<String> getAllVehiclesOfUser(String userID) {
		List<String> vehicleIDs = new ArrayList<String>();
		try {
			PreparedStatement select = conn.prepareStatement("SELECT vehicleID FROM Vehicles WHERE userID=?");
			select.setString(1, userID);
	
			ResultSet uprs = select.executeQuery();
			
			//TODO: support multiple vehicles
			while(uprs.next()){
				vehicleIDs.add(uprs.getString("vehicleID"));
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return vehicleIDs;
	}

}
