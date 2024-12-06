package main;

import config.DatabaseConfig;
import controller.AuthController;
import controller.ChatController;
import controller.DrawingController;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("Server is running...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected.");

                new Thread(() -> {
                    try {
                        new AuthController(clientSocket).handle();
                        new ChatController(clientSocket).handle();
                        new DrawingController(clientSocket).handle();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConfig.close();
        }
    }
}
