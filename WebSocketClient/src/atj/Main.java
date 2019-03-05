package atj;

import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

	private void primaryStage_Hiding(WindowEvent e, FXMLLoader fxmlLoader) {
		((WebSocketChatStageControler) fxmlLoader.getController())
				.closeSession(new CloseReason(CloseCodes.NORMAL_CLOSURE, ""));
	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/dialog.fxml"));
			AnchorPane root = fxmlLoader.load();
			Scene scene = new Scene(root);
			primaryStage.setResizable(false);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Chat");
			primaryStage.setOnHiding(e -> primaryStage_Hiding(e, fxmlLoader));
			primaryStage.show();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}