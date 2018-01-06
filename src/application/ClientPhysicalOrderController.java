package application;

import common.ControllerIF;
import common.Params;
import common.TalkToServer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

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
    private Button bSubmit;
    private ApplicationMain main;
    private Params params;


    @FXML
    void bSumbitClick(ActionEvent event) {
    	//todo: we need to somehow know in which parking lot we are
    	Params orderParams = Params.getEmptyInstance();
    	orderParams.addParam("action", "ClientPhysicalOrder");
    	orderParams.addParam("ID", tfID.getText());
    	orderParams.addParam("vehicleID", tfVehicleID.getText());
    	orderParams.addParam("leaveTime", tfLeaveTime.getText());
    	orderParams.addParam("email", tfEmail.getText());
    	System.out.println("sending request to server");
    	TalkToServer.getInstance().send(orderParams.toString(), msg -> {
    		System.out.println("ClientPhysicalOrderController got msg from server:" + msg);
    	});
    }

	@Override
	public void init(ApplicationMain main, Params params) {
		this.main = main;
		this.params = params;
	}

}
