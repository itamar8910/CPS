package application;

import common.ControllerIF;
import common.Params;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class ClientEnterController implements ControllerIF{

    @FXML
    private Button bPhysical;

    @FXML
    private Button bOnlnie;

    private ApplicationMain main;
    private Params params;

    @FXML
    void bOnlnieClick(ActionEvent event) {
    	System.out.println("bOnlnieClick");
    }

    @FXML
    void bPhysicalClick(ActionEvent event) {
    	System.out.println("bPhysicalClick");
    	main.setScene("ClientPhysicalView.fxml", params);

    }

	@Override
	public void init(ApplicationMain main, Params params) {
		this.main = main;
		this.params = params;
	}

}
