class LinkInfo{
    String name; //name of the hyperlink
    RectBound rect; //contains id
    String pathVideoSec; // path to the secondary video
    int frameSec; //frame# of secondary video
    int durationSec;
    int framePrim;
    int durationPrim;

    LinkInfo(String name_in, RectBound rect_in, int framePrimary_in, int duration1_in,
             String pathVideo_in, int frameSec_in, int duration2_in){
        name = name_in;
        rect = rect_in;
        setVideo1Info(framePrimary_in,duration1_in);
        setVideo2Info(pathVideo_in,frameSec_in,duration2_in);
    }
    public void setVideo1Info(int framePrimary_in, int duration1_in){
        framePrim = framePrimary_in;
        durationPrim = duration1_in;
    }
    public void setVideo2Info(String pathVideo_in, int frameSec_in, int duration2_in){
        pathVideoSec = pathVideo_in;
        frameSec = frameSec_in;
        durationSec = duration2_in;
    }
    public void setName(String name_in){
        name = name_in;
    }
    public String getName(){
        return name;
    }
    public int getID(){
        return rect.getId();
    }
    public String getPathVideoSec(){
        return pathVideoSec;
    }
    public int getFrameSec(){
        return frameSec;
    }
    public int getFramePrim(){
        return framePrim;
    }
    public int getDurationPrim(){
        return durationPrim;
    }
    public int getDurationSec(){
        return durationSec;
    }
    public String toString(){

        return pathVideoSec+" "+rect.getX()+" "+rect.getY()+" "+rect.getWidth()+" "+rect.getHeight() +" "
                +framePrim+" "+(framePrim+durationPrim)+" "+ frameSec + " "+(frameSec+durationSec)+'\n'; //subject to change. Depending on the file format
    }

}