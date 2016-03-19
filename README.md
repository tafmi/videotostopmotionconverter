# videotostopmotionconverter
Application that converts videos to stop-motion videos.
## features
This application converts a video file in mp4 flv mov avi mpeg format, to a stop-motion video file 

in mp4 format. You can choose the seconds between snapshots from the input video file and the frame 

rate (super slow-super fast) of the output video file.
## usage
args:

-i input_videofile -o output_videofile

or

-o output_videofile -i input_videofile

in windows cmd:

1. cd project's path

2. path\to\videotostopmotionconverter>java -cp bin;lib\slf4j-api-1.7.19.jar;lib\slf4j-simple-1.7.19.jar;lib\xuggle-xuggler-5.4.jar videostopmotionconverter.VideoStopMotionConverter -i path\to\myvideo.mov -o path\to\mystopmotionvideo.mp4

#### supports
.mp4 .flv .mov .avi .mpeg video file formats for input

.mp4 video file format for output
