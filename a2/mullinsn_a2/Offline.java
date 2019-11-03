/* Offline processor 
 * 
 */
import java.io.*;
import java.util.*;

public class Offline {

    public static void addStemToMap(String stem, String docid, TreeMap<String, ArrayList<String[]>> stems) {
        ArrayList<String[]> stemData = new ArrayList<>();
        String doc[] = new String[2];
        doc[0] = docid;
        doc[1] = Integer.toString(1);
        stemData.add(doc);
        stems.put(stem, stemData);
    }

    public static void updateTF(String stem, String docid, TreeMap<String, ArrayList<String[]>> stems) {
        ArrayList<String[]> stemData = stems.get(stem);
        //look for existing doc entry
        for (int a = 0; a < stemData.size(); a++) {
            if (stemData.get(a)[0].equals(docid)) {
                int tf = Integer.parseInt(stemData.get(a)[1]);
                stemData.get(a)[1] = Integer.toString(++tf);
                return;
            }
        }
        //No doc entry found for stem
        String doc[] = new String[2];
        doc[0] = docid;
        doc[1] = Integer.toString(1);
        stemData.add(doc);
    }

    public static void writeDictionaryFile(TreeMap<String, ArrayList<String[]>> stems) throws Exception {
        BufferedWriter dictionaryFile = new BufferedWriter(new FileWriter("dictionary.txt"));
        dictionaryFile.write(Integer.toString(stems.size()) + "\n");
        for (Map.Entry<String, ArrayList<String[]>> entry : stems.entrySet()) {
            int df = entry.getValue().size();
            dictionaryFile.write(entry.getKey() + " " + Integer.toString(df) + "\n");
        }
        dictionaryFile.close();
    }

    public static void writePostingsFile(TreeMap<String, ArrayList<String[]>> stems) throws Exception {
        BufferedWriter postingsFile = new BufferedWriter(new FileWriter("postings.txt"));
        int numEntries = 0;
        for (Map.Entry<String, ArrayList<String[]>> entry : stems.entrySet()) {
            numEntries += entry.getValue().size();
        }
        postingsFile.write(Integer.toString(numEntries) + "\n");
        for (Map.Entry<String, ArrayList<String[]>> entry : stems.entrySet()) {
            for (int a = 0; a < entry.getValue().size(); a++) {
                postingsFile.write(entry.getValue().get(a)[0] + " " + entry.getValue().get(a)[1] + "\n");
            }
        }
        postingsFile.close();
    }

    public static void writeDocIDsFile(ArrayList<String[]> docids) throws Exception {
        BufferedWriter docidsFile = new BufferedWriter(new FileWriter("docids.txt"));
        docidsFile.write(Integer.toString(docids.size()) + "\n");
        for (int a = 0; a < docids.size(); a++) {
            docidsFile.write(docids.get(a)[0] + " " + docids.get(a)[1] + " " + docids.get(a)[2] + "\n");
        }
        docidsFile.close();
    }

    public static void parseLine(TreeMap<String, ArrayList<String[]>> stems, String curDocID, String line) {
        String[] tokens = line.split("[ ]");
        for (String tok : tokens) {
            if (stems.containsKey(tok)) {
                updateTF(tok, curDocID, stems);
            } else {
                addStemToMap(tok, curDocID, stems);
            }
        }
    }

    public static void main(String args[]) {
        try {
            for (String arg : args) {
                InputStream infile = new FileInputStream(arg);
                BufferedReader buf = new BufferedReader(new InputStreamReader(infile));
                
                TreeMap<String, ArrayList<String[]>> stems = new TreeMap<>();
                ArrayList<String[]> docids = new ArrayList<>();
                String line = null;
                String curDocID = "";
                int lineNum = 0;
                while ((line = buf.readLine()) != null && ++lineNum > 0) {
                    if (line.contains("$DOC")) {
                        String doc[] = new String[3];
                        curDocID = line.split("[ ]")[1];
                        doc[0] = curDocID;
                        doc[1] = Integer.toString(lineNum);
                        doc[2] = "NULL";
                        docids.add(doc);
                        continue;
                    } else if (line.contains("$TITLE")) {
                        line = buf.readLine();
                        lineNum++;
                        docids.get(docids.size()-1)[2] = line;
                        parseLine(stems, curDocID, line);
                    } else {
                        if (line.contains("$TEXT"))
                            continue;
                        parseLine(stems, curDocID, line);
                    }
                }
                writeDictionaryFile(stems);
                writePostingsFile(stems);
                writeDocIDsFile(docids);
            }
        } catch (Exception e) {
            System.out.println("Unexpected exception:");
            e.printStackTrace();
        }
    }
}

