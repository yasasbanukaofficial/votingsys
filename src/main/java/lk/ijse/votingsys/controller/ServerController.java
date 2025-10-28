package lk.ijse.votingsys.controller;

import lk.ijse.votingsys.dto.VoteDTO;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerController {
    private final CopyOnWriteArrayList<ObjectOutputStream> clientOutputStreams = new CopyOnWriteArrayList<>();
    private final VoteDTO voteDto = new VoteDTO();

    public void startConnection() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(3000)){
                System.out.println("Server Started");
                System.out.println("Waiting for Client on port: 3000");
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    ObjectOutputStream objectOS = new ObjectOutputStream(clientSocket.getOutputStream());
                    ObjectInputStream objectIS = new ObjectInputStream(clientSocket.getInputStream());
                    System.out.println("Client Connected");

                    clientOutputStreams.add(objectOS);
                    synchronized (voteDto) {
                        objectOS.writeObject(voteDto);
                        objectOS.flush();
                    }

                    new Thread(() -> handleClient(clientSocket, objectIS, objectOS)).start();
                }
            } catch (Exception e) {
                System.out.println("Client Disconnected");
                e.printStackTrace();
            }
        }).start();
    }

    private void handleClient(Socket clientSocket, ObjectInputStream objectIS, ObjectOutputStream objectOS) {
        try {
            while (!clientSocket.isClosed()) {
                VoteDTO receivedDTO = (VoteDTO) objectIS.readObject();
                synchronized (voteDto) {
                    voteDto.addCount(receivedDTO.getOption());
                    broadcastVotes();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            clientOutputStreams.remove(objectOS);
            handleConnections(clientSocket, objectIS, objectOS);
        }
    }

    private void broadcastVotes(){
        for (ObjectOutputStream cos : clientOutputStreams) {
            try {
                cos.reset();
                cos.writeObject(voteDto);
                cos.flush();
            } catch (Exception e) {
                clientOutputStreams.remove(cos);
                e.printStackTrace();
            }
        }
    }

    private void handleConnections(Socket clientSocket, ObjectInputStream objectIS, ObjectOutputStream objectOS) {
        try {
            if (objectIS != null) objectIS.close();
            if (objectOS != null) objectOS.close();
            if (clientSocket != null) clientSocket.close();
        } catch (Exception ioException) {
            ioException.printStackTrace();
        }
    }
}
