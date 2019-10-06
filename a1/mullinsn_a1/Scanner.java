/* Scanner 
 * This file will use the jflex tokenized data and 
 * output data with correct formatting. It will also 
 * provide a slightly deeper analysis on tokens 
 * looking for hyphenated cases 
 */
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.FileInputStream; 
import java.io.InputStream;  

public class Scanner {
    private Lexer scanner = null;

    public Scanner( Lexer lexer ) {
        scanner = lexer; 
    }

    //Gets the next token from the scanner and checks the hyphenated case
    public String getNextToken() throws java.io.IOException {
        Token tok = scanner.yylex();
        if (tok == null)
            return null;
        String str = tok.toString();
        int c = 0;
        //Check how many '-' there are
        for (int i = 0; i < str.length(); i++)
            if (str.charAt(i)=='-')
                c++;
        //If theres more than 3 chunks connected we split all '-' and '
        if (c>2) {
            String newTok = "";
            for (int i = 0; i < str.length(); i++)
                if (str.charAt(i)=='-' || str.charAt(i) == '\'')
                    newTok += " "+str.charAt(i)+" ";
                else
                    newTok += str.charAt(i);
            str = newTok;
        }
        return str;
    }

    public static void main(String args[]) {
        try {
            for (String arg : args) {
                InputStream infile = new FileInputStream(arg);
                Scanner scanner = new Scanner(new Lexer(new InputStreamReader(infile)));
                String tok = "";
                PrintWriter writer = new PrintWriter("sample.tokenized", "UTF-8");
                //Write tokens to new file
                while((tok=scanner.getNextToken()) != null)
                    writer.print(tok);
                writer.close();
            }
        } catch (Exception e) {
            System.out.println("Unexpected exception:");
            e.printStackTrace();
        }
    }
}
