import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mysql.jdbc.DatabaseMetaData;

public class TestDB {

	private static TestDB instance;

	private Connection conn;

	public TestDB(Connection conn){
		this.conn = conn;
	}

	public static TestDB getInstance(){ // implements the singleton design pattern
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

	            instance = new TestDB(conn);

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

	public int getParkingLotWidth(String parkingLotName){
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

	public JSONArray getParkingLotJsonData(String parkingLotName){
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

	public void updateParkingLotData(String name, String data){
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

}
