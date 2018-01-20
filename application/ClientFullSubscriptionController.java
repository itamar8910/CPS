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

public class ClientFullSubscriptionController implements ControllerIF{

 	@FXML
    private TextField tfID;

    @FXML
    private TextField tfVehicleID;

    @FXML
    private TextField tfStartDate;

    @FXML
    private Button bSubmit;

    @FXML
    private TextField tfEmail;

    private ApplicationMain main;
    private Params params;

    @FXML
    void bBackClick(){
    	main.setScene("ClientOnlineView.fxml", Params.getEmptyInstance());
    }
    
    @FXML
    void bSumbitClick(ActionEvent event) {
    	

//    	boolean isFull = Utils.getIsFull(tfParkingLot.getText(), main.primaryStage);
//    	if(isFull){
//    		return;
//    	}
    	
    	handleClientFullSubscription(tfID.getText(), tfVehicleID.getText(), tfStartDate.getText(), tfEmail.getText(), msg->{
     		System.out.println("ClientfullSubscriptionController got msg from server:" + msg);
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
    		}else {
    			Platform.runLater(new Runnable() {
    	  		      @Override public void run() {
    	  	    		 final Stage dialog = new Stage();
    	  	             dialog.initModality(Modality.APPLICATION_MODAL);
    	  	             dialog.initOwner(main.primaryStage);
    	  	             VBox dialogVbox = new VBox(20);
    	  	             dialogVbox.getChildren().add(new Text("Sorry, your request could not be granted"));
    	  	             if(respParams.hasParam("message")) {
  	    	  	             dialogVbox.getChildren().add(new Text("message:" + respParams.getParam("message")));
    	  	             }
    	  	             Scene dialogScene = new Scene(dialogVbox, 300, 200);
    	  	             dialog.setScene(dialogScene);
    	  	             dialog.show();
    	  	             System.out.println("showed dialog");
    	  		      }
  	  		    	});
    		}
    	});
    	
//    	Params orderParams = Params.getEmptyInstance();
//    	orderParams.addParam("action", "FullSubscription");
//    	orderParams.addParam("ID", tfID.getText());
//    	orderParams.addParam("vehicleID", tfVehicleID.getText()); //TODO: handle multiple vehicles
//    	orderParams.addParam("startDate", tfStartDate.getText());
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
    
    public static void handleClientFullSubscription(String userID, String vehicleID, String startDate, String email, StrCallbackIF callback) {
    	Params orderParams = Params.getEmptyInstance();
    	orderParams.addParam("action", "FullSubscription");
    	orderParams.addParam("ID", userID);
    	orderParams.addParam("vehicleID", vehicleID);
    	orderParams.addParam("startDate", startDate);
    	orderParams.addParam("email", email);
    	System.out.println("sending request to server");
    	TalkToServer.getInstance().send(orderParams.toString(), callback );
    }

    @Override
	public void init(ApplicationMain main, Params params) {
		this.main = main;
		this.params = params;
	}

}
