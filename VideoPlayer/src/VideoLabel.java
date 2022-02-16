import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class VideoLabel extends JLabel {

    private HashMap<Integer, Vector<LinkButton>> links; //frame#, linkInfo+Button
    private ImageIcon img;

    VideoLabel(){
        links = new HashMap<>();
        img = new ImageIcon(this.getClass().getResource("/images/open.png"));
        this.setIcon(img);
    }

    public void setDefaultImg(){
        this.setIcon(img);
    }

    public LinkButton addLink(int f1Begin, int f1End, Rectangle dim, String path, int f2Begin,int f2End){
        LinkButton button = new LinkButton(path,f2Begin,f2End);
        button.setBounds(dim);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createLineBorder(Color.RED, 3));
        button.setVisible(false);
        this.add(button);

        if(!links.containsKey(f1Begin)){
            links.put(f1Begin,new Vector<>());
        }
        links.get(f1Begin).add(button);


        if(!links.containsKey(f1End)){
            links.put(f1End,new Vector<>());
        }
        links.get(f1End).add(button);

        return button;
    }

    public void enableButton(int f){
        if(links.containsKey(f)){
            Vector<LinkButton> buttons = links.get(f);
            for(LinkButton button :buttons){
                boolean isVisible = button.isVisible();
                System.out.println("before" + isVisible);
                button.setVisible(!isVisible);
                System.out.println("after" + button.isVisible());
            }
        }
    }

    public Vector<LinkButton> inactivateButtons(){
        Vector<LinkButton> ans = new Vector<>();
        for(Map.Entry<Integer,Vector<LinkButton>> bttns : links.entrySet()){
            for(int i=0;i<bttns.getValue().size();i++){
                LinkButton tmp = bttns.getValue().get(i);
                if(tmp.isVisible()){
                    ans.add(tmp);
                    tmp.setVisible(false);
                }
            }
        }
        return ans;
    }

    public void activateButtons(int frameNum){
        inactivateButtons();
        for(Map.Entry<Integer,Vector<LinkButton>> bttns : links.entrySet()){
            if(bttns.getKey()<frameNum ){
                for(int i=0;i<bttns.getValue().size();i++){
                    LinkButton tmp = bttns.getValue().get(i);
                    boolean isVisible = tmp.isVisible();
                    tmp.setVisible(!isVisible);
                }
            }

        }

    }


    public HashMap<Integer, Vector<LinkButton>> getMap(){
        return links;
    }

    public void setMap(HashMap<Integer, Vector<LinkButton>> map){
        links = map;
    }
    public void resetMap(){
        links=new HashMap<>();
    }
    public void removeButtons(){
        for(Map.Entry<Integer,Vector<LinkButton>> bttns : links.entrySet()){
            for(int i=0;i<bttns.getValue().size();i++){
                if(!bttns.getValue().get(i).isRemoved){
                    bttns.getValue().get(i).isRemoved = true;
                    this.remove(bttns.getValue().get(i));
                }

            }
        }
        resetMap();
    }
}
