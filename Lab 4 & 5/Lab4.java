import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.*;

public class Lab4 {
    public static double rwi(int ri, int N, double ni, int R) {
		return Math.log((ri + 0.5)*(N - ni - R + ri + 0.5) / (ni - ri + 0.5)*(R - ri + 0.5));
    }

    public static void main(String[] args) throws Exception {
		String query1 = "bone";
		String query2 = "disease";	
		String query = query1 + "%20" + query2;	
		String simf = "BM25";
		String k = "1.2";
		String b = "0.75";
		String numwanted = "10";

		URL url = new URL("http://136.206.115.117:8080/IRModelGenerator/SearchServlet?query=\"" + query +"\"&simf=" + simf +"&k="+ k +"&b="+ b +"&numwanted=" + numwanted);
		URLConnection uc = url.openConnection();
		BufferedReader in = new BufferedReader(
									new InputStreamReader(
									uc.getInputStream()));

		String inputLine = in.readLine(); // whole page
		in.close();

		String[] eachDocLi = inputLine.split("<li>"); // split from <li> to </li>
		// eachDocLi[0] is "<ul>"
		// eachDocLi[1] is first doc
		// eachDocLi[2] is second doc ...
		// remove first elem from eachDocLi
		String[] eachDoc = Arrays.copyOfRange(eachDocLi, 1, eachDocLi.length);

		// eachDocFreqVector is the frequency vector from the html (the second div in the li)
		String[] eachDocFreqVector = new String[eachDoc.length];
		String[] docNames = new String[eachDoc.length];
		String targetBlank = "target=\"_blank\">";
		for(int i=0; i<eachDoc.length; i++) {
			docNames[i] = eachDoc[i].substring(eachDoc[i].indexOf(targetBlank) + targetBlank.length(), eachDoc[i].indexOf("</a></div>"));
			String[] temp = eachDoc[i].split("</div>");
			eachDocFreqVector[i] = temp[1];
		}

		// create map of each docs words, with there frequency, idf and df
		// e.g. {LA030689-0082=[[bone, 28, 168.25581, 47.555] ...]}
		String freqVectorBr = "Freq Vector: <br>";
		HashMap<String, List<String[]>> allDocs = new HashMap<>();
		for(int i=0; i<docNames.length; i++) {
			List<String[]> wordsInDoc = new LinkedList<>();
			String docFreqVector = eachDocFreqVector[i].substring(eachDocFreqVector[i].indexOf(freqVectorBr) + freqVectorBr.length());
			StringTokenizer tokenizer = new StringTokenizer(docFreqVector);
			while (tokenizer.hasMoreTokens()) {
				String[] toAdd = new String[4];
				String[] temp3 = tokenizer.nextToken().split(":");
				toAdd[0] = temp3[0]; // word "bone"
				toAdd[1] = temp3[1].substring(0, temp3[1].length()-1); // freq 28
				toAdd[2] = tokenizer.nextToken(); // idf 168.25581
				toAdd[3] = String.valueOf((1/Double.parseDouble(toAdd[2]))*500000); //df = (1/idf)*500000
				wordsInDoc.add(toAdd);
			}
			allDocs.put(docNames[i], wordsInDoc);
		}

		int N = 500000; // total number of documents in the collection
		int R = Integer.parseInt(numwanted); // the total number of known relevant documents in the collection archive. assume its 10

		HashMap<String, Double> allRwi = new HashMap<>();
		HashMap<String, Integer> allRi = new HashMap<>();
		// calculate rwi for all words in allDocs
		// put all rwi into allRwi
		for(Map.Entry<String, List<String[]>> entry : allDocs.entrySet()) {
			// for eachDoc in allDoc
			String docName = entry.getKey();
			List<String[]> wordsInDoc = entry.getValue();
			ListIterator iterator = wordsInDoc.listIterator();	
			while(iterator.hasNext()) {
				// for word in eachWord
				String[] wordContents = (String[])iterator.next();
				String word = wordContents[0];
				int ri = 0;
				double ni = N/Double.parseDouble(wordContents[2]);
				
				for(Map.Entry<String, List<String[]>> entry2 : allDocs.entrySet()) {
					// for eachDoc in allDoc
					String docName2 = entry2.getKey();
					List<String[]> wordsInDoc2 = entry2.getValue();
					ListIterator iterator2 = wordsInDoc2.listIterator();	
					while(iterator2.hasNext()) {
						// for each array in list
						String[] wordContents2 = (String[])iterator2.next();
						if(word.equals(wordContents2[0])) {
							ri++;
							break;
						}
					}
					allRi.put(word, ri);
				}
				double rwi = rwi(ri, N, ni, R);
				allRwi.put(word, rwi);
			}	
		}

		// calculate owi for all words in allRwi
		HashMap<Double, String> allOwi = new HashMap<>();
		for(Map.Entry<String, Double> entry : allRwi.entrySet()) {
			String word = entry.getKey();
			double rwi = entry.getValue();
			double owi = allRi.get(word) * rwi;
			allOwi.put(owi, word);
		}

		// get all owis
		Set<Double> owis = allOwi.keySet();
		List<Double> owisList = new ArrayList<>(owis);
		// sort all owis in ascending order
		Collections.sort(owisList);

		// top 5 words and owis
		for(int i=1; i<6; i++) {
			double owi = owisList.get(owisList.size()-i);
			String word = allOwi.get(owi);
			System.out.println(word + ": " + owi);
		}        
	}
}
