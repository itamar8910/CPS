package application;

import java.net.URL;
import java.util.ResourceBundle;

import org.json.JSONArray;
import org.json.JSONException;

import common.ControllerIF;
import common.Params;
import common.TalkToServer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class handleParkingController implements ControllerIF{
	
    private ApplicationMain main;
    private Params params;
    private String typeOfWorker;
    private int currentFloor;
    private Rectangle[] recArr;
    private int numOfCols;
    private boolean wait;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;
    
    @FXML
    private Button editButton;

    @FXML
    private Text currentFloorText;
    
    @FXML
    private Button backButton;
    
    @FXML
    private Button floorDownButton;

    @FXML
    private Text nameOfParking;

    @FXML
    private Button nextFloorButton;
    
    @FXML
    private HBox Hbox1;

    @FXML
    private HBox Hbox2;

    @FXML
    private HBox Hbox3;

    @FXML
    private TextField parkingTextBox;
    

    @FXML
    void backPressed(ActionEvent event) {
    	if(typeOfWorker.equals("1"))
    		main.setScene("FacilityWorkerMain.fxml", params);
    	else
    		main.setScene("ServiceEmployeeMain.fxml", params);
    }
        
    Color statusToColor(String Status) {
    	Color ret = null;
    	switch(Status) {
    	case "e":	ret = Color.GREEN; 	break;// empty
    	case "f":	ret = Color.RED;	break;// full
    	case "i":	ret = Color.BLACK;	break;// invalid
    	case "s":	ret = Color.BLUE;	break;// saved

    	default: return ret;
    	}
    	return ret;
    }
    
    void updateParkingSpots() {
    	currentFloorText.setText("Floor: " + this.currentFloor);
    	// ask itamar for info about all the parking spots
    	Params serverRequest = Params.getEmptyInstance();
    	serverRequest.addParam("action", "getParkingSlotStatus");
    	serverRequest.addParam("name", params.getParam("parkingName").toString());
    	
    	final Params temp = Params.getEmptyInstance();

    	// send this JSON to server
    	TalkToServer.getInstance().send(serverRequest.toString(), msg -> {
    		final Params res2 = new Params(msg);
    		
    		if(true || res2.getParam("status").equals("OK")){ 
    			Platform.runLater(new Runnable() {
    	  		      @Override public void run() {
    	  	    		final Stage dialog = new Stage();
    	  	    		try {
    	  	    			JSONArray arr = null;
							arr = new JSONArray(res2.getParam("array"));
							// get 3D point and update spots colors
							for(int i=0; i<arr.length(); i++) {
								String floor = arr.getJSONObject(i).get("i").toString();
								if(!floor.equals(String.valueOf(currentFloor-1)))
									continue;
								
								int pos = (Integer.parseInt(arr.getJSONObject(i).get("j").toString())*numOfCols)+Integer.parseInt(arr.getJSONObject(i).get("k").toString());
								try {	// this try is for ignoring indices not in range
									recArr[pos].setFill(statusToColor(arr.getJSONObject(i).get("status").toString()));
								} catch (Exception e){
								}
								
							}
						} catch (JSONException e1) {
							e1.printStackTrace();
						}
    	  	    		
    	  		      }});}});
    }
    
    String spotStatus(String numOfParking) {
    	Params serverUpdate = Params.getEmptyInstance();
    	String posForItamar = String.valueOf(Integer.parseInt(numOfParking)-1);

		serverUpdate.addParam("action", "getSpotStatus");
		serverUpdate.addParam("floor", String.valueOf(currentFloor-1));
		serverUpdate.addParam("position", posForItamar);
		serverUpdate.addParam("name", params.getParam("parkingName"));
	    
   		Params got = Params.getEmptyInstance();
   		
	   	// send this JSON to server
   		wait =true;
   		
	   	TalkToServer.getInstance().send(serverUpdate.toString(), msg -> {
	   		Params res = new Params(msg);
	   		got.addParam("status", res.getParam("status")); 
	   		wait = false;});
	   		
	   	while(wait) {
	   		System.out.print("");
	   	}
	   	
	   	return got.getParam("status").toString();
    }
    
    void editClicked(String numOfParking) {
    	String posForItamar = String.valueOf(Integer.parseInt(numOfParking)-1);
    	Platform.runLater(new Runnable() {
		      @Override public void run() {
	    		 final Stage dialog = new Stage();
	    		 dialog.initModality(Modality.APPLICATION_MODAL);
	    		 dialog.initOwner(main.primaryStage);
	    		 VBox dialogVbox = new VBox(20);
	    		 dialogVbox.getChildren().add(new Text("Parking "+numOfParking+ " Selcted"));
	    		 dialogVbox.getChildren().add(new Text("Select Action"));
	    		 
	    		 String buttonText = "Reserve Spot";
	    		 final Params temp = Params.getEmptyInstance();
	    		 if(spotStatus(numOfParking).equals("s")) {
	    			 buttonText = "Cancel Reservation";
	    			 temp.addParam("bool", "false");
	    		 }
	    		 else
	    			 temp.addParam("bool", "true");
	    			 
	    		 Button reserveButton = new Button(buttonText);
	    		 
	    		 reserveButton.setOnAction(event ->{
	    			 Params serverUpdate = Params.getEmptyInstance();
	    			 
	    			 serverUpdate.addParam("action", "reserveSpot");
	    			 serverUpdate.addParam("status", temp.getParam("bool").toString());
	    			 serverUpdate.addParam("name", params.getParam("parkingName"));
	    			 serverUpdate.addParam("floor", String.valueOf(currentFloor-1));
	    			 serverUpdate.addParam("position", posForItamar);
	    			 
	    			 if(temp.getParam("bool").toString().equals("true")) {
	    				 reserveButton.setText("Cancel Reservation");
		    			 temp.addParam("bool", "false");
	    			 }
	    			 else {
	    				 reserveButton.setText("Reserve Spot");
		    			 temp.addParam("bool", "true");
	    			 }

	    		   	// send this JSON to server
	    		   	TalkToServer.getInstance().send(serverUpdate.toString(), msg -> 
	    		   	{updateParkingSpots();	});
	    			 
	    		 });
	    		 
	    		 
	    		 dialogVbox.getChildren().add(reserveButton);
	    		 
	    		 Button disabledButton = new Button("Spot Disabled");

	    		 final Params temp2 = Params.getEmptyInstance();
	    		 if(spotStatus(numOfParking).equals("i")) {
	    			 reserveButton.setDisable(true);
	    			 disabledButton.setText("Spot Not Disabled");
	    			 temp2.addParam("bool", "false");
	    		 }
	    		 else
	    			 temp2.addParam("bool", "true");
	    		 
	    		 disabledButton.setOnAction(event ->{
	    			 Params serverUpdate = Params.getEmptyInstance();
	    			 
	    			 serverUpdate.addParam("action", "spotDisabled");
	    			 serverUpdate.addParam("name", params.getParam("parkingName"));
	    			 serverUpdate.addParam("status", temp2.getParam("bool").toString());
	    			 serverUpdate.addParam("floor", String.valueOf(currentFloor-1));
	    			 serverUpdate.addParam("position", posForItamar);
	    			 
	    			 if(temp2.getParam("bool").toString().equals("true")) {
	    				 disabledButton.setText("Spot Not Disabled");
		    			 temp2.addParam("bool", "false");
		    			 reserveButton.setDisable(true);
	    			 }
	    			 else {
	    				 disabledButton.setText("Spot Disabled");
		    			 temp2.addParam("bool", "true");
		    			 reserveButton.setDisable(false);
	    			 }
	    			 	    		    	
	    		   	// send this JSON to server
	    		   	TalkToServer.getInstance().send(serverUpdate.toString(), msg -> 
	    		   	{updateParkingSpots();});
	             	 
	    		 });
	    		 if(typeOfWorker.equals("1"))
		    		 dialogVbox.getChildren().add(disabledButton);
	    		 
	    		 Scene dialogScene = new Scene(dialogVbox, 300, 200);
	    		 dialog.setScene(dialogScene);
	    		 dialog.show();
		      }
    	});
    }

    @FXML
    void nextFloorClicked(ActionEvent event) {
    	if(this.currentFloor == 3)
    		return;
    	this.currentFloor+=1;
    	updateParkingSpots();
    }
    
    @FXML
    void floorDownClicked(ActionEvent event) {
    	if(this.currentFloor == 1)
    		return;
    	this.currentFloor-=1;
    	updateParkingSpots();
    }

    @FXML
    void initialize() {
        assert floorDownButton != null : "fx:id=\"floorDownButton\" was not injected: check your FXML file 'handleParking.fxml'.";
        assert currentFloorText != null : "fx:id=\"currentFloorText\" was not injected: check your FXML file 'handleParking.fxml'.";
        assert editButton != null : "fx:id=\"editButton\" was not injected: check your FXML file 'handleParking.fxml'.";
        assert nameOfParking != null : "fx:id=\"nameOfParking\" was not injected: check your FXML file 'handleParking.fxml'.";
        assert nextFloorButton != null : "fx:id=\"nextFloorButton\" was not injected: check your FXML file 'handleParking.fxml'.";
        assert Hbox1 != null : "fx:id=\"Hbox1\" was not injected: check your FXML file 'handleParking.fxml'.";
        assert Hbox2 != null : "fx:id=\"Hbox2\" was not injected: check your FXML file 'handleParking.fxml'.";
        assert Hbox3 != null : "fx:id=\"Hbox3\" was not injected: check your FXML file 'handleParking.fxml'.";
        assert backButton != null : "fx:id=\"backButton\" was not injected: check your FXML file 'handleParking.fxml'.";
        assert parkingTextBox != null : "fx:id=\"parkingTextBox\" was not injected: check your FXML file 'handleParking.fxml'.";
    }
    
	@Override
	public void init(ApplicationMain main, Params params) {
		this.main = main;
		this.params = params;
		this.currentFloor = 1;
		
		
		Params serverUpdate = Params.getEmptyInstance();
		serverUpdate.addParam("action", "getParkingLotWidth");
		serverUpdate.addParam("name", params.getParam("parkingName"));
	    this.wait = true;
	   	// send this JSON to server
	   	TalkToServer.getInstance().send(serverUpdate.toString(), msg -> {
    		final Params res = new Params(msg);
	   		this.numOfCols = Integer.parseInt(res.getParam("width").toString());
	   		wait = false;
	   	});
	   	
	   	while(wait) {
	   		System.out.print("");}
	   	
		nameOfParking.setText(params.getParam("parkingName"));
		typeOfWorker = params.getParam("amIaWorker").toString();
		
		double screenWidth = main.primaryStage.getWidth();
		double screenHeight = main.primaryStage.getHeight();
		double recWidth = 50;
		int startingX = 30;
        		
		this.recArr = new Rectangle[this.numOfCols*3];

		HBox[] curr = new HBox[]{Hbox1, Hbox2, Hbox3};
		
		HBox x = new HBox();
		
		for(int j=0; j<3; j++) {
			for(int i=1; i<=this.numOfCols; i++) {
				Rectangle tempRec = new Rectangle();
				tempRec.setX(startingX+ i*recWidth);	
				tempRec.setY(300);
				
				tempRec.setWidth(recWidth);
				tempRec.setHeight(50);
				tempRec.setArcHeight(20);
				tempRec.setArcWidth(20);
			
				this.recArr[(i-1)+(j*this.numOfCols)] = tempRec;
				curr[j].getChildren().add(tempRec);
			}
		}

		for(int i=0; i< this.recArr.length ; i++) {
			final int temp = i+1;
			this.recArr[i].setOnMousePressed(new EventHandler<MouseEvent>() {
			    @Override 
			    public void handle(MouseEvent event) {
			        if (event.isPrimaryButtonDown() && event.getClickCount() == 1) {
			        	editClicked(String.valueOf(temp));
			        }
			    }});	
		}
		updateParkingSpots();
	}

}

