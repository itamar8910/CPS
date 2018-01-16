package application;

import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import application_itamar.ApplicationMain;
import common.ControllerIF;
import common.Params;
import common.TalkToServer;

import org.json.JSONArray;
import org.json.JSONException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class bigManagerController implements ControllerIF{
	
    private ApplicationMain main;
    private Params params;
    private JSONArray curList;

    @FXML
    private ResourceBundle resources;
    
    @FXML
    private ListView<String> parkingList;
    
    @FXML
    private TextField day;

    @FXML
    private TextField daysBack;

    @FXML
    private TextField month;
    
    @FXML
    private TextField year;

    @FXML
    private URL location;

    @FXML
    private Button activityButton;

    @FXML
    private Button currentStateButton;

    @FXML
    private Button performanceButton;
    
    @FXML
    private Button priceRequests;
    
    long dateToMillis(String dateStr){
		DateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
		Date date;
		try {
			date = format.parse(dateStr);
			return date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return -1;
	}


    @FXML
    void activityClicked(ActionEvent event) throws JSONException {
    	if(parkingList.getSelectionModel().getSelectedIndex() == -1)
    		return;
    	if(day.getText().equals("") || year.getText().equals("")|| month.getText().equals("") || daysBack.getText().equals(""))
    		return;
    	
    	String date = day.getText() + "-"+month.getText() +"-"+ year.getText();
		long unixD = dateToMillis(date);
    	String facID = "";
        for (int i = 0; i < curList.length(); i++) {
  			if(parkingList.getSelectionModel().getSelectedIndex() == i) {
  				facID = curList.getJSONObject(i).getString("facID");
  				break;
  			}
  		}
        
        // send request to server for pdf creator
        Params serverRequest = Params.getEmptyInstance();
    	serverRequest.addParam("action", "returnActivityDataReport");
    	serverRequest.addParam("facID", facID);
    	serverRequest.addParam("startDate", String.valueOf(unixD));
    	serverRequest.addParam("numDays", daysBack.getText().toString());
    	
    	final Params temp = Params.getEmptyInstance();

    	// send this JSON to server
    	TalkToServer.getInstance().send(serverRequest.toString(), msg -> {
    		Params res = new Params(msg);
    		System.out.println(res.toString());
    		if(true || res.getParam("status").equals("OK")){ 
    			Platform.runLater(new Runnable() {
    	  		      @Override public void run() {
    	  	    		final Stage dialog = new Stage();
    	  	    		JSONArray gil;
    	  	    		try {
							gil = new JSONArray(res.getParam("data").toString());
							temp.addParam("name", res.getParam("name").toString());

	    	  	    		PDFstatus pdf = new PDFstatus();
	    	  	 	        pdf.createActivityReport(temp.getParam("name").toString(),gil ,unixD, Integer.parseInt(daysBack.getText().toString()));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
    	  		      }});}});

    	
  		//main.setScene("activityReport.fxml",params);
    }

    @FXML
    void currentStateClicked(ActionEvent event) throws JSONException {
    	if(parkingList.getSelectionModel().getSelectedIndex() == -1)
    		return;
    	
    	String facID = "";
        for (int i = 0; i < curList.length(); i++) {
  			if(parkingList.getSelectionModel().getSelectedIndex() == i) {
  				facID = curList.getJSONObject(i).getString("facID");
  				break;
  			}
  		}
        
        // send request to server for pdf creator
        Params serverRequest = Params.getEmptyInstance();
    	serverRequest.addParam("action", "requestCurrentParkingStatusReport");
    	serverRequest.addParam("facID", facID);
    	
    	final Params temp = Params.getEmptyInstance();

    	// send this JSON to server
    	TalkToServer.getInstance().send(serverRequest.toString(), msg -> {
    		Params res = new Params(msg);
    		System.out.println(res.toString());
    		if(true || res.getParam("status").equals("OK")){ 
    			Platform.runLater(new Runnable() {
    	  		      @Override public void run() {
    	  	    		final Stage dialog = new Stage();
    	  	    		temp.addParam("data", res.getParam("data").toString());
    	  	    		temp.addParam("name", res.getParam("name").toString());
    	  	    		temp.addParam("dimension", res.getParam("dimension").toString());

    	  	    		PDFstatus pdf = new PDFstatus();
    	  	 	        pdf.createCurrentStatusPDF("currentStatus.pdf",temp.getParam("name").toString() ,Integer.parseInt(temp.getParam("dimension").toString()), temp.getParam("data").toString());
    	  	    		
    	  		      }});}});

    	
  		//main.setScene("requestReport.fxml",params);
    }

    @FXML
    void performanceClicked(ActionEvent event) {
    	if(parkingList.getSelectionModel().getSelectedIndex() == -1)
    		return;
    	
    	Params serverRequest = Params.getEmptyInstance();
    	serverRequest.addParam("action", "getSubscriptionStats");
    	serverRequest.addParam("name", parkingList.getSelectionModel().getSelectedItem().toString());
    	
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

  		//main.setScene("PerformanceReport.fxml",params);
    }
    
    @FXML
    void priceReqClicked(ActionEvent event) {
  		main.setScene("FacilityManager.fxml",params);
    }
    
    public void showList() {
    	System.out.println("Loading List");

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
    	  	    		
    	  	      	System.out.println("List Loaded");

    	  		      }
    	  	    });
    		}
    		
    	});

    }
    
    
    @FXML
    void initialize() {
        assert activityButton != null : "fx:id=\"activityButton\" was not injected: check your FXML file 'bigManagerOptions.fxml'.";
        assert currentStateButton != null : "fx:id=\"currentStateButton\" was not injected: check your FXML file 'bigManagerOptions.fxml'.";
        assert performanceButton != null : "fx:id=\"performanceButton\" was not injected: check your FXML file 'bigManagerOptions.fxml'.";
        assert parkingList != null : "fx:id=\"parkingList\" was not injected: check your FXML file 'bigManagerOptions.fxml'.";
        assert day != null : "fx:id=\"day\" was not injected: check your FXML file 'bigManagerOptions.fxml'.";
        assert daysBack != null : "fx:id=\"daysBack\" was not injected: check your FXML file 'bigManagerOptions.fxml'.";
        assert month != null : "fx:id=\"month\" was not injected: check your FXML file 'bigManagerOptions.fxml'.";
        assert year != null : "fx:id=\"year\" was not injected: check your FXML file 'bigManagerOptions.fxml'.";
        assert priceRequests != null : "fx:id=\"priceRequests\" was not injected: check your FXML file 'bigManagerOptions.fxml'.";
		showList();
    }
    
	@Override
	public void init(ApplicationMain main, Params params) {
		this.main = main;
		this.params = params;
	}




}
