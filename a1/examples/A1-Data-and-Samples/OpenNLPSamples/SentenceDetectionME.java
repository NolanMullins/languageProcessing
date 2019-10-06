import java.io.FileInputStream; 
import java.io.InputStream;  
import java.io.InputStreamReader;
import java.io.BufferedReader;

import opennlp.tools.sentdetect.SentenceDetectorME; 
import opennlp.tools.sentdetect.SentenceModel;  

public class SentenceDetectionME { 
   
   public static String readString(String arg) throws Exception {
      InputStream infile = new FileInputStream(arg);
      BufferedReader buf = new BufferedReader(new InputStreamReader(infile));
      StringBuilder sb = new StringBuilder();
      String line = buf.readLine();
      while( line != null ) {
        sb.append(line + " ");
        line = buf.readLine();
      }
      return sb.toString();  
   }
  
   public static void main(String args[]) throws Exception { 
   
      //Load sentence detector model 
      InputStream modelData = new FileInputStream("OpenNLP_models/en-sent.bin"); 
      SentenceModel model = new SentenceModel(modelData); 
       
      //Instantiate SentenceDetectorME 
      SentenceDetectorME detector = new SentenceDetectorME(model);  
    
      //Allow multiple files to be processed
      for (String arg : args ) {
        //Split a file into sentences
        String sentences[] = detector.sentDetect(readString(arg)); 

        //Print the sentences 
        for(String sent : sentences)        
           System.out.println(sent);  
      }
   } 
}
