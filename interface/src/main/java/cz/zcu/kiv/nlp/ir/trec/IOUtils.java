package cz.zcu.kiv.nlp.ir.trec;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import javax.xml.stream.*;

import cz.zcu.kiv.nlp.ir.trec.data.Topic;

/**
 * @author tigi
 */
public class IOUtils {

	public static void nactiXML(String soubor) {
		try {
			XMLInputFactory f = XMLInputFactory.newInstance();
			XMLStreamReader r = f.createXMLStreamReader(new FileInputStream(soubor));
			
			HashSet<Topic> topics = new HashSet<Topic>();
			Topic t = null;
			
			while (r.hasNext() == true) {
				r.next();
				if (r.isStartElement() == true) {
					if (r.getLocalName().equals(Tags.TOPIC.getTag())) {
						// new topic
						t = new Topic();
						// set lang
						t.setLang(r.getAttributeValue(null, Tags.LANG.getTag()));
						
					} else if (r.getLocalName().equals(Tags.IDENTIFIER.getTag())) {
						// set identifier
						t.setId(r.getElementText());
						
					} else if (r.getLocalName().equals(Tags.TITLE.getTag())) {
						// set title
						t.setTitle(r.getElementText());
						
					} else if (r.getLocalName().equals(Tags.DESCRIPTION.getTag())) {
						// set description
						t.setDescription(r.getElementText());
						
					} else if (r.getLocalName().equals(Tags.NARRATIVE.getTag())) {
						// set narrative
						t.setNarrative(r.getElementText());
						
					}
					// TODO set close of TOPic and add topicObject to the topics hashset
				}
			}
			
		} catch(XMLStreamException e) {
			System.out.println("Chyba pri cteni XML souboru " + soubor + ".");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    /**
     * Read lines from the stream; lines are trimmed and empty lines are ignored.
     *
     * @param inputStream stream
     * @return list of lines
     */
    public static List<String> readLines(InputStream inputStream) {
        if (inputStream == null) {
            throw new IllegalArgumentException("Cannot locate stream");
        }
        try {
            List<String> result = new ArrayList<String>();

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line;

            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    result.add(line.trim());
                }
            }

            inputStream.close();

            return result;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Read lines from the stream; lines are trimmed and empty lines are ignored.
     *
     * @param inputStream stream
     * @return text
     */
    public static String readFile(InputStream inputStream) {
        StringBuilder sb = new StringBuilder();
        if (inputStream == null) {
            throw new IllegalArgumentException("Cannot locate stream");
        }
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            inputStream.close();

            return sb.toString().trim();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Saves lines from the list into given file; each entry is saved as a new line.
     *
     * @param file file to save
     * @param list lines of text to save
     */
    public static void saveFile(File file, Collection<String> list) {
        PrintStream printStream = null;
        try {
            printStream = new PrintStream(new FileOutputStream(file), true, "UTF-8");

            for (String text : list) {
                printStream.println(text);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (printStream != null) {
                printStream.close();
            }
        }
    }

    /**
     * Saves lines from the list into given file; each entry is saved as a new line.
     *
     * @param file file to save
     * @param text text to save
     */
    public static void saveFile(File file, String text) {
        PrintStream printStream = null;
        try {
            printStream = new PrintStream(new FileOutputStream(file), true, "UTF-8");

            printStream.println(text);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (printStream != null) {
                printStream.close();
            }
        }
    }
}
