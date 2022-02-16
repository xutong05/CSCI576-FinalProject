import javax.swing.*;
import java.awt.*;

public class VideoFrame extends JFrame {
    VideoLabel video;
    JButton playBttn ;
    JButton pauseBttn ;
    JButton backBttn ;
    JButton importBttn;
    GridBagConstraints cImg;

    VideoFrame(){
        video = new VideoLabel();
         playBttn = new JButton("play");
         pauseBttn = new JButton("pause");
         backBttn = new JButton("back");
         importBttn = new JButton("import");
        cImg= new GridBagConstraints(0,1,1,1,1,1,
                GridBagConstraints.CENTER,GridBagConstraints.NONE,
                new Insets(1,1,1,1),0,0);
        drawUI();
    }

    public void drawUI(){
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GridBagLayout gLayout = new GridBagLayout();
        this.setLayout(gLayout);
        /*
        GridBagConstraints cImg= new GridBagConstraints(0,1,1,1,1,1,
                GridBagConstraints.CENTER,GridBagConstraints.NONE,
                new Insets(1,1,1,1),0,0);

         */
        GridBagConstraints cbttn1= new GridBagConstraints(0,2,1,1,1,1,
                GridBagConstraints.WEST,GridBagConstraints.NONE,
                new Insets(1,1,1,1),0,0);
        GridBagConstraints cbttn2= new GridBagConstraints(0,0,1,1,1,1,
                GridBagConstraints.WEST,GridBagConstraints.NONE,
                new Insets(1,1,1,1),0,0);

        JPanel bttnPanel1 = new JPanel();
        bttnPanel1.add(playBttn);
        bttnPanel1.add(pauseBttn);

        JPanel bttnPanel2 = new JPanel();
        bttnPanel2.add(backBttn);
        bttnPanel2.add(importBttn);

        this.add(video,cImg);
        this.add(bttnPanel1,cbttn1);
        this.add(bttnPanel2,cbttn2);

        this.pack();
        this.setVisible(true);
    }



}
