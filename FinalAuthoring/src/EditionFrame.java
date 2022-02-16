import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

public class EditionFrame extends JFrame {

    boolean listIsDisplayed = false;

    VideoGraphicsPanel video1;
    VideoGraphicsPanel video2;

    //Buttons
    JButton editBttn;
    JButton saveBttn;
    JButton exitBttn;
    JButton listBttn;

    JTextField editName;

    //menu
    JMenuItem menuItemSave;
    JMenuItem menuItemPrim;
    JMenuItem menuItemSec;


    EditionFrame(){

        video1=new VideoGraphicsPanel();
        video2 = new VideoGraphicsPanel();
        // pictureLabel = video1.getImgLabel();

        editBttn = new JButton("New");
        saveBttn = new JButton("Save Change");
        exitBttn = new JButton("Exit");
        listBttn = new JButton(new ImageIcon(this.getClass().getResource("/images/list.png")));
        editName = new JTextField("Enter Name", 15);

        menuItemSave = new JMenuItem("Export File");
        menuItemPrim= new JMenuItem("Import Video1");
        menuItemSec = new JMenuItem("Import Video2");;

    }

    public void drawUI(){
        listIsDisplayed = false;

        GridBagLayout fLayout = new GridBagLayout();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(fLayout);

        //draw menu bar
        this.setJMenuBar(createMenuBar());

        //Edition Tools -- Edit/new, Save/add, Exit
        GridBagConstraints cTool = new GridBagConstraints(0,0,1,1,0.95,0.05,
                GridBagConstraints.WEST,GridBagConstraints.NONE,
                new Insets(1,1,1,1),0,0);
        JPanel toolPanel = createToolButtonPanel();
        this.getContentPane().add(toolPanel,cTool);

        GridBagConstraints cListBttn = new GridBagConstraints(1,0,1,1,0.05,0.05,
                GridBagConstraints.EAST,GridBagConstraints.NONE,
                new Insets(1,1,1,1),0,0);
        this.getContentPane().add(listBttn,cListBttn);

        //image display (flow layout)
        GridBagConstraints cVideo = new GridBagConstraints(0,1,2,1,1,0.95,
                GridBagConstraints.CENTER,GridBagConstraints.NONE,
                new Insets(1,1,1,1),0,0);
        JPanel videoPanels = createVideoPanel();
        this.getContentPane().add(videoPanels,cVideo);

        //configure frame
        this.setSize(800,500);
        this.pack();
        this.setVisible(true);
    }

    private JMenuBar createMenuBar(){
        JMenuBar menuBar = new JMenuBar();
        JMenu menuFile = new JMenu("File");

        menuItemSave.setEnabled(false); // enable on successfully importing Video1
        //add listeners
        menuItemPrim.addActionListener(new ImportListener());
        menuItemSec.addActionListener(new ImportListener());

        menuFile.add(menuItemPrim);
        menuFile.add(menuItemSec);
        menuFile.add(menuItemSave);
        menuBar.add(menuFile);
        return menuBar;
    }
    private JPanel createToolButtonPanel(){
        JPanel toolPanel = new JPanel();

        editBttn.setEnabled(false); //activate on successfully import video1
        saveBttn.setEnabled(false);
        exitBttn.setEnabled(false);
        editName.setEnabled(false);
        //add buttons
        toolPanel.add(editBttn);
        toolPanel.add(editName);
        toolPanel.add(saveBttn);
        toolPanel.add(exitBttn);
        // toolPanel.add(listBttn); // view links
        return toolPanel;
    }



    private JPanel createVideoPanel(){
        GridBagLayout vLayout = new GridBagLayout();
        GridBagConstraints cVideo1 = new GridBagConstraints(0,0,1,1,0.5,1,
                GridBagConstraints.CENTER,GridBagConstraints.NONE,
                new Insets(10,1,10,1),0,0);
        GridBagConstraints cVideo2 = new GridBagConstraints(1,0,1,1,0.5,1,
                GridBagConstraints.CENTER,GridBagConstraints.NONE,
                new Insets(10,1,10,1),0,0);
        JPanel videoPanels = new JPanel();
        videoPanels.setLayout(vLayout);
        videoPanels.add(video1, cVideo1);
        videoPanels.add(video2, cVideo2);
        return videoPanels;
    }

    class ImportListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            String path = null;

            //JFileChooser
            //Goal: Obtain filePath
            
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "AVI Video", "avi","rgb");
            JFileChooser j = new JFileChooser(FileSystemView.getFileSystemView().getDefaultDirectory());
            j.setFileSelectionMode(JFileChooser.FILES_ONLY);
            j.setFileFilter(filter);
            int val = j.showOpenDialog(null);

            if (val == JFileChooser.APPROVE_OPTION) {
                // set the label to the path of the selected directory
                path = j.getSelectedFile().getAbsolutePath();
                System.out.println(path);
            }else{
                System.out.println("cancelled");
                return;
            }

            String tokens[];
            String filepath = null;
            try{
                tokens = path.split("\\.");
            }catch (Exception err){
                System.out.println("Parsing error");
                return;
            }

            if(tokens.length==2 && tokens[1].equals("avi")){
                filepath = tokens[0];
            }else if(tokens.length==2 && tokens[1].equals("rgb")){
                filepath = tokens[0];
                filepath = filepath.substring(0,filepath.length()-4);
            }
            System.out.println(filepath);



            //test only
            // String filepath = "/Users/tianyizhao/Desktop/USC/USCOne/USCOne";

            //Do Not Change code below-----------------------------------------------
            boolean isSet = false;
            if(video1.getVideoPathRoot()==null &&
                    e.getActionCommand().equals("Import Video1") && filepath !=null){
                isSet = video1.setVideoPathRoot(filepath);
                if(isSet){
                    menuItemSave.setEnabled(true);
                    menuItemPrim.setEnabled(false);
                }
            }else if(e.getActionCommand().equals("Import Video2") && filepath !=null){
                isSet = video2.setVideoPathRoot(filepath);
            }

            if(video1.getVideoPathRoot()!=null && video2.getVideoPathRoot()!=null){
                editBttn.setEnabled(true);
            }

        }
    }


}
