import java.io.FileInputStream; 
import java.io.InputStream; 
import java.io.InputStreamReader;
import java.io.BufferedReader;

import opennlp.tools.tokenize.TokenizerME; 
import opennlp.tools.tokenize.TokenizerModel; 
import opennlp.tools.util.Span;  
import opennlp.tools.stemmer.PorterStemmer;

public class TokenizerMEProbs { 
   
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
      
      //Load and instantiate the Tokenizer 
      InputStream tokenStream = new FileInputStream("OpenNLP_models/en-token.bin"); 
      TokenizerModel tokenModel = new TokenizerModel(tokenStream); 
      TokenizerME tokenizer = new TokenizerME(tokenModel); 

      //Create an instance of stemmer
      PorterStemmer stemmer = new PorterStemmer();

      for( String arg : args ) {
        //Retrieve the positions of the tokens 
        String input = readString(arg);
        Span tokens[] = tokenizer.tokenizePos(input); 
          
        //Get the probabilities of the recent calls to tokenizePos() method 
        double[] probs = tokenizer.getTokenProbabilities(); 

        //Print the spans of tokens 
        for(int i = 0; i < tokens.length; i++) {
           String token = input.substring(tokens[i].getStart(), tokens[i].getEnd());
           System.out.println(tokens[i] + " " + token + "/" + stemmer.stem(token) + 
             " (" + probs[i] + ")");
        }
      }
   } 
}      

