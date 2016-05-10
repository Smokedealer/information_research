package cz.zcu.kiv.nlp.ir.trec.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Tigi on 8.1.2015.
 */
public class DocumentImpl implements Document, Serializable {
    String text;
    String id;
    String title;
    List<String> tags;
    List<String> locations;
    Date date;

    public DocumentImpl() {

        tags = new ArrayList<String>();
        locations = new ArrayList<String>();
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
