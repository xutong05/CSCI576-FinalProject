import javax.swing.*;
import java.util.HashMap;
import java.util.Vector;

public class VideoContext {
    long microSecond;
    int frameS;
    int frameE;
    boolean isplaying = false;
    String path = null;
    Vector<LinkButton> buttons;
    HashMap<Integer, Vector<LinkButton>> linkMap;

    VideoContext(String path_in, int frameS_in,int frameE_in, boolean playing,
                 HashMap<Integer, Vector<LinkButton>> links){
        microSecond = (frameS_in-1)*33333;
        frameS = frameS_in;
        frameE = frameE_in;
        isplaying = playing;
        path = path_in;
        buttons = new Vector<>();
        linkMap = links;
    }
}
