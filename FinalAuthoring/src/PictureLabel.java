import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.TreeMap;
import java.util.Vector;

public class PictureLabel extends JLabel {
    private ImageIcon imgIcon; // the image being displayed
    // private TreeMap<Integer, Vector<RectBound>> shapes; //key: frame#, val: the RectBounds added to this frame
    private Vector<RectBound> shapes;
    private Graphics2D graphics; // It's used to draw shapes

    private int indexHighlight = -1;
    // info regarding the RectBound being edited
    private RectBound rectTar; //its preview
    private int indexTar = -1; //its index <0, ==0, >0
    private int frameTar = -1; //the frame# it's added to

    // Current states
    private int frameCur = -1; //the current frame that is displayed
    private boolean isEditing = false;
    private boolean isResizing = false;
    private boolean isRelocating = false;

    PictureLabel(){
        init();
    }

    PictureLabel(JPanel panel){
        init();
        enableListener(panel);
    }
    private void init(){
        imgIcon = new ImageIcon(this.getClass().getResource("/images/upload.png"));
        this.setIcon(imgIcon);

        shapes = new Vector<>();
        rectTar = null;


        //unit test:
        // isEditing = true;
        // rectTar = addNewShape(1);

    }

    public void enableListener(JPanel panel_in){
        LabelMouseAction mouseListener = new LabelMouseAction(panel_in);
        this.addMouseListener(mouseListener);
        this.addMouseMotionListener(mouseListener);
    }

    public void clearAll(){
        endEdit();
        shapes.clear();
    }

    public void setIndexHighlight(int index_in){
        if(index_in<1 || isEditing){
            indexHighlight = -1;
            repaint();
            return;
        }
        RectBound rect = findShape(index_in);
        if(rect!=null ){
            indexHighlight = index_in;
            repaint();
            return;
        }
        indexHighlight = -1;
    }

    public void setImage(BufferedImage img_in){
        imgIcon = new ImageIcon(img_in);
        this.setIcon(imgIcon);
    }
    public void setFrame(int frame_in){
        frameCur = frame_in;
    }

    public int getIndexTar(){
        return indexTar;
    }

    public int getFrame(){
        return frameCur;
    }

    /*
    public RectBound getRectTar(){
        return rectTar;
    }
    */



    //Pass the item to be edited to rectTar
    //change isEditing to true on success
    public boolean startEdit(int index){ //
        //look for target in tree
        RectBound bound = findShape(index);
        if(bound!=null){
            indexHighlight = -1;
            isEditing = true;
            rectTar = new RectBound();
            rectTar.setParam(bound.getX(),bound.getY(),bound.getWidth(),bound.getHeight(),-1,-1);
            indexTar = index;
            frameTar = bound.getfStart();
            repaint();
            return true;
        }

        endEdit();
        return false; //the edition mode fails to launch
    }

    public boolean startEdit(){ //
        indexHighlight = -1;
        isEditing = true;
        rectTar = new RectBound();
        indexTar = 0;
        frameTar = -1;
        repaint();
        return true;
    }


    //addNew shape to tree(record)
    public RectBound addRectTar(int frame_in,int x, int y, int w, int h, int duration){
        if ( frame_in<1 ) return null;

        RectBound rect = new RectBound(x,y,w,h,frame_in,frame_in+duration);
        shapes.add(rect);

        return rect;
    }

    public RectBound addRectTar(int frame_in, int duration){//
        if (rectTar==null || (!isEditing)) return null;
        if (frameTar >= 0 || indexTar != 0) return null;
        if (frame_in<1 || duration<1 ||frame_in+duration>9000) return null;

        RectBound rect = new RectBound(rectTar.getX(),rectTar.getY(),
                rectTar.getWidth(),rectTar.getHeight(),frame_in,frame_in+duration);
        shapes.add(rect);

        frameTar = frame_in;
        indexTar = rect.getId();

        return rect;
    }


    public boolean updateRectTar(int frameS, int duration){// 加duration
        if((!isEditing) || rectTar==null) return false;
        if (frameS<1 || duration<1 ||frameS+duration>9000) return false;

        boolean ans = updateRectTar_helper(frameS,frameS+duration,indexTar,
                rectTar.getX(),rectTar.getY(),rectTar.getWidth(),rectTar.getHeight());
        if(ans){
            frameTar = frameS;
        }
        return ans;
    }

    public boolean updateRectTar(int frameS, int duration, int index,int x,int y, int w, int h){
        if (frameS<1 || duration<1 ||frameS+duration>9000) return false;
        return updateRectTar_helper(frameS,frameS+duration,index,x,y,w,h);
    }

    private boolean updateRectTar_helper(int frameS, int frameEnd, int index,int x,int y, int w, int h){//
        RectBound rect = findShape(index);
        if(rect == null){
            return false;
        }

        rect.setParam(x,y,w,h,frameS,frameEnd);

        return true;
    }

