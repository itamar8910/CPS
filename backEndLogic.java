import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pojos.Car;
import pojos.User;
import algorithm.Algorithm;

import common.Params;
import common.Utils;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;


public class backEndLogic {

	public static Params isParkingLotFull(Params params) {
		String parkingName = params.getParam("name");
		String fullRespStr = DBHandler.getInstance().canBeInParking(Params.getEmptyInstance().addParam("facName", parkingName));
		Params fullResp = new Params(fullRespStr);

		return Params.getEmptyInstance().addParam("isFull", fullResp.getParam("isFull").equals("1") ? "yes" : "false").addParam("alternative", fullResp.getParam("alternative"));
	}


	public static Params getVehicleStatus(Params params) {
		String vehicleID = params.getParam("vehicleID");
		boolean isParked = DBHandler.getInstance().getIsVehicleInParking(vehicleID);
		if(!isParked) {
			return Params.getEmptyInstance().addParam("status", "OK").addParam("isParked", "false");
		}
		Params resp = Params.getEmptyInstance().addParam("isParked", "true");
		String parkingLot = DBHandler.getInstance().getVehicleParkingLot(vehicleID);
		resp.addParam("parkingLot", parkingLot);
		resp.addParam("startTime", Utils.unixTimeToHour(DBHandler.getInstance().getVehicleStartParkTime(vehicleID)));
		int[] spot = DBHandler.getInstance().getVehicleParkingSpot(vehicleID, parkingLot);
		resp.addParam("parkingSpot", Arrays.toString(spot));
		resp.addParam("status", "OK");
		return resp;
	}


	public static Params getVehiclesOfUser(Params params) {
		String userID = params.getParam("userID");
		List<String> vehiclesIDs = DBHandler.getInstance().getAllVehiclesOfUser(userID);
		JSONArray vehiclesJsonArr = new JSONArray();
		for(String vID : vehiclesIDs) {
			vehiclesJsonArr.put(vID);
		}
		return Params.getEmptyInstance().addParam("status", "OK").addParam("vehiclesArr", vehiclesJsonArr.toString());
	}


	  //Instance methods ************************************************

	//add user 20% of this price
	//TODO
	//THIS WILL BE CALLED WHEN THE CLIENT IS LATE AND WANTS TO USE PARKING
	//IT WILL SUE THE USER WITH 20% OF THE PARKING PRICE
	public void handleClientWantsToKeepParking(int userID,double parkingPrice)  {
		//call to update DB
		DBHandler.getInstance().sueUser(userID, parkingPrice);;
	}

	//send email to costumer
	  public static void sendEmailToCostumerForBeginLate(String email, String string) {

		  try {


			  String to = email;
		      String from = "cps.system.14@gmail.com";


		      // Get system properties
		      Properties properties = System.getProperties();
		      properties.put("mail.smtp.starttls.enable", true); // added this line
		      properties.put("mail.smtp.host", "smtp.gmail.com");
		      properties.put("mail.smtp.user", "cps.system.14");
		      properties.put("mail.smtp.password", "cps123456789");
		      properties.put("mail.smtp.port", "587");
		      properties.put("mail.smtp.auth", true);

		      // Get the default Session object.
		      Session session = Session.getDefaultInstance(properties,
		    		    new javax.mail.Authenticator(){
		    		        protected PasswordAuthentication getPasswordAuthentication() {
		    		            return new PasswordAuthentication(
		    		            		from, "cps123456789");// Specify the Username and the PassWord
		    		        }
		    		});

		      try {
		         // Create a default MimeMessage object.
		         MimeMessage message = new MimeMessage(session);
		         message.setFrom(new InternetAddress(from));
		         message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

		         // Set Subject: header field
		         message.setSubject("Car parking system alert");

		         // Now set the actual message
		         message.setText(string);

		         // Send message
		         Transport.send(message);
		         System.out.println("Sent message successfully....");
		      } catch (MessagingException mex) {
		         mex.printStackTrace();
		      }
		  } catch(Exception e) {
			  e.printStackTrace();
		  }

	}


