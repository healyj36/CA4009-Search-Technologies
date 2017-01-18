/*
 * Jordan Healy    - 13379226
 * Tríona Barrow   - 11319851
 */
//package wordcounter;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class WordCounter {
    String fileName;
    PorterStemmer ps = new PorterStemmer();
    
    static final String DELIMS = " \t,;.?!'\"";
    
    WordCounter(String fileName) {
        this.fileName = fileName;
    }
    
    List<String> getTerms() throws Exception {
        List<String> terms = new ArrayList<>();
        FileReader fr = new FileReader(fileName);
        BufferedReader br = new BufferedReader(fr);
        
        String line;
        
        while ((line = br.readLine())!= null) {
            StringTokenizer st = new StringTokenizer(line, DELIMS);
            while (st.hasMoreTokens()) {
		// Convert term to lowercase
                String term = st.nextToken().toLowerCase();
                // Stem term
		ps.add(term.toCharArray(), term.length());
		ps.stem();
		{
		    String stemmedTerm = ps.toString();

		    terms.add(stemmedTerm);
		}
            }
        }
        return terms;
    }
    
    HashMap<String, Integer> getTfs() throws Exception {
        HashMap<String, Integer> wordCountMap = new HashMap<>();
        List<String> terms = getTerms();
        for (String term: terms) {
            Integer tf = wordCountMap.get(term);
            if (tf == null) {
                tf = new Integer(0);
            }
            tf++;
            wordCountMap.put(term, tf);
        }
        return wordCountMap;
    }
    
    void showTerms(HashMap<String, Integer> map, int k) {
	// Sort map by values (term fequency)
	Set<Entry<String, Integer>> set = map.entrySet();
        List<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>(set);
        Collections.sort( list, new Comparator<Map.Entry<String, Integer>>()
        {
            public int compare( Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2 )
            {
                return (o2.getValue()).compareTo( o1.getValue() );
            }
        } );
	// Print out first k in the format <WORD>: <FREQ>
	int i = 0;
	Iterator entries = list.iterator();
	if(k > list.size()) {
	    k=list.size();
	}
	while(i<k) {
	    Entry thisEntry = (Entry) entries.next();
	    System.out.println(thisEntry.getKey() + ": " + thisEntry.getValue());
	    i++;
	}

    }

    public static void main(String[] args) {        
	if(args.length > 0) {
	    int k = Integer.parseInt(args[0]);
	    try {
	        WordCounter wc = new WordCounter("test.txt");
	        HashMap<String, Integer> wmap = wc.getTfs();
	        wc.showTerms(wmap, k);	    
	    }
	    catch (Exception ex) {
	        ex.printStackTrace();
	    }
	} else {
	// If user doesn't input a number for terms
	    System.out.println("Please enter k number of terms");	
	    System.exit(0);
	}
    }
    
}
