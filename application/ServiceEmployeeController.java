package application;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;

import common.ControllerIF;
import common.Params;
import common.TalkToServer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Popup;

public class ServiceEmployeeController implements ControllerIF{
	
    private ApplicationMain main;
    private Params params;
    private	JSONArray curList;
    private	JSONArray complainsArray;

	
	@FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ListView<String> complainsList;
    
    @FXML
    private Button refreshButton;

    @FXML
    private TextField refundAmount;
    
    @FXML
    private ListView<String> parkingList;

    @FXML
    private Button deleteButton;

    @FXML
    private Button refundButton;
    
    @FXML
    private Button selectParkingButton;

   
    @FXML
    void deleteClicked(ActionEvent event) {
    	if(complainsList.getSelectionModel().getSelectedIndex() == -1)
    		return;
    	
    	Params updateServer = Params.getEmptyInstance();
    	updateServer.addParam("action", "handleComplaint");
    	updateServer.addParam("money", "-1");
    	
    	String complaintID = "";
    	String userID = "";
    	
    	for (int i = 0; i < complainsArray.length(); i++) {
  			if(complainsList.getSelectionModel().getSelectedIndex() == i) {
  				try {
					complaintID = complainsArray.getJSONObject(i).getString("id");
	  				userID = complainsArray.getJSONObject(i).getString("userID");
				} catch (JSONException e) {
					e.printStackTrace();
				}
  				break;
  			}
  		}
    	
    	updateServer.addParam("complaintID", complaintID);
    	updateServer.addParam("userID", userID);
    	updateServer.addParam("shouldResolve", "0");

    	// send this JSON to server
    	TalkToServer.getInstance().send(updateServer.toString(), msg -> 
    	{ 	    		});    
    	
    	complainsList.getItems().remove(complainsList.getSelectionModel().getSelectedIndex());
    }

    @FXML
    void refundClicked(ActionEvent event) {
    	if(complainsList.getSelectionModel().getSelectedIndex() == -1)
    		return;
    	
    	if(refundAmount.getText() == "")
    		return;
    	
    	Params updateServer = Params.getEmptyInstance();
    	updateServer.addParam("action", "handleComplaint");
    	// add money textbox
    	updateServer.addParam("money", refundAmount.getText());
    	
    	String complaintID = "";
    	String userID = "";
    	
    	for (int i = 0; i < complainsArray.length(); i++) {
  			if(complainsList.getSelectionModel().getSelectedIndex() == i) {
  				try {
					complaintID = complainsArray.getJSONObject(i).getString("id");
	  				userID = complainsArray.getJSONObject(i).getString("userID");
				} catch (JSONException e) {
					e.printStackTrace();
				}
  				break;
  			}
  		}
    	
    	updateServer.addParam("complaintID", complaintID);
    	updateServer.addParam("userID", userID);
    	updateServer.addParam("shouldResolve", "1");

    	// send this JSON to server
    	TalkToServer.getInstance().send(updateServer.toString(), msg -> 
    	{ 	    		});    
    	
    	refundAmount.setText("");
    	complainsList.getItems().remove(complainsList.getSelectionModel().getSelectedIndex());
    }

    @FXML
    void selectClicked(ActionEvent event) {
    	Params params2 = Params.getEmptyInstance();
    	params2.addParam("parkingName", parkingList.getSelectionModel().getSelectedItem().toString());
    	params2.addParam("amIaWorker", "0");
    	main.setScene("handleParking.fxml", params2);
    }
    
    @FXML
    void refreshClicked(ActionEvent event) {
	      Platform.runLater(new Runnable() {
	  			@Override
	  			public void run() {

	  				initComplainsTable();

	  			}
	  		});
    }
    
    void initParkingTable() {
    	Params refRequest = Params.getEmptyInstance();
    	refRequest.addParam("action", "requestParkings");
    	
    	parkingList.getItems().clear();
    	
    	// send this JSON to server
    	TalkToServer.getInstance().send(refRequest.toString(), msg -> 
    	{
    		Params res = new Params(msg);

    		if(true || res.getParam("status").equals("OK")){ 
    			Platform.runLater(new Runnable() {
    	  		      @Override public void run() {
    	  	    		final Stage dialog = new Stage();
    	  	    		// code here 
    	  	    		try {
							curList = new JSONArray(res.getParam("array"));
	    	  	    		for (int i = 0; i < curList.length(); i++) {
	    	  	    			parkingList.getItems().add(curList.getJSONObject(i).get("name").toString());
	    	  	    		}
	    	  	    		
						} catch (JSONException e) {
							e.printStackTrace();
						}
    	  	    		
    	  	      	System.out.println("Parking List Loaded");
        	  	      Platform.runLater(new Runnable() {
            	  			@Override
            	  			public void run() {

            	  				initComplainsTable();

            	  			}
            	  		});

    	  		      }
    	  	    });
    		}
    		
    	});
    }
    
