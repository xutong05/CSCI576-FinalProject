import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

public class VideoEditor {

    Vector<LinkInfo> linkInfos;
    boolean listIsDisplayed = false;
    ListDialog dialog;
    EditionFrame frame;
    PictureLabel pictureLabel;

    // Edition states
    boolean editionModeOn = false;
    int duration1=-1;
    int duration2=-1;
    int frameNumber1 =-1;
    int frameNumber2 = -1;
    LinkInfo link = null;
    String nameLink = "Enter Name";


    VideoEditor(){
        init();
    }

    private void init(){
        linkInfos = new Vector<>();
        dialog = new ListDialog();
        frame = new EditionFrame();
        pictureLabel = frame.video1.getImgLabel();
        addListeners();
    }

    public void drawUI(){
        frame.drawUI();
        dialog.setVisible(false);
    }

    private void addListeners(){
        frame.editBttn.addActionListener(new EditListener());
        frame.saveBttn.addActionListener(new SaveListener());
        frame.exitBttn.addActionListener(new ExitListener());
        frame.listBttn.addActionListener(new OpenListener());
        dialog.list.addMouseListener(new ListMouseAction());
        dialog.deleteBttn.addActionListener(new DeleteListener());
        frame.menuItemSave.addActionListener(new ExportListener());
    }

    private void turnOnEditPane(){
        if(frame.video2.getVideoPathRoot() == null ||
                frame.video1.getVideoPathRoot() == null ){ return; }
        duration1=75;
        duration2=75;
        link = null;
        nameLink = "New Link";

        frame.menuItemSec.setEnabled(false);
        frame.editBttn.setEnabled(false);
        frame.saveBttn.setEnabled(true);
        frame.exitBttn.setEnabled(true);
        frame.editName.setText(nameLink);
        frame.editName.setEnabled(true);
        frame.video1.setEditMode(true);
        frame.video2.setEditMode(true);
        frame.video1.setDurationDisplay(-1,-1);
        frame.video2.setDurationDisplay(-1,-1);
        pictureLabel.startEdit();

        editionModeOn = true;
    }
    private void turnOnEditPane(int index){
        link = findLink(index);
        if(link == null) return;
        duration1=link.getDurationPrim();
        duration2=link.getDurationSec();
        frameNumber1 = link.getFramePrim();
        frameNumber2 = link.getFrameSec();
        nameLink = link.getName();
        boolean isSet = frame.video2.setVideoPathRoot(link.getPathVideoSec());
        if(!isSet){
            turnOffEditPane();
            return;
        }

        frame.video1.setSliderFrame(frameNumber1);
        frame.video2.setSliderFrame(frameNumber2);

        frame.menuItemSec.setEnabled(false);
        frame.editBttn.setEnabled(false);
        frame.saveBttn.setEnabled(true);
        frame.exitBttn.setEnabled(true);
        frame.editName.setText(nameLink);
        frame.editName.setEnabled(true);
        frame.video1.setEditMode(true);
        frame.video2.setEditMode(true);
        frame.video1.setDurationDisplay(frameNumber1,frameNumber1+duration1);
        frame.video2.setDurationDisplay(frameNumber2,frameNumber2+duration2);
        isSet = pictureLabel.startEdit(link.getID());
        if(!isSet){
            turnOffEditPane();
            return;
        }
        editionModeOn = true;
    }

    private void turnOffEditPane(){
        duration1=-1;
        duration2=-1;
        frameNumber1 =-1;
        frameNumber2 = -1;
        link = null;
        nameLink = "Enter Name";

        pictureLabel.endEdit();
        frame.menuItemSec.setEnabled(true);
        frame.editBttn.setEnabled(true);
        frame.saveBttn.setEnabled(false);
        frame.exitBttn.setEnabled(false);
        frame.editName.setText(nameLink);
        frame.editName.setEnabled(false);
        frame.video1.setEditMode(false);
        frame.video2.setEditMode(false);
        frame.video1.setDurationDisplay(-1,-1);
        frame.video2.setDurationDisplay(-1,-1);

        editionModeOn = false;
    }

    private void displayLink(int index){
        turnOffEditPane();
        link = findLink(index);
        if(link == null) return;
        boolean isSet = frame.video2.setVideoPathRoot(link.getPathVideoSec());
        if(!isSet){
            return;
        }
        frame.video1.displayDuration(link.getFramePrim(),link.getFramePrim()+link.getDurationPrim());
        frame.video2.displayDuration(link.getFrameSec(),link.getFrameSec()+link.getDurationSec());
        frame.video1.setSliderFrame(link.getFramePrim());
        frame.video2.setSliderFrame(link.getFrameSec());
        pictureLabel.setIndexHighlight(link.getID());
    }
    private void unSelect(){
        dialog.list.clearSelection();
        dialog.linkName.setText("(Empty)");
        pictureLabel.setIndexHighlight(-1);
        frame.video1.displayDuration(-1,-1);
        frame.video2.displayDuration(-1,-1);
    }

