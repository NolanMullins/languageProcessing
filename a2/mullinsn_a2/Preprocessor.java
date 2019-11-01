/* Preprocessor 
 *
 */
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.FileInputStream; 
import java.io.InputStream;  
import java.util.HashMap;
import opennlp.tools.stemmer.PorterStemmer;

public class Preprocessor {
    private Lexer scanner = null;
    PorterStemmer stemmer = null;

    public Preprocessor(Lexer lexer) {
        scanner = lexer;
        stemmer = new PorterStemmer();
    }

    //Gets the next token from the scanner and checks the hyphenated case
    public String getNextToken(HashMap<String, Boolean> stopWords) throws java.io.IOException {
        Token tok = scanner.yylex();
        if (tok == null)
            return null;
        if (tok.m_type == Token.DELIMITER || tok.m_type == Token.NEG_NUM || tok.m_type == Token.NUM || tok.m_type == Token.PUNCTUATION)
            return "";
        String str = tok.toString();
        if (stopWords.containsKey(str.replaceAll("[ ]", "").toLowerCase()))
            return "";
        if (str.contains("$DOC") || str.contains("$TEXT") || str.contains("$TITLE"))
            return str;
        if (str.contains(" "))
            return stemmer.stem(str.replaceAll("[ ]", "").toLowerCase()) + " ";
        return stemmer.stem(str.toLowerCase());
    }

    public static void main(String args[]) {
        try {
            InputStream stopWordsFile = new FileInputStream("data/stopwords.txt");
            BufferedReader stopWordsReader = new BufferedReader(new InputStreamReader(stopWordsFile));
            HashMap<String, Boolean> stopWords = new HashMap<>();
            String line = null;
            while((line = stopWordsReader.readLine()) != null)
                stopWords.put(line, true);
            for (String arg : args) {
                InputStream infile = new FileInputStream(arg);
                Preprocessor processor = new Preprocessor(new Lexer(new InputStreamReader(infile)));
                String tok = "";
                //Write tokens to new file
                while((tok=processor.getNextToken(stopWords)) != null)
                    System.out.print(tok);
            }
        } catch (Exception e) {
            System.out.println("Unexpected exception:");
            e.printStackTrace();
        }
    }
}
