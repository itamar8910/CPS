package application;

import common.ControllerIF;
import common.Params;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ClientPhysicalEnterController implements ControllerIF{

    @FXML
    private TextField tfID;

    @FXML
    private Button bSubmit;

    private ApplicationMain main;
    private Params params;

    @FXML
    void bSubmitClick(ActionEvent event) {

    }

	@Override
	public void init(ApplicationMain main, Params params) {
		this.main = main;
		this.params = params;
	}

}