    private LinkInfo findLink(int index){
        for(LinkInfo tmp : linkInfos){
            if(tmp.getID() == index){
                return tmp;
            }
        }

        return null;
    }
    private void deleteLink(int index){
        for(int i=0;i<linkInfos.size();i++){
            if(linkInfos.get(i).getID() == index){
                linkInfos.remove(i);
            }
        }

    }

    //listeners
    class EditListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(!editionModeOn && frame.video2.getVideoPathRoot() != null &&
                    frame.video1.getVideoPathRoot() != null) {
                turnOnEditPane();

            }
        }
    }

    class SaveListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(!editionModeOn) { return; }
            nameLink = frame.editName.getText();

            if(frame.video1.getFrameStart()<1 || frame.video1.getFrameEnd()<1){
                if(frame.video1.getFrameStart()>=1 || frameNumber1<1||duration1<1){
                    frameNumber1 = Math.min(frame.video1.getFrame(),8999);
                    duration1 = 9000-frameNumber1;
                }


            }else {
                frameNumber1 = frame.video1.getFrameStart();
                duration1 = frame.video1.getFrameEnd() - frame.video1.getFrameStart();
            }
            frame.video1.setDurationDisplay(frameNumber1,frameNumber1+duration1);

            if(frame.video2.getFrameStart()<1 || frame.video2.getFrameEnd()<1){
                if(frame.video2.getFrameStart()>=1 || frameNumber2<1||duration2<1){
                    frameNumber2 = Math.min(frame.video2.getFrame(),8999);
                    duration2 = 9000-frameNumber2;
                }

            }else {
                frameNumber2 = frame.video2.getFrameStart();
                duration2 = frame.video2.getFrameEnd() - frame.video2.getFrameStart();
            }
            frame.video2.setDurationDisplay(frameNumber2,frameNumber2+duration2);


            if(frameNumber1<1 || frameNumber2<1){ return; }
            if(pictureLabel.getIndexTar()==0){
                RectBound rect = pictureLabel.addRectTar(frameNumber1,duration1);
                link = new LinkInfo(nameLink,rect,frameNumber1,duration1,
                        frame.video2.getVideoPathRoot(),frameNumber2,duration2);
                linkInfos.add(link);
                dialog.addItem(nameLink,link.getID());
            }else if (pictureLabel.getIndexTar() > 0 && link!=null
                    && link.getID() == pictureLabel.getIndexTar()){
                pictureLabel.updateRectTar(frameNumber1,duration1);
                link.setName(nameLink);
                link.setVideo1Info(frameNumber1,duration1);
                link.setVideo2Info(frame.video2.getVideoPathRoot(),frameNumber2,duration2);
                dialog.updateItem(link.getName(),link.getID());
            }
            frame.video1.setSliderFrame(link.getFramePrim());
            frame.video2.setSliderFrame(link.getFrameSec());


        }
    }

    class ExitListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(editionModeOn) {
                turnOffEditPane();
            }
        }
    }

    class OpenListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(listIsDisplayed == false){
                dialog.setBounds(frame.getLocationOnScreen().x+frame.getWidth()+1,
                        frame.getLocationOnScreen().y,300,frame.getHeight());
                dialog.setVisible(true);
                listIsDisplayed = !listIsDisplayed;
            }else{
                unSelect();
                dialog.setVisible(false);
                listIsDisplayed = !listIsDisplayed;
            }
        }
    }

    class ListMouseAction extends MouseInputAdapter{
        int lastIndex = -1;
        @Override
        public void mouseClicked(MouseEvent e) {
            if(e.getButton() != MouseEvent.BUTTON1){
                return;
            }

            if(dialog.list.getSelectedValue() == null) return;
            int id = dialog.list.getSelectedValue().getIndex();
            if(e.getClickCount()==2){
                dialog.linkName.setText(dialog.list.getSelectedValue().toString());
                turnOnEditPane(id);
            }else if(e.getClickCount()==1 && !editionModeOn){
                if(lastIndex == id){
                    unSelect();
                    lastIndex = -1;
                    return;
                }else{
                    dialog.linkName.setText(dialog.list.getSelectedValue().toString());
                    displayLink(id);
                }
            }
            lastIndex = id;
        }
    }

    class DeleteListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(editionModeOn) return;
            int posInList = dialog.list.getSelectedIndex();
            if(posInList < 0) return;
            int linkId = dialog.list.getSelectedValue().getIndex();
            LinkInfo tmp = findLink(linkId);
            if(tmp==null) return;
            pictureLabel.deleteShape(tmp.getID());
            deleteLink(tmp.getID());
            dialog.deleteItem(posInList);
            unSelect();

        }
    }

    class ExportListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                File myFile = new File(frame.video1.getVideoPathRoot()+".txt");
                myFile.createNewFile();
                FileWriter writer = new FileWriter(myFile);
                writer.write(frame.video1.getVideoPathRoot()+'\n');
                for(int i=0;i<linkInfos.size();i++){
                    writer.write(linkInfos.get(i).toString());
                }
                writer.write("EOF");
                writer.close();

            } catch (IOException err) {
                System.out.println("Export: An error occurred.");

            }
        }
    }

}





