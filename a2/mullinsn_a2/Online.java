/* Online processor 
 * 
 */
import java.io.*;
import java.io.FileReader;
import java.util.*;
import opennlp.tools.stemmer.PorterStemmer;

public class Online {
    static PorterStemmer stemmer = null;
    static HashMap<String, Boolean> stopWords = new HashMap<>();

    public static void loadStopWords() throws Exception {
        InputStream stopWordsFile = new FileInputStream("data/stopwords.txt");
        if (stopWordsFile == null)
            throw new Exception("Cannot locate stopwords file");
        BufferedReader stopWordsReader = new BufferedReader(new InputStreamReader(stopWordsFile));
        String line = null;
        while((line = stopWordsReader.readLine()) != null)
            stopWords.put(line, true);
    }

    public static String[][] readDictionary() throws Exception {
        BufferedReader dicFile = new BufferedReader(new FileReader("dictionary.txt"));
        if (dicFile == null)
            throw new Exception("Cannot find dictionary.txt");
        int docTotal = Integer.parseInt(dicFile.readLine());
        int offset = 0;
        String dictionary[][] = new String[docTotal][2];
        for (int a = 0; a < docTotal; a++) {
            String line[] = dicFile.readLine().split("[ ]");
            if (line.length < 2)
                continue;
            dictionary[a][0] = line[0];
            dictionary[a][1] = Integer.toString(offset);
            offset += Integer.parseInt(line[1]);
        }
        return dictionary;
    }

    public static String[][] readPostings() throws Exception {
        BufferedReader postFile = new BufferedReader(new FileReader("postings.txt"));
        if (postFile == null)
            throw new Exception("Cannot find postings.txt");
        int postTot = Integer.parseInt(postFile.readLine());
        String postings[][] = new String[postTot][2];
        for (int a = 0; a < postTot; a++) {
            String line[] = postFile.readLine().split("[ ]");
            if (line.length < 2)
                continue;
            postings[a][0] = line[0];
            postings[a][1] = line[1];
        }
        return postings;
    }

    public static ArrayList<String> preprocessQuery(String query) throws Exception {
        Lexer processor = new Lexer(new StringReader(query));
        if (stemmer == null)
            stemmer = new PorterStemmer();
        Token tok = null;
        ArrayList<String> processedQuery = new ArrayList<>();
        while((tok=processor.yylex()) != null) {
            if (tok.m_type == Token.DELIMITER || tok.m_type == Token.NUM || tok.m_type == Token.PUNCTUATION)
                continue;
            String str = tok.toString();
            if (stopWords.containsKey(str.replaceAll("[ ]", "").toLowerCase()))
                continue;
            if (str.contains(" "))
                str = str.replaceAll("[ ]", "");
            processedQuery.add(stemmer.stem(str.toLowerCase()));
            System.out.println(processedQuery.get(processedQuery.size()-1));
        }
        return processedQuery;
    }

    public static void main(String args[]) {
        try {
            //Load data into system
            String dictionary[][] = readDictionary();
            String postings[][] = readPostings();
            loadStopWords();

            Console console = System.console();
            String query = "";
            while (!(query = console.readLine("Enter: ")).toLowerCase().equals("q") && !query.toLowerCase().equals("quit")) {
                ArrayList<String> pQuery = preprocessQuery(query);
            }

        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
}