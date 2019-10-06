/* POSTTaggingMEDemo 
 * This file will use opennlp to tag individual tokens 
 */
import java.io.FileInputStream; 
import java.io.InputStream; 
import java.io.InputStreamReader;
import java.io.BufferedReader;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

public class POSTaggingMEDemo { 
  
    public static void main(String args[]) throws Exception{     
        
        // Load and instantiate the POSTagger
        InputStream posTagStream = new FileInputStream("OpenNLP_models/en-pos-maxent.bin");
        POSModel posModel = new POSModel(posTagStream);
        POSTaggerME posTagger = new POSTaggerME(posModel);
    
        for( String arg : args ) {
            InputStream infile = new FileInputStream(arg);
            BufferedReader buf = new BufferedReader(new InputStreamReader(infile));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while( (line=buf.readLine()) != null ) {
                //Check if formating info line
                if (line.contains("$DOC") || line.contains("$TEXT") || line.contains("$TITLE")) {
                    System.out.println(line);
                    continue;
                }
                String tokens[] = line.split("[ \n]+");       
                //Tagging all tokens
                String tags[] = posTagger.tag(tokens);
                //Printing the token-tag pairs  
                for( int i = 0; i < tokens.length; i++ ) 
                    System.out.print(tokens[i] + "/" + tags[i] +" "); 
                System.out.println();
            }

        }
    } 
} 

