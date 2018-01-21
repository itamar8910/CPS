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
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;


public class facilityWorkerMainController implements ControllerIF{

    private ApplicationMain main;
    private Params params;
    private boolean parkingFull;
    private boolean parkingDisabled;
    private String parkingName;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button facilityDownButton;

    @FXML
    private Button facilityFullButton;

    @FXML
    private Button initFacilityButton;

    @FXML
    private Button updateFacilityButton;

    @FXML
    private Button logoutButton;

    @FXML
    void logoutPressed(ActionEvent event) {
    	main.setScene("WorkerMainScene.fxml", params);
    }

    @FXML
    void facilityDownClicked(ActionEvent event) {
    	Params updateServer = Params.getEmptyInstance();
    	updateServer.addParam("action", "changeParkingDisabled");
    	updateServer.addParam("facID", params.getParam("facID"));

    	if(this.parkingDisabled) {
    		updateServer.addParam("isDisabled", "0");
    		facilityDownButton.setText("Facility Disabled");
    		this.parkingDisabled = false;
    		updateFacilityButton.setDisable(false);
    	}
    	else {
    		updateServer.addParam("isDisabled", "1");
    		facilityDownButton.setText("Facility Not Disabled");
    		updateFacilityButton.setDisable(true);
    		this.parkingDisabled = true;
    	}

    	// send this JSON to server
    	TalkToServer.getInstance().send(updateServer.toString(), msg ->
    	{ 	    		});
    }

    @FXML
    void facilityFullClicked(ActionEvent event) {
    	Params updateServer = Params.getEmptyInstance();
    	updateServer.addParam("action", "changeParkingFull");
    	updateServer.addParam("facID", params.getParam("facID"));

    	if(this.parkingFull) {
    		updateServer.addParam("isFull", "0");
    		facilityFullButton.setText("Facility Full");
    		updateFacilityButton.setDisable(false);
    		this.parkingFull = false;
    	}
    	else {
    		updateServer.addParam("isFull", "1");
    		facilityFullButton.setText("Facility Not Full");
    		updateFacilityButton.setDisable(true);
    		this.parkingFull = true;
    	}

    	// send this JSON to server
    	TalkToServer.getInstance().send(updateServer.toString(), msg ->
    	{ 	    		});
    }

    @FXML
    void initFacilityClicked(ActionEvent event) {
    	Params updateServer = Params.getEmptyInstance();
    	updateServer.addParam("action", "initParkingFacility");
    	updateServer.addParam("facID", params.getParam("facID"));

    	// send this JSON to server
//    	TalkToServer.getInstance().send(updateServer.toString(), msg ->
//    	{ 	    		});
    }

    @FXML
    void updateFacilityClicked(ActionEvent event) {
    	params.addParam("amIaWorker", "1");
    	params.addParam("parkingName", parkingName);
    	main.setScene("handleParking.fxml", params);
    }

    @FXML
    void initialize() {
        assert facilityDownButton != null : "fx:id=\"facilityDownButton\" was not injected: check your FXML file 'FacilityWorkerMain.fxml'.";
        assert facilityFullButton != null : "fx:id=\"facilityFullButton\" was not injected: check your FXML file 'FacilityWorkerMain.fxml'.";
        assert initFacilityButton != null : "fx:id=\"initFacilityButton\" was not injected: check your FXML file 'FacilityWorkerMain.fxml'.";
        assert updateFacilityButton != null : "fx:id=\"updateFacilityButton\" was not injected: check your FXML file 'FacilityWorkerMain.fxml'.";
        assert logoutButton != null : "fx:id=\"logoutButton\" was not injected: check your FXML file 'FacilityWorkerMain.fxml'.";
        }

	@Override
	public void init(ApplicationMain main, Params params) {
		this.main = main;
		this.params = params;

		// init parking status
    	Params updateServer = Params.getEmptyInstance();
    	updateServer.addParam("action", "requestParkingStatusData");
    	updateServer.addParam("facID", params.getParam("facID"));

    	TalkToServer.getInstance().send(updateServer.toString(), msg ->
    	{
    		Params res = new Params(msg);
    		if(true){
    			Platform.runLater(new Runnable() {

					@Override public void run() {
    	  	    		final Stage dialog = new Stage();
    	  	    		if(res.getParam("isFull").equals("1")) {
							parkingFull = true;
							facilityFullButton.setText("Facility Not Full");
							}
						if(res.getParam("isFull").equals("0")) {
							parkingFull = false;
							facilityFullButton.setText("Facility Full");
							}
						if(res.getParam("isDisabled").toString().equals("1")) {
							parkingDisabled = true;
							facilityDownButton.setText("Facility Not Disabled");
							}
						if(res.getParam("isDisabled").equals("0")) {
							parkingDisabled = false;
							facilityDownButton.setText("Facility Disabled");
							}
    	  	    		parkingName = res.getParam("name");
    	  		      }
    	  	    });
    		}

    	});
	}
}
