package lk.ijse.votingsys.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import lk.ijse.votingsys.dto.VoteDTO;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientController implements Initializable {
    public TextField usernameTxt;
    public RadioButton rbA;
    public RadioButton rbB;
    public RadioButton rbC;
    public Button submitBtn;
    public PieChart voteChart;
    public Label statuslbl;

    private Socket clientSocket;
    private ObjectInputStream objectIS;
    private ObjectOutputStream objectOS;
    private VoteDTO voteDTO = new VoteDTO();
    private String selectedOption;
    private boolean isConnected = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        rbA.setOnAction(e -> {
            selectedOption = "A";
            rbB.setSelected(false);
            rbC.setSelected(false);
        });
        rbB.setOnAction(e -> {
            selectedOption = "B";
            rbA.setSelected(false);
            rbC.setSelected(false);
        });
        rbC.setOnAction(e -> {
            selectedOption = "C";
            rbA.setSelected(false);
            rbB.setSelected(false);
        });
        connectToServer();
        Platform.runLater(() -> {
            Stage stage = (Stage) statuslbl.getScene().getWindow();
            stage.setOnCloseRequest(e -> {
                handleDisconnection();
            });
        });
    }

    private void connectToServer() {
        new Thread(() -> {
            try {
                clientSocket = new Socket("localhost", 3000);
                objectOS = new ObjectOutputStream(clientSocket.getOutputStream());
                objectIS = new ObjectInputStream(clientSocket.getInputStream());
                Platform.runLater(() -> statuslbl.setText("Connected to Server"));

                isConnected = true;
                this.voteDTO = (VoteDTO) objectIS.readObject();
                displayCount();
                new Thread(() -> handleMessages(clientSocket)).start();
            } catch (Exception e) {
                Platform.runLater(() -> statuslbl.setText("Cannot Connect to Server"));
                isConnected = false;
                e.printStackTrace();
            }
        }).start();
    }

    private void displayCount() {
        Platform.runLater(() -> {
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                    new PieChart.Data("Option A - " + voteDTO.getOptionACount(), voteDTO.getOptionACount()),
                    new PieChart.Data("Option B - " + voteDTO.getOptionBCount(), voteDTO.getOptionBCount()),
                    new PieChart.Data("Option C - " + voteDTO.getOptionCCount(), voteDTO.getOptionCCount())
            );
            voteChart.setData(pieChartData);
        });
    }

    private void handleMessages(Socket clientSocket) {
        try {
            while (isConnected && clientSocket.isConnected()) {
                VoteDTO receivedDTO = (VoteDTO) objectIS.readObject();
                if (voteDTO != null) {
                    this.voteDTO = receivedDTO;
                    displayCount();
                }
            }
        } catch (SocketException se) {
            System.out.println("Client disconnected");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void submitVote(MouseEvent mouseEvent) {
        try {
            if (clientSocket != null && clientSocket.isConnected()) {
                objectOS.writeObject(new VoteDTO(usernameTxt.getText(), selectedOption));
                objectOS.flush();

                rbA.setDisable(true);
                rbB.setDisable(true);
                rbC.setDisable(true);
                submitBtn.setDisable(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleDisconnection() {
        try {
            isConnected = false;
            if (objectIS != null) {
                objectIS.close();
            }
            if (objectOS != null) {
                objectOS.close();
            }
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
            Platform.exit();
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
