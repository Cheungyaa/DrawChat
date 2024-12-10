// src/service/DrawingService.java

package service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class DrawingService {
    public void saveDrawing(String username, String imageData) throws IOException {
        File file = new File("drawings/" + username + ".png");
        Files.write(file.toPath(), imageData.getBytes());
    }

    public byte[] loadDrawing(String username) throws IOException {
        File file = new File("drawings/" + username + ".png");
        return Files.readAllBytes(file.toPath());
    }
}
