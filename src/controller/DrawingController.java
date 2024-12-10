// src/controller/DrawingController.java

package controller;

import service.DrawingService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class DrawingController {
    private Socket socket;
    private DrawingService drawingService;

    public DrawingController(Socket socket) {
        this.socket = socket;
        this.drawingService = new DrawingService();
    }

    public void handle() throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());

        String request = in.readLine();
        if (request.startsWith("DRAW")) {
            String[] parts = request.split(" ");
            drawingService.saveDrawing(parts[1], parts[2]);
            out.write("DRAW_SAVED\n");
        }
        out.flush();
    }
}
