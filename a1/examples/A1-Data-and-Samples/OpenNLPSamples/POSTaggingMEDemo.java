import java.io.FileInputStream; 
import java.io.InputStream; 
import java.io.InputStreamReader;
import java.io.BufferedReader;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

public class POSTaggingMEDemo { 
  
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

   public static void main(String args[]) throws Exception{     
     
      // Load and instantiate the POSTagger
      InputStream posTagStream = new FileInputStream("OpenNLP_models/en-pos-maxent.bin");
      POSModel posModel = new POSModel(posTagStream);
      POSTaggerME posTagger = new POSTaggerME(posModel);
   
      for( String arg : args ) {
        // Split text into space-separated tokens
        String tokens[] = readString(arg).split("[ \n]+");       
          
        //Tagging all tokens
        String tags[] = posTagger.tag(tokens);

        //Printing the token-tag pairs  
        for( int i = 0; i < tokens.length; i++ ) 
           System.out.println(tokens[i] + "/" + tags[i]); 
      }
   } 
} 

