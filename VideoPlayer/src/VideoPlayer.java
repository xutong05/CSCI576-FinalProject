import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.Timer;

public class VideoPlayer {
    //UI
    VideoFrame frame;
    AudioPlayer audio;
    VideoContext current = null;
    Timer timer = null;
    Stack<VideoContext> contexts;
    RedirectListener redirectListener;

    VideoPlayer(){
        frame= new VideoFrame();
        audio = new AudioPlayer();
        contexts = new Stack<>();
        redirectListener = new RedirectListener();
        addListeners();
    }
    private void addListeners(){
        frame.importBttn.addActionListener(new ImportListener());
        frame.playBttn.addActionListener(new PlayListener());
        frame.pauseBttn.addActionListener(new PauseListener());
        frame.backBttn.addActionListener(new BackListener());
    }

    private void updateFrame(){

        if(current.frameS>current.frameE){
            audio.stop();
            timer.cancel();
            return;
        }

        int num = current.frameS;
        //check video frame against audio frame
        long timestampA = audio.clip.getMicrosecondPosition();
        long timestampV = (current.frameS-1)*(33333); //0.033333s / frame(change to microsecond)
        if(timestampV - timestampA >= 100000){ //Video faster than Audio 100ms
            return;
        }else if(timestampV - timestampA <= -25000){ //Video slower than Audio25ms - 45ms
            current.frameS = (int)(1.0*timestampA/33333+1);
            if(current.frameS>current.frameE){
                audio.stop();
                timer.cancel();
                return;
            }
        }
        //set visibility of links
        for(int i= num;i<=current.frameS;i++){
            frame.video.enableButton(i);
        }
        //update frame
        BufferedImage img = MyImageReader.readImageRGB(352,288,
                    current.path+normalizeNum(current.frameS)+".rgb");
        frame.video.setIcon(new ImageIcon(img));
        current.frameS+=1;

    }

    private void playVideo(){

        if(current == null || current.isplaying
        || current.frameS<1 || current.frameS>9000) return;
        // sync audio
        current.microSecond = (current.frameS-1)*33333;
        //play
        timer = new Timer();
        current.isplaying=true;
        audio.start(current.microSecond); //audio
        timer.scheduleAtFixedRate(new TimerTask() { //video
            @Override
            public void run() {
                updateFrame();
            }
        },0,33);
    }

    private void pauseVideo(){
        current.microSecond = audio.pause();
        timer.cancel();
        current.isplaying = false;
    }
    private void stopVideo(){
        current.microSecond = audio.stop();
        timer.cancel();
        current.isplaying = false;
    };


    private void nextContext(String filepath,int fS,int fE){
        stopVideo();

        // memorize the active buttons
        current.buttons=frame.video.inactivateButtons();
        current.linkMap=frame.video.getMap();
        //sync
        if(current.frameS>current.frameE) current.frameS = current.frameE;
        current.microSecond = (current.frameS-1)*33333;
        //record
        contexts.push(current);

        // set New
        initializeContext(filepath,fS,fE);
        frame.video.activateButtons(fS);
    }

    private void prevContext(){
        if(contexts.isEmpty()) return;
        stopVideo();
        frame.video.removeButtons();

        current = contexts.pop();
        for (LinkButton bttn : current.buttons){
            bttn.setVisible(true);
        }
        current.buttons.clear();
        initializeContext(current);
    }

    private void initializeContext(String filepath, int fS,int fE){
        if(loadFile_helper(filepath,fS)){
            frame.video.resetMap();
            readMeta(filepath);
            current = new VideoContext(filepath,fS,fE,false,frame.video.getMap());
        }else {
            current = null;
            frame.video.setDefaultImg();
        }
    }

    private void initializeContext(VideoContext context){
        if(context != null && loadFile_helper(context.path,current.frameS)){
            context.isplaying = false;
            current = context;
            frame.video.setMap(current.linkMap);
        }else {
            current = null;
            frame.video.setDefaultImg();
        }
    }

    private boolean loadFile_helper(String filepath, int fS){
        //Try to load img & audio
        BufferedImage tmp= MyImageReader.readImageRGB(352,288,filepath+normalizeNum(fS)+".rgb");
        boolean readyAudio = audio.setUpNew(filepath+".wav");
        if(tmp!=null && readyAudio){ //if successful

            frame.video.setIcon(new ImageIcon(tmp));
            return true;

        }else{
            System.out.println("VideoPlayer 76: Error Loading IMG");
            current = null;
            return false;
        }

    }



    class PlayListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(current.microSecond>=0 && !current.isplaying){
                playVideo();
            }
        }
    }

    class PauseListener implements  ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(current.isplaying){
                pauseVideo();
            }

        }
    }

    class BackListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(current != null){
                prevContext();
            }
        }
    }

    class RedirectListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            LinkButton tmp = (LinkButton) e.getSource();
            nextContext(tmp.pathVideo,tmp.frameBegin,tmp.frameEnd);
        }
    }

    

    class ImportListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

            String path = null;

            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    ".avi or .rgb", "avi","rgb");
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
            try{
                tokens = path.split("\\.");
            }catch (Exception err){
                System.out.println("Parsing error");
                return;
            }
            String filepath = null;
            if(tokens.length==2 && tokens[1].equals("avi")){
                filepath = tokens[0];
            }else if(tokens.length==2 && tokens[1].equals("rgb")){
                filepath = tokens[0];
                filepath = filepath.substring(0,filepath.length()-4);
            }
            if(filepath == null) return;
            System.out.println(filepath);
            
            // String filepath = "/Users/tianyizhao/Desktop/AIFilmOne/AIFilmOne";
            while(!contexts.isEmpty()){
                prevContext();
            }
            frame.video.removeButtons();
            initializeContext(filepath,1,9000);

        }
    }

    public void readMeta(String filepath) {
        String path;
        Rectangle dim;
        int f1Begin;
        int f1End;
        int f2Begin;
        int f2End;
        try{
            File file = new File(filepath+".txt");
            Scanner sc = new Scanner(file);
            if(sc.hasNextLine()){
                System.out.println(sc.nextLine());
            }
            while(sc.hasNextLine()){
                String input = sc.nextLine();
                if(input=="EOF") return;
                String tokens[] = input.split(" ");
                if(tokens.length!=9) return;
                path = tokens[0];
                dim = new Rectangle(Integer.parseInt(tokens[1]),Integer.parseInt(tokens[2]),
                        Integer.parseInt(tokens[3]),Integer.parseInt(tokens[4]));
                f1Begin = Integer.parseInt(tokens[5]);
                f1End = Integer.parseInt(tokens[6]);
                f2Begin = Integer.parseInt(tokens[7]);
                f2End = Integer.parseInt(tokens[8]);
                LinkButton bttn = frame.video.addLink(f1Begin,f1End,dim,path,f2Begin,f2End);
                bttn.addActionListener(redirectListener);
            }

        }catch (FileNotFoundException err){
            System.out.println("FileNotFound");
            return;
        }catch (NumberFormatException err){
            System.out.println("Cannot Convert to Integer");
            return;
        }

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


    public static void main(String[] args) {
        VideoPlayer videoPlayer = new VideoPlayer();

    }
}
