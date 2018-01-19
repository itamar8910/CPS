
import java.io.*;
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

import common.Params;
import common.Utils;
import algorithm.Algorithm;
import ocsf.server.*;
import pojos.Car;
import pojos.User;

public class Server extends AbstractServer
{
  //Class variables *************************************************

  //default port to listen on
  final public static int DEFAULT_PORT = 55560;

  //Constructors ****************************************************

  
  //Instance methods ************************************************

  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
    
    
  }

  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
	  //receive params from user
	  Params params = new Params(msg.toString()); 
	  Params resp;
	  String currentAction = params.getParam("action");
	  DBHandler dbInstance = DBHandler.getInstance();
	  String data;
	  
	  System.out.println("Current Action " + currentAction);
	  
	  try {
		//try all api
		  switch(currentAction) {
		  	 
		  	//------------------------ Reports API
		  
			//try: returns "Costumar Complaints" Data for report
			 //rec:  facID
			 //returns: data,status
			  case "returnCosCompReports":
				  data = dbInstance.returnCostumerComplaintsReport(params);
				  
				  System.out.println("Data Costumer Complaints : " + data);
				  this.sendResponseToClient(data, client);		  
			  break;
		  
			//try: returns "Orders " Data for report
			 //rec:  facID
			 //returns: data,status
			  case "returnOrdersReport":
				  data = dbInstance.returnOrdersReport(params);
				  this.sendResponseToClient(data, client);		  
			  break;
			  
			//try: returns "Problematic lots " Data for report
			 //rec:  facID
			 //returns: data,status
			  case "returnProbLotsReport":
				  data = dbInstance.returnProbLotsReport(params);
				  this.sendResponseToClient(data, client);		  
			  break;
			  
			//try: returns Activity Report Data for Main Mangager
			 //rec:  facID, startDate(unix)(start of week), numDays,number of days back
			 //returns: data,status
			  case "returnActivityDataReport":
				  data = dbInstance.returnActivityDataReport(params);
				  this.sendResponseToClient(data, client);		  
			  break;
			  
			  //gets facID
			  //returns data for current status of parking
		  	  case "requestCurrentParkingStatusReport":
		  		  
				System.out.println("Request current parking status : ");
				  
				String parkingName = dbInstance.getParkingNameByID(Integer.parseInt(params.getParam("facID")));
							
				String dataRet = backEndLogic.generateParkinglotDataForPDF(parkingName);
				String width = String.valueOf(DBHandler.getInstance().getParkingLotWidth(parkingName));
				
				Params resData = Params.getEmptyInstance();
				resData.addParam("name", parkingName);
				resData.addParam("data", dataRet);
				resData.addParam("dimension", width);
				resData.addParam("status", "OK");
				
				System.out.println("Data : " + resData.toString());
				
				try {
					client.sendToClient(resData.toString());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}	
			break;
		  
		  
		     //------------------------Parking Facility Worker API
			
			//try: pay to system
			//rec: userID,amount
			//returns: status
		  	 case "pay":
		  		System.out.println("Payment from : " + params.toString());
		  		data = dbInstance.performPayment(params);
				this.sendResponseToClient(data, client);	
		  	  break;
			
			
			//try: init parking
			//rec: facID
			//returns: status
		  	case "initParkingFacility":
		  		  System.out.println("Initilizating parking facility" );
		  		  
		  		  String name = dbInstance.getParkingNameByID(Integer.parseInt(params.getParam("facID")));
		  		  backEndLogic.initParkingLotData(name);
		  		  
		  		  Params resDataInit = Params.getEmptyInstance();
		  		  resDataInit.addParam("status", "OK");
		  		  this.sendResponseToClient(resDataInit, client);		  
			  break;
			
		  	//try: Set parking to full/not full
		  	 //rec:  facID,isFull
		  	 //returns: status: OK/BAD
		  	  case "changeParkingFull":
		  		  System.out.println("2.Changed :" +params.toString() );
		  		  data = dbInstance.setParkingFullStatus(params);
		  		  this.sendResponseToClient(data, client);		  
			  break;
		  
			//try: Set parking to disabled/not disabled
		  	 //rec:  facID,isDisabled
		  	 //returns: status: OK/BAD
		  	  case "changeParkingDisabled":
		  		  System.out.println("1.Changed :" +params.toString() );
		  		  data = dbInstance.setParkingDisabledStatus(params);
		  		  this.sendResponseToClient(data, client);		  
			  break;
			  
			//try: return parking status,name
		  	 //rec:  facID
		  	 //returns: status: OK/BAD, isFull,isDisabled,name
		  	  case "requestParkingStatusData":
		  		  data = dbInstance.requestParkingStatusData(params);
		  		  System.out.println("Parking status data : "+ data);
		  		  this.sendResponseToClient(data, client);		  
			  break;
			  
		
		  
		  
		  	  //--------------------- workers api
		  	  
		  	  //try: Trys to log in employee,
		  	 //rec:  UserName, Password
		  	 //returns: type: -1 = failed, 1/2/3 type of worker, user ID, fac ID
		  	  case "employeLogin":
		  		  data = dbInstance.emplyeLogin(params);
		  		  this.sendResponseToClient(data, client);		  
			  break;
			 
			  //try: Request for change of price from manger of facility
		  	 //rec: facID , price, type
		  	 //returns: res: 1 for success, 0 for failure
		  	  case "requestChangePrice":
		  		  data = dbInstance.requestChangeOfPrice(params);
		  		  this.sendResponseToClient(data, client);		  
			  break;
			  
			  //try: approve/disapprove change of price
		  	 //rec: facID, approve(1/0), updatedPrice
		  	 //returns: res: 1 for success, 0 for failure
		  	  case "finishChangePrice":
		  		  data = dbInstance.finishChangePrice(params);
		  		  this.sendResponseToClient(data, client);		  
			  break;
			  
			  //requests change prices requests for manger
		  	  case "requestChangePrices":
		  		  data = dbInstance.requestChangePrices(params);
		  		  this.sendResponseToClient(data, client);		  
			  break;
			  
			//request all parking available
		  	  case "requestParkings":
		  		  
		  		  data = dbInstance.requestParkings(params);
		  		  
		  		  System.out.println("Request Parking : " + data);
		  		  
		  		  this.sendResponseToClient(data, client);		  
			  break;
			  
			 
			  
			  
			  //--------------------- client api
		  	  
			  
			  //if parking sent full
			  //try: send to alternative if full
			  //gets: facName
			  //returns: status: OK/BAD, isFull: 0/1, alternative: name
		  	  case "canBeInParking":
		  		data = dbInstance.canBeInParking(params);
		  		this.sendResponseToClient(data, client);	
		      break;
			  
			  
			  ///-----handling complaints
			  
			  //client sending complaint
			  //try: add complaint to system
		  	 //rec: text,ID, facID
		  	 //returns: OK/BAD 
		  	  case "clientContact":
		  		data = dbInstance.addUserComplaint(params);
		  		this.sendResponseToClient(data, client);	
		      break;
		      
		      //return all complaints to worker
		     //try: return all comaplints not handled
		  	 //rec: facID
		  	 //returns:return all comaplints not handled in format {id,userID,text,dateTime)
		  	  case "returnComplaints":
		  		data = dbInstance.returnUsersComplaints(params);
		  		
		  		System.out.println("Return Complaints : " + data);
		  		
		  		this.sendResponseToClient(data, client);
		  	  break;
		      
		  	//try: handle complaint, ignore / sendMoney
		  	 //rec: shouldResolve, money, complaintID, userID
		  	 //returns:status: OK/BAD
		  	  case "handleComplaint":
		  		data = dbInstance.handleComplaint(params);
		  		this.sendResponseToClient(data, client);
		  	  break;
		  	  
		  	  case "RoutineSubscription":
	    		resp = backEndLogic.handleRoutineSubscription(params);
	    		client.sendToClient(resp.toString());
	    	  break;
		  	  case "FullSubscription":
	    		resp = backEndLogic.handleFullSubscription(params);
	    		client.sendToClient(resp.toString());
	    	  break;
		  	case "ClientPhysicalOrder":
	    		resp = backEndLogic.handleClientPhysicalOrder(params);
	    		client.sendToClient(resp.toString());
	    	break;
		  	case "clientOneTimeOrder":
	    		resp = backEndLogic.handleClientOneTimeOrder(params);
	    		client.sendToClient(resp.toString());
	    	break;
		  	case "clientLeave":
	    		resp = backEndLogic.handleClientLeave(params);
	    		client.sendToClient(resp.toString());	
	    	break;
		  	case "clientEnter":
	    		resp = backEndLogic.handleClientEnter(params);
	    		client.sendToClient(resp.toString());	    	
	    	break;
		  	case "clientEnterWithSubscriptionID":
	    		System.out.println("clientEnterWithSubscriptionID");
	    		resp = backEndLogic.handleClientEnter(params);
	    		client.sendToClient(resp.toString());
	    	break;
		  	case "clientCancelOrder":
	    		System.out.println("clientCancelOrder");
	    		resp = backEndLogic.handleClientCancelOrder(params);
	    		client.sendToClient(resp.toString());
	    		//resp.addParam("status", "OK");
//	    		resp.addParam("returnAmount", "42155");
//	    		client.sendToClient(resp.toString());
	    	break;
		  
		  	case "getParkingSlotStatus":
	    		System.out.println("getParkingSlotStatus");
	    		resp = backEndLogic.handleGetParkingSlotStatus(params);
	    		System.out.println("getParkingSlotStatus returning:" + resp.toString());
	    		client.sendToClient(resp.toString());
	    	break;
		  	case "getNumOfSubscribers":
	    		System.out.println("getNumOfSubscribers");
	    		resp = backEndLogic.handleGNumOfSubscribers(params);
	    		client.sendToClient(resp.toString());
	    	break;
		  	case "getNumOfSubscribersWithMoreThanOneCar":
	    		System.out.println("getNumOfSubscribersWithMoreThanOneCar");
	    		resp = backEndLogic.handleGNumOfSubscribersWithMoreThanOneCar(params);
	    		client.sendToClient(resp.toString());
	    		break;
		  	case "getSubscriptionStats":
	    		System.out.println("getSubscriptionStats");
	    		resp = backEndLogic.getSubscriptionStats(params);
	    		client.sendToClient(resp.toString());
	    	break;
		  	case "reserveSpot":
	    		System.out.println("reserveSpot");
	    		resp = backEndLogic.reserveSpot(params);
	    		client.sendToClient(resp.toString());
	    	break;
		  	case "spotDisabled":
	    		System.out.println("sportDisabled");
	    		resp = backEndLogic.toggleDisableSpot(params);
	    		client.sendToClient(resp.toString());
	    		break;
		  	case "getSpotStatus":
	    		System.out.println("getSpotStatus");
	    		resp = backEndLogic.getSpotStatus(params);
	    		client.sendToClient(resp.toString());
	    		break;
		  	case "getParkingLotWidth":
	    		System.out.println("getParkingLotWidth");
	    		int parkingLotWidth = DBHandler.getInstance().getParkingLotWidth(params.getParam("name"));
	    		resp = Params.getEmptyInstance().addParam("status", "OK").addParam("width", String.valueOf(parkingLotWidth));
	    		client.sendToClient(resp.toString());
	    	break;
		  	case "isParkingLotFull":
	    		System.out.println("isParkingLotFull");
	    		resp = backEndLogic.isParkingLotFull(params);
	    		client.sendToClient(resp.toString());
	    		break;
		  	case "getVehicleStatus":
	    		System.out.println("getVehicleStatus");
	    		resp = backEndLogic.getVehicleStatus(params);
	    		client.sendToClient(resp.toString());
	    		break;
		  	case "getAllVehiclesOfUser":
	    		System.out.println("getAllVehiclesOfUser");
	    		resp = backEndLogic.getVehiclesOfUser(params);
	    		client.sendToClient(resp.toString());
	    	break;
		
		  		
			  //------------------------default api
			  //if no action specified
			  default:
				  System.out.println("No case found");
				  
			  break;
		  }
	  }catch(Exception e) {
		  System.out.println("Error in actions : ");
		  e.printStackTrace();
		  
	  }
	  
	 
  } 
	  
  
  /*
   * 
   * Start of itamars section
   */

  //Constructors ****************************************************

  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public Server(int port)
  {
    super(port);
    //System.out.println("Tables:");
    //TestDB.getInstance().printAllTables();
    final boolean DO_CONJOBS = true; //TODO: remember to change this before submitting :)
    if(DO_CONJOBS) { 
	    new Thread(()-> {
	    	while(true) {
		    	System.out.println("CRON JOB 1 MIN");
		    	
		    	//do logic
		    	//check for all orders if are late
		    	List<User> allUsers = DBHandler.getInstance().getAllUsers();
		    	System.out.println("Printing all users:");
		    	for(User user : allUsers) {
    				System.out.println("Fucking your late mother fucker user ID : " + user.getUserID());
		    		if(user.getSubscriptionParams().getParam("type").equals("orderedOneTimeParking")) {
		    			long orderTimeUnix = Long.valueOf(user.getSubscriptionParams().getParam("enterTimeMS"));
		    			System.out.println("order unix time:" + orderTimeUnix);
		    			System.out.println("current unix time:" + System.currentTimeMillis());
		    			// if customer is late and this is the first minute shes is late in
		    			if(System.currentTimeMillis() > orderTimeUnix && System.currentTimeMillis() < orderTimeUnix + 60*1000) {
		    				System.out.println("Fucking your late mother fucker");
		    				//update stats
		    				backEndLogic.addToStatistics(DBHandler.getInstance().getVehicleParkingLot(user.getVehicleID()), 1, 0, 0, 0);
		    				//notify via email
		    				backEndLogic.sendEmailToCostumerForBeginLate(user.getEmail(), "Hello, You are late for your order");
		    			}
		    		}
		    	}
		    	System.out.println("END");
		    	try {	    		
		    		//sleep till next minute
		    		Calendar c = Calendar.getInstance();
		    		c.setTimeInMillis(System.currentTimeMillis());
		    		c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE) + 1);
					System.out.println("calendar millis:" + c.getTimeInMillis());
		    		System.out.println("current millis :" + System.currentTimeMillis());
					long diff = c.getTimeInMillis() - System.currentTimeMillis();
		    		System.out.println("sleeping untill next minute for:" + diff);
					Thread.sleep(diff);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
	    	
	    }).start();
	    new Thread(()-> {
	    	while(true) {
	    		
	    		try {	    		
		    		//sleep till next day
		    		Calendar c = Calendar.getInstance();
		    		c.setTimeInMillis(System.currentTimeMillis());
		    		c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE)+1, 0,0);
					System.out.println("calendar millis:" + c.getTimeInMillis());
		    		System.out.println("current millis :" + System.currentTimeMillis());
					long diff = c.getTimeInMillis() - System.currentTimeMillis();
		    		System.out.println("sleeping untill next day for:" + diff);
					Thread.sleep(diff);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		
	    		System.out.println("CRON JOB START OF DAY");
	    		
	    		
	    		//do logic
	    		List<User> allUsers = DBHandler.getInstance().getAllUsers();
		    	System.out.println("Printing all users:");
		    	for(User user : allUsers) {
		    		Params subscriptionParams = user.getSubscriptionParams();
		    		if(subscriptionParams.getParam("type").equals("routineSubscription") || subscriptionParams.getParam("type").equals("fullSubscription")) {
		    			//check if subscription is still valid
		    			long subscriptionStartUnix = Long.parseLong(subscriptionParams.getParam("subscriptionStartMS"));
		    			int numDaysAgo = Utils.getNumDaysAgo(subscriptionStartUnix);
		    			if(numDaysAgo > 28) {
		    				DBHandler.getInstance().removeUser(user.getUserID());
		    				continue;
		    			}else if(numDaysAgo >= 21) {
		    				backEndLogic.sendEmailToCostumerForBeginLate(user.getEmail(), "Please notice: your subscription is about to end in a week");
		    			}
		    			
		    			backEndLogic.handleCallParkingAlgoOrderForSubscription(user.getUserID(), user.getVehicleID(), DBHandler.getInstance().getVehicleParkingLot(user.getVehicleID()), subscriptionParams);
		    		}	
		    	}
		    	
	    		/*
	    		 * for each user that's a subscriber
	    		 * call: handleCallParkingAlgoOrderForSubscription
	    		 */
	    		
	    	}
	    }).start();
    }
    //TestDB.getInstance().addRow("itamar", 100);
    //TestDB.getInstance().getBalanceOf("itamar");
  }




/*
 * 
 * End of itamars section
 */


  public void sendResponseToClient(Object msg, ConnectionToClient client) {
	  try {
		  client.sendToClient(msg);
	  }catch (Exception error ) {
		  System.out.println("Error sending data to user: " + error.getMessage());
	  }
  }
  

  
  
  
  ///--------- start server
  /**
   * This method is responsible for the creation of
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555
   *          if no argument is entered.
   */
  public static void main(String[] args)
  {
    int port = 0; //Port to listen on

    DBHandler.getInstance();
    
    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }

    Server sv = new Server(port);
   // sv.initParkingLotData("misgavParking");
    
   /*
    //test data
	DBHandler dbInstance = DBHandler.getInstance();
	// facID, startDate(unix)(start of week), numDays,number of days back
    String data = dbInstance.returnActivityDataReport(new Params("{'facID':2,'startDate':1512079200000,'numDays':14}"));
    System.out.println("Data test : " + data);
   
   
    
    backEndLogic.sendEmailToCostumerForBeginLate("gil.maman.5@gmail.com","fuck you");
     */
    try
    {
      sv.listen(); //Start listening for connections
    }
    catch (Exception ex)
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
  }
}
//End of EchoServer class