    // SET EDITING STATE
    public void endEdit(){//
        isEditing = false;
        rectTar=null;
        indexTar = -1;
        frameTar = -1;
        indexHighlight = -1;
        repaint();
        return;
    }


    //delete
    public boolean deleteShape(int index){//

        for(int i=0;i<shapes.size();i++){
            if(shapes.get(i).getId()==index){
                shapes.remove(i);
                repaint();
                return true;
            }
        }

        return false;
    }

    private RectBound findShape(int index){//

        for(RectBound bound : shapes){
            if(bound.getId()==index){
                return bound;
            }
        }

        return null;
    }
    //

    @Override
    public void paint(Graphics g) { //essential!
        super.paint(g);

        graphics = (Graphics2D) g;
        graphics.setColor(Color.BLUE);
        graphics.setStroke(new BasicStroke(2.0f));

        for(RectBound bound : shapes){
            if(bound.getfStart()<=frameCur && bound.getfEnd() >=frameCur) {
                if (bound.getId() == indexHighlight) {
                    graphics.setColor(Color.GREEN);
                }
                if (bound.getId() != indexTar) {
                    graphics.drawRect(bound.getX(), bound.getY(), bound.getWidth(), bound.getHeight());
                    graphics.setColor(Color.BLUE);
                }
            }
        }

        if (isEditing && rectTar!=null){
            graphics.setColor(Color.RED);
            graphics.drawRect(rectTar.getX(),rectTar.getY(),rectTar.getWidth(),rectTar.getHeight());
        }

    }

    //add mouse listener
    class LabelMouseAction extends MouseInputAdapter {
        int hostWidth = 0;
        int hostHeight = 0;
        int x_prev;
        int y_prev;
        JPanel panelRef;

        LabelMouseAction(JPanel panel_in){
            panelRef = panel_in;
        }


        @Override
        public void mouseMoved(MouseEvent e) {
            // super.mouseMoved(e); //?the implementation is empty?
            if(!isEditing){
                return;
            }
            // change cursor icon
            int x = e.getX();
            int y = e.getY();

            if(hoverShaping(x,y)){
                if (panelRef.getCursor().getType()!=Cursor.SE_RESIZE_CURSOR){
                    panelRef.setCursor(new Cursor(Cursor.SE_RESIZE_CURSOR));
                }
            }else if (panelRef.getCursor().getType()!=Cursor.DEFAULT_CURSOR){
                panelRef.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }

        }

        @Override
        public void mousePressed(MouseEvent e) {
            // super.mousePressed(e);
            if(!isEditing){
                return;
            }
            int x = e.getX();
            int y = e.getY();
            if(hoverShaping(x,y)){
                isResizing = true;
            }else if(hoverRect(x,y)){
                isRelocating = true;
            }
            hostWidth = (int)((JLabel) e.getSource()).getSize().getWidth();
            hostHeight = (int)((JLabel) e.getSource()).getSize().getHeight();
            x_prev=x;
            y_prev=y;

        }

        @Override
        public void mouseReleased(MouseEvent e) {
            // super.mouseReleased(e);
            isResizing = false;
            isRelocating = false;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            // super.mouseDragged(e);
            if(!isEditing){
                return;
            }
            int x = e.getX();
            int y = e.getY();
            int deltaX=x-x_prev;
            int deltaY=y-y_prev;
            if(isResizing){
                rectTar.setParam(rectTar.getX(),rectTar.getY(),
                        Math.min(hostWidth,rectTar.getWidth()+deltaX),
                        Math.min(hostHeight,rectTar.getHeight()+deltaY));
            }else if(isRelocating){
                rectTar.setParam(
                        Math.min(rectTar.getX()+deltaX,hostWidth-rectTar.getWidth()),
                        Math.min(rectTar.getY()+deltaY,hostHeight-rectTar.getHeight()),
                        rectTar.getWidth(), rectTar.getHeight());
            }
            x_prev=x;
            y_prev=y;
            ((JLabel) e.getSource()).repaint();
        }

        boolean hoverRect(int x, int y){
            if(rectTar!=null){
                return rectTar.getX()<=x && x<rectTar.getX()+rectTar.getWidth()
                        && rectTar.getY()<=y&&y<rectTar.getY()+rectTar.getHeight();
            }
            return false;
        }

        boolean hoverShaping(int x, int y){
            if(rectTar!=null){
                int right = rectTar.getX()+rectTar.getWidth();
                int bottom = rectTar.getY()+rectTar.getHeight();
                return right-5 <= x && x< right+5
                        && bottom -5 <= y && y<bottom + 5;
            }
            return false;
        }

    }
}
