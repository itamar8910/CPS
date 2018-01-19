package application;

import common.ControllerIF;
import common.Params;
import common.StrCallbackIF;
import common.TalkToServer;
import common.Utils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ClientRoutineSubscriptionController implements ControllerIF{

    @FXML
    private TextField tfID;

    @FXML
    private TextField tfVehicleID;

    @FXML
    private TextField tfParkingLot;

    @FXML
    private TextField tfStartDate;

    @FXML
    private Button bSubmit;
    
    @FXML
    private TextField tfEnterTime;
    
    @FXML
    private TextField tfLeavingTime;

    @FXML
    private TextField tfEmail;

    private ApplicationMain main;
    private Params params;

    @FXML
    void bSumbitClick(ActionEvent event) {
    	

    	boolean isFull = Utils.getIsFull(tfParkingLot.getText(), main.primaryStage);
    	if(isFull){
    		return;
    	}
    	
    	handleClientRoutineSubscription(tfID.getText(), tfVehicleID.getText(),
    			tfParkingLot.getText(),
    			tfStartDate.getText(), tfEnterTime.getText(),
    			tfLeavingTime.getText(), tfEmail.getText(),
    			new StrCallbackIF() {
					
					@Override
					public void handle(String msg) {
						System.out.println("ClientRoutineSubscriptionController got msg from server:" + msg);
			    		Params respParams = new Params(msg);

			    		if(respParams.getParam("status").equals("OK")){
			    			Platform.runLater(new Runnable() {
			    	  		      @Override public void run() {
			    	  		    	  //TODO: handle payment
			    	  	    		 final Stage dialog = new Stage();
			    	  	    		 String subscriptionID = respParams.getParam("subscriptionID");
			    	  	             dialog.initModality(Modality.APPLICATION_MODAL);
			    	  	             dialog.initOwner(main.primaryStage);
			    	  	             VBox dialogVbox = new VBox(20);
			    	  	             dialogVbox.getChildren().add(new Text("Your subscription ID:" + subscriptionID));
			    	  	             dialogVbox.getChildren().add(new Text("Please pay:" + respParams.getParam("price")));

			    	  	             Scene dialogScene = new Scene(dialogVbox, 300, 200);
			    	  	             dialog.setScene(dialogScene);
			    	  	             dialog.show();
			    	  	             System.out.println("showed dialog");
			    	  		      }
			  		    });
			    		}
					}
			});
//    	
//    	Params orderParams = Params.getEmptyInstance();
//    	orderParams.addParam("action", "RoutineSubscription");
//    	orderParams.addParam("ID", tfID.getText());
//    	orderParams.addParam("vehicleID", tfVehicleID.getText()); //TODO: handle multiple vehicles
//    	orderParams.addParam("parkingLot", tfParkingLot.getText());
//    	orderParams.addParam("startDate", tfStartDate.getText());
//    	orderParams.addParam("enterTime", tfEnterTime.getText());
//    	orderParams.addParam("leaveTime", tfLeavingTime.getText());
//    	orderParams.addParam("email", tfEmail.getText());
//    	System.out.println("sending request to server");
//    	TalkToServer.getInstance().send(orderParams.toString(), msg -> {
//    		System.out.println("ClientRoutineSubscriptionController got msg from server:" + msg);
//    		Params respParams = new Params(msg);
//
//    		if(respParams.getParam("status").equals("OK")){
//    			Platform.runLater(new Runnable() {
//    	  		      @Override public void run() {
//    	  		    	  //TODO: handle payment
//    	  	    		 final Stage dialog = new Stage();
//    	  	    		 String subscriptionID = respParams.getParam("subscriptionID");
//    	  	             dialog.initModality(Modality.APPLICATION_MODAL);
//    	  	             dialog.initOwner(main.primaryStage);
//    	  	             VBox dialogVbox = new VBox(20);
//    	  	             dialogVbox.getChildren().add(new Text("Your subscription ID:" + subscriptionID));
//    	  	             dialogVbox.getChildren().add(new Text("Please pay:" + respParams.getParam("price")));
//
//    	  	             Scene dialogScene = new Scene(dialogVbox, 300, 200);
//    	  	             dialog.setScene(dialogScene);
//    	  	             dialog.show();
//    	  	             System.out.println("showed dialog");
//    	  		      }
//  		    });
//    		}
//
//    	});
    }

    public static void handleClientRoutineSubscription(String userID, String vehicleID, String parkingLot, String startDate, String enterTime, String leaveTime, String email, StrCallbackIF callback) {
    	Params orderParams = Params.getEmptyInstance();
    	orderParams.addParam("action", "RoutineSubscription");
    	orderParams.addParam("ID", userID);
    	orderParams.addParam("vehicleID",vehicleID); //TODO: handle multiple vehicles
    	orderParams.addParam("parkingLot", parkingLot);
    	orderParams.addParam("startDate", startDate);
    	orderParams.addParam("enterTime", enterTime);
    	orderParams.addParam("leaveTime", leaveTime);
    	orderParams.addParam("email", email);
    	System.out.println("sending request to server");
    	TalkToServer.getInstance().send(orderParams.toString(), callback);
    }
    
    @Override
	public void init(ApplicationMain main, Params params) {
		this.main = main;
		this.params = params;
	}

}