    void initComplainsTable() {
    	Params refRequest = Params.getEmptyInstance();
    	refRequest.addParam("action", "returnComplaints");
    	refRequest.addParam("facID", params.getParam("facID"));
    	
    	complainsList.getItems().clear();
    	
    	// send this JSON to server
    	TalkToServer.getInstance().send(refRequest.toString(), msg -> 
    	{
    		Params res = new Params(msg);
    		
    		if(true || res.getParam("status").equals("OK")){ 
    			Platform.runLater(new Runnable() {
    	  		      @Override public void run() {
    	  	    		final Stage dialog = new Stage();
    	  	    		// code here 
    	  	    		try {
    	  	    			complainsArray = new JSONArray(res.getParam("array"));
	    	  	    		for (int i = 0; i < complainsArray.length(); i++) {
	    	  	    			java.util.Date time=new java.util.Date(
	    	  	    					Long.valueOf(complainsArray.getJSONObject(i).get("dateTime").toString()).longValue()*1000);
	    	  	    			String s = "user ID: "+complainsArray.getJSONObject(i).get("userID").toString()+ "  ("  +time+")";
	    	  	    			complainsList.getItems().add(s);
	    	  	    		}
	    	  	    		
						} catch (JSONException e) {
							e.printStackTrace();
						}
    	  	    		
    	  	      	System.out.println("Complains List Loaded");
    	  		      }
    	  	    });
    		}
    		
    	});
    }

    @FXML
    void initialize() {
    	assert complainsList != null : "fx:id=\"complainsList\" was not injected: check your FXML file 'ServiceEmployeeMain.fxml'.";
        assert deleteButton != null : "fx:id=\"deleteButton\" was not injected: check your FXML file 'ServiceEmployeeMain.fxml'.";
        assert parkingList != null : "fx:id=\"parkingList\" was not injected: check your FXML file 'ServiceEmployeeMain.fxml'.";
        assert refreshButton != null : "fx:id=\"refreshButton\" was not injected: check your FXML file 'ServiceEmployeeMain.fxml'.";
        assert refundAmount != null : "fx:id=\"refundAmount\" was not injected: check your FXML file 'ServiceEmployeeMain.fxml'.";
        assert refundButton != null : "fx:id=\"refundButton\" was not injected: check your FXML file 'ServiceEmployeeMain.fxml'.";
        assert selectParkingButton != null : "fx:id=\"selectParkingButton\" was not injected: check your FXML file 'ServiceEmployeeMain.fxml'.";
    }
	
	@Override
	public void init(ApplicationMain main, Params params){
		this.main = main;
		this.params = params;
		
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				initParkingTable();
			}
		});

		
		complainsList.setOnMousePressed(new EventHandler<MouseEvent>() {
		    @Override 
		    public void handle(MouseEvent event) {
		        if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
		            if(complainsList.getSelectionModel().getSelectedItem() == null)
		            	return;
		            
	                // create pop-up with information about complain
		            Platform.runLater(new Runnable() {
	    	  		      @Override public void run() {
	    	  	    		 final Stage dialog = new Stage();
	    	  	    		 String textCom = "";
	    	  	    		 for (int i = 0; i < complainsArray.length(); i++) {
	    	  	    			if(complainsList.getSelectionModel().getSelectedIndex() == i) {
	    	  	    				try {
	    	  	  	  				textCom = complainsArray.getJSONObject(i).getString("text");
	    	  	  				} catch (JSONException e) {
	    	  	  					e.printStackTrace();
	    	  	  				}
	    	  	    				break;
	    	  	    			}
	    	  	    		 }

	    	  	             dialog.initModality(Modality.APPLICATION_MODAL);
	    	  	             dialog.initOwner(main.primaryStage);
	    	  	             VBox dialogVbox = new VBox(20);
	    	  	             dialogVbox.getChildren().add(new Text(textCom));
	    	  	             Scene dialogScene = new Scene(dialogVbox, 300, 200);
	    	  	             dialog.setScene(dialogScene);
	    	  	             dialog.show();
	    	  	             System.out.println("showed dialog");
	    	  		      }
		  		    });
	                
		        }
		    }
		});
	}

}
