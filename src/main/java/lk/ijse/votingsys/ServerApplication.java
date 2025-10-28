package lk.ijse.votingsys;

import lk.ijse.votingsys.controller.ServerController;

public class ServerApplication {
    public static void main(String[] args) {
        ServerController serverController = new ServerController();
        serverController.startConnection();
    }
}
