
package videostopmotionconverter;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import com.xuggle.xuggler.Global;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.TreeMap;

public class SnapShooter {
    
    private  final String inputFilename ;
    private  final long MICRO_SECONDS_BETWEEN_FRAMES;     
    private  int mVideoStreamIndex = -1;
    private  long mLastPtsWrite = Global.NO_PTS;
    private  final TreeMap<Integer,BufferedImage> imageMap=new TreeMap();
    private  int counter=0;
    private  int width=0 , height=0;
    
  
    public SnapShooter(String inputFilename,int seconds) throws IOException {
  
        this.inputFilename=inputFilename;
        MICRO_SECONDS_BETWEEN_FRAMES= (long)(Global.DEFAULT_PTS_PER_SECOND * seconds);
  
    }
    
    public void Shoot(){
        IMediaReader mediaReader = ToolFactory.makeReader(inputFilename);
        mediaReader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);
        mediaReader.addListener(new SpanshotListener());
        while (mediaReader.readPacket() == null) ;
    }
    
    
    public  class SpanshotListener extends MediaListenerAdapter {
    
     @Override
     public void onVideoPicture(IVideoPictureEvent event){
         
             if (event.getStreamIndex() != mVideoStreamIndex) {
                if (mVideoStreamIndex == -1)
                    mVideoStreamIndex = event.getStreamIndex();
                else
                    return;
              }
              if (mLastPtsWrite == Global.NO_PTS)
                  mLastPtsWrite = event.getTimeStamp() - MICRO_SECONDS_BETWEEN_FRAMES;
              
              if (event.getTimeStamp() - mLastPtsWrite >= MICRO_SECONDS_BETWEEN_FRAMES) {
                  BufferedImage bi=event.getImage();
                  if(width==0){
                      width=bi.getWidth();
                      height=bi.getHeight();
                  }
                  imageMap.put(counter,bi);
                  counter++;
                  double seconds = ((double) event.getTimeStamp()) / Global.DEFAULT_PTS_PER_SECOND;
                  System.out.printf("Snapshot at %6.3f seconds \n", seconds);
                mLastPtsWrite += MICRO_SECONDS_BETWEEN_FRAMES;
            }
     }
       

}

    public  TreeMap<Integer, BufferedImage> getImageMap() {
        return imageMap;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
    
    
}
