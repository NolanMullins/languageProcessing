import java.io.FileInputStream; 
import java.io.InputStream;  
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.ArrayList;

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

    public static String[] fixSentence(String sent) 
    {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < sent.length(); i++) {
            if (sent.charAt(i) == '$' && sent.charAt(i+1)=='T') {
                if (i>0)
                    list.add(sent.substring(0,i));
                sent = sent.substring(i);
                for (int j=0; i < sent.length(); j++) {
                    if (sent.charAt(j)==' ') {
                        list.add(sent.substring(0,j));
                        sent = sent.substring(j+1);
                        break;
                    }
                }
                i=0;
            } else if (sent.charAt(i) == '$' && sent.charAt(i+1)=='D') {
                if (i > 0) {
                    list.add(sent.substring(0,i));
                    sent = sent.substring(i);
                    i=0;
                }
            }
        }
        list.add(sent);
        String stringList[] = new String[list.size()];
        for (int i = 0; i < list.size(); i++)
            stringList[i] = list.get(i);
        return stringList;
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
                for (String fixed : fixSentence(sent))
                    System.out.println(fixed);  
        }
    } 
}
