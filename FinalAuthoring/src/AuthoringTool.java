import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class AuthoringTool {
    //lbIm1.repaint();
    //imgOne = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

    public static void main(String[] args) {
        VideoEditor videoEditor = new VideoEditor();
        videoEditor.drawUI();

    }


}
