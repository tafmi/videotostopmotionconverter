/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package videostopmotionconverter;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import com.xuggle.xuggler.Global;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.TreeMap;

/**
 *
 * @author Teo
 */
public class SnapShooter {
    
    private  final String inputFilename ;
    private  final long MICRO_SECONDS_BETWEEN_FRAMES;     
    private  int mVideoStreamIndex = -1;
    private  long mLastPtsWrite = Global.NO_PTS;
    private  final TreeMap<Integer,BufferedImage> imageMap=new TreeMap();
    private  int counter=0;
    private  int width=0 , height=0;
    
  
    public SnapShooter(String inputFilename,int seconds) throws IOException {
        // TODO code application logic here
        this.inputFilename=inputFilename;
        MICRO_SECONDS_BETWEEN_FRAMES= (long)(Global.DEFAULT_PTS_PER_SECOND * seconds);
  
    }
    
    public void Shoot(){
        IMediaReader mediaReader = ToolFactory.makeReader(inputFilename);
        // stipulate that we want BufferedImages created in BGR 24bit color space
        mediaReader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);
        mediaReader.addListener(new SpanshotListener());
        // read out the contents of the media file and
        // dispatch events to the attached listener
        while (mediaReader.readPacket() == null) ;
    }
    
    
    public  class SpanshotListener extends MediaListenerAdapter {
    
     @Override
     public void onVideoPicture(IVideoPictureEvent event){
         
             if (event.getStreamIndex() != mVideoStreamIndex) {
                // if the selected video stream id is not yet set, go ahead an
                // select this lucky video stream
                if (mVideoStreamIndex == -1)
                    mVideoStreamIndex = event.getStreamIndex();
                // no need to show frames from this video stream
                else
                    return;
              }
              // if uninitialized, back date mLastPtsWrite to get the very first frame
              if (mLastPtsWrite == Global.NO_PTS)
                  mLastPtsWrite = event.getTimeStamp() - MICRO_SECONDS_BETWEEN_FRAMES;
              
              // if it's time to write the next frame
              if (event.getTimeStamp() - mLastPtsWrite >= MICRO_SECONDS_BETWEEN_FRAMES) {
                  BufferedImage bi=event.getImage();
                  if(width==0){
                      width=bi.getWidth();
                      height=bi.getHeight();
                  }
                  imageMap.put(counter,bi);
                  counter++;
                  // indicate file written
                  double seconds = ((double) event.getTimeStamp()) / Global.DEFAULT_PTS_PER_SECOND;
                  System.out.printf("Snapshot at %6.3f seconds \n", seconds);
                // update last write time
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
