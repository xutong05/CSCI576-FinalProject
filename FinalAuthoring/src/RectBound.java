
public class RectBound {
    private static int idCounter = 1;
    private final int id;
    private int x;
    private int y;
    private int width;
    private int height;
    // ?
    private int fStart;
    private int fEnd;

    RectBound(int x_in, int y_in, int w_in, int h_in, int fs, int fe){
        id = idCounter; // assume will not overflow
        idCounter++;

        x = Math.max(0,x_in);
        y = Math.max(0,y_in);
        width = Math.max(20,w_in);
        height = Math.max(20,h_in);

        fStart = fs;
        fEnd = fe;
    }
    RectBound(){
        id = 0;
        x = 0;
        y = 0;
        width = 50;
        height = 50;

        fStart = -1;
        fEnd = -1;
    }

    public void setFrames(int s, int e){
        fStart = s;
        fEnd = e;
    }
    public int getfStart(){
        return fStart;
    }
    public int getfEnd(){
        return fEnd;
    }

    public int getId(){
        return id;
    }
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    public int getWidth(){
        return width;
    }
    public int getHeight(){
        return height;
    }
    public void setParam(int x_in, int y_in, int w_in, int h_in,int fs, int fe){
        x = Math.max(0,x_in);
        y = Math.max(0,y_in);
        width = Math.max(20,w_in);
        height = Math.max(20,h_in);
        fStart = fs;
        fEnd = fe;
    }
    public void setParam(int x_in, int y_in, int w_in, int h_in){
        x = Math.max(0,x_in);
        y = Math.max(0,y_in);
        width = Math.max(20,w_in);
        height = Math.max(20,h_in);

    }

    @Override
    public String toString() {
        return " id= " + id +
                " x= " + x +
                " y= " + y +
                " width= " + width +
                " height= " + height +
                ' ';
    }
}
