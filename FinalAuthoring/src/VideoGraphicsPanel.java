import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class VideoGraphicsPanel extends JPanel {

    // Display image
    private String videoPathRoot;
    private PictureLabel imgLabel;
    // Slider
    private JLabel statusLabel;
    private JSlider frameSlider;
    int frameNum; //current frame
    // Video name
    private JLabel nameLabel;
    // Buttons;
    JPanel toolPanel;
    JButton selectFrameBttn;
    JButton locateStartBttn;
    JButton locateEndBttn;
    int frameStart = -1;
    int frameEnd = -1;
    JTextField text1;
    JTextField text2;

    private GridBagLayout gLayout;
    private GridBagConstraints cName;
    private GridBagConstraints cImg;
    private GridBagConstraints cSldr; //layout detail for slider
    private GridBagConstraints cLbl;
    private GridBagConstraints cBttn;

    VideoGraphicsPanel(){
        frameNum = 1;
        videoPathRoot=null;
        // videoPathRoot = "/Users/tianyizhao/Desktop/USC/USCOne/USCOne";
        init(400,400);
    }


    private void init(int width, int height){
        gLayout = new GridBagLayout();
        this.setLayout(gLayout);
        this.setSize(new Dimension(width,height));


        cBttn = new GridBagConstraints(0,0,2,1,1,0.125,
                GridBagConstraints.WEST,GridBagConstraints.NONE,
                new Insets(1,1,1,1),0,0);
        this.add(createDurationPanel(),cBttn);

        cName = new GridBagConstraints(0,1,2,1,1,0.125,
                GridBagConstraints.CENTER,GridBagConstraints.NONE,
                new Insets(10,1,10,1),0,0);
        nameLabel = new JLabel("Please upload video");
        nameLabel.setFont(new Font("Arial",Font.BOLD,14));
        this.add(nameLabel,cName);

        cImg = new GridBagConstraints(0,2,2,1,1,0.625,
                GridBagConstraints.CENTER,GridBagConstraints.NONE,
                new Insets(1,1,1,1),0,0);
        imgLabel = new PictureLabel(this);
        this.add(imgLabel,cImg);

        cLbl = new GridBagConstraints(1,3,1,1,0.135,0.125,
                GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,
                new Insets(1,1,1,1),0,0);
        statusLabel = new JLabel("#"+normalizeNum(frameNum),JLabel.CENTER);
        this.add(statusLabel,cLbl);

        cSldr = new GridBagConstraints(0,3,1,1,0.875,0.125,
                GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,
                new Insets(1,1,1,1),0,0);
        frameSlider = new JSlider(JSlider.HORIZONTAL,1,9000,frameNum);
        frameSlider.addChangeListener(new VideoGraphicsPanel.SliderListener());
        this.add(frameSlider,cSldr);


        this.setVisible(true);
    }

    private JPanel createDurationPanel(){
        JPanel toolPanel = new JPanel();
        selectFrameBttn  = new JButton("SET");
        locateStartBttn = new JButton("From");
        locateEndBttn = new JButton("To");
        text1 = new JTextField("",4);
        text2 = new JTextField("",4);
        text1.setDocument(new NumberField());
        text2.setDocument(new NumberField());

        selectFrameBttn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(text1.getText().equals("")  || text2.getText().equals("")
                || text1.getText() == null || text2.getText() == null){
                    frameStart = 1;
                    frameEnd = 9000;
                    setDurationDisplay(frameStart,frameEnd);
                    setSliderFrame(frameStart);
                    return;
                }
                System.out.println(text1.getText());
                System.out.println(text2.getText());

                try{
                    int num1 = Integer.parseInt(text1.getText());
                    int num2 = Integer.parseInt(text2.getText());
                    if(num1<1 || num1 >= 9000 || num2<=1 || num2>9000 ){
                        frameStart = 1;
                        frameEnd = 9000;
                        setDurationDisplay(frameStart,frameEnd);
                        setSliderFrame(frameStart);
                        return;
                    }else if(num1>=num2){
                        num2 = num1 +1;
                    }
                    setDurationDisplay(num1,num2);
                    setSliderFrame(num1);
                }catch (Exception err){
                    System.out.println("t1 holds invalid num");
                    setDurationDisplay(frameStart,frameEnd);
                    setSliderFrame(frameStart);
                }

            }
        });

        locateStartBttn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(selectFrameBttn.isEnabled()){
                    if(frameStart>=1 && frameStart<9000){
                        setSliderFrame(frameStart);
                        displayDuration(frameStart,frameEnd);
                    }
                }else{
                    try{
                        int num1 = Integer.parseInt(text1.getText());
                        int num2 = Integer.parseInt(text2.getText());
                        if(num1>=1 && num1<9000){
                            setSliderFrame(num1);
                        }
                    }catch (Exception err){
                        return;
                    }

                }

            }
        });
        locateEndBttn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(selectFrameBttn.isEnabled()){
                    if(frameEnd>1 && frameEnd<=9000){
                        setSliderFrame(frameEnd);
                        displayDuration(frameStart,frameEnd);
                    }
                }else{
                    try{
                        int num2 = Integer.parseInt(text2.getText());
                        if(num2>1 && num2<=9000){
                            setSliderFrame(num2);
                        }
                    }catch (Exception err){
                        return;
                    }

                }
            }
        });


        selectFrameBttn.setEnabled(false);
        locateStartBttn.setEnabled(false);
        locateEndBttn.setEnabled(false);
        text1.setEnabled(false);
        text2.setEnabled(false);
        //add buttons
        toolPanel.add(locateStartBttn);
        toolPanel.add(text1);
        toolPanel.add(locateEndBttn);
        toolPanel.add(text2);
        toolPanel.add(selectFrameBttn);


        return toolPanel;
    }

    public void setEditMode(boolean val){
        selectFrameBttn.setEnabled(val);
        text1.setEnabled(val);
        text2.setEnabled(val);

    }

    public void setDurationDisplay(int start, int end){
        if(start<1 || end <=start || end>9000 || start >= 9000){
            frameStart = -1;
            frameEnd = -1;
        }else{
            frameStart = start;
            frameEnd = end;
        }
        displayDuration(start,end);

    }

    public void displayDuration(int start, int end){
        if(start<1 || end <=start || end>9000 || start >= 9000){
            text1.setText("");
            text2.setText("");
            locateStartBttn.setEnabled(false);
            locateEndBttn.setEnabled(false);
        }else{
            text1.setText(Integer.toString(start));
            text2.setText(Integer.toString(end));
            locateStartBttn.setEnabled(true);
            locateEndBttn.setEnabled(true);
        }
        repaint();
    }



    public int getFrameStart(){
        return frameStart;
    }
    public int getFrameEnd(){
        return frameEnd;
    }


    public boolean setVideoPathRoot(String path_in){
        if(path_in == null) return false;
        if(path_in.equals( videoPathRoot)) return true;
        BufferedImage img= ImageReader.readImageRGB(352,288,path_in+normalizeNum(frameNum)+".rgb");
        if(img!=null){
            // System.out.println("Slider:img not null");
            videoPathRoot = path_in;
            nameLabel.setText(videoPathRoot);
            imgLabel.setImage(img);
            imgLabel.setFrame(frameNum);
            return true;
        }
        return false;
    }

    public boolean setSliderFrame(int num){
        if(1<=num && num <=9000){
            frameSlider.setValue(num);
            return true;
        }
        return false;
    }

    public PictureLabel getImgLabel(){
        return imgLabel;
    }

    public int getFrame(){
        return frameNum;
    }

    public String getVideoPathRoot() {
        return videoPathRoot;
    }

    private String normalizeNum(int num_in){
        String ans ="";
        int divisor = 1000;
        while(divisor>=1){
            if(num_in/divisor==0){
                ans+="0";
            }else{
                ans+=Integer.toString(num_in);
                break;
            }
            divisor/=10;
        }
        return ans;
    }


    class SliderListener implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            // System.out.println("Slider change captured");
            JSlider source = (JSlider) e.getSource();
            frameNum = source.getValue();
            statusLabel.setText("#" + normalizeNum(frameNum));
            if ((!source.getValueIsAdjusting()) && videoPathRoot != null) {
                // System.out.println("Slider will update img");

                BufferedImage img = ImageReader.readImageRGB(352, 288, videoPathRoot + normalizeNum(frameNum) + ".rgb");
                if (img != null) {
                    // System.out.println("Slider:img not null");
                    imgLabel.setImage(img);
                    imgLabel.setFrame(frameNum);
                }

            }
        }
    }

    class NumberField extends PlainDocument{
        @Override
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            if(str == null || str.length()+this.getLength()>4){
                return;
            }
            if(str.equals("")){
                super.insertString(offs, str, a);
                return;
            }

            boolean isNumber;
            try {
                Integer.parseInt(str);
                isNumber = true;
            }catch (Exception err){
                isNumber = false;
            }
            if(!isNumber) return;

            try {

                String target = getText(0, offs) + str + getText(offs,this.getLength()- offs);
                int num = Integer.parseInt(target);
                if (num < 1) {
                    str = "1";
                    this.replace(0,this.getLength(),str,a);
                } else if (num > 9000){
                    str = "9000";
                    this.replace(0,this.getLength(),str,a);
                }else {
                    super.insertString(offs, str, a);
                }

            }catch (Exception err){
                this.replace(0,this.getLength(),"",a);
                return;
            }
        }
    }
}
