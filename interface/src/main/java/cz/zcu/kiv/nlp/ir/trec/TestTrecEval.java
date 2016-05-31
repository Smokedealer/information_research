package cz.zcu.kiv.nlp.ir.trec;

import cz.zcu.kiv.nlp.ir.exceptions.QueryParserException;
import cz.zcu.kiv.nlp.ir.trec.data.Document;
import cz.zcu.kiv.nlp.ir.trec.data.DocumentImpl;
import cz.zcu.kiv.nlp.ir.trec.data.Result;
import cz.zcu.kiv.nlp.ir.trec.data.Topic;
import org.apache.log4j.*;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @author tigi
 */

public class TestTrecEval {

    static Logger log = Logger.getLogger(TestTrecEval.class);
    static final String OUTPUT_DIR = "./TREC";
    static final int TOP_X = 10;
    private static String path = "";

    protected static void configureLogger() {
        BasicConfigurator.resetConfiguration();
        BasicConfigurator.configure();

        File results = new File(path + OUTPUT_DIR);
        if (!results.exists()) {
            results.mkdir();
        }

        try {
            Appender appender = new WriterAppender(new PatternLayout(), new FileOutputStream(new File(path + OUTPUT_DIR + "/" + SerializedDataHelper.SDF.format(System.currentTimeMillis()) + " - " + ".log"), false));
            BasicConfigurator.configure(appender);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Logger.getRootLogger().setLevel(Level.INFO);
    }

    public static void main(String args[]) throws IOException, QueryParserException {
        if (args[0] != null) {
            path = args[0];
        }

        final File trecRelevanceFile = new File(path + OUTPUT_DIR + "/AH-CLEF2007_cs.xml");
        configureLogger();

//        todo constructor
        final long startQueryResults = System.currentTimeMillis();
        Index index = new Index();

        File serializedData = new File(path + OUTPUT_DIR + "/czechData.bin");

        List<Document> documents = new ArrayList<Document>();
        log.info("load");
        try {
            if (serializedData.exists()) {
                documents = SerializedDataHelper.load(serializedData);
            } else {
                log.error("Cannot find " + serializedData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        index.index(documents);
        log.info("Documents: " + documents.size());

        List<String> lines = new ArrayList<String>();

        List<Topic> topics = new ArrayList<Topic>();
        try {
            topics.addAll(parseTopics(trecRelevanceFile));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (args[1] != null) {
            List<Result> resultHits = index.search(args[1]);

            Comparator<Result> cmp = new Comparator<Result>() {
                public int compare(Result o1, Result o2) {
                    if (o1.getScore() > o2.getScore()) return -1;
                    if (o1.getScore() == o2.getScore()) return 0;
                    return 1;
                }
            };

            Collections.sort(resultHits, cmp);

            System.out.println("***** Vysledky vyhledavani *****");
            for (int i = 0; i < TOP_X; i++) {
                Result r = resultHits.get(i);
                System.out.println(i + ". " + r.toString(r.getDocumentID()));
            }

        } else {
            for (Topic t : topics) {
                List<Result> resultHits = index.search(t.getTitle() + " " + t.getDescription());

                Comparator<Result> cmp = new Comparator<Result>() {
                    public int compare(Result o1, Result o2) {
                        if (o1.getScore() > o2.getScore()) return -1;
                        if (o1.getScore() == o2.getScore()) return 0;
                        return 1;
                    }
                };

                Collections.sort(resultHits, cmp);
                for (Result r : resultHits) {
                    final String line = r.toString(t.getId());
                    lines.add(line);
//                log.info(line);
                }
            }
        }


        final long queryResults = System.currentTimeMillis();
        System.out.println("Time - cele to trvalo: " + ((queryResults - startQueryResults) / 1000) / 60 + " minut" +
                " " + ((queryResults - startQueryResults) / 1000) % 60 + " sekund " + ((queryResults - startQueryResults) % 1000) + " milisekund" );

        final File outputFile = new File(path + OUTPUT_DIR + "/results " + SerializedDataHelper.SDF.format(System.currentTimeMillis()) + ".txt");
        IOUtils.saveFile(outputFile, lines);
        //try to run evaluation
        try {
            runTrecEval(outputFile.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String runTrecEval(String predictedFile) throws IOException {

        String commandLine = path + "./trec_eval.8.1/./trec_eval " +
                path + "./trec_eval.8.1/czcech" +
                " " + predictedFile;

        System.out.println(commandLine);
        Process process = Runtime.getRuntime().exec(commandLine);

        BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        String trecEvalOutput = null;
        StringBuilder output = new StringBuilder("TREC EVAL output:\n");
        for (String line = null; (line = stdout.readLine()) != null; ) output.append(line).append("\n");
        trecEvalOutput = output.toString();
        System.out.println(trecEvalOutput);

        int exitStatus = 0;
        try {
            exitStatus = process.waitFor();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        System.out.println(exitStatus);

        stdout.close();
        stderr.close();

        return trecEvalOutput;
    }

    private static List<Document> parseDocuments(File directory) throws ParseException, ParserConfigurationException, IOException, SAXException, XPathExpressionException {

        final String SUFFIX = ".dtd";
        final String DOC = "DOC";
        final String DOCID = "DOCID";
        final String DATE = "DATE";
        final String TITLE = "TITLE";
        final String HEADING = "HEADING";
        final String GEOGRAPHY = "GEOGRAPHY";
        final String TEXT = "TEXT";
        DateFormat df = new SimpleDateFormat("MM/dd/yy", Locale.ENGLISH);

        List<Document> result = new ArrayList<Document>();
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            for (File f : files) {
                if (f.getName().endsWith(SUFFIX)) {
                    //Skip
                } else {

                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    org.w3c.dom.Document doc = builder.parse(f);

                    // Create XPathFactory object
                    XPathFactory xpathFactory = XPathFactory.newInstance();

                    // Create XPath object
                    XPath xpath = xpathFactory.newXPath();

                    XPathExpression expr = xpath.compile("//CZE/" + DOC);
                    NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
                    for (int i = 0; i < nodes.getLength(); i++) {
                        org.w3c.dom.Node element = nodes.item(i);
                        DocumentImpl d = new DocumentImpl();
                        d.setId((String) xpath.compile(".//" + DOCID + "/text()").evaluate(element, XPathConstants.STRING));
                        d.setDate(df.parse((String) xpath.compile(".//" + DATE + "/text()").evaluate(element, XPathConstants.STRING)));
                        d.setTitle((String) xpath.compile(".//" + TITLE + "/text()").evaluate(element, XPathConstants.STRING));

//                        d.setAuthor(author);
//                        d.setType(type);

                        List<String> locations = extractNodeValues((NodeList) xpath.compile(".//" + GEOGRAPHY + "/text()").evaluate(element, XPathConstants.NODESET));
                        d.setLocations(locations);

                        List<String> tags = extractNodeValues((NodeList) xpath.compile(".//" + HEADING + "/text()").evaluate(element, XPathConstants.NODESET));
                        d.setTags(tags);

                        List<String> list = extractNodeValues((NodeList) xpath.compile(".//" + TEXT + "/text()").evaluate(element, XPathConstants.NODESET));
                        String text = "";
                        for (String s : list) {
                            text += s + " ";
                        }
                        text = text.trim();
                        d.setText(text);

                        result.add(d);
                    }

                }
            }
        }
        return result;
    }


    private static List<Topic> parseTopics(File file) throws ParseException, ParserConfigurationException, IOException, SAXException, XPathExpressionException {

        final String TOPIC = "topic";
        final String ID = "identifier";
        final String DESCRIPTION = "description";
        final String TITLE = "title";
        final String NARRATIVE = "narrative";
        final String LANG = "lang";

        List<Topic> result = new ArrayList<Topic>();
        if (file.exists()) {

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document doc = builder.parse(file);

            // Create XPathFactory object
            XPathFactory xpathFactory = XPathFactory.newInstance();

            // Create XPath object
            XPath xpath = xpathFactory.newXPath();

            XPathExpression expr = xpath.compile("//topics/" + TOPIC);
            NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); i++) {
                org.w3c.dom.Node element = nodes.item(i);
                Topic t = new Topic();
                t.setId((String) xpath.compile(".//" + ID + "/text()").evaluate(element, XPathConstants.STRING));
                t.setDescription((String) xpath.compile(".//" + DESCRIPTION + "/text()").evaluate(element, XPathConstants.STRING));
                t.setTitle((String) xpath.compile(".//" + TITLE + "/text()").evaluate(element, XPathConstants.STRING));
                t.setNarrative((String) xpath.compile(".//" + NARRATIVE + "/text()").evaluate(element, XPathConstants.STRING));
                t.setLang((String) xpath.compile("./@" + LANG).evaluate(element, XPathConstants.STRING));

                result.add(t);
            }

        }
        return result;
    }

    private static List<String> extractNodeValues(NodeList nodes) throws XPathExpressionException {
        List<String> values = new ArrayList<String>();
        for (int j = 0; j < nodes.getLength(); j++) {
            org.w3c.dom.Node n = nodes.item(j);
            values.add(n.getNodeValue());
        }
        return values;
    }


}
