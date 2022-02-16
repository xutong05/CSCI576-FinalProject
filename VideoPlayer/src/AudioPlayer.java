import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

public class AudioPlayer {

    Clip clip;
    AudioInputStream audioInputStream;
    private boolean isSetUp = false;
    private boolean isPlaying = false;

    public boolean setUpNew(String path){
        if(path == null) return false;
        try{
            File audioFile = new File(path);
            audioInputStream = AudioSystem.getAudioInputStream(audioFile);
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            isSetUp = true;
            isPlaying = false;
            return true;

        }catch (Exception e){
            System.out.println("Error Loading Audio");
            isSetUp = false;
            isPlaying = false;
            return false;
        }
    }

    public void start(long microSecond){
        if(isSetUp && !isPlaying){
            clip.setMicrosecondPosition(microSecond);
            isPlaying = true;
            clip.start();
        }
    }

    public long pause(){
        long ans = -1;
        if(isPlaying){
            clip.stop();
        }
        if(isSetUp){
            ans = clip.getMicrosecondPosition();
        }

        isPlaying = false;
        return ans;
    }

    public long stop(){
        long ans = -1;
        if(isPlaying){
            clip.stop();
        }
        if (isSetUp){
            ans = clip.getMicrosecondPosition();
            clip.close();
        }
        isSetUp=false;
        isPlaying = false;
        return ans;
    }
}
