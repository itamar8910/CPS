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

public class ItamarDB {

	private static ItamarDB instance;

	private Connection conn;

	public ItamarDB(Connection conn){
		this.conn = conn;
	}

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
			

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

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
	 * @param data: facName,
	 * @return
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
