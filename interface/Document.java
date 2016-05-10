package cz.zcu.kiv.nlp.ir.trec.data;

import java.util.Date;
import java.util.List;

/**
 * Created by Tigi on 8.1.2015.
 */
public interface Document {

    String getText();

    String getId();

    String getTitle();

    List<String> getTags();

    List<String> getLocations();

    Date getDate();

}