	public JSONArray generateEmptyParkingLotDataJson(int rows, int height, int cols){

		  JSONArray slots = new JSONArray();
		  for(int r = 0; r < rows; r++){
			  for(int h = 0; h < height; h++){
				  for(int c = 0; c < cols; c++){
					  JSONObject slotJson = new JSONObject();
					  try {
						slotJson.put("row", r);
						slotJson.put("height", h);
						slotJson.put("col", c);
						slotJson.put("status", "f");
						slotJson.put("enterTime", 0);
						slotJson.put("leaveTime", 0);
						slotJson.put("Vid", -1);
						slots.put(slotJson);
						//slots.add(slotJson);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				  }
			  }
		  }

		  return slots;
	  }



	//  public static void callParkingAlgo(String op, String vehicleID, String parkingLot, long startTime, long leaveTime) {
	//	  try{
	//	  final int width = DBHandler.getInstance().getParkingLotWidth(parkingLot);
	//	  JSONArray data = DBHandler.getInstance().getParkingLotJsonData(parkingLot);
	//	  String dataStr = "";
	//
	//	  for(int i = 0; i < data.length(); i++){
	//		  JSONObject carJson = data.getJSONObject(i);
	//		  dataStr += carJson.getString("row") + ", " + carJson.getString("height") + ", " + carJson.getString("col") + ", " + carJson.getString("status") + ", "
	//				  + carJson.getString("enterTime") + ", " + carJson.getString("leaveTime") + ", " + carJson.getString("Vid") + "\n";
	//	  }
	//
	//	  String opStr = "";
	//	  if(op.equals("Reserve")){
	//		  opStr = "Reserve " + startTime + ", " + leaveTime + ", " + vehicleID;
	//	  } else if(op.equals("Enter")){
	//		  opStr = "Enter " + startTime + ", " + leaveTime + ", " + vehicleID;
	//	  }else if(op.equals("Leave")){
	//		  opStr = "Leave " + vehicleID;
	//	  }
	//
	//	  String newData = ParkingAlgo.doParking(dataStr, width, opStr);
	//	  JSONArray newDataJson = new JSONArray();
	//
	//	  for(String line : newData.split("\n")){
	//		  String[] lineData = line.split(",");
	//		  JSONObject slotJson = new JSONObject();
	//		  try {
	//			slotJson.put("row", lineData[0]);
	//			slotJson.put("height", lineData[1]);
	//			slotJson.put("col", lineData[2]);
	//			slotJson.put("status", lineData[3]);
	//			slotJson.put("enterTime", lineData[4]);
	//			slotJson.put("leaveTime", lineData[5]);
	//			slotJson.put("Vid", lineData[6]);
	//			newDataJson.put(slotJson);
	//			//slots.add(slotJson);
	//		} catch (JSONException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		}
	//	  }
	//	  System.out.println("calling updateParkingLotData");
	//	  DBHandler.getInstance().updateParkingLotData(parkingLot, newDataJson.toString());
	//	  System.out.println("called updateParkingLotData");
	//
	//	  }catch(Exception e){
	//		  e.printStackTrace();
	//	  }
	//  }

		final static boolean CALL_ALGO = true;

	  public static void callParkingAlgoEnter(String parkingLot, String vehicleID, long leaveTime) {
		  if(!CALL_ALGO) {
			  return;
		  }
		  final int width = DBHandler.getInstance().getParkingLotWidth(parkingLot);
		  JSONObject data = DBHandler.getInstance().getParkingLotJsonData(parkingLot);
		  JSONArray start;
		try {
			start = data.getJSONArray("parkingData");
			System.out.println("Calling ParkingAlgo with data:");
			System.out.println(data.getJSONArray("parkingData").toString());
			System.out.println(data.getJSONArray("statusData").toString());
			Algorithm alg = new Algorithm(width, data.getJSONArray("parkingData").toString(), data.getJSONArray("statusData").toString());
			//TODO: integrate insertion with status (in this case 'order')
			System.out.println("inserting car:" +vehicleID + ","  + System.currentTimeMillis() + "," + leaveTime);
			alg.insertCar(new Car(vehicleID, System.currentTimeMillis(), leaveTime));
			//TODO: handle parking lot is full
			JSONObject result = new JSONObject();
			result.put("parkingData", new JSONArray(alg.generateDBString()));
			result.put("statusData", new JSONArray(alg.generateStatusString()));
			DBHandler.getInstance().updateParkingLotData(parkingLot, result.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	  }

	 public static void callParkingAlgoOrder(String parkingLot, String vehicleID, long entryTime, long leaveTime) {
		 if(!CALL_ALGO) {
			  return;
		  }
		 final int width = DBHandler.getInstance().getParkingLotWidth(parkingLot);
		  JSONObject data = DBHandler.getInstance().getParkingLotJsonData(parkingLot);
		  JSONArray start;
		try {
			start = data.getJSONArray("parkingData");
			Algorithm alg = new Algorithm(width, data.getJSONArray("parkingData").toString(), data.getJSONArray("statusData").toString());
			//TODO: integrate insertion with status (in this case 'order')
			alg.insertOrderedCar(new Car(vehicleID, entryTime, leaveTime));
			//TODO: handle parking lot is full
			JSONObject result = new JSONObject();
			result.put("parkingData", new JSONArray(alg.generateDBString()));
			result.put("statusData", new JSONArray(alg.generateStatusString()));
			DBHandler.getInstance().updateParkingLotData(parkingLot, result.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }

	 public static void callParkingAlgoLeave(String parkingLot, String vehicleID) {
		 if(!CALL_ALGO) {
			  return;
		  }
		 final int width = DBHandler.getInstance().getParkingLotWidth(parkingLot);
		  JSONObject data = DBHandler.getInstance().getParkingLotJsonData(parkingLot);
		  JSONArray start;
		try {
			start = data.getJSONArray("parkingData");
			Algorithm alg = new Algorithm(width, data.getJSONArray("parkingData").toString(), data.getJSONArray("statusData").toString());
			//TODO: integrate insertion with status (in this case 'order')
			alg.ejectCar(new Car(vehicleID, System.currentTimeMillis(), System.currentTimeMillis()));
			//alg.insertOrderedCar(new Car(vehicleID, leaveTime, entryTime), entryTime, leaveTime);
			//TODO: handle parking lot is full
			JSONObject result = new JSONObject();
			result.put("parkingData", new JSONArray(alg.generateDBString()));
			result.put("statusData", new JSONArray(alg.generateStatusString()));
			DBHandler.getInstance().updateParkingLotData(parkingLot, result.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
	
	 public static void createParkingLot(String name, int width, String location) {
		 DBHandler.getInstance().addParkingLot(name, width, location);
		 initParkingLotData(name);
	 }
	 
	 public static void deleteParkingLot(String name) {
		 DBHandler.getInstance().removeParkingLot(name);
	 }
	 
	 public static void initParkingLotData(String parkingLotName) {
		  final int width = DBHandler.getInstance().getParkingLotWidth(parkingLotName);
		  //JSONObject data = DBHandler.getInstance().getParkingLotJsonData(parkingLotName);
		  //JSONArray start;
		try {


			Algorithm alg = new Algorithm(width);
			JSONObject result = new JSONObject();
			result.put("parkingData", new JSONArray(alg.generateDBString()));
			result.put("statusData", new JSONArray(alg.generateStatusString()));
			DBHandler.getInstance().updateParkingLotData(parkingLotName, result.toString());
			//DBHandler.getInstance().updateParkingLotData(parkingLotName, result.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	}
	 }

	 public static class ThreeIndices implements Comparable{
		 int i,j,k;
		 String data;
		 public ThreeIndices(int ii, int jj, int kk, String data) {
			 i = ii;
			 j = jj;
			 k = kk;
			 this.data = data;
		 }
		@Override
		public int compareTo(Object o) {
			if((o instanceof ThreeIndices)) {
				ThreeIndices other = (ThreeIndices)o;
				if(this.i != other.i) {
					return this.i - other.i;
				}else if(this.j != other.j){
					return this.j - other.j;
				}else if(this.k != other.k) {
					return this.k - other.k;
				}else {
					return 0;
				}
			}
			return 0;
		}

	 }

	 public static String generateParkinglotDataForPDF(String parkingLotName) {
		 try {
			 JSONObject data = DBHandler.getInstance().getParkingLotJsonData(parkingLotName);
			 JSONArray statusesJsonArr = data.getJSONArray("statusData");
			 List<ThreeIndices> statuses = new ArrayList<ThreeIndices>();
			 for(int i = 0; i < statusesJsonArr.length(); i++) {
				 JSONObject spotData = statusesJsonArr.getJSONObject(i);
				 statuses.add(new ThreeIndices(spotData.getInt("i"),spotData.getInt("j"),spotData.getInt("k"),String.valueOf(spotData.getString("status"))));
			 }
			 Collections.sort(statuses);
			 String dataForPDF = "";
			 int index = 0;
			 for(ThreeIndices status : statuses) {
				 dataForPDF += String.valueOf(index++) + status.data;
			 }
			 return dataForPDF;

		 }catch(JSONException e) {
			 e.printStackTrace();
		 }
		 return "";

	 }

	 public static void addToStatistics(String parkingLotName, int lateDelta, int cancelDelta, int arrivedDelta, int numDisabledDelta) {
		 int parkingLotID = DBHandler.getInstance().getParkingLotIDByName(parkingLotName);
		 Calendar current = Calendar.getInstance();
		 current.set(Calendar.HOUR_OF_DAY, 0);
		 current.set(Calendar.MINUTE, 0);
		 current.set(Calendar.SECOND, 0);
		 current.set(Calendar.MILLISECOND, 0);
		 long todayUnixTime = current.getTimeInMillis();
		 DBHandler.getInstance().initStatsIfDoesntExists(parkingLotID, todayUnixTime);
		 DBHandler.getInstance().addToDailyStats(parkingLotID, todayUnixTime, lateDelta, cancelDelta, arrivedDelta, numDisabledDelta);

	 }

	  public static Params handleClientPhysicalOrder(Params params) {
		  boolean isInTable = DBHandler.getInstance().isInTable("Users", "userID", params.getParam("ID"));
		  if(isInTable){
			  String vehicleID = DBHandler.getInstance().getUserVehicleID(params.getParam("ID"));
			  if(vehicleID.equals(params.getParam("vehicleID"))) { // if is in db with same vehicleID
				  Params resp = Params.getEmptyInstance();
				  resp.addParam("status", "BAD");
				  return resp;
			  }
			  
		  }
		  //check leave time
		  long leaveTimeMillis = Utils.todayTimeToMillis(params.getParam("leaveTime"));
		  if(leaveTimeMillis == -1l) {
			  Params resp = Params.getEmptyInstance();
			  resp.addParam("status", "BAD");
			  resp.addParam("message", "invalid leaveing time");
			  return resp;
		  }
		  Params typeParams = Params.getEmptyInstance();
		  typeParams.addParam("type", "physicalOrder");
		  typeParams.addParam("parkingLot", params.getParam("parkingLot"));
		  typeParams.addParam("startTimeMS", String.valueOf(System.currentTimeMillis()));
		  typeParams.addParam("leaveTimeMS", String.valueOf(Utils.todayTimeToMillis(params.getParam("leaveTime"))));
		  final String type = typeParams.toString();

		  DBHandler.getInstance().addUser(params.getParam("ID"), params.getParam("vehicleID"), params.getParam("email"), type);

		  handleAddVehicleToDB(params.getParam("ID"), params.getParam("parkingLot"), params.getParam("vehicleID"));//DBHandler.getInstance().addVehicle(params.getParam("ID"), params.getParam("vehicleID"), params.getParam("parkingLot"), startTime, leaveTime);
		  //TODO: handle if parking lot is full
		  //callParkingAlgoEnter(params.getParam("parkingLot"), params.getParam("vehicleID"), Utils.todayTimeToMillis(params.getParam("leaveTime")));

		  // automatically calls handleClientEnter for physical order
		  Params enterInpParams = Params.getEmptyInstance();
		  enterInpParams.addParam("action", "clientEnter");
		  enterInpParams.addParam("vehicleID", params.getParam("vehicleID"));
		  enterInpParams.addParam("parkingLot", params.getParam("parkingLot"));
		  Params enterRespParams = handleClientEnter(enterInpParams);
		  if(enterRespParams.getParam("status").equals("BAD")) {
			  return enterRespParams;
		  }else {
			  Params resp = Params.getEmptyInstance();
			  resp.addParam("status", "OK");
			  return resp;
		  }
	  }

	  public static double calcPriceUpfrontForOneTimeOrder(String parkingLot, Params subscriptionParams) {
			System.out.println("calcPriceUpfrontForOneTimeOrder");
			List<Integer> prices = DBHandler.getInstance().getPrices(parkingLot);
			long startTime = Long.valueOf(subscriptionParams.getParam("enterTimeMS"));
			long leaveTime = Long.valueOf(subscriptionParams.getParam("leaveTimeMS"));
			long diff = leaveTime - startTime;
			System.out.println("diff:" + diff);
			double numHours = (diff / 1000.0 / 60.0 / 60.0);
			return numHours * prices.get(1);
	  }

	  public static Params handleClientOneTimeOrder(Params params){
		  boolean isInTable = DBHandler.getInstance().isInTable("Users", "userID", params.getParam("ID"));
//		  if(isInTable){
//			  Params resp = Params.getEmptyInstance();
//			  resp.addParam("status", "BAD");
//			  return resp;
//		  }
		  
		  if(Utils.dateAndTimeToMillis(params.getParam("enterDate"), params.getParam("enterTime")) == -1 || Utils.dateAndTimeToMillis(params.getParam("leaveDate"), params.getParam("leaveTime")) == -1) {
			  return Params.getEmptyInstance().addParam("status", "BAD").addParam("message", "time is invalid");
		  }
		 
		  

		  Params typeParams = Params.getEmptyInstance();
		  typeParams.addParam("type", "orderedOneTimeParking");
		  typeParams.addParam("parkingLot", params.getParam("parkingLot"));
	//	  typeParams.addParam("enterDate", params.getParam("enterDate"));
	//	  typeParams.addParam("enterTime", params.getParam("enterTime"));
		  typeParams.addParam("enterTimeMS", String.valueOf(Utils.dateAndTimeToMillis(params.getParam("enterDate"), params.getParam("enterTime"))));
	//	  typeParams.addParam("leaveDate", params.getParam("leaveDate"));
	//	  typeParams.addParam("leaveTime", params.getParam("leaveTime"));
		  typeParams.addParam("leaveTimeMS", String.valueOf(Utils.dateAndTimeToMillis(params.getParam("leaveDate"), params.getParam("leaveTime"))));

		  final String type = typeParams.toString();

		  DBHandler.getInstance().addUser(params.getParam("ID"), params.getParam("vehicleID"), params.getParam("email"), type);

	//	  final long startTime = Utils.dateAndTimeToMillis(params.getParam("enterDate"), params.getParam("enterTime"));
	//	  final long leaveTime =Utils.dateAndTimeToMillis(params.getParam("leaveDate"), params.getParam("leaveTime"));

		  handleAddVehicleToDB(params.getParam("ID"), params.getParam("parkingLot"), params.getParam("vehicleID"));//DBHandler.getInstance().addVehicle(params.getParam("ID"), params.getParam("vehicleID"), params.getParam("parkingLot"), startTime, leaveTime);
		  callParkingAlgoOrder(params.getParam("parkingLot"), params.getParam("vehicleID"), Utils.dateAndTimeToMillis(params.getParam("enterDate"), params.getParam("enterTime")),Utils.dateAndTimeToMillis(params.getParam("leaveDate"), params.getParam("leaveTime")) );
		  //callParkingAlgo("Ordered" , params.getParam("vehicleID"), params.getParam("parkingLot"), startTime, leaveTime);

		  double priceToPay = calcPriceUpfrontForOneTimeOrder(params.getParam("parkingLot"), typeParams);
		  addToUserMoney(params.getParam("ID"), -priceToPay);
		  Params resp = Params.getEmptyInstance();
		  resp.addParam("status", "OK");
		  resp.addParam("price", String.valueOf(priceToPay));
		  return resp;
	  }

	  public static Params handleRoutineSubscription(Params params) {

		  //TODO: support of routine subscriber that wants to enter another parking lot one time so orders in a different way (currently will return BAD b.c there is already a user with the same ID)
//		  boolean isInTable = DBHandler.getInstance().isInTable("Users", "userID", params.getParam("ID"));
//		  if(isInTable){
//			  Params resp = Params.getEmptyInstance();
//			  resp.addParam("status", "BAD");
//			  return resp;
//		  }
		  
		  if(Utils.todayTimeToMillis(params.getParam("enterTime")) == -1l || Utils.todayTimeToMillis(params.getParam("leaveTime")) == -1l) {
			  return Params.getEmptyInstance().addParam("status", "BAD").addParam("message", "invalid enter/exit time");
		  }

		  Params typeParams = Params.getEmptyInstance();
		  typeParams.addParam("type", "routineSubscription");
		  typeParams.addParam("subscriptionStartMS", String.valueOf(Utils.dateToMillis(params.getParam("startDate"))));
		  typeParams.addParam("enterTimeHHMM", params.getParam("enterTime"));
		  typeParams.addParam("leaveTimeHHMM", params.getParam("leaveTime"));
		  typeParams.addParam("parkingLot", params.getParam("parkingLot"));
		  final String type = typeParams.toString();

		  DBHandler.getInstance().addUser(params.getParam("ID"), params.getParam("vehicleID"), params.getParam("email"), type);

		  handleAddVehicleToDB(params.getParam("ID"), params.getParam("parkingLot"), params.getParam("vehicleID")); // adds vehicle to db if in the same day

		  handleCallParkingAlgoOrderForSubscription(params.getParam("ID"), params.getParam("vehicleID"), params.getParam("parkingLot"), typeParams);


		  int subscriptionID = DBHandler.getInstance().getIndexIDOfUser(params.getParam("ID"));
		  typeParams.addParam("subscriptionID", String.valueOf(subscriptionID));
		  DBHandler.getInstance().updateUserType(params.getParam("ID"), typeParams.toString());
		  Params resp = Params.getEmptyInstance();
		  resp.addParam("status", "OK");
		  resp.addParam("subscriptionID", String.valueOf(subscriptionID));
		  double price = calcSubscriptionPrice(params.getParam("parkingLot"), typeParams);
		  System.out.println("price:" + price);
		  addToUserMoney(params.getParam("ID"), -price);
		  resp.addParam("price", String.valueOf(price));

		  return resp;

	  }

	public static void handleCallParkingAlgoOrderForSubscription(String userID, String vehicleID, String parkingLot, Params subscriptionParams) {
		if(!(subscriptionParams.equals("routineSubscription") || subscriptionParams.equals("fullSubscription"))) {
			System.out.println("ERR: called handleCallParkingAlgoOrderForSubscription with subscriptionType that are not of a subscription");
			return;
		}
		long subscriptionStartTimeMS = Long.valueOf(subscriptionParams.getParam("subscriptionStartMS"));
		//if subscription is valid
		if(System.currentTimeMillis() > subscriptionStartTimeMS && Utils.isInLastMonth(subscriptionStartTimeMS)) {
			//call parking algo for ordering a parking spot
			String endTimeHour = (subscriptionParams.equals("routineSubscription") ? subscriptionParams.getParam("leaveTimeHHMM") : "23:59");
			String startTimeHour = (subscriptionParams.equals("routineSubscription") ? subscriptionParams.getParam("enterTimeHHMM") : "00:01");
			if(parkingLot.equals("**ANY**")) { // if is full subscription, need to order in all parking lots
				List<String> allParkingLots = DBHandler.getInstance().getAllParkingLotNames();
				for(String aParkingLot : allParkingLots) {
					callParkingAlgoOrder(aParkingLot, vehicleID, Utils.timeToMillis(startTimeHour),Utils.timeToMillis(endTimeHour));
				}
			}else { // if is routine subscription order only for specific parking lot
				callParkingAlgoOrder(parkingLot, vehicleID, Utils.timeToMillis(startTimeHour), Utils.timeToMillis(endTimeHour));
			}
		}

	}


	public static double calcSubscriptionPrice(String parkingLot, Params subscriptionParams) {
		List<Integer> prices = DBHandler.getInstance().getPrices(parkingLot);

		if(subscriptionParams.getParam("type").equals("routineSubscription")) {
	//		long enterTimeUnix = Long.valueOf(subscriptionParams.getParam("enterTimeHHMM"));
	//		long leaveTimeUnix = Long.valueOf(subscriptionParams.getParam("enterTimeHHMM"));
	//		long diff = leaveTimeUnix - enterTimeUnix;
	//		//TODO: update users money value in DB
	//		//TODO: support multiple vehicles
	//		double numHours = (int)(diff / 1000.0 / 60.0 / 60.0);
	//		return (int)(numHours * prices.get(2) * prices.get(1));
			//TODO: update users money value in DB
			//TODO: support multiple vehicles
			System.out.println("calcing routine sub price");
			System.out.println(prices.get(2) + "," + prices.get(1));
			return (prices.get(2) * prices.get(1));
		}else if(subscriptionParams.getParam("type").equals("fullSubscription")) {
			prices = DBHandler.getInstance().getPrices("Default");

			return (prices.get(4) * prices.get(1));
		}
		return 0;
	}


	public static Params handleFullSubscription(Params params) {

		  //TODO: support of routine subscriber that wants to enter another parking lot one time so orders in a different way (currently will return BAD b.c there is already a user with the same ID)
//		  boolean isInTable = DBHandler.getInstance().isInTable("Users", "userID", params.getParam("ID"));
//		  if(isInTable){
//			  Params resp = Params.getEmptyInstance();
//			  resp.addParam("status", "BAD");
//			  return resp;
//		  }
		
		if(Utils.dateToMillis(params.getParam("startDate")) == -1l){
			return Params.getEmptyInstance().addParam("status", "BAD").addParam("message", "Invalid start date");
		}
		  
		  //TODO: handle max park time is 14 days
		  //TODO: can't park more than subscription unless the subscription is renewed
		  //TODO: system reminds user a week before subscription is over

		  Params typeParams = Params.getEmptyInstance();
		  typeParams.addParam("type", "fullSubscription");
		  typeParams.addParam("subscriptionStartMS", String.valueOf(Utils.dateToMillis(params.getParam("startDate"))));
		  final String type = typeParams.toString();

		  DBHandler.getInstance().addUser(params.getParam("ID"), params.getParam("vehicleID"), params.getParam("email"), type);

		  handleAddVehicleToDB(params.getParam("ID"), "", params.getParam("vehicleID")); // adds vehicle to db if in the same day
		  handleCallParkingAlgoOrderForSubscription(params.getParam("ID"), params.getParam("vehicleID"), "**ANY**", typeParams);

		  int subscriptionID = DBHandler.getInstance().getIndexIDOfUser(params.getParam("ID"));
		  typeParams.addParam("subscriptionID", String.valueOf(subscriptionID));
		  DBHandler.getInstance().updateUserType(params.getParam("ID"), typeParams.toString());
		  Params resp = Params.getEmptyInstance();
		  resp.addParam("status", "OK");
		  resp.addParam("subscriptionID", String.valueOf(subscriptionID));
		  double price = calcSubscriptionPrice("", typeParams);
		  System.out.println("price:" + price);
		  addToUserMoney(params.getParam("ID"), -price);
		  resp.addParam("price", String.valueOf(price));
		  return resp;

	  }

	/**
	 *
	 * @param userID
	 * @param parkingLot
	 * @param command: Order or Enter
	 */
	public static void handleAddVehicleToDB(String userID, String parkingLot, String vehicleID) {
		//first inserts vehicles to Vehicles table
		//TODO: support multiple vehicles
		//String vehicleID = DBHandler.getInstance().getUserVehicleID(userID);
		DBHandler.getInstance().addVehicle(userID, vehicleID, parkingLot, 0, 0, false);

		//TODO: also calls parkingAlgo for all vehicles of today
		//TODO: call routinely as a cron job.

	}

	public static void addToUserMoney(String userID, double amount) {
		DBHandler.getInstance().addToUserMoney(userID, (amount));
	}

	public static List<Integer> getPrices(String parkingLot){
		return DBHandler.getInstance().getPrices(parkingLot);
	}

	public static String updateVehicleEntered(String userID, String vehicleID, String parkingLot, Params subscriptionTypeParams) {
		boolean isInTable = DBHandler.getInstance().isInTable("Vehicles", "vehicleID", vehicleID);
		if(!isInTable) {
			return "VehicleID is not in Vehicles table";
		}
		String currentUnixTime = String.valueOf(System.currentTimeMillis());
		String expectedLeaveTimeUnix = getExpectedLeaveTimeUnix(subscriptionTypeParams);
		DBHandler.getInstance().updateVehicleStartLeaveTimes(vehicleID, currentUnixTime, expectedLeaveTimeUnix);
		DBHandler.getInstance().updateVehicleIsParking(vehicleID, "true");
		//TODO: call car parking algo with 'Enter' command
		return "OK";
	}

	//public static void updateVehiclesForAllUsersDaily(String param) {
	//	//TODO: impl. Adds all vehicles to Vehicles Table based on users (handles orders, routine subscriptions, full subscription, etc.)
	//}




	public static String getExpectedLeaveTimeUnix(Params subscriptionParams) {
		String subType = subscriptionParams.getParam("type");
		if(subType.equals("physicalOrder")) {
			return subscriptionParams.getParam("leaveTimeMS");
		}else if(subType.equals("orderedOneTimeParking")) {
			return subscriptionParams.getParam("leaveTimeMS");
		}else if(subType.equals("routineSubscription")) {
			return String.valueOf(Utils.todayTimeToMillis(subscriptionParams.getParam("leaveTimeHHMM")));
		}else if(subType.equals("fullSubscription")) {
			return "0";
		}
		return "0";

	}


	public static Params handleClientEnter(Params params) {
		String vehicleID = params.getParam("vehicleID");
		String parkingLot = params.getParam("parkingLot");
		boolean isInTable = DBHandler.getInstance().isInTable("Vehicles", "vehicleID", vehicleID);
		if(!isInTable) {
			Params resp = Params.getEmptyInstance();
			resp.addParam("status", "BAD");
			return resp;
		}
		String userID = DBHandler.getInstance().getUserIdFromVehicleID(vehicleID);
		String subscriptionParamsStr = DBHandler.getInstance().getUserSubscriptionTypeStr(userID);
		Params subscriptionParams = new Params(subscriptionParamsStr);
		if(needsSubscriptionID(subscriptionParams.getParam("type")) && !params.hasParam("subscriptionID")) {
			return Params.getEmptyInstance().addParam("status", "OK").addParam("needsSubscriptionID", "Yes");
		}
		String subscriptionID = needsSubscriptionID(subscriptionParams.getParam("type")) ? params.getParam("subscriptionID") : "";
		String canEnter = canEnterParking(userID, parkingLot, subscriptionID, subscriptionParams);
		if(canEnter.equals("OK")) {
			//add car to vehicles table
			updateVehicleEntered(userID, vehicleID, parkingLot, subscriptionParams);
			long leaveTime = getVehicleExpectedLeaveTime(userID, vehicleID, subscriptionParams);
			callParkingAlgoEnter(parkingLot, vehicleID, leaveTime);
	//		int price = 0;
	//		if(subscriptionParams.getParam("tpye").equals("orderedOneTimeParking")) {
	//			price = calcPriceUpfrontForOneTimeOrder(subscriptionParams);
	//		}
			if(subscriptionParams.getParam("type").equals("orderedOneTimeParking")) {
				//update stats
				addToStatistics(parkingLot, 0, 0, 1, 0);
			}
			return Params.getEmptyInstance().addParam("status", "OK").addParam("needsSubscriptionID", "No");
		}else {
			return Params.getEmptyInstance().addParam("status", "BAD").addParam("message", canEnter);
		}
	}



	public static long getVehicleExpectedLeaveTime(String userID, String vehicleID, Params subscriptionParams) {
		if(subscriptionParams.getParam("type").equals("physicalOrder")) {
			return Long.valueOf(subscriptionParams.getParam("leaveTimeMS"));
		}else if(subscriptionParams.getParam("type").equals("orderedOneTimeParking")) { //TODO: impl. for other types
			return Long.valueOf(subscriptionParams.getParam("leaveTimeMS"));
		}else if(subscriptionParams.getParam("type").equals("routineSubscription")) {
			return Long.valueOf(subscriptionParams.getParam("leaveTimeHHMM"));
		}else if(subscriptionParams.getParam("type").equals("fullSubscription")) {
			return Utils.timeToMillis("23:59");
		}else {
			System.out.println("ERR: getVehicleExpectedLeaveTime with unexpected subscription type");
		}
		return 0l;
	}


	public static boolean needsSubscriptionID(String type) {
		return (type.equals("routineSubscription") || type.equals("fullSubscription"));
	}


	public static String canEnterParking(String userID, String parkingLot, String subscriptionID, Params subscriptionParams) {
		String subType = subscriptionParams.getParam("type");
		if(subType.equals("physicalOrder")) {
			//TODO: check if parkingLot is full
			return "OK";
		}else if(subType.equals("orderedOneTimeParking")) {
			//check if same parking lot
			if(!parkingLot.equals(subscriptionParams.getParam("parkingLot"))) {
				return "Not same parking lot";
			}
			//check if arrived in time
			//TODO: handle case where user is late
			//check if time matches
			if((Utils.isCurrentTimeAfter(subscriptionParams.getParam("enterTimeMS")) && !Utils.isCurrentTimeAfter(subscriptionParams.getParam("leaveTimeMS")))){
				return "Time doesn't match";
			}
			return "OK";
		}else if(subType.equals("routineSubscription") || subType.equals("fullSubscription")) {
			//check subscription id
			if(!subscriptionID.equals(subscriptionParams.getParam("subscriptionID"))) {
				return "Not same subscriptionID";
			}
			long subscriptionStartUnixtime = Long.valueOf(subscriptionParams.getParam("subscriptionStartMS"));
			System.out.println("subscription time ms:" + subscriptionStartUnixtime);
			if(!Utils.isInLastMonth(subscriptionStartUnixtime)) {
				//subscription expired
				return "Subscription expired";
			}
			if(subType.equals("routineSubscription")) {
				//check if same parking lot
				if(!parkingLot.equals(subscriptionParams.getParam("parkingLot"))) {
					return "Not same parking lot";
				}
				//check if not in weekend
				if(Utils.isCurrentlyWeekend()) {
					return "Cannot park with routine subscription in weekends!";
				}
				//check if time matches
				if(!Utils.isCurrentHourBetween(subscriptionParams.getParam("enterTimeHHMM"),subscriptionParams.getParam("leaveTimeHHMM"))) {
					return "Current hour not included in your subscription";
				}
				return "OK";
			}else { // full subscription
				return "OK";
			}
			}

		return "Invalid subscription type";

		}

	public static Params handleClientLeave(Params params) {
		String vehicleID = params.getParam("vehicleID");
		String parkingLot = params.getParam("parkingLot");
		boolean isInTable = DBHandler.getInstance().isInTable("Vehicles", "vehicleID", vehicleID);
		if(!isInTable) {
			Params resp = Params.getEmptyInstance();
			resp.addParam("status", "BAD").addParam("message", "Vehicle not in parking");
			return resp;
		}
		boolean isVehicleInParking = DBHandler.getInstance().getIsVehicleInParking(vehicleID);
		if(!isVehicleInParking) {
			return Params.getEmptyInstance().addParam("status", "BAD").addParam("message", "Vehicle is not currently parked");
		}
		DBHandler.getInstance().updateVehicleIsParking(vehicleID, "false");
		String userID = DBHandler.getInstance().getUserIdFromVehicleID(vehicleID);
		String subscriptionParamsStr = DBHandler.getInstance().getUserSubscriptionTypeStr(userID);
		Params subscriptionParams = new Params(subscriptionParamsStr);
		double priceToPay = calcPriceToPay(parkingLot, vehicleID, subscriptionParams);
		// remove from Users table if is physicalOrder or OneTimeOrder
		if(subscriptionParams.getParam("type").equals("physicalOrder") || subscriptionParams.getParam("type").equals("orderedOneTimeParking")){
			DBHandler.getInstance().removeUser(userID);
		}
		//from vehicle from vehicles table
		DBHandler.getInstance().removeVehicle(vehicleID);
		callParkingAlgoLeave(parkingLot, vehicleID);
		//returns response to user;
		return Params.getEmptyInstance().addParam("status", "OK").addParam("payAmount", String.valueOf(priceToPay));

	}


	public static double calcPriceToPay(String parkingLot, String vehicleID, Params subscriptionParams) {
		List<Integer> prices = DBHandler.getInstance().getPrices(parkingLot);
		if(subscriptionParams.getParam("type").equals("physicalOrder")) {
			System.out.println("inside pay for single order");
			long startParkTimeUnix = DBHandler.getInstance().getVehicleStartParkTime(vehicleID);
			long deltaTimeUnix = System.currentTimeMillis() - startParkTimeUnix;
			System.out.println("deltaTime is:" + deltaTimeUnix);
			double numHours = (deltaTimeUnix / 1000.0 / 60.0 / 60.0);
			System.out.println("num hours:" + numHours);
			return (numHours * prices.get(0));
		}else if(subscriptionParams.getParam("type").equals("orderedOneTimeParking")) {
			// handle if client stayed parking more than she ordered
			long plannedLeaveTimeUnix = Long.valueOf(subscriptionParams.getParam("leaveTimeMS"));
			long deltaTimeUnix = System.currentTimeMillis() - plannedLeaveTimeUnix;
			double numHours = (deltaTimeUnix / 1000.0 / 60.0 / 60.0);
			final double LATE_TO_EXIT_PRICE_FACTOR = 2;
			return (numHours * prices.get(1) * LATE_TO_EXIT_PRICE_FACTOR);
		}

		return 0;
	}


	public static Params handleClientCancelOrder(Params params) {
		String vehicleID = params.getParam("vehicleID");
		String userID = DBHandler.getInstance().getUserIDByVehicleID(vehicleID);
		String parkingLot = DBHandler.getInstance().getVehicleParkingLot(vehicleID);
		Params subscriptionParams = new Params(DBHandler.getInstance().getUserSubscriptionTypeStr(userID));
		if (!subscriptionParams.getParam("type").equals("orderedOneTimeParking")) {
			return Params.getEmptyInstance().addParam("status", "BAD").addParam("message", "Your Order/subscription type cannot be canceled");
		}
		addToStatistics(parkingLot, 0, 1, 0, 0);
		double priceToPay = -1;
		double hoursDiff = Utils.getHoursDiff(subscriptionParams.getParam("enterTimeMS"));
		System.out.println("hours diff is:" + hoursDiff);
		double originalPrice = calcPriceUpfrontForOneTimeOrder(parkingLot, subscriptionParams);
		addToUserMoney(userID, originalPrice); // first refund to user his original payment
		//calculate how much use has to pay
		if(hoursDiff > 3.0) {
			priceToPay = originalPrice * 0.1;
		}else if(hoursDiff > 1.0) {
			priceToPay = originalPrice * 0.5;
		}else {
			priceToPay = originalPrice;
		}

		addToUserMoney(userID, -priceToPay); // user has to pay the fine for canceling
		return Params.getEmptyInstance().addParam("status", "OK").addParam("returnAmount", String.valueOf(originalPrice - priceToPay));

	}



	//
	//public static Params handleGetParkingSlotStatus(Params params) {
	//	String parkingLotName = params.getParam("name");
	//	System.out.println("starting handleGetParkingSlotsStatus computation");
	//	JSONObject data = DBHandler.getInstance().getParkingLotJsonData(parkingLotName);
	//	Params resp = Params.getEmptyInstance();
	//	resp.addParam("status", "OK");
	//	try {
	//		resp.addParam("array", data.getJSONArray("statusData").toString());
	//	} catch (JSONException e) {
	//		// TODO Auto-generated catch block
	//		e.printStackTrace();
	//	}
	//	return resp;
	//}


	public static Params handleGetParkingSlotStatus(Params params) {
		String parkingLotName = params.getParam("name");
		JSONObject data = DBHandler.getInstance().getParkingLotJsonData(parkingLotName);
		Params resp = Params.getEmptyInstance();
		resp.addParam("status", "OK");
		try {

			//TODO: ***change here
			resp.addParam("array", data.getJSONArray("statusData").toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resp;
	}

	public static Params handleGNumOfSubscribers(Params params) {
		int parkingLotID = Integer.valueOf(params.getParam("facID"));
		String parkingLotName = DBHandler.getInstance().getParkingLotNameByID(parkingLotID);
		List<User> users = DBHandler.getInstance().getAllUsers();
		int count = 0;
		for(User user : users) {
			if(!DBHandler.getInstance().getVehicleParkingLot(user.getVehicleID()).equals(parkingLotName)) {
				continue;
			}
			if(user.getSubscriptionParams().getParam("type").equals("routineSubscription") || user.getSubscriptionParams().getParam("type").equals("fullSubscription")) {
				count++;
			}
		}
		return Params.getEmptyInstance().addParam("status", "OK").addParam("num", String.valueOf(count));
	}

	public static Params handleGNumOfSubscribersWithMoreThanOneCar(Params params) {
		int parkingLotID = Integer.valueOf(params.getParam("facID"));
		String parkingLotName = DBHandler.getInstance().getParkingLotNameByID(parkingLotID);
		List<User> users = DBHandler.getInstance().getAllUsers();
		Map<String, List<String>> userToVehicle = new HashMap<String,List<String>>();
		int count = 0;
		for(User user : users) {
			if(!DBHandler.getInstance().getVehicleParkingLot(user.getVehicleID()).equals(parkingLotName)) {
				continue;
			}
			if(user.getSubscriptionParams().getParam("type").equals("routineSubscription") || user.getSubscriptionParams().getParam("type").equals("fullSubscription")) {
				if(!userToVehicle.containsKey(user.getUserID())){
					userToVehicle.put(user.getUserID(), new ArrayList<String>());
				}
				if(userToVehicle.get(user.getUserID()).size() > 0){
					if(!userToVehicle.get(user.getUserID()).contains(user.getVehicleID())){
						count += 1;
					}
				}
				userToVehicle.get(user.getUserID()).add(user.getUserID());
			}
		}
		return Params.getEmptyInstance().addParam("status", "OK").addParam("num", String.valueOf(count));
	}

	public static Params getSubscriptionStats(Params params) {
		String parkingName = params.getParam("name");
		String facID = String.valueOf(DBHandler.getInstance().getParkingLotIDByName(parkingName));
		params.addParam("facID", facID);

		Params respNumSubs = handleGNumOfSubscribers(params);
		Params resNumsSubsMoreThanOneVehicle = handleGNumOfSubscribersWithMoreThanOneCar(params);
		DBHandler dbInstance = DBHandler.getInstance();

		System.out.println("Parking name : " + parkingName + " Data : " + params.toString());
		return Params.getEmptyInstance().addParam("name", parkingName).addParam("monthly", respNumSubs.getParam("num")).addParam("monthlyWithMoreCars", resNumsSubsMoreThanOneVehicle.getParam("num"));

	}

	public static Params toggleDisableSpot(Params params) {
		//status:true/false. floor, position, name
		try {
			//int facID = Integer.parseInt(params.getParam("faceID"));
			int floor = Integer.parseInt(params.getParam("floor"));
			int position = Integer.parseInt(params.getParam("position"));
			String parkingLotName = params.getParam("name");
			String value = params.getParam("status"); //true/false

			int numCols = DBHandler.getInstance().getParkingLotWidth(parkingLotName);
			JSONObject parkingData = DBHandler.getInstance().getParkingLotJsonData(parkingLotName);
			JSONArray statusData = new JSONArray(parkingData.getJSONArray("statusData").toString());
			int i = floor;
			int j = position / numCols;
			int k = position % numCols;
			JSONArray newStatusData = new JSONArray();
			for(int index = 0; index < statusData.length(); index++) {
				JSONObject spotStatus = statusData.getJSONObject(index);
				if(spotStatus.getInt("i") == i && spotStatus.getInt("j") == j && spotStatus.getInt("k") == k) {
					spotStatus.put("status", String.valueOf((value)).equals("true")  ? String.valueOf('i') : String.valueOf('e'));
				}
				newStatusData.put(spotStatus);
			}
			parkingData.put("statusData", newStatusData);
			DBHandler.getInstance().updateParkingLotData(parkingLotName, parkingData.toString());
			return Params.getEmptyInstance().addParam("status", "OK");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Params.getEmptyInstance().addParam("status", "BAD");
	}


	public static Params reserveSpot(Params params) {
		//"floor" , "position":1D, name

		try {
			//int facID = Integer.parseInt(params.getParam("faceID"));
			int floor = Integer.parseInt(params.getParam("floor"));
			int position = Integer.parseInt(params.getParam("position"));
			String parkingLotName = params.getParam("name");
			String val = params.getParam("status");
			int numCols = DBHandler.getInstance().getParkingLotWidth(parkingLotName);
			JSONObject parkingData = DBHandler.getInstance().getParkingLotJsonData(parkingLotName);
			JSONArray statusData = new JSONArray(parkingData.getJSONArray("statusData").toString());
			int i = floor;
			int j = position / numCols;
			int k = position % numCols;
			System.out.println("reserving spot:"+i+","+j+","+k);
			JSONArray newStatusData = new JSONArray();
			for(int index = 0; index < statusData.length(); index++) {
				JSONObject spotStatus = statusData.getJSONObject(index);
				if(spotStatus.getInt("i") == i && spotStatus.getInt("j") == j && spotStatus.getInt("k") == k) {
					spotStatus.put("status", String.valueOf((val.equals("true")? 's' : 'e')));
				}
				newStatusData.put(spotStatus);
			}
			System.out.println("new status data:" + newStatusData.toString());
			parkingData.put("statusData", newStatusData);
			DBHandler.getInstance().updateParkingLotData(parkingLotName, parkingData.toString());
			return Params.getEmptyInstance().addParam("status", "OK");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Params.getEmptyInstance().addParam("status", "BAD");
	}



	public static Params getSpotStatus(Params params) {

		try {
			//int facID = Integer.parseInt(params.getParam("faceID"));
			int floor = Integer.parseInt(params.getParam("floor"));
			int position = Integer.parseInt(params.getParam("position"));
			String parkingLotName = params.getParam("name");
			int numCols = DBHandler.getInstance().getParkingLotWidth(parkingLotName);
			JSONObject parkingData = DBHandler.getInstance().getParkingLotJsonData(parkingLotName);
			JSONArray statusData = new JSONArray(parkingData.getJSONArray("statusData").toString());
			int i = floor;
			int j = position / numCols;
			int k = position % numCols;
			JSONArray newStatusData = new JSONArray();
			for(int index = 0; index < statusData.length(); index++) {
				JSONObject spotStatus = statusData.getJSONObject(index);
				if(spotStatus.getInt("i") == i && spotStatus.getInt("j") == j && spotStatus.getInt("k") == k) {
					return Params.getEmptyInstance().addParam("status", "OK")
							.addParam("status", spotStatus.getString("status"))
							.addParam("isDisabled", String.valueOf(spotStatus.getString("status").equals("i")))
							.addParam("isReserved", String.valueOf(spotStatus.getString("status").equals("s")));
				}
				newStatusData.put(spotStatus);
			}
			parkingData.put("statusData", newStatusData);
			DBHandler.getInstance().updateParkingLotData(parkingLotName, parkingData.toString());
			return Params.getEmptyInstance().addParam("status", "OK");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Params.getEmptyInstance().addParam("status", "BAD");
	}

}
