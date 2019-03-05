package atj;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class FileChat {

	private String fileDestination;

	FileChat() {
		fileDestination = null;
	}

	private void saveFile() {
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Destination ");

		Stage stage = new Stage();
		File path = chooser.showSaveDialog(stage);

		if (path != null) {
			fileDestination = path.toString();
		}
	}

	public void downloadFile(ByteBuffer buf) {
		try {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setHeaderText("Do you want to download?");

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK)
				saveFile();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (fileDestination != null) {
			File file = new File(fileDestination);
			try {
				FileOutputStream oStream = new FileOutputStream(file, false);
				FileChannel channel = oStream.getChannel();

				channel.write(buf);
				oStream.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}