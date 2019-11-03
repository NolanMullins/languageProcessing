/* Online processor 
 * Used to search through the processed document
 */
import java.io.*;
import java.io.FileReader;
import java.util.*;
import java.lang.Math;
import opennlp.tools.stemmer.PorterStemmer;

public class Online {
    static PorterStemmer stemmer = null;
    static HashMap<String, Boolean> stopWords = new HashMap<>();
    static String docInfo[][] = null;
    //loads the stopwords into memory, only needs to be calld at the start of the program
    public static void loadStopWords() throws Exception {
        InputStream stopWordsFile = new FileInputStream("data/stopwords.txt");
        if (stopWordsFile == null)
            throw new Exception("Cannot locate stopwords file");
        BufferedReader stopWordsReader = new BufferedReader(new InputStreamReader(stopWordsFile));
        String line = null;
        while((line = stopWordsReader.readLine()) != null)
            stopWords.put(line, true);
    }

    //reads in the dictionary txt and converts it to an offset array
    public static String[][] readDictionary() throws Exception {
        BufferedReader dicFile = new BufferedReader(new FileReader("dictionary.txt"));
        if (dicFile == null)
            throw new Exception("Cannot find dictionary.txt");
        int docTotal = Integer.parseInt(dicFile.readLine());
        int offset = 0;
        String dictionary[][] = new String[docTotal][2];

        //read each stem in the dictionary file
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

    //reads in the postings file
    public static String[][] readPostings() throws Exception {
        BufferedReader postFile = new BufferedReader(new FileReader("postings.txt"));
        if (postFile == null)
            throw new Exception("Cannot find postings.txt");
        int postTot = Integer.parseInt(postFile.readLine());
        String postings[][] = new String[postTot][2];

        //store each entry in the file
        for (int a = 0; a < postTot; a++) {
            //split into docid and tf
            String line[] = postFile.readLine().split("[ ]");
            if (line.length < 2)
                continue;
            postings[a][0] = line[0];
            postings[a][1] = line[1];
        }
        return postings;
    }

    //filters the users query, removes stopwords, punctuation, numbers and stems words
    public static ArrayList<String> preprocessQuery(String query) throws Exception {
        Lexer processor = new Lexer(new StringReader(query));
        if (stemmer == null)
            stemmer = new PorterStemmer();
        Token tok = null;
        ArrayList<String> processedQuery = new ArrayList<>();

        //clean up each token in the array
        while((tok=processor.yylex()) != null) {
            //remove these types
            if (tok.m_type == Token.DELIMITER || tok.m_type == Token.NUM || tok.m_type == Token.PUNCTUATION)
                continue;

            String str = tok.toString();
            //remove stop words
            if (stopWords.containsKey(str.replaceAll("[ ]", "").toLowerCase()))
                continue;
            
            if (str.contains(" "))
                str = str.replaceAll("[ ]", "");
            processedQuery.add(stemmer.stem(str.toLowerCase()));
        }

        return processedQuery;
    }

    //searches through the dictionary and postings and calculates the similarity 
    public static void computeSimForTerm(TreeMap<String, Double> sim, String term, String[][] dictionary, String[][] postings, int totDocs) throws Exception {
        //find the term
        int a = 0;
        for (a = 0; a < dictionary.length; a++)
            if (dictionary[a][0].equals(term))
                break;
        //Not found
        if (a==dictionary.length)
            return;
        int offset = Integer.parseInt(dictionary[a][1]);

        //Calc how many docs contain our search term
        int df = postings.length - offset;
        if (a+1 < dictionary.length)
            df = Integer.parseInt(dictionary[a+1][1]) - offset;
        double idf = Math.log((double)totDocs / (double)df);

        //Compute similarity value for each document in 
        for (a = offset; a < offset + df; a++) {
            int tf = Integer.parseInt(postings[a][1]);
            double simVal = tf*idf;
            if (sim.containsKey(postings[a][0]))
                simVal += sim.get(postings[a][0]);
            sim.put(postings[a][0], simVal);
        }
    }

    //Outputs the top 10 search results to the console
    public static void displaySearch(TreeMap<String, Double> sim) {
        System.out.println("Results: ");
        //get top 10 documents by similarity 
        for (int a = 0; a < 10; a++) {
            Map.Entry<String, Double> top = null;
            //look for top result left in set
            for (Map.Entry<String, Double> entry : sim.entrySet()) {
                if (top == null)
                    top = entry;
                else if (entry.getValue() > top.getValue())
                    top = entry;
            }
            //No matches found, finish
            if (top == null || top.getValue() == 0)
                break;
            //remove top result from list and output to user 
            else {
                System.out.println(top.getKey() + " " + top.getValue());
                sim.remove(top.getKey());
            }
        }
        sim.clear();
    }

    public static void main(String args[]) {
        try {
            //Load data into system
            String dictionary[][] = readDictionary();
            String postings[][] = readPostings();
            loadStopWords();

            //Load the number of documents
            BufferedReader docidsFile = new BufferedReader(new FileReader("docids.txt"));
            if (docidsFile == null)
                throw new Exception("Cannot find docids.txt");
            int numDocs = Integer.parseInt(docidsFile.readLine());
            docInfo = new String[numDocs][3];
            for (int a = 0; a < numDocs; a++) {
                String line[] = docidsFile.readLine().split("[ ]");
                docInfo[a][0] = line[0];
                docInfo[a][1] = line[1];
                docInfo[a][2] = "";
                for (int b = 2; b < line.length; b++) {
                    docInfo[a][2] += line[b] +" ";
                }
            }

            Console console = System.console();
            String query = "";
            TreeMap<String, Double> sim = new TreeMap<>();
            
            //Allow for multiple queries until "q" or "quit" is entered
            while (!(query = console.readLine("Enter: ")).toLowerCase().equals("q") && !query.toLowerCase().equals("quit")) {
                ArrayList<String> pQuery = preprocessQuery(query);
                for (int a = 0; a < pQuery.size(); a++) {
                    computeSimForTerm(sim, pQuery.get(a), dictionary, postings, numDocs);
                }
                displaySearch(sim);
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e);
        }
    }
}