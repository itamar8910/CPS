package application;

import common.ControllerIF;
import common.Params;
import common.ParkingAlgo;
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

public class ClientPhysicalOrderController implements ControllerIF{

    @FXML
    private TextField tfID;

    @FXML
    private TextField tfVehicleID;

    @FXML
    private TextField tfLeaveTime;

    @FXML
    private TextField tfEmail;

    @FXML
    private TextField tfParkingLot;
    
    @FXML
    private Button bSubmit;

    private ApplicationMain main;
    private Params params;


    @FXML
    void bBackClick(){
    	main.setScene("ClientPhysicalView.fxml", Params.getEmptyInstance());
    }
    
    @FXML
    void bSumbitClick(ActionEvent event) {
    	
    	boolean isFull = Utils.getIsFull(tfParkingLot.getText(), main.primaryStage);
    	if(isFull){
    		return;
    	}
    	
    	handleClientPhysicalOrder(tfID.getText(), tfVehicleID.getText(), tfLeaveTime.getText(), tfEmail.getText(), tfParkingLot.getText(), new StrCallbackIF() {
			
			@Override
			public void handle(String msg) {
				System.out.println("ClientPhysicalOrderController got msg from server:" + msg);
	    		Params resp = new Params(msg);
	    		if(resp.getParam("status").equals("OK")) {
	    			Platform.runLater(new Runnable() {
	    	  		      @Override public void run() {
	    	  	    		 final Stage dialog = new Stage();
	    	  	             dialog.initModality(Modality.APPLICATION_MODAL);
	    	  	             dialog.initOwner(main.primaryStage);
	    	  	             VBox dialogVbox = new VBox(20);
	    	  	             dialogVbox.getChildren().add(new Text("Access granted, Please exit your vehicle"));
	    	  	             Scene dialogScene = new Scene(dialogVbox, 300, 200);
	    	  	             dialog.setScene(dialogScene);
	    	  	             dialog.show();
	    	  	             System.out.println("showed dialog");
	    	  		      }
		  		    	});
	    		}else {
	    			Platform.runLater(new Runnable() {
	    	  		      @Override public void run() {
	    	  	    		 final Stage dialog = new Stage();
	    	  	             dialog.initModality(Modality.APPLICATION_MODAL);
	    	  	             dialog.initOwner(main.primaryStage);
	    	  	             VBox dialogVbox = new VBox(20);
	    	  	             dialogVbox.getChildren().add(new Text("Sorry, your request could not be granted"));
	    	  	             Scene dialogScene = new Scene(dialogVbox, 300, 200);
	    	  	             dialog.setScene(dialogScene);
	    	  	             dialog.show();
	    	  	             System.out.println("showed dialog");
	    	  		      }
		  		    	});
	    		}
				
			}
		});
    	
    	/*
    	 System.out.println("ClientPhysicalOrderController got msg from server:" + msg);
    		Params resp = new Params(msg);
    		if(resp.getParam("status").equals("OK")) {
    			Platform.runLater(new Runnable() {
    	  		      @Override public void run() {
    	  	    		 final Stage dialog = new Stage();
    	  	             dialog.initModality(Modality.APPLICATION_MODAL);
    	  	             dialog.initOwner(main.primaryStage);
    	  	             VBox dialogVbox = new VBox(20);
    	  	             dialogVbox.getChildren().add(new Text("Access granted, Please exit your vehicle"));
    	  	             Scene dialogScene = new Scene(dialogVbox, 300, 200);
    	  	             dialog.setScene(dialogScene);
    	  	             dialog.show();
    	  	             System.out.println("showed dialog");
    	  		      }
	  		    	});
    		}else {
    			Platform.runLater(new Runnable() {
    	  		      @Override public void run() {
    	  	    		 final Stage dialog = new Stage();
    	  	             dialog.initModality(Modality.APPLICATION_MODAL);
    	  	             dialog.initOwner(main.primaryStage);
    	  	             VBox dialogVbox = new VBox(20);
    	  	             dialogVbox.getChildren().add(new Text("Sorry, your request could not be granted"));
    	  	             Scene dialogScene = new Scene(dialogVbox, 300, 200);
    	  	             dialog.setScene(dialogScene);
    	  	             dialog.show();
    	  	             System.out.println("showed dialog");
    	  		      }
	  		    	});
    		}
    	 */
    	
    }

    public static void handleClientPhysicalOrder(String userID, String vehicleID, String leaveTime, String email, String parkingLot, StrCallbackIF callback) {
    
    	
//    	System.out.println("isFull:" + isFull);
//    	if(true) {
//    		return;
//    	}
    	
    	
    	Params orderParams = Params.getEmptyInstance();
    	orderParams.addParam("action", "ClientPhysicalOrder");
    	orderParams.addParam("ID", userID);
    	orderParams.addParam("parkingLot", parkingLot); 
    	orderParams.addParam("vehicleID", vehicleID);
    	orderParams.addParam("leaveTime", leaveTime);
    	orderParams.addParam("email", email);
    	System.out.println("sending request to server");
    	TalkToServer.getInstance().send(orderParams.toString(),callback);
    }
	

	@Override
	public void init(ApplicationMain main, Params params) {
		this.main = main;
		this.params = params;
	}

}
