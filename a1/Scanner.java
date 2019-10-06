import java.io.InputStreamReader;
import java.io.PrintWriter;

public class Scanner {
    private Lexer scanner = null;

    public Scanner( Lexer lexer ) {
        scanner = lexer; 
    }

    public String getNextToken() throws java.io.IOException {
        Token tok = scanner.yylex();
        if (tok == null) {
            return "---NULL---";
        }
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

    public static void main(String argv[]) {
        try {
            Scanner scanner = new Scanner(new Lexer(new InputStreamReader(System.in)));
            String tok = "";
            PrintWriter writer = new PrintWriter("sample.tokenized", "UTF-8");
            //Write tokens to new file
            while(!(tok=scanner.getNextToken()).equals("---NULL---"))
                writer.print(tok);
            writer.close();
        } catch (Exception e) {
            System.out.println("Unexpected exception:");
            e.printStackTrace();
        }
    }
}
