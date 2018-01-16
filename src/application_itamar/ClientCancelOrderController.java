package application_itamar;

import common.ControllerIF;
import common.Params;
import common.TalkToServer;
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

public class ClientCancelOrderController implements ControllerIF{

    @FXML
    private TextField tfVehicleID;

    @FXML
    private Button bSubmit;

    private ApplicationMain main;
    private Params params;

    @FXML
    void bSubmitClick(ActionEvent event) {
    	Params orderParams = Params.getEmptyInstance();

    	orderParams.addParam("action", "clientCancelOrder");
    	orderParams.addParam("vehicleID", tfVehicleID.getText());

    	System.out.println("sending request to server");
    	TalkToServer.getInstance().send(orderParams.toString(), msg -> {
    		System.out.println("ClientCancelOrderController got msg from server:" + msg);
    		Params respParams = new Params(msg);
    		if(respParams.getParam("status").equals("OK")){

	    		Platform.runLater(new Runnable() {
		  		      @Override public void run() {
		  	    		 final Stage dialog = new Stage();

		  	             dialog.initModality(Modality.APPLICATION_MODAL);
		  	             dialog.initOwner(main.primaryStage);
		  	             VBox dialogVbox = new VBox(20);
		  	             dialogVbox.getChildren().add(new Text("Your order was canceled, you will get " + respParams.getParam("returnAmount") + "$ back"));
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
