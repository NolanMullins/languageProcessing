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

    public static void main(String args[]) {
        try {
            for (String arg : args) {
                InputStream infile = new FileInputStream(arg);
                BufferedReader buf = new BufferedReader(new InputStreamReader(infile));
                BufferedWriter dictonaryFile = new BufferedWriter(new FileWriter("dictionary.txt"));
                BufferedWriter postingsFile = new BufferedWriter(new FileWriter("postings.txt"));
                BufferedWriter docidsFile = new BufferedWriter(new FileWriter("docids.txt"));
                
                TreeMap<String, ArrayList<String[]>> stems = new TreeMap<>();
                ArrayList<String[]> docids = new ArrayList<>();
                String line = null;
                String curDocID = "";
                int lineNum = 0;
                while ((line = buf.readLine()) != null && ++lineNum > 0) {
                    if (line.contains("$DOC")) {
                        String doc[] = new String[2];
                        curDocID = line.split("[ ]")[1];
                        doc[0] = curDocID;
                        doc[1] = Integer.toString(lineNum);
                        docids.add(doc);
                        continue;
                    } else if (line.contains("$TEXT") || line.contains("$TITLE")) {
                        continue;
                    } else {
                        String[] tokens = line.split("[ ]");
                        for (String tok : tokens) {
                            if (stems.containsKey(tok)) {
                                updateTF(tok, curDocID, stems);
                            } else {
                                addStemToMap(tok, curDocID, stems);
                            }
                        }

                    }
                }
                //TODO output
                for (Map.Entry<String, ArrayList<String[]>> entry : stems.entrySet()) {
                    System.out.println(entry.getKey() +" - "+ entry.getValue().get(0)[1]);
                }
            }
        } catch (Exception e) {
            System.out.println("Unexpected exception:");
            e.printStackTrace();
        }
    }
}

