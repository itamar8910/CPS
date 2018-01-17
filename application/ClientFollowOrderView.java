package application;

import org.json.JSONArray;
import org.json.JSONException;

import common.ControllerIF;
import common.Params;
import common.TalkToServer;
import common.Utils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class ClientFollowOrderView implements ControllerIF{
	
    @FXML
    private TextField tfSubID;

    @FXML
    private TextField vehicleID;
    
    String[] vIDS;
    
    @FXML
    void bSubIDSubmit(ActionEvent event) {
    	System.out.println("bSubIDSubmit");
    	Params getVehicles = Params.getEmptyInstance();
    	getVehicles.addParam("action", "getAllVehiclesOfUser");
    	getVehicles.addParam("userID", tfSubID.getText());
    	TalkToServer.getInstance().send(getVehicles.toString(), msg -> {
    		System.out.println("bSubIDSubmit got resp:" + msg);
    		Params resp = new Params(msg);
    		try {
				JSONArray vehicles = new JSONArray(resp.getParam("vehiclesArr"));
				vIDS = new String[vehicles.length()];
				for(int i = 0; i < vehicles.length(); i++) {
					String vID = vehicles.getString(i);
					System.out.println("calling showDataForVehicleID with V:" + vID);
					vIDS[i] = "" + vID + "";
					showDataForVehicleID(vID, i);
					//Thread.sleep(5000);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	});
    }

    void showDataForVehicleID(final String vID, int index) {
    	//String tempVID = vIDS[index];
    	 System.out.println("b4 showing popup for V:" + vID);
    	Params requestParams = Params.getEmptyInstance();
    	requestParams.addParam("action", "getVehicleStatus");
    	requestParams.addParam("vehicleID", vID);
    	TalkToServer.getInstance().send(requestParams.toString(), msg -> {
    		System.out.println("follow order by vehicleID resp:" + msg);
    		Params resp = new Params(msg);
    		if(resp.getParam("status").equals("OK")) {
    			Platform.runLater(new Runnable() {
  			      @Override public void run() {
  			    	  String tempVID = vIDS[index];
  			    	  System.out.println("showing popup for V:" + tempVID);
  		    		 final Stage dialog = new Stage();
  		             dialog.initModality(Modality.APPLICATION_MODAL);
  		             dialog.initOwner(main.primaryStage);
  		             VBox dialogVbox = new VBox(20);
  		             boolean isParked = resp.getParam("isParked").equals("true");
  		             dialogVbox.getChildren().add(new Text(isParked ? "Your car is currently parked" : "Your car is not currently parked"));
  		             if(isParked) {
  		            	 dialogVbox.getChildren().add(new Text("VehicleID:"+ vID));
  		            	 dialogVbox.getChildren().add(new Text("Your car is in the parking lot:" + resp.getParam("parkingLot")));
  		            	 dialogVbox.getChildren().add(new Text("In Spot:"+ resp.getParam("parkingSpot")));
  		            	 dialogVbox.getChildren().add(new Text("Starting time:" + resp.getParam("startTime")));
  		            	 dialogVbox.getChildren().add(new Text("Current time:" + Utils.unixTimeToHour(System.currentTimeMillis())));	            	 
  		             }
  	
  		             Scene dialogScene = new Scene(dialogVbox, 300, 200);
  		             dialog.setScene(dialogScene);
  		             dialog.show();
  		             System.out.println("showed dialog");
  			      }
  		    });
    		}
	    	
    	});
    }
    
    @FXML
    void bVehicleIDSubmit(ActionEvent event) {
    	vIDS = new String[] {vehicleID.getText()};
    	showDataForVehicleID(vehicleID.getText(), 0);
    }
	
	private ApplicationMain main;
	   private Params params;
	    
	    

		@Override
		public void init(ApplicationMain main, Params params) {
			this.main = main;
			this.params = params;
		}
	    
}
