package application;
	
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		// constructing our scene
		try {
			
			URL url = getClass().getResource("clientView.fxml");
			
			
			FXMLLoader fxmlLoader = new FXMLLoader(url);     

			AnchorPane pane = (AnchorPane)fxmlLoader.load();          
			ClientController controller = fxmlLoader.<ClientController>getController();
			
			List<String> args = getParameters().getRaw();
			String host = "localhost";
			int port = 5555;
			if(args.size() >= 2) {
				host = args.get(0);
				port = Integer.parseInt(args.get(1));
			}
			controller.initConnection(host, port);
			
			//AnchorPane pane = FXMLLoader.load( url );
			Scene scene = new Scene( pane );
			
			// setting the stage
			primaryStage.setScene( scene );
			primaryStage.setTitle( "Client View" );
			primaryStage.show();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		try {
//			BorderPane root = new BorderPane();
//			Scene scene = new Scene(root,400,400);
//			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
//			primaryStage.setScene(scene);
//			primaryStage.show();
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
