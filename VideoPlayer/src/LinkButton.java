import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LinkButton extends JButton{
    String pathVideo; // path to the target video
    int frameBegin; //frame# of target video
    int frameEnd;
    boolean isRemoved = false;

    LinkButton(String path, int fBegin, int fEnd){
        pathVideo = path;
        frameBegin = fBegin;
        frameEnd = fEnd;
        isRemoved = false;
    }


}
