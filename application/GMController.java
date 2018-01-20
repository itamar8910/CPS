package application;

import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

import org.json.JSONArray;
import org.json.JSONException;

import common.ControllerIF;
import common.Params;
import common.TalkToServer;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class GMController implements ControllerIF{
	
    private ApplicationMain main;
    private Params params;
    private JSONArray curList;
    private String myFacID;
    private int c = 0;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button acceptButton;

    @FXML
    private Button backButton;

    @FXML
    private Button refreshButton;

    @FXML
    private Button rejectButton;

    @FXML
    private ListView<String> tableReq;
    
    @FXML
    void backPressed(ActionEvent event) {
    	main.setScene("bigManagerOptions.fxml", params);
    }

    @FXML
    void acceptClicked(ActionEvent event) throws JSONException { 
    	if(tableReq.getSelectionModel().getSelectedIndex() == -1) {
    		Platform.runLater(new Runnable() {
	  		      @Override public void run() {
	  	    		 final Stage dialog = new Stage();
	  	             dialog.initModality(Modality.APPLICATION_MODAL);
	  	             dialog.initOwner(main.primaryStage);
	  	             VBox dialogVbox = new VBox(20);
	  	             dialogVbox.getChildren().add(new Text("Please Select Request"));
	  	             Scene dialogScene = new Scene(dialogVbox, 300, 200);
	  	             dialog.setScene(dialogScene);
	  	             dialog.show();
	  	             System.out.println("showed dialog");
	  		      }
		    	});
    		return;
    	}
    	
    	System.out.println("accept clicked");
    	
        String newPrice = "";
        String facID = "";
        String type = "";
        
    	Params updateServer = Params.getEmptyInstance();
    	updateServer.addParam("action", "finishChangePrice");
  		for (int i = 0; i < curList.length(); i++) {
  			if(tableReq.getSelectionModel().getSelectedIndex() == i) {
  				newPrice = curList.getJSONObject(i).getString("priceChangeRequest");
  				facID = curList.getJSONObject(i).getString("facID");
  				type = curList.getJSONObject(i).getString("type");
  				break;
  			}
  		}
    	updateServer.addParam("facID", facID);
    	updateServer.addParam("approve", "1");
    	updateServer.addParam("updatedPrice", newPrice);
    	updateServer.addParam("type", type);

    	// send this JSON to server
    	TalkToServer.getInstance().send(updateServer.toString(), msg -> 
    	{ 	    		});
    	
    	tableReq.getItems().remove(tableReq.getSelectionModel().getSelectedIndex());

    }

    @FXML
    void refreshClicked(ActionEvent event) {
    	System.out.println("Pressed refresh");

    	Params refRequest = Params.getEmptyInstance();
    	refRequest.addParam("action", "requestChangePrices");
    	
    	tableReq.getItems().clear();
    	
    	// send this JSON to server
    	TalkToServer.getInstance().send(refRequest.toString(), msg -> 
    	{
    		Params res = new Params(msg);

    		if(true || res.getParam("status").equals("OK")){ //TODO: remove true ||, this is for dbg
    			Platform.runLater(new Runnable() {
    	  		      @Override public void run() {
    	  	    		final Stage dialog = new Stage();
    	  	    		// code here 
    	  	    		String type ="";

    	  	    		try {
							curList = new JSONArray(res.getParam("array"));
	    	  	    		for (int i = 0; i < curList.length(); i++) {
	        	  	    		if(curList.getJSONObject(i).get("type").toString().equals("0"))
	        	  	    			type = "One Time Parking";
	        	  	    		if(curList.getJSONObject(i).get("type").toString().equals("1"))
	        	  	    			type = "Ordered One Time Parking";
	        	  	    		if(curList.getJSONObject(i).get("type").toString().equals("2"))
	        	  	    			type = "Routine Subscription - One Vehicle";
	        	  	    		if(curList.getJSONObject(i).get("type").toString().equals("3"))
	        	  	    			type = "Full Subscription";
	        	  	    		if(curList.getJSONObject(i).get("type").toString().equals("4"))
	        	  	    			type = "Routine Subscription";
	    	  	    			tableReq.getItems().add(curList.getJSONObject(i).get("name").toString()+" new rate for "+type+": "+curList.getJSONObject(i).get("priceChangeRequest").toString());
	    	  	    		}
	    	  	    		
						} catch (JSONException e) {
							e.printStackTrace();
						}
    	  	    		

    	  		      }
    	  	    });
    		}
    		
    	});

    }

    @FXML
    void rejectClicked(ActionEvent event) throws JSONException {   
    	
    	if(tableReq.getSelectionModel().getSelectedIndex() == -1) {
    		Platform.runLater(new Runnable() {
	  		      @Override public void run() {
	  	    		 final Stage dialog = new Stage();
	  	             dialog.initModality(Modality.APPLICATION_MODAL);
	  	             dialog.initOwner(main.primaryStage);
	  	             VBox dialogVbox = new VBox(20);
	  	             dialogVbox.getChildren().add(new Text("Please Select Request"));
	  	             Scene dialogScene = new Scene(dialogVbox, 300, 200);
	  	             dialog.setScene(dialogScene);
	  	             dialog.show();
	  	             System.out.println("showed dialog");
	  		      }
		    	});
    		return;
    	}
    				
    	System.out.println("reject clicked");
    	
        String newPrice = "";
        String facID = "";
        String type = "";
        
    	Params updateServer = Params.getEmptyInstance();
    	updateServer.addParam("action", "finishChangePrice");
  		for (int i = 0; i < curList.length(); i++) {
  			if(tableReq.getSelectionModel().getSelectedIndex() == i) {
  				newPrice = curList.getJSONObject(i).getString("priceChangeRequest");
  				facID = curList.getJSONObject(i).getString("facID");
  				type = curList.getJSONObject(i).getString("type");
  				break;
  			}
  		}
    	updateServer.addParam("facID", facID);
    	updateServer.addParam("approve", "0");
    	updateServer.addParam("updatedPrice", newPrice);
    	updateServer.addParam("type", type);


    	// send this JSON to server
    	TalkToServer.getInstance().send(updateServer.toString(), msg -> 
    	{ 	    		});    
    	tableReq.getItems().remove(tableReq.getSelectionModel().getSelectedIndex());

    }

    @FXML
    void initialize() {
        assert acceptButton != null : "fx:id=\"acceptButton\" was not injected: check your FXML file 'FacilityManager.fxml'.";
        assert refreshButton != null : "fx:id=\"refreshButton\" was not injected: check your FXML file 'FacilityManager.fxml'.";
        assert rejectButton != null : "fx:id=\"rejectButton\" was not injected: check your FXML file 'FacilityManager.fxml'.";
        assert tableReq != null : "fx:id=\"tableReq\" was not injected: check your FXML file 'FacilityManager.fxml'.";
        assert backButton != null : "fx:id=\"backButton\" was not injected: check your FXML file 'FacilityManager.fxml'.";
        refreshClicked(null);
    }
    
	@Override
	public void init(ApplicationMain main, Params params) {
		this.main = main;
		this.params = params;
		this.myFacID = this.params.getParam("facID");
	}

}
