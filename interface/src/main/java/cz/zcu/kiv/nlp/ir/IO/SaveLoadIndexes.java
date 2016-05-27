package cz.zcu.kiv.nlp.ir.IO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cz.zcu.kiv.nlp.ir.indexing.TermInfo;

public class SaveLoadIndexes {

	private static final String INDEXES = "./Indexes/indexes.txt";
	
	public static void saveIndexes(HashMap<String, TermInfo> dictionary) {
		
		PrintStream printStream = null;
        try {
            printStream = new PrintStream(new FileOutputStream(new File(INDEXES)), true, "UTF-8");

            Iterator it = dictionary.entrySet().iterator();
            while (it.hasNext()) {
            	Map.Entry pair = (Map.Entry) it.next();
            	printStream.println(pair.getKey() + ";" + pair.getValue().toString());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (printStream != null) {
                printStream.close();
            }
        }
		
	}
	
	public static HashMap<String, TermInfo> loadIndexes() throws FileNotFoundException {
		InputStream inputStream = new FileInputStream(INDEXES);
		HashMap<String, TermInfo> dictionary = new HashMap<String, TermInfo>();
		
		try {
			
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			String line;
			
			while ((line = br.readLine()) != null) {
				if (!line.trim().isEmpty()) {
					String [] data = line.split(";");
					if (!data[1].isEmpty()) {
						String key = data[0];
						String [] postingsArray = data[2].split(",");
						TermInfo termInfo = new TermInfo(key, Integer.parseInt(data[1]), new HashSet<String>(Arrays.asList(postingsArray)));
						dictionary.put(key, termInfo);
					}
				}
			}
			
			inputStream.close();
			
			return dictionary;
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	
}
