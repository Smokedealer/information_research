package cz.zcu.kiv.nlp.ir.IO;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cz.zcu.kiv.nlp.ir.trec.SerializedDataHelper;
import cz.zcu.kiv.nlp.ir.trec.data.Document;

public class LoadData {
	
	static final String OUTPUT_DIR = "./TREC";
	File serializedData = new File(OUTPUT_DIR + "/czechData.bin");

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
