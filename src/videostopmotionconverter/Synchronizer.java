
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
        screenBounds = Toolkit.getDefaultToolkit().getScreenSize();
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
          }
        }
        writer.encodeVideo(0, blackImage,System.nanoTime() - startTime,TimeUnit.NANOSECONDS);
        writer.close();
    }
    
    public BufferedImage convertToType(BufferedImage sourceImage, int targetType) {
        BufferedImage image;
        if (sourceImage.getType() == targetType) {
            image = sourceImage;
        }
        else {
            image = new BufferedImage(sourceImage.getWidth(),
                 sourceImage.getHeight(), targetType);
            image.getGraphics().drawImage(sourceImage, 0, 0, null);
        }
        return image;
    }
    
}
