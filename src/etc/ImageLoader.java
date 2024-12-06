package etc;

import javax.swing.*;
import java.awt.*;

public class ImageLoader {
    public static JButton loadButtonWithImage(String text, String imagePath) {
        ImageIcon icon = new ImageIcon(imagePath);
        JButton button = new JButton(text, icon);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        return button;
    }
}
