/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package videostopmotionconverter;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;

/**
 *
 * @author Teo
 */
public class Synchronizer {
    
    private  final double frameRate;
    private final  TreeMap<Integer,BufferedImage> imageMap;
    private static Dimension screenBounds;
    private final IMediaWriter writer;
    
    public Synchronizer(String outputFilename,TreeMap<Integer,BufferedImage> imageMap,long frameRate ){
           
        this.imageMap=imageMap;
        this.frameRate=frameRate;
        writer = ToolFactory.makeWriter(outputFilename);
        
    }
    
    public void synchronize(int width,int height) throws IOException{    
        // let's make a IMediaWriter to write the file.
        screenBounds = Toolkit.getDefaultToolkit().getScreenSize();
        // We tell it we're going to add one video stream, with id 0,
        // at position 0, and that it will have a fixed frame rate of FRAME_RATE.
        writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4,
                   screenBounds.width/2, screenBounds.height/2);
        long startTime = System.nanoTime();
        BufferedImage image=ImageIO.read(new File("black_image.jpg"));
        BufferedImage blackImage=new BufferedImage(width,height,BufferedImage.TYPE_3BYTE_BGR);
        blackImage.getGraphics().drawImage(image, 0, 0, null);
        long sleeptime;
        if(frameRate==1/2){
            sleeptime=2000;
        }
        else{
            sleeptime=(long)(1000/frameRate);
        }
        for(Map.Entry pair:imageMap.entrySet()){
          BufferedImage spanShot=(BufferedImage)pair.getValue();
          BufferedImage bgrScreen = convertToType(spanShot, BufferedImage.TYPE_3BYTE_BGR);
          writer.encodeVideo(0, bgrScreen,System.nanoTime() - startTime,TimeUnit.NANOSECONDS);
          try {
              Thread.sleep(sleeptime);
          }
          catch (InterruptedException e) {
               // ignore
          }
        }
        writer.encodeVideo(0, blackImage,System.nanoTime() - startTime,TimeUnit.NANOSECONDS);
        // tell the writer to close and write the trailer if  needed
        writer.close();
    }
    
    public BufferedImage convertToType(BufferedImage sourceImage, int targetType) {
        BufferedImage image;
        // if the source image is already the target type, return the source image
        if (sourceImage.getType() == targetType) {
            image = sourceImage;
        }
        // otherwise create a new image of the target type and draw the new image
        else {
            image = new BufferedImage(sourceImage.getWidth(),
                 sourceImage.getHeight(), targetType);
            image.getGraphics().drawImage(sourceImage, 0, 0, null);
        }
        return image;
    }
    
}
