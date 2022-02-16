import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class ImageReader {
    public static BufferedImage readImageRGB(int width, int height, String imgPath)
    {
        try
        {
            BufferedImage img = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
            int frameLength = width*height*3;

            File file = new File(imgPath);
            RandomAccessFile raf = new RandomAccessFile(file, "r"); //read only
            raf.seek(0); //start from 0

            long len = frameLength;
            byte[] bytes = new byte[(int) len];

            raf.read(bytes);

            int ind = 0;
            for(int y = 0; y < height; y++)
            {
                for(int x = 0; x < width; x++)
                {
                    byte a = 0;
                    byte r = bytes[ind];
                    byte g = bytes[ind+height*width];
                    byte b = bytes[ind+height*width*2];

                    int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                    //int pix = ((a << 24) + (r << 16) + (g << 8) + b);
                    img.setRGB(x,y,pix);
                    ind++;
                }
            }
            return img;
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            System.out.println("error reading img:"+imgPath);
            return null;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println("error reading img:"+imgPath);
            return null;
        }
    }
}
