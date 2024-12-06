package controller;

import service.AuthService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class AuthController {
    private Socket socket;
    private AuthService authService;

    public AuthController(Socket socket) {
        this.socket = socket;
        this.authService = new AuthService();
    }

    public void handle() throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());

        String request = in.readLine();
        if (request.startsWith("REGISTER")) {
            String[] parts = request.split(" ");
            authService.register(parts[1], parts[2]); 
            out.write("REGISTER_SUCCESS\n");
        } else if (request.startsWith("LOGIN")) {
            String[] parts = request.split(" ");
            if (authService.login(parts[1], parts[2])) {
                out.write("LOGIN_SUCCESS\n");
            } else {
                out.write("LOGIN_FAIL\n");
            }
        }
        out.flush();
    }
}
