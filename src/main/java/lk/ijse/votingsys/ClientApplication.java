package lk.ijse.votingsys;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lk.ijse.votingsys.controller.ClientController;

import java.util.Objects;

public class ClientApplication extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(ClientApplication.class.getResource("/view/Client.fxml")));
        Scene clientScene = new Scene(root);
        stage.setScene(clientScene);
        stage.setTitle("Client");
        stage.show();
    }
}
