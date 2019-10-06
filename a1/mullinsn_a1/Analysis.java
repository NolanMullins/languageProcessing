/* Analysis 
 * This file will analyze tagged files and output a small summary 
 * of the data
 */
import java.io.FileInputStream; 
import java.io.InputStream;  
import java.io.InputStreamReader;
import java.io.BufferedReader;

public class Analysis { 

    /* DocInfo 
     * Utility class to allow a cleaner way to pass document 
     * information around
     */
    static class DocInfo {
        public int sentCount = 0;
        public int tokCount = 0;
    }

    //Will use a reader to parse through a single document and return a basic summary
    public static DocInfo parseDoc(BufferedReader buf) throws Exception {
        DocInfo info = new DocInfo();
        String line = null;
        //Move over title information 
        while ((line=buf.readLine()) != null && !line.contains("$TEXT"));
        //Read through text and record # of sentences and tokens
        while ((line=buf.readLine()) != null && !line.contains("$DOC")) {
            info.sentCount++;
            info.tokCount += line.split("[ ]").length;
            buf.mark(0);
        }
        //move the reader back a line so the main loop can read the $DOC tag
        if (line != null && line.contains("$DOC"))
            buf.reset();
        return info;
    }
   
    public static void main(String args[]) throws Exception { 
        for( String arg : args ) {
            int docCount = 0;
            int minSent = 9999999, avgSent = 0, maxSent = 0;
            int minTok = 9999999, avgTok = 0, maxTok = 0;

            InputStream infile = new FileInputStream(arg);
            BufferedReader buf = new BufferedReader(new InputStreamReader(infile));
            String line = null;

            //Print out Document info
            System.out.println("----------------------------");
            System.out.println(arg);

            //Loop through each document
            while( (line=buf.readLine()) != null ) {
                if (line.contains("$DOC")) {
                    System.out.print(line);
                    docCount++;
                    DocInfo info = parseDoc(buf);
                    if (info.sentCount < minSent)
                        minSent = info.sentCount;
                    if (info.sentCount > maxSent)
                        maxSent = info.sentCount;
                    if (info.tokCount < minTok)
                        minTok = info.tokCount;
                    if (info.tokCount > maxTok)
                        maxTok = info.tokCount;
                    
                    avgSent += info.sentCount;
                    avgTok += info.tokCount;
                    System.out.println("average sentence length: "+(info.tokCount / info.sentCount));
                }
            }

            //Tot # of tokens / tot # of sentences
            int avgTokPerSentence = avgTok / avgSent;
            avgSent = avgSent / docCount;
            avgTok = avgTok / docCount;

            //print out document info
            System.out.println("--------");
            System.out.println("Document count: "+ docCount);
            System.out.println("Average sentence length: "+avgTokPerSentence);
            System.out.println("Min sentences: "+minSent);
            System.out.println("Avg sentences: "+avgSent);
            System.out.println("Max sentences: "+maxSent);

            System.out.println("Min token: "+minTok);
            System.out.println("Avg token: "+avgTok);
            System.out.println("Max token: "+maxTok);
            System.out.println("----------------------------");
        }
    } 
}

