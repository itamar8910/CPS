package application;

import common.ControllerIF;
import common.Params;
import common.TalkToServer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;

public class ClientOneTimeOrderController implements ControllerIF{

	private ApplicationMain main;
	private Params params;

    @FXML
    private TextField tfID;

    @FXML
    private TextField tfVehicleID;

    @FXML
    private TextField tfParkingLot;

    @FXML
    private TextField tfEnterDate;

    @FXML
    private Button bSubmit;

    @FXML
    private TextField tfEnterTime;

    @FXML
    private TextField tfLeaveDate;

    @FXML
    private TextField tfLeaveTime;

    @FXML
    private TextField tfEmail;

    @FXML
    void bSumbitClick(ActionEvent event) {
    	Params orderParams = Params.getEmptyInstance();
    	orderParams.addParam("action", "clientOneTimeOrder");
    	orderParams.addParam("ID", tfID.getText());
    	orderParams.addParam("vehicleID", tfVehicleID.getText());
    	orderParams.addParam("parkingLot", tfParkingLot.getText());
    	orderParams.addParam("enterDate", tfEnterDate.getText());
    	orderParams.addParam("enterTime", tfEnterTime.getText());
    	orderParams.addParam("leaveDate", tfLeaveDate.getText());
    	orderParams.addParam("leaveTime", tfLeaveTime.getText());
    	orderParams.addParam("email", tfEmail.getText());
    	System.out.println("sending request to server");
    	TalkToServer.getInstance().send(orderParams.toString(), msg -> {
    		System.out.println("ClientOneTimeController got msg from server:" + msg);

    		Params respParams = new Params(msg);

    		if(true || respParams.getParam("status").equals("OK")){ //TODO: remove true ||, this is for dbg
    			Platform.runLater(new Runnable() {
    	  		      @Override public void run() {
    	  	    		 final Stage dialog = new Stage();

    	  	             dialog.initModality(Modality.APPLICATION_MODAL);
    	  	             dialog.initOwner(main.primaryStage);
    	  	             VBox dialogVbox = new VBox(20);
    	  	             dialogVbox.getChildren().add(new Text("Please pay X money"));
    	  	             Scene dialogScene = new Scene(dialogVbox, 300, 200);
    	  	             dialog.setScene(dialogScene);
    	  	             dialog.show();
    	  	             System.out.println("showed dialog");
    	  		      }
	  		    });
    		}
    	});
    }

    @Override
 	public void init(ApplicationMain main, Params params) {
 		this.main = main;
 		this.params = params;
 	}


}
