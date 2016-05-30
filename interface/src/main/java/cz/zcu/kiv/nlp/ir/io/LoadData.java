package cz.zcu.kiv.nlp.ir.io;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.zcu.kiv.nlp.ir.trec.SerializedDataHelper;
import cz.zcu.kiv.nlp.ir.trec.data.Document;

/**
 * Class help to user load data from files
 *
 * @author Jakub Zíka
 * @version 1.0
 */
public class LoadData {
	
//	static final String OUTPUT_DIR = "./TREC";
    /** Path to TREC */
	static final String OUTPUT_DIR = "/home/dzejkob23/GIT/information_research/interface/TREC";

    /** Path to czechData.bin */
    File serializedData = new File(OUTPUT_DIR + "/czechData.bin");

    /**
     * Load data from file
     * @return list of documents
     */
	public static List<Document> loadData() {
		File serializedData = new File(OUTPUT_DIR + "/czechData.bin");

        List<Document> documents = new ArrayList<Document>();
        System.out.println("load");
        try {
            if (serializedData.exists()) {
                documents = SerializedDataHelper.load(serializedData);
            } else {
            	System.err.println("Cannot find " + serializedData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Documents: " + documents.size());
        return documents;
	}
	
	
	
	
}
