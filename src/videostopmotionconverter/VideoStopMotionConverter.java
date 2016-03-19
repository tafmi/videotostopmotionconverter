
package videostopmotionconverter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.TreeMap;
import videostopmotionconverter.exceptions.InputFileMissingException;
import videostopmotionconverter.exceptions.InvalidArgumentException;
import videostopmotionconverter.exceptions.OutputPathMissingException;

public class VideoStopMotionConverter {

    public static void main(String[] args) {
 
        try {
            if (args.length != 4) {
                throw new InvalidArgumentException();
            } else {

                String inputFile;
                String outputPath;

                if ("-i".equals(args[0])) {
                    inputFile = GetInputPath(args[1]);
                } else if ("-i".equals(args[2])) {
                    inputFile = GetInputPath(args[3]);
                } else {
                    throw new InputFileMissingException("Please specify input filedirectory and try again.");
                }

                if ("-o".equals(args[0])) {
                    outputPath = GetOutputPath(args[1]);
                } else if ("-o".equals(args[2])) {
                    outputPath = GetOutputPath(args[3]);
                } else {
                    throw new OutputPathMissingException("Please specify output path and try again.");
                }
                
                Convert(inputFile,outputPath);
                
            }
        } catch (IOException ex) {
            System.out.println(String.format("Error: %s", ex.getMessage()));
        } catch (InputFileMissingException | OutputPathMissingException ex) {
            System.out.println(String.format("Argument error: %s", ex.getMessage()));
        } catch (InvalidArgumentException invalidArgEx) {
            System.out.println("Application usage:");
            System.out.println("<executable_name> -i <input_directory> -o <output_path>");
            System.out.println("<executable_name> -o <output_path> -i <input_directory>");
        }
    }
    
    private static void Convert(String input,String output) throws IOException{
       
        boolean validinput1=false;
        boolean validinput2=false;
        int seconds=0;
        long speed=0;
        System.out.println("Please specify the seconds between snapshots and press ENTER.");
        while(validinput1==false){
            Scanner in=new Scanner(System.in);
            String stringSeconds=in.nextLine();
            int counter=0;
            for(int i=0;i<stringSeconds.length();i++){
                if(!Character.isDigit(stringSeconds.charAt(i))){
                    counter++;
                }
            }
            if(counter!=0){
                System.out.println("Please specify a valid number of seconds and press ENTER.");
            }
            else{
                validinput1=true;
                seconds=Integer.parseInt(stringSeconds);
            }    
        }
        System.out.println("Please specify the frame rate of the output video.");
        System.out.println("1. Super slow");
        System.out.println("2. Slow");
        System.out.println("3. Medium");
        System.out.println("4. Fast");
        System.out.println("5. Super fast");
        System.out.println("Please type one of the above numbers and press ENTER.");
        while(validinput2==false){
            Scanner in=new Scanner(System.in);
            String stringRate= in.nextLine();
            int counter=0;
            for(int i=0;i<stringRate.length();i++){
                if(!Character.isDigit(stringRate.charAt(i))){
                    counter++;
                }
            }
            if(counter!=0){
                System.out.println("Invalid input!");
                System.out.println("1. Super slow");
                System.out.println("2. Slow");
                System.out.println("3. Medium");
                System.out.println("4. Fast");
                System.out.println("5. Super fast");
                System.out.println("Please type one of the above numbers and press ENTER.");
            }
            else{
                int rate=Integer.parseInt(stringRate);
                if(rate!=1 && rate!=2 && rate!=3 && rate!=4 && rate!=5){
                   System.out.println("Invalid input!");
                   System.out.println("1. Super slow");
                   System.out.println("2. Slow");
                   System.out.println("3. Medium");
                   System.out.println("4. Fast");
                   System.out.println("5. Super fast");
                   System.out.println("Please type one of the above numbers and press ENTER."); 
                }
                else{
                    speed=rate;
                    validinput2=true;
                }
            }
        }
        long rate;
        if(speed==1){
           rate=1/2; 
        }
        else if(speed==2){
           rate=1;
        }
        else if(speed==3){
           rate=2;
        }
        else if(speed==4){
           rate=5;
        }
        else{
           rate=10; 
        }
        
        SnapShooter shooter=new SnapShooter(input,seconds);
        shooter.Shoot();
        TreeMap<Integer,BufferedImage> imageMap=shooter.getImageMap();
        int width=shooter.getWidth();
        int height=shooter.getHeight();
        Synchronizer synchronizer=new Synchronizer(output,imageMap,rate);
        synchronizer.synchronize(width,height);

    }
    
    private static String GetInputPath(String argument) throws FileNotFoundException, IOException {
        String inputDirectory = argument;

        File file = new File(inputDirectory);

        if (!file.exists()) {
            throw new FileNotFoundException("Input directory does not exist. Please specify a valid input directory.");
        }

        if (file.isDirectory()) {
            throw new IOException("The file specified as input is a directory. Please specify a name that is not a directory.");
        }
        
        if(!getFileExtension(file).equals("mp4") && !getFileExtension(file).equals("mov") && !getFileExtension(file).equals("flv")
                && !getFileExtension(file).equals("avi") && !getFileExtension(file).equals("mpeg")) {
            throw new IOException("The file format of the input video is not supported.\nPlease specify a file in the following formats:\n"
                    + "avi\tflv\tmov\nmp4\tmpeg"); 
        }
        return inputDirectory;
    }

    private static String GetOutputPath(String argument) throws IOException {
        String outputPath = argument;

        File file = new File(outputPath);

        if(!file.getParentFile().exists() || !file.getParentFile().isDirectory()) {
            throw new IOException("Something is wrong with the output path you specified. Please check the path and try again.");
        }
        
        if (file.isDirectory()) {
            throw new IOException("The file specified as output is a directory. Please specify a name that is not a directory.");
        }
        
        if(!getFileExtension(file).equals("mp4")) {
            throw new IOException("The file specified as output is not in mp4 video format. Please specify a video in mp4 file format."); 
        }

        return outputPath;
    }
    
    public static String getFileExtension(File file) {
        return (file.getName().substring(file.getName().lastIndexOf(".") + 1));
    }
}
