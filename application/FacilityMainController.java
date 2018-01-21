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
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class FacilityMainController implements ControllerIF{

    private ApplicationMain main;
    private Params params;
    private JSONArray curList;
    private String parkingName;

    @FXML
    private ResourceBundle resources;

    @FXML
    private Text parkingNameText;

    @FXML
    private Button perfReportButton;

    @FXML
    private URL location;

    @FXML
    private Button RateChangeButton;

    @FXML
    private Button complainsReportButton;

    @FXML
    private Button ordersReportButton;

    @FXML
    private Button parkingDownReport;


    @FXML
    void RateChangeClicked(ActionEvent event) {
  		main.setScene("SniffManager.fxml",params);
    }

    @FXML
    void complainsReportClicked(ActionEvent event) {
    	Params serverRequest = Params.getEmptyInstance();
    	serverRequest.addParam("action", "returnCosCompReports");
    	serverRequest.addParam("facID", params.getParam("facID").toString());

    	final Params temp = Params.getEmptyInstance();

    	// send this JSON to server
    	TalkToServer.getInstance().send(serverRequest.toString(), msg -> {
    		Params res = new Params(msg);

    		if(true || res.getParam("status").equals("OK")){
    			Platform.runLater(new Runnable() {
    	  		      @Override public void run() {
    	  	    		final Stage dialog = new Stage();
    	  	    		temp.addParam("data", res.getParam("data").toString());
    	  	    		temp.addParam("name", res.getParam("name").toString());

    	  	    		try {
    	  	 			 PDFstatus pdf = new PDFstatus();
    	  	 	    	 JSONArray gil;
    	  	 			 gil = new JSONArray(temp.getParam("data").toString());
    	  	 	    	 pdf.createComplaintsReport(temp.getParam("name").toString(),gil);
    	  	 		} catch (JSONException e) {
    	  	 			e.printStackTrace();
    	  	 		}

    	  		      }});}});



    }

    @FXML
    void ordersReportClicked(ActionEvent event) {
    	Params serverRequest = Params.getEmptyInstance();
    	serverRequest.addParam("action", "returnOrdersReport");
    	serverRequest.addParam("facID", params.getParam("facID").toString());

    	final Params temp = Params.getEmptyInstance();

    	// send this JSON to server
    	TalkToServer.getInstance().send(serverRequest.toString(), msg -> {
    		Params res = new Params(msg);

    		if(true || res.getParam("status").equals("OK")){
    			Platform.runLater(new Runnable() {
    	  		      @Override public void run() {
    	  	    		final Stage dialog = new Stage();
    	  	    		temp.addParam("data", res.getParam("data").toString());
    	  	    		temp.addParam("name", res.getParam("name").toString());

    	  	    		try {
    	  	 			 PDFstatus pdf = new PDFstatus();
    	  	 	    	 JSONArray gil;
    	  	 			 gil = new JSONArray(temp.getParam("data").toString());
    	  	 	    	 pdf.createOrdersReport(temp.getParam("name").toString(),gil);
    	  	 		} catch (JSONException e) {
    	  	 			e.printStackTrace();
    	  	 		}

    	  		      }});}});
    }

    @FXML
    void parikingReportClicked(ActionEvent event) {
    	Params serverRequest = Params.getEmptyInstance();
    	serverRequest.addParam("action", "returnProbLotsReport");
    	serverRequest.addParam("facID", params.getParam("facID").toString());

    	final Params temp = Params.getEmptyInstance();

    	// send this JSON to server
    	TalkToServer.getInstance().send(serverRequest.toString(), msg -> {
    		Params res = new Params(msg);

    		if(true || res.getParam("status").equals("OK")){
    			Platform.runLater(new Runnable() {
    	  		      @Override public void run() {
    	  	    		final Stage dialog = new Stage();
    	  	    		temp.addParam("data", res.getParam("data").toString());
    	  	    		temp.addParam("name", res.getParam("name").toString());

    	  	    		try {
    	  	 			 PDFstatus pdf = new PDFstatus();
    	  	 	    	 JSONArray gil;
    	  	 			 gil = new JSONArray(temp.getParam("data").toString());
    	  	 	    	 pdf.createDisabledLotsReports(temp.getParam("name").toString(),gil);
    	  	 		} catch (JSONException e) {
    	  	 			e.printStackTrace();
    	  	 		}

    	  		      }});}});
    	}

    @FXML
    void performanceClicked(ActionEvent event) {
    	Params serverRequest = Params.getEmptyInstance();
    	serverRequest.addParam("action", "getSubscriptionStats");
    	serverRequest.addParam("name", parkingName);

    	final Params temp = Params.getEmptyInstance();

    	// send this JSON to server
    	TalkToServer.getInstance().send(serverRequest.toString(), msg -> {
    		Params res = new Params(msg);

    		if(true || res.getParam("status").equals("OK")){
    			Platform.runLater(new Runnable() {
    	  		      @Override public void run() {
    	  	    		final Stage dialog = new Stage();
    	  	    		temp.addParam("monthly", res.getParam("monthly").toString());
    	  	    		temp.addParam("monthlyWithMoreCars", res.getParam("monthlyWithMoreCars").toString());

    	  	 			PDFstatus pdf = new PDFstatus();
       	  	 	    	pdf.createCurrentReport(res.getParam("name").toString(),temp);

    	  		      }});
    			}});
    }

    @FXML
    void initialize() {
        assert RateChangeButton != null : "fx:id=\"RateChangeButton\" was not injected: check your FXML file 'FacilityManagerMain.fxml'.";
        assert complainsReportButton != null : "fx:id=\"complainsReportButton\" was not injected: check your FXML file 'FacilityManagerMain.fxml'.";
        assert ordersReportButton != null : "fx:id=\"ordersReportButton\" was not injected: check your FXML file 'FacilityManagerMain.fxml'.";
        assert parkingDownReport != null : "fx:id=\"parkingDownReport\" was not injected: check your FXML file 'FacilityManagerMain.fxml'.";
        assert parkingNameText != null : "fx:id=\"parkingNameText\" was not injected: check your FXML file 'FacilityManagerMain.fxml'.";
        assert perfReportButton != null : "fx:id=\"perfReportButton\" was not injected: check your FXML file 'FacilityManagerMain.fxml'.";
    }

	@Override
	public void init(ApplicationMain main, Params params) {
		this.main = main;
		this.params = params;

		Params serverRequest = Params.getEmptyInstance();
    	serverRequest.addParam("action", "requestParkingStatusData");
    	serverRequest.addParam("facID", this.params.getParam("facID").toString());
    	// send this JSON to server
    	TalkToServer.getInstance().send(serverRequest.toString(), msg -> {
    		Params res = new Params(msg);
    		parkingName = res.getParam("name").toString();
        	parkingNameText.setText(parkingName);
    	});

	}



}