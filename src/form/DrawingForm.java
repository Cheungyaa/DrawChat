package form;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class DrawingForm extends JFrame {
    public DrawingForm() {
        setTitle("Drawing Form");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showSaveDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    System.out.println("File chosen: " + file.getAbsolutePath());
                    // Save the drawing to the chosen file
                }
            }
        });

        add(saveButton, BorderLayout.SOUTH);
        setVisible(true);
    }

    public static void main(String[] args) {
        new DrawingForm();
    }
}
