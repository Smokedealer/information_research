package cz.zcu.kiv.nlp.ir.io;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.zcu.kiv.nlp.ir.trec.SerializedDataHelper;
import cz.zcu.kiv.nlp.ir.trec.data.Document;

public class LoadData {
	
//	static final String OUTPUT_DIR = "./TREC";
	static final String OUTPUT_DIR = "/home/dzejkob23/GIT/information_research/interface/TREC";
	File serializedData = new File(OUTPUT_DIR + "/czechData.bin");

	public static Map<String, Document> loadData() {
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

        Map<String, Document> map = new HashMap<String, Document>();
        for (Document doc : documents) map.put(doc.getId(), doc);

        System.out.println("Documents: " + documents.size());
        return map;
	}
	
	
	
	
}
