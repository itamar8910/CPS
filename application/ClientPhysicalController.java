package application;

import common.ControllerIF;
import common.Params;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class ClientPhysicalController implements ControllerIF{


    @FXML
    private Button bOrder;

    @FXML
    private Button bLeave;

    @FXML
    private Button bEnter;

    private ApplicationMain main;
    private Params params;


    @FXML
    void bEnterClick(ActionEvent event) {
    	System.out.println("bEnterClick");
    	main.setScene("clientPhysicalEnterView.fxml", params);

    }

    @FXML
    void bLeaveClick(ActionEvent event) {
    	System.out.println("bLeaveClick");
    	main.setScene("clientPhysicalLeaveView.fxml", params);

    }

    @FXML
    void bOrderClick(ActionEvent event) {
    	System.out.println("bLeaveClick");
    	main.setScene("clientPhysicalOrderView.fxml", params);

    }
    
    @FXML
    void bBackClick(){
    	main.setScene("ClientEnterView.fxml", Params.getEmptyInstance());
    }

	@Override
	public void init(ApplicationMain main, Params params) {
		this.main = main;
		this.params = params;
	}

}
